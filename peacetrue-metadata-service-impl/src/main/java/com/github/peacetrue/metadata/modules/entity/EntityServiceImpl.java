package com.github.peacetrue.metadata.modules.entity;

import com.github.peacetrue.core.IdCapable;
import com.github.peacetrue.core.OperatorCapable;
import com.github.peacetrue.core.Operators;
import com.github.peacetrue.core.Range;
import com.github.peacetrue.metadata.modules.property.PropertyAdd;
import com.github.peacetrue.metadata.modules.property.PropertyService;
import com.github.peacetrue.metadata.modules.property.PropertyVO;
import com.github.peacetrue.result.ResultType;
import com.github.peacetrue.result.exception.ResultException;
import com.github.peacetrue.spring.data.relational.core.query.CriteriaUtils;
import com.github.peacetrue.spring.data.relational.core.query.UpdateUtils;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.function.Tuple2;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 实体服务实现
 *
 * @author xiayx
 */
@Slf4j
@Service
public class EntityServiceImpl implements EntityService {

    @Autowired
    private R2dbcEntityOperations entityOperations;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PropertyService propertyService;

    public static Criteria buildCriteria(EntityQuery params) {
        return CriteriaUtils.and(
                CriteriaUtils.nullableCriteria(CriteriaUtils.smartIn("id"), params::getId),
                CriteriaUtils.nullableCriteria(Criteria.where("code")::like, value -> "%" + value + "%", params::getCode),
                CriteriaUtils.nullableCriteria(Criteria.where("name")::like, value -> "%" + value + "%", params::getName),
                CriteriaUtils.nullableCriteria(Criteria.where("serialNumber")::is, params::getSerialNumber),
                CriteriaUtils.nullableCriteria(Criteria.where("creatorId")::is, params::getCreatorId),
                CriteriaUtils.nullableCriteria(Criteria.where("createdTime")::greaterThanOrEquals, params.getCreatedTime()::getLowerBound),
                CriteriaUtils.nullableCriteria(Criteria.where("createdTime")::lessThan, DateUtils.DATE_CELL_EXCLUDE, params.getCreatedTime()::getUpperBound),
                CriteriaUtils.nullableCriteria(Criteria.where("modifierId")::is, params::getModifierId),
                CriteriaUtils.nullableCriteria(Criteria.where("modifiedTime")::greaterThanOrEquals, params.getModifiedTime()::getLowerBound),
                CriteriaUtils.nullableCriteria(Criteria.where("modifiedTime")::lessThan, DateUtils.DATE_CELL_EXCLUDE, params.getModifiedTime()::getUpperBound)
        );
    }

    @Override
    @Transactional
    public Mono<EntityVO> add(EntityAdd params) {
        log.info("新增或获取实体信息[{}]", params);
        return this.get(Operators.setOperator(params, new EntityGet(params.getCode())))
                .switchIfEmpty(Mono.defer(() -> this.doAdd(params)));
    }

