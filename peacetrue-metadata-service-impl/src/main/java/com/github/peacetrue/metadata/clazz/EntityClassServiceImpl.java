package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.ServiceMetadataProperties;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeAdd;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeGet;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeService;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeVO;
import com.github.peacetrue.dictionary.modules.dictionaryvalue.DictionaryValueAdd;
import com.github.peacetrue.dictionary.modules.dictionaryvalue.DictionaryValueGet;
import com.github.peacetrue.dictionary.modules.dictionaryvalue.DictionaryValueService;
import com.github.peacetrue.metadata.modules.entity.*;
import com.github.peacetrue.metadata.modules.property.PropertyAdd;
import com.github.peacetrue.spring.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:04
 **/
@Slf4j
@Service
public class EntityClassServiceImpl implements EntityClassService {

    @Autowired
    private R2dbcEntityOperations entityOperations;
    @Autowired
    private EntityService entityService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private DictionaryValueService dictionaryValueService;
    @Autowired
    private ServiceMetadataProperties properties;

    @Override
    public Mono<Void> init() {
        log.info("初始化实体类服务");
        return Mono.just(new DictionaryTypeGet("javaBasicType"))
                .flatMap(dictionaryTypeService::get)
                .switchIfEmpty(Mono.defer(this::addJavaBasicTypeDictionary))
                .then();
    }

    public Mono<DictionaryTypeVO> addJavaBasicTypeDictionary() {
        log.info("新增【java 基础类型】字典");
        return Mono.just(new DictionaryTypeAdd("javaBasicType", "java 基础类型", "", Arrays.asList(
                new DictionaryValueAdd(String.class.getName(), "字符串", 0, ""),
                new DictionaryValueAdd(Character.class.getName(), "字符", 0, ""),
                new DictionaryValueAdd(Byte.class.getName(), "字节", 0, ""),
                new DictionaryValueAdd(Short.class.getName(), "短整形", 0, ""),
                new DictionaryValueAdd(Integer.class.getName(), "整型", 0, ""),
                new DictionaryValueAdd(Long.class.getName(), "长整型", 0, ""),
                new DictionaryValueAdd(Float.class.getName(), "单精度浮点型", 0, ""),
                new DictionaryValueAdd(Double.class.getName(), "双精度浮点型", 0, ""),
                new DictionaryValueAdd(Boolean.class.getName(), "布尔", 0, ""),
                new DictionaryValueAdd(Date.class.getName(), "日期", 0, ""),
                new DictionaryValueAdd(LocalDate.class.getName(), "本地日期", 0, ""),
                new DictionaryValueAdd(LocalDateTime.class.getName(), "本地日期时间", 0, "")
        )))
                .doOnNext(dictionaryTypeAdd -> dictionaryTypeAdd.setOperatorId(1L))
                .flatMap(dictionaryTypeService::add);
    }

    @Override
    public Mono<EntityAdd> resolveClass(Class<?> entityClass) {
        log.info("解析实体类[{}]", entityClass.getName());
        EntityAdd entityAdd = new EntityAdd();
        entityAdd.setCode(entityClass.getName());
        entityAdd.setName(resolveEntityDesc(entityClass));
        return Flux
                .fromArray(BeanUtils.getPropertyDescriptors(entityClass))
                .flatMap((descriptor) -> resolveProperty(entityClass, descriptor))
                .collectList()
                .contextWrite(Context.of(entityClass, entityAdd))
                .doOnNext(entityAdd::setProperties)
                .thenReturn(entityAdd)
                ;
    }

