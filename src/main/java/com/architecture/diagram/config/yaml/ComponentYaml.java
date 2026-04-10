package com.architecture.diagram.config.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * YAML DTO for component definition
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentYaml {
    private String id;
    private String type;
    private String label;
    private String description;
    private boolean multipleLayers;
    private List<String> layers;
    private Map<String, String> metadata;
    private String color;
    private String icon;
}
