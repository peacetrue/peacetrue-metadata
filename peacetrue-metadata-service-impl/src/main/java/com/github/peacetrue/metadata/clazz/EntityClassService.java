package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.metadata.modules.entity.EntityAdd;
import reactor.core.publisher.Mono;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:03
 **/
public interface EntityClassService {

    Mono<EntityAdd> resolveClass(Class<?> entityClass);

}
