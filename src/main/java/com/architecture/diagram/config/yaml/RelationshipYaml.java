package com.architecture.diagram.config.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * YAML DTO for relationship definition
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipYaml {
    private String from;
    private String to;
    private String label;
    private String style;
    private boolean bypass;
    private String annotationId;
}
