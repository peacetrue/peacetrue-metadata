package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.TestServiceMetadataAutoConfiguration;
import com.github.peacetrue.metadata.modules.entity.EntityServiceImpl;
import com.github.peacetrue.metadata.modules.property.PropertyAdd;
import com.github.peacetrue.spring.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.util.Map;

/**
 * @author : xiayx
 * @since : 2020-12-26 10:00
 **/
@Slf4j
@SpringBootTest(classes = TestServiceMetadataAutoConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EntityClassServiceImplTest {

    @Autowired
    private EntityClassServiceImpl entityClassService;
    @Autowired
    private EntityServiceImpl entityService;

    @Test
    void addJavaBasicTypeDictionary() {
        entityClassService.addJavaBasicTypeDictionary()
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Assertions.assertEquals(data.getCode(), "javaBasicType");
                    Assertions.assertTrue(data.getDictionaryValues().size() > 0);
                })
                .verifyComplete();
    }

    @Test
    void resolveClassSelfReference() {
        log.info("测试自引用：自己引用自己");
        entityClassService
                .addJavaBasicTypeDictionary()
                .then(entityClassService.resolveClass(UserSelfReference.class))
                .as(StepVerifier::create)
                .assertNext(entityAdd -> {
                    Assertions.assertEquals(UserSelfReference.class.getName(), entityAdd.getCode());
                    Assertions.assertTrue(entityAdd.getProperties().stream().anyMatch(propertyAdd -> propertyAdd.getReference() != null));
//                    Map<String, PropertyAdd> map = BeanUtils.map(entityAdd.getProperties(), "code");
//                    Assertions.assertEquals("用户名", map.get("username").getName());
                })
                .verifyComplete();
    }

    @Test
    void resolveClassSingleReference() {
        Hooks.onOperatorDebug();
        log.info("测试单引用：自己引用别人，别人不引用自己");
        entityClassService
                .init()
                .then(entityClassService.resolveClass(EntitySingleReference.class))
                .as(StepVerifier::create)
                .assertNext(entityAdd -> {
                    log.info("entityAdd: {}", entityAdd);
                    Assertions.assertEquals(EntitySingleReference.class.getName(), entityAdd.getCode());
                    Assertions.assertTrue(entityAdd.getProperties().stream().anyMatch(propertyAdd -> propertyAdd.getReference() != null));
                })
                .verifyComplete();
    }

    @Test
    void resolveClassCrossReference() {
        Hooks.onOperatorDebug();
        log.info("测试互引用：自己引用别人，别人引用自己");
        entityClassService
                .init()
                .then(entityClassService.resolveClass(EntityCrossReference.class))
                .as(StepVerifier::create)
                .assertNext(entityAdd -> {
                    log.info("entityAdd: {}", entityAdd);
                    Assertions.assertEquals(EntityCrossReference.class.getName(), entityAdd.getCode());
                    Assertions.assertTrue(entityAdd.getProperties().stream().anyMatch(propertyAdd -> propertyAdd.getReference() != null));
                })
                .verifyComplete();
    }

    @Test
    void add() {
        Hooks.onOperatorDebug();
        entityClassService
                .init()
                .then(entityClassService.resolveClass(EntityCrossReference.class))
                .doOnNext(entityAdd -> entityAdd.setOperatorId(1L))
                .flatMap(entityAdd -> entityService.add(entityAdd))
                .subscribe()
//                .as(StepVerifier::create)
//                .assertNext(vo -> {
//                    log.info("entityVO: {}", vo);
//                    Assertions.assertEquals(EntityCrossReference.class.getName(), vo.getCode());
//                })
//                .verifyComplete()
        ;
    }
}
