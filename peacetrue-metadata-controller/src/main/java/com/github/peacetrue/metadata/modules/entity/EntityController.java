package com.github.peacetrue.metadata.modules.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 实体控制器
 *
 * @author xiayx
 */
@Slf4j
@RestController
@RequestMapping(value = "/entitys")
public class EntityController {

    @Autowired
    private EntityService entityService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<EntityVO> addByForm(EntityAdd params) {
        log.info("新增实体信息(请求方法+表单参数)[{}]", params);
        return entityService.add(params);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<EntityVO> addByJson(@RequestBody EntityAdd params) {
        log.info("新增实体信息(请求方法+JSON参数)[{}]", params);
        return entityService.add(params);
    }

    @GetMapping(params = "page")
    public Mono<Page<EntityVO>> query(EntityQuery params, Pageable pageable, String... projection) {
        log.info("分页查询实体信息(请求方法+参数变量)[{}]", params);
        return entityService.query(params, pageable, projection);
    }

    @GetMapping
    public Flux<EntityVO> query(EntityQuery params, Sort sort, String... projection) {
        log.info("全量查询实体信息(请求方法+参数变量)[{}]", params);
        return entityService.query(params, sort, projection);
    }

    @GetMapping("/{id}")
    public Mono<EntityVO> getByUrlPathVariable(@PathVariable Long id, String... projection) {
        log.info("获取实体信息(请求方法+路径变量)详情[{}]", id);
        return entityService.get(new EntityGet(id), projection);
    }

    @RequestMapping("/get")
    public Mono<EntityVO> getByPath(EntityGet params, String... projection) {
        log.info("获取实体信息(请求路径+参数变量)详情[{}]", params);
        return entityService.get(params, projection);
    }

    @PutMapping(value = {"", "/*"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<Integer> modifyByForm(EntityModify params) {
        log.info("修改实体信息(请求方法+表单参数)[{}]", params);
        return entityService.modify(params);
    }

    @PutMapping(value = {"", "/*"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Integer> modifyByJson(@RequestBody EntityModify params) {
        log.info("修改实体信息(请求方法+JSON参数)[{}]", params);
        return entityService.modify(params);
    }

    @DeleteMapping("/{id}")
    public Mono<Integer> deleteByUrlPathVariable(@PathVariable Long id) {
        log.info("删除实体信息(请求方法+URL路径变量)[{}]", id);
        return entityService.delete(new EntityDelete(id));
    }

    @DeleteMapping(params = "id")
    public Mono<Integer> deleteByUrlParamVariable(EntityDelete params) {
        log.info("删除实体信息(请求方法+URL参数变量)[{}]", params);
        return entityService.delete(params);
    }

    @RequestMapping(path = "/delete")
    public Mono<Integer> deleteByPath(EntityDelete params) {
        log.info("删除实体信息(请求路径+URL参数变量)[{}]", params);
        return entityService.delete(params);
    }


}
