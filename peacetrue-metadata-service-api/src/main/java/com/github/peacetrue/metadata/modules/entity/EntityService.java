package com.github.peacetrue.metadata.modules.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

/**
 * 实体服务接口
 *
 * @author xiayx
 */
public interface EntityService {

    /** 新增 */
    Mono<EntityVO> add(EntityAdd params);

    /** 分页查询 */
    Mono<Page<EntityVO>> query(EntityQuery params, @Nullable Pageable pageable, String... projection);

    /** 全量查询 */
    Flux<EntityVO> query(EntityQuery params, @Nullable Sort sort, String... projection);

    /** 全量查询 */
    default Flux<EntityVO> query(EntityQuery params, String... projection) {
        return this.query(params, (Sort) null, projection);
    }

    /** 获取 */
    Mono<EntityVO> get(EntityGet params, String... projection);

    /** 修改 */
    Mono<Integer> modify(EntityModify params);

    /** 删除 */
    Mono<Integer> delete(EntityDelete params);

}
