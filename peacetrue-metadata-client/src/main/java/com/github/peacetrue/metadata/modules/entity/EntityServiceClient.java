package com.github.peacetrue.metadata.modules.entity;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

/**
 * 实体客户端
 *
 * @author xiayx
 */
@ReactiveFeignClient(name = "peacetrue-metadata", url = "${peacetrue.Entity.url:${peacetrue.server.url:}}")
public interface EntityServiceClient {

    @PostMapping(value = "/entitys")
    Mono<EntityVO> add(EntityAdd params);

    @GetMapping(value = "/entitys", params = "page")
    Mono<Page<EntityVO>> query(@Nullable @SpringQueryMap EntityQuery params, @Nullable Pageable pageable, @SpringQueryMap String... projection);

    @GetMapping(value = "/entitys", params = "sort")
    Flux<EntityVO> query(@SpringQueryMap EntityQuery params, Sort sort, @SpringQueryMap String... projection);

    @GetMapping(value = "/entitys")
    Flux<EntityVO> query(@SpringQueryMap EntityQuery params, @SpringQueryMap String... projection);

    @GetMapping(value = "/entitys/get")
    Mono<EntityVO> get(@SpringQueryMap EntityGet params, @SpringQueryMap String... projection);

    @PutMapping(value = "/entitys")
    Mono<Integer> modify(EntityModify params);

    @DeleteMapping(value = "/entitys/delete")
    Mono<Integer> delete(@SpringQueryMap EntityDelete params);

}
