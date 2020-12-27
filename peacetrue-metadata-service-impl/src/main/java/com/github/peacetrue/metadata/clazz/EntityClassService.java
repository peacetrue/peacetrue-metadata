package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.metadata.modules.entity.EntityAdd;
import com.github.peacetrue.metadata.modules.entity.EntityVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:03
 **/
public interface EntityClassService {

    /** 初始化。目前做的具体事项包括：设置【java 基础类型】字典 */
    Mono<Void> init();

    Mono<EntityAdd> resolveClass(Class<?> entityClass);

    Flux<EntityVO> addClass(Set<Class<?>> entityClasses);

}
