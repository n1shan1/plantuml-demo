package com.architecture.diagram.config.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * YAML DTO for annotation definition
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationYaml {
    private String id;
    private String text;
    private String attachedTo;
    private String type;
    private String color;
}
