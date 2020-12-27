package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.ServiceMetadataProperties;
import com.github.peacetrue.core.IdCapable;
import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : xiayx
 * @since : 2020-12-25 20:01
 **/
@Slf4j
public class AddEntityFromClassApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private EntityClassService entityClassService;
    @Autowired
    private ServiceMetadataProperties properties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Set<String> classNames = properties.getClasses().keySet();
        log.info("应用启动完成后，从实体类添加实体信息[{}]", classNames);
        Set<Class<?>> entityClasses = classNames.stream()
                .map(Unchecked.function(Class::forName))
                .collect(Collectors.toSet());

        if (properties.getBasePackages() != null) {
            Set<Class<?>> scannedEntityClasses = findEntityClasses(properties.getBasePackages());
            log.debug("在[{}]包下扫描到实体类：{}", Arrays.toString(properties.getBasePackages()), scannedEntityClasses);
            entityClasses.addAll(scannedEntityClasses);
        }

        entityClassService.init()
                .thenMany(entityClassService.addClass(entityClasses))
                .subscribe();
    }

    public static Set<Class<?>> findEntityClasses(String... basePackages) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
        return Stream
                .of(basePackages)
                .map(AddEntityFromClassApplicationListener::toClassLocationPattern)
                .flatMap(Unchecked.function(locationPattern -> Stream.of(resolver.getResources(locationPattern))))
                .map(Unchecked.function(resource -> metadataReaderFactory.getMetadataReader(resource).getClassMetadata()))
                .filter(AddEntityFromClassApplicationListener::isEntityClass)
                .map(ClassMetadata::getClassName)
                .map(Unchecked.function(Class::forName))
                .collect(Collectors.toSet())
                ;
    }

    public static String toClassLocationPattern(String basePackage) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(basePackage)
                + "/**/*.class"
                ;
    }

    public static boolean isEntityClass(ClassMetadata classMetadata) {
        if (!Arrays.asList(classMetadata.getInterfaceNames()).contains(IdCapable.class.getName())) return false;
        if (OperatorCapableImpl.class.getName().equals(classMetadata.getSuperClassName())) return false;
        if (Enum.class.getName().equals(classMetadata.getSuperClassName())) return false;
        if (classMetadata.getClassName().endsWith("VO")) return false;
        return true;
    }

}
