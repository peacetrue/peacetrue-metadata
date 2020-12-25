package com.github.peacetrue.metadata.modules.property;

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
 * 属性控制器
 *
 * @author xiayx
 */
@Slf4j
@RestController
@RequestMapping(value = "/propertys")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<PropertyVO> addByForm(PropertyAdd params) {
        log.info("新增属性信息(请求方法+表单参数)[{}]", params);
        return propertyService.add(params);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PropertyVO> addByJson(@RequestBody PropertyAdd params) {
        log.info("新增属性信息(请求方法+JSON参数)[{}]", params);
        return propertyService.add(params);
    }

    @GetMapping(params = "page")
    public Mono<Page<PropertyVO>> query(PropertyQuery params, Pageable pageable, String... projection) {
        log.info("分页查询属性信息(请求方法+参数变量)[{}]", params);
        return propertyService.query(params, pageable, projection);
    }

    @GetMapping
    public Flux<PropertyVO> query(PropertyQuery params, Sort sort, String... projection) {
        log.info("全量查询属性信息(请求方法+参数变量)[{}]", params);
        return propertyService.query(params, sort, projection);
    }

    @GetMapping("/{id}")
    public Mono<PropertyVO> getByUrlPathVariable(@PathVariable Long id, String... projection) {
        log.info("获取属性信息(请求方法+路径变量)详情[{}]", id);
        return propertyService.get(new PropertyGet(id), projection);
    }

    @RequestMapping("/get")
    public Mono<PropertyVO> getByPath(PropertyGet params, String... projection) {
        log.info("获取属性信息(请求路径+参数变量)详情[{}]", params);
        return propertyService.get(params, projection);
    }

    @PutMapping(value = {"", "/*"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<Integer> modifyByForm(PropertyModify params) {
        log.info("修改属性信息(请求方法+表单参数)[{}]", params);
        return propertyService.modify(params);
    }

    @PutMapping(value = {"", "/*"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Integer> modifyByJson(@RequestBody PropertyModify params) {
        log.info("修改属性信息(请求方法+JSON参数)[{}]", params);
        return propertyService.modify(params);
    }

    @DeleteMapping("/{id}")
    public Mono<Integer> deleteByUrlPathVariable(@PathVariable Long id) {
        log.info("删除属性信息(请求方法+URL路径变量)[{}]", id);
        return propertyService.delete(new PropertyDelete(id));
    }

    @DeleteMapping(params = "id")
    public Mono<Integer> deleteByUrlParamVariable(PropertyDelete params) {
        log.info("删除属性信息(请求方法+URL参数变量)[{}]", params);
        return propertyService.delete(params);
    }

    @RequestMapping(path = "/delete")
    public Mono<Integer> deleteByPath(PropertyDelete params) {
        log.info("删除属性信息(请求路径+URL参数变量)[{}]", params);
        return propertyService.delete(params);
    }


}
