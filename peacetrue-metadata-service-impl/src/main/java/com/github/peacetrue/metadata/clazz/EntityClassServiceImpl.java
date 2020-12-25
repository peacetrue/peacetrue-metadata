package com.github.peacetrue.metadata.clazz;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;

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
    private DictionaryValueService dictionaryValueService;

    @Override
    public Mono<EntityAdd> resolveClass(Class<?> entityClass) {
        log.info("解析实体类[{}]", entityClass.getName());
        EntityAdd entityAdd = new EntityAdd();
        entityAdd.setCode(entityClass.getName());
        entityAdd.setName(resolveEntityName(entityClass));
        return Flux.fromArray(BeanUtils.getPropertyDescriptors(entityClass))
                .flatMap(descriptor ->
                        Mono
                                .just(new PropertyAdd())
                                .doOnNext(propertyAdd -> {
                                    propertyAdd.setCode(descriptor.getName());
                                    propertyAdd.setName(resolvePropertyName(entityClass, descriptor.getName()));
                                })
                                .flatMap(propertyAdd ->
                                        dictionaryValueService.get(new DictionaryValueGet("javaBasicType", descriptor.getPropertyType().getName()))
                                                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("找不到字典[%s]", descriptor.getPropertyType().getName()))))
                                                .doOnNext(dictionaryValueVO -> propertyAdd.setTypeId(dictionaryValueVO.getId()))
                                                .thenReturn(propertyAdd)
                                )
                                .flatMap(propertyAdd ->
                                        resolveAssociateEntity(entityClass, descriptor.getName())
                                                .doOnNext(id -> {
                                                    if (id instanceof Long) {
                                                        propertyAdd.setAssociateEntityId((Long) id);
                                                    } else {
                                                        propertyAdd.setAssociateEntity((EntityAdd) id);
                                                    }
                                                })
                                                .thenReturn(propertyAdd)
                                                .switchIfEmpty(Mono.just(propertyAdd))
                                ))
                .collectList()
                .doOnNext(entityAdd::setProperties)
                .thenReturn(entityAdd)
                ;
    }

    private String resolveEntityName(Class<?> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        return entity == null
                ? entityClass.getSimpleName()
                : entity.name();
    }

    private String resolvePropertyName(Class<?> entityClass, String name) {
        Field field = ReflectionUtils.findField(entityClass, name);
        Property property = field.getAnnotation(Property.class);
        return property == null ? name : property.name();
    }

    private Mono<Object> resolveAssociateEntity(Class<?> entityClass, String name) {
        Field field = ReflectionUtils.findField(entityClass, name);
        Property property = field.getAnnotation(Property.class);
        if (property == null || property.associate().equals("")) return Mono.empty();
        return entityService.get(new EntityGet(null, property.associate()))
                .<Object>map(EntityVO::getId)
                .switchIfEmpty(
                        Mono
                                .fromCallable(() -> Class.forName(property.associate()))
                                .flatMap(this::resolveClass)
                )
                ;
    }
}
