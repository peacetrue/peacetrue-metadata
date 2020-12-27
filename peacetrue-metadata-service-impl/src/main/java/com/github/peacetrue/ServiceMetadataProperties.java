package com.github.peacetrue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author xiayx
 */
@Data
@ConfigurationProperties(prefix = "peacetrue.metadata")
public class ServiceMetadataProperties {

    private boolean enableClass = false;
    private Map<String, ClassConfiguration> classes = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassConfiguration {
        public static final ClassConfiguration DEFAULT = new ClassConfiguration(null, Collections.emptyMap());
        private String desc;
        private Map<String, PropertyConfiguration> properties;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyConfiguration {
        public static final PropertyConfiguration DEFAULT = new PropertyConfiguration();
        private String desc;
        private Class<?> reference;
    }

    public ClassConfiguration getClassConfiguration(Class<?> clazz) {
        ClassConfiguration configuration = classes.get(clazz.getName());
        return configuration == null ? ClassConfiguration.DEFAULT : configuration;
    }

    public String getClassDesc(Class<?> clazz, Supplier<String> defaultValue) {
        return Objects.toString(getClassConfiguration(clazz).getDesc(), defaultValue.get());
    }

    public PropertyConfiguration getPropertyConfiguration(Class<?> clazz, String propertyName) {
        return getClassConfiguration(clazz).getProperties().getOrDefault(propertyName, PropertyConfiguration.DEFAULT);
    }
}
