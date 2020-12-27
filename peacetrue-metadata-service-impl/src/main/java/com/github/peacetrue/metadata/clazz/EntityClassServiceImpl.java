package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.ServiceMetadataProperties;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeAdd;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeGet;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeService;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeVO;
import com.github.peacetrue.dictionary.modules.dictionaryvalue.DictionaryValueAdd;
import com.github.peacetrue.dictionary.modules.dictionaryvalue.DictionaryValueGet;
import com.github.peacetrue.dictionary.modules.dictionaryvalue.DictionaryValueService;
import com.github.peacetrue.metadata.modules.entity.EntityAdd;
import com.github.peacetrue.metadata.modules.entity.EntityGet;
import com.github.peacetrue.metadata.modules.entity.EntityService;
import com.github.peacetrue.metadata.modules.entity.EntityVO;
import com.github.peacetrue.metadata.modules.property.PropertyAdd;
import com.github.peacetrue.spring.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:04
 **/
@Slf4j
@Service
public class EntityClassServiceImpl implements EntityClassService {

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
        return properties.getClassDesc(entityClass, () -> {
            EntityMetadata entity = entityClass.getAnnotation(EntityMetadata.class);
            return entity == null ? entityClass.getSimpleName() : entity.desc();
        });
    }

    protected String resolvePropertyDesc(Class<?> entityClass, String name) {
        String desc = properties.getPropertyConfiguration(entityClass, name).getDesc();
        if (!StringUtils.isEmpty(desc)) return desc;

        Field field = ReflectionUtils.findField(entityClass, name);
        PropertyMetadata property = field.getAnnotation(PropertyMetadata.class);
        return property == null ? name : property.desc();
    }

    protected String resolveReference(Class<?> entityClass, String name) {
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
}
