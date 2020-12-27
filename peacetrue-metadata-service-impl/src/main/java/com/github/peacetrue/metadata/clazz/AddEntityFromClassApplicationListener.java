package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.ServiceMetadataProperties;
import com.github.peacetrue.metadata.modules.entity.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:01
 **/
@Slf4j
//@Component
public class AddEntityFromClassApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private EntityClassService entityClassService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private ServiceMetadataProperties properties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("应用启动完成后，从实体类添加实体信息[{}]", properties.getClasses().keySet());
        entityClassService.init()
                .thenMany(
                        Flux
                                .fromIterable(properties.getClasses().keySet())
                                .flatMap(entityClass -> Mono.fromCallable(() -> Class.forName(entityClass)))
                                //TODO 重复解析，在内部关联时已经解析过，迭代时再次解析
                                .flatMap(entityClass -> entityClassService.resolveClass(entityClass))
                                .doOnNext(entityAdd -> entityAdd.setOperatorId(1L))
                                .flatMap(entityService::add)
                )
                .subscribe();
    }

}
