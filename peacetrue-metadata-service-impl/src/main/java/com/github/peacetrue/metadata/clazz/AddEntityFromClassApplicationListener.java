package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.ServiceMetadataProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:01
 **/
@Slf4j
public class AddEntityFromClassApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private EntityClassService entityClassService;
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
                                .collectList()
                                .flatMapMany(classes -> entityClassService.addClass(new HashSet<>(classes)))
                )
                .subscribe();
    }

}