    private Mono<PropertyAdd> resolveProperty(Class<?> entityClass, PropertyDescriptor descriptor) {
        log.info("解析属性[{}.{}]", entityClass.getName(), descriptor.getName());
        return Mono
                .just(new PropertyAdd())
                .doOnNext(propertyAdd -> {
                    propertyAdd.setCode(descriptor.getName());
                    propertyAdd.setName(resolvePropertyDesc(entityClass, descriptor.getName()));
                })
                .flatMap(propertyAdd ->
                        dictionaryValueService.get(new DictionaryValueGet("javaBasicType", descriptor.getPropertyType().getName()))
                                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("找不到字典[%s]", descriptor.getPropertyType().getName()))))
                                .doOnNext(dictionaryValueVO -> propertyAdd.setTypeId(dictionaryValueVO.getId()))
                                .thenReturn(propertyAdd)
                )
                .flatMap(propertyAdd ->
                        resolveReferenceMono(entityClass, descriptor.getName())
                                .doOnNext(reference -> {
                                    if (reference instanceof Long) {
                                        propertyAdd.setReferenceId((Long) reference);
                                    } else if (reference instanceof EntityAdd) {
                                        propertyAdd.setReference((EntityAdd) reference);
                                    } else {
                                        log.error("不期待的分支[{}]", reference);
                                    }
                                })
                                .thenReturn(propertyAdd)
                );
    }

    protected String resolveEntityDesc(Class<?> entityClass) {
        log.debug("解析实体类[{}]描述", entityClass.getName());
        return properties.getClassDesc(entityClass, () -> {
            EntityMetadata entity = entityClass.getAnnotation(EntityMetadata.class);
            return entity == null ? entityClass.getSimpleName() : entity.desc();
        });
    }

    protected String resolvePropertyDesc(Class<?> entityClass, String name) {
        log.debug("解析实体类属性[{}.{}]描述", entityClass.getName(), name);
        String desc = properties.getPropertyConfiguration(entityClass, name).getDesc();
        if (!StringUtils.isEmpty(desc)) return desc;

        Field field = ReflectionUtils.findField(entityClass, name);
        PropertyMetadata property = field.getAnnotation(PropertyMetadata.class);
        return property == null ? name : property.desc();
    }

    protected String resolveReference(Class<?> entityClass, String name) {
        log.debug("解析实体类属性[{}.{}]引用", entityClass.getName(), name);
        Class<?> reference = properties.getPropertyConfiguration(entityClass, name).getReference();
        if (reference != null) return reference.getName();
        Field field = ReflectionUtils.findField(entityClass, name);
        PropertyMetadata property = field.getAnnotation(PropertyMetadata.class);
        return property == null ? null : property.reference();
    }

    private Mono<Object> resolveReferenceMono(Class<?> entityClass, String name) {
        String reference = resolveReference(entityClass, name);
        if (StringUtils.isEmpty(reference)) return Mono.empty();
        return entityService.get(new EntityGet(null, reference))
                .<Object>map(EntityVO::getId)
                .switchIfEmpty(
                        Mono
                                .fromCallable(() -> Class.forName(reference))
                                .flatMap(clazz ->
                                        Mono.deferContextual(contextView ->
                                                Mono.justOrEmpty(contextView.getOrEmpty(clazz))
                                                        .switchIfEmpty(Mono.defer(() -> this.resolveClass(clazz)))
                                        )
                                )
                )
                ;
    }

    @Override
    @Transactional
    public Flux<EntityVO> addClass(Set<Class<?>> entityClasses) {
        log.info("添加实体类[{}]", entityClasses);
        return maxSerialNumber()
                .flatMapMany(serialNumber -> this.doAddClass(entityClasses, serialNumber));
    }

    private Flux<EntityVO> doAddClass(Set<Class<?>> entityClasses, Long serialNumber) {
        Set<EntityAdd> processedEntityAdds = new HashSet<>(entityClasses.size());
        Long[] serialNumbers = {serialNumber + 1};
        return Flux
                .fromIterable(entityClasses)
                //挑选出未入库的
                .filterWhen(entityClass -> exists(entityClass).map(aBoolean -> !aBoolean))
                //挑选出未解析过的
                .filter(entityClass -> processedEntityAdds.stream().map(EntityAdd::getCode).noneMatch(s -> s.equals(entityClass.getName())))
                .flatMap(entityClass ->
                        this.resolveClass(entityClass)
                                .doOnNext(entityAdd -> {
                                    Set<EntityAdd> selfAndReferences = entityAdd.getSelfAndReferences();
                                    log.debug("取得自身和引用: {}", selfAndReferences);
                                    selfAndReferences.forEach(item -> {
                                        item.setSerialNumber(serialNumbers[0]++);
                                        item.setOperatorId(1L);
                                    });
                                    processedEntityAdds.addAll(selfAndReferences);
                                })
                )
                .flatMap(entityService::add)
                ;
    }

    public Mono<Long> maxSerialNumber() {
        return max(entityOperations.getDatabaseClient(), "entity", "serial_number", 0L);
    }

    public static Mono<Long> max(DatabaseClient databaseClient, String tableName, String columnName) {
        return databaseClient
                .execute(String.format("select max(%s) from %s", columnName, tableName))
                .map((row) -> Optional.ofNullable(row.get(0, Long.class)))
                .first()
                .flatMap(Mono::justOrEmpty)
                ;
    }

    public static Mono<Long> max(DatabaseClient databaseClient, String tableName, String columnName, Long defaultValue) {
        return max(databaseClient, tableName, columnName)
                .doOnNext(value -> log.info("取得 {} 表最大的 {} = {}", tableName, columnName, value))
                .switchIfEmpty(Mono.just(defaultValue).doOnNext((value) -> log.info(" {} 表不存在最大的 {} 默认为 {}", tableName, columnName, value)))
                ;
    }

    private Mono<Boolean> exists(Class<?> entityClass) {
        return entityOperations
                .exists(Query.query(Criteria.where("code").is(entityClass.getName())), Entity.class)
                ;
    }
}
