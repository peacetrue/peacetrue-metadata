package com.github.peacetrue;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;


/**
 * @author xiayx
 */
@Configuration
@EnableConfigurationProperties(ControllerMetadataProperties.class)
@ComponentScan(basePackageClasses = ControllerMetadataAutoConfiguration.class)
@PropertySource("classpath:/application-metadata-controller.yml")
public class ControllerMetadataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ReactivePageableHandlerMethodArgumentResolver.class)
    public ReactivePageableHandlerMethodArgumentResolver reactivePageableHandlerMethodArgumentResolver() {
        return new ReactivePageableHandlerMethodArgumentResolver();
    }

    @Bean
    @ConditionalOnMissingBean(ReactivePageableHandlerMethodArgumentResolver.class)
    public ReactiveSortHandlerMethodArgumentResolver reactiveSortHandlerMethodArgumentResolver() {
        return new ReactiveSortHandlerMethodArgumentResolver();
    }
}
