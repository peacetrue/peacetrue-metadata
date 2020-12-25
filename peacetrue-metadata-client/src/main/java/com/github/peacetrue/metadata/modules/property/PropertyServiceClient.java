package com.github.peacetrue.metadata.modules.property;

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
 * 属性客户端
 *
 * @author xiayx
 */
@ReactiveFeignClient(name = "peacetrue-metadata", url = "${peacetrue.Property.url:${peacetrue.server.url:}}")
public interface PropertyServiceClient {

    @PostMapping(value = "/propertys")
    Mono<PropertyVO> add(PropertyAdd params);

    @GetMapping(value = "/propertys", params = "page")
    Mono<Page<PropertyVO>> query(@Nullable @SpringQueryMap PropertyQuery params, @Nullable Pageable pageable, @SpringQueryMap String... projection);

    @GetMapping(value = "/propertys", params = "sort")
    Flux<PropertyVO> query(@SpringQueryMap PropertyQuery params, Sort sort, @SpringQueryMap String... projection);

    @GetMapping(value = "/propertys")
    Flux<PropertyVO> query(@SpringQueryMap PropertyQuery params, @SpringQueryMap String... projection);

    @GetMapping(value = "/propertys/get")
    Mono<PropertyVO> get(@SpringQueryMap PropertyGet params, @SpringQueryMap String... projection);

    @PutMapping(value = "/propertys")
    Mono<Integer> modify(PropertyModify params);

    @DeleteMapping(value = "/propertys/delete")
    Mono<Integer> delete(@SpringQueryMap PropertyDelete params);

}
