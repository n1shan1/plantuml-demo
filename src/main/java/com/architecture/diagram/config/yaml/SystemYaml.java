package com.architecture.diagram.config.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * YAML DTO for complete system definition
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemYaml {
    private String name;
    private String description;
    private List<ComponentYaml> components;
    private List<RelationshipYaml> relationships;
    private List<AnnotationYaml> annotations;
    private Map<String, String> metadata;

    // For nested structure support
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemWrapper {
        private SystemYaml system;
    }
}
