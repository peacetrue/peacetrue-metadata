package com.github.peacetrue.metadata.modules.property;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

/**
 * 属性服务接口
 *
 * @author xiayx
 */
public interface PropertyService {

    /** 新增 */
    Mono<PropertyVO> add(PropertyAdd params);

    /** 分页查询 */
    Mono<Page<PropertyVO>> query(@Nullable PropertyQuery params, @Nullable Pageable pageable, String... projection);

    /** 全量查询 */
    Flux<PropertyVO> query(PropertyQuery params, @Nullable Sort sort, String... projection);

    /** 全量查询 */
    default Flux<PropertyVO> query(PropertyQuery params, String... projection) {
        return this.query(params, (Sort) null, projection);
    }

    /** 获取 */
    Mono<PropertyVO> get(PropertyGet params, String... projection);

    /** 修改 */
    Mono<Integer> modify(PropertyModify params);

    /** 删除 */
    Mono<Integer> delete(PropertyDelete params);

}