    private Mono<EntityVO> doAdd(EntityAdd params) {
        log.info("执行新增实体信息[{}]", params);
        return Mono
                .just(params)
                .doOnNext(BeanUtils::setDefaultValue)
                .map(entityAdd -> {
                    Entity entity = BeanUtils.map(params, Entity.class);
                    entity.setCreatorId(params.getOperatorId());
                    entity.setCreatedTime(LocalDateTime.now());
                    entity.setModifierId(entity.getCreatorId());
                    entity.setModifiedTime(entity.getCreatedTime());
                    return entity;
                })
                .flatMap(entity -> entityOperations.insert(entity))
                .map(item -> BeanUtils.map(item, EntityVO.class))
                .flatMap(vo -> addProperty(params, vo))
                .doOnNext(item -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(item, params)))
                ;
    }

    private Mono<EntityVO> addProperty(EntityAdd params, EntityVO vo) {
        return addPropertyPart(params, vo)
                .contextWrite(Context.of(params, vo))
                .collectList()
                .doOnNext(vo::setProperties)
                .thenReturn(vo);
    }

    private Flux<PropertyVO> addPropertyPart(EntityAdd params, EntityVO vo) {
        return Mono
                .justOrEmpty(params.getProperties())
                .flatMapMany(Flux::fromIterable)
                .index()
                .doOnNext(tuple2 -> {
                    tuple2.getT2().setEntityId(vo.getId());
                    tuple2.getT2().setSerialNumber(tuple2.getT1().intValue() + 1);
                    Operators.setOperator(params, tuple2.getT2());
                })
                .map(Tuple2::getT2)
                .flatMap(propertyAdd -> Mono.deferContextual(contextView -> {
                    if ((propertyAdd.getReferenceId() == null || propertyAdd.getReferenceId() == 0L) && propertyAdd.getReference() != null) {
                        Optional<EntityVO> orEmpty = contextView.getOrEmpty(propertyAdd.getReference());
                        if (orEmpty.isPresent()) {
                            propertyAdd.setReferenceId(orEmpty.get().getId());
                        } else {
                            return addReference(propertyAdd);
                        }
                    }
                    return Mono.just(propertyAdd);
                }))
                .flatMap(propertyService::add)
                ;
    }

    private Mono<? extends PropertyAdd> addReference(PropertyAdd params) {
        Operators.setOperator(params, params.getReference());
        return this
                .add(params.getReference())
                .map(EntityVO::getId)
                .doOnNext(params::setReferenceId)
                .thenReturn(params)
                ;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<EntityVO>> query(EntityQuery params, @Nullable Pageable pageable, String... projection) {
        log.info("分页查询实体信息[{}]", params);
        //if (params == null) params = EntityQuery.DEFAULT;
        if (params.getCreatedTime() == null) params.setCreatedTime(Range.LocalDateTime.DEFAULT);
        if (params.getModifiedTime() == null) params.setModifiedTime(Range.LocalDateTime.DEFAULT);
        Pageable finalPageable = pageable == null ? PageRequest.of(0, 10) : pageable;
        Criteria where = buildCriteria(params);

        return entityOperations.count(Query.query(where), Entity.class)
                .flatMap(total -> total == 0L ? Mono.empty() : Mono.just(total))
                .<Page<EntityVO>>flatMap(total -> {
                    Query query = Query.query(where).with(finalPageable).sort(finalPageable.getSortOr(Sort.by("createdTime").descending()));
                    return entityOperations.select(query, Entity.class)
                            .map(item -> BeanUtils.map(item, EntityVO.class))
                            .collectList()
                            .doOnNext(item -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(item, params)))
                            .map(item -> new PageImpl<>(item, finalPageable, total));
                })
                .switchIfEmpty(Mono.just(new PageImpl<>(Collections.emptyList(), finalPageable, 0L)));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EntityVO> query(EntityQuery params, @Nullable Sort sort, String... projection) {
        log.info("全量查询实体信息[{}]", params);
        if (params.getCreatedTime() == null) params.setCreatedTime(Range.LocalDateTime.DEFAULT);
        if (params.getModifiedTime() == null) params.setModifiedTime(Range.LocalDateTime.DEFAULT);
        if (sort == null) sort = Sort.by("createdTime").descending();
        Criteria where = buildCriteria(params);
        Query query = Query.query(where).sort(sort).limit(100);
        return entityOperations.select(query, Entity.class)
                .map(item -> BeanUtils.map(item, EntityVO.class))
                .doOnNext(item -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(item, params)))
                ;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EntityVO> get(EntityGet params, String... projection) {
        log.info("获取实体信息[{}]", params);
        if (Stream.of(params.getId(), params.getCode()).allMatch(Objects::isNull)) {
            return Mono.error(new ResultException(ResultType.failure.name(), "id 、code 不能同时为空"));
        }
        Criteria where = CriteriaUtils.and(
                CriteriaUtils.nullableCriteria(Criteria.where("id")::is, params::getId),
                CriteriaUtils.nullableCriteria(Criteria.where("code")::is, params::getCode)
        );
        return entityOperations.selectOne(Query.query(where), Entity.class)
                .map(item -> BeanUtils.map(item, EntityVO.class))
                .doOnNext(item -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(item, params)))
                ;
    }

    @Override
    @Transactional
    public Mono<Integer> modify(EntityModify params) {
        log.info("修改实体信息[{}]", params);
        return this.modifyGeneric(params);
    }

    private <T extends IdCapable<Long> & OperatorCapable<Long>> Mono<Integer> modifyGeneric(T params) {
        Criteria where = Criteria.where("id").is(params.getId());
        Query idQuery = Query.query(where);
        return entityOperations.selectOne(idQuery, Entity.class)
                .zipWhen(entity -> {
                    Entity modify = BeanUtils.map(params, Entity.class);
                    modify.setModifierId(params.getOperatorId());
                    modify.setModifiedTime(LocalDateTime.now());
                    Update update = UpdateUtils.selectiveUpdateFromExample(modify);
                    return entityOperations.update(idQuery, update, Entity.class);
                })
                .map(tuple2 -> {
                    EntityVO vo = BeanUtils.map(tuple2.getT1(), EntityVO.class);
                    BeanUtils.copyProperties(params, vo, BeanUtils.EMPTY_PROPERTY_VALUE);
                    eventPublisher.publishEvent(new PayloadApplicationEvent<>(vo, params));
                    return tuple2.getT2();
                })
                .switchIfEmpty(Mono.just(0));
    }

    @Override
    @Transactional
    public Mono<Integer> delete(EntityDelete params) {
        log.info("删除实体信息[{}]", params);
        Criteria where = Criteria.where("id").is(params.getId());
        Query idQuery = Query.query(where);
        return entityOperations.selectOne(idQuery, Entity.class)
                .map(item -> BeanUtils.map(item, EntityVO.class))
                .zipWhen(region -> entityOperations.delete(idQuery, Entity.class))
                .doOnNext(tuple2 -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(tuple2.getT1(), params)))
                .map(Tuple2::getT2)
                .switchIfEmpty(Mono.just(0));
    }

}
