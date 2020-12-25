package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeAdd;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeGet;
import com.github.peacetrue.dictionary.modules.dictionarytype.DictionaryTypeService;
import com.github.peacetrue.dictionary.modules.dictionaryvalue.DictionaryValueAdd;
import com.github.peacetrue.metadata.modules.entity.Entity;
import com.github.peacetrue.metadata.modules.entity.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:01
 **/
@Slf4j
@Component
public class AddEntityFromClassApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private EntityClassService entityClassService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("应用启动完成后，从实体类添加实体信息");
        Mono.just(new DictionaryTypeGet("javaBasicType"))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(dictionaryTypeService::get)
                .switchIfEmpty(
                        Mono.defer(() ->
                                Mono.just(new DictionaryTypeAdd("javaBasicType", "java 基础类型", "", Arrays.asList(
                                        new DictionaryValueAdd(String.class.getName(), "字符串", 0, ""),
                                        new DictionaryValueAdd(Character.class.getName(), "字符", 0, ""),
                                        new DictionaryValueAdd(Byte.class.getName(), "字节", 0, ""),
                                        new DictionaryValueAdd(Short.class.getName(), "短整形", 0, ""),
                                        new DictionaryValueAdd(Integer.class.getName(), "整型", 0, ""),
                                        new DictionaryValueAdd(Long.class.getName(), "长整型", 0, ""),
                                        new DictionaryValueAdd(Float.class.getName(), "单精度浮点型", 0, ""),
                                        new DictionaryValueAdd(Double.class.getName(), "双精度浮点型", 0, ""),
                                        new DictionaryValueAdd(Boolean.class.getName(), "布尔", 0, ""),
                                        new DictionaryValueAdd(Date.class.getName(), "日期", 0, ""),
                                        new DictionaryValueAdd(LocalDate.class.getName(), "本地日期", 0, ""),
                                        new DictionaryValueAdd(LocalDateTime.class.getName(), "本地日期时间", 0, "")
                                )))
                                        .doOnNext(dictionaryTypeAdd -> dictionaryTypeAdd.setOperatorId(1L))
                                        .flatMap(dictionaryTypeService::add)
                        )
                )
                .flatMap(dictionaryTypeVO ->
                        entityClassService
                                .resolveClass(Entity.class)
                                .doOnNext(entityAdd -> entityAdd.setOperatorId(1L))
                                .flatMap(entityService::add)
                )
                .subscribe();
    }
}
