package com.architecture.diagram.service;

import com.architecture.diagram.config.yaml.SystemYaml;
import com.architecture.diagram.config.yaml.ComponentYaml;
import com.architecture.diagram.config.yaml.RelationshipYaml;
import com.architecture.diagram.config.yaml.AnnotationYaml;
import com.architecture.diagram.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for parsing YAML files into SystemModel objects.
 * Handles deserialization and basic validation.
 */
@Service
@Slf4j
public class YamlParserService {
    private final ObjectMapper yamlMapper;

    public YamlParserService() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Parse YAML string into SystemModel
     */
    @SuppressWarnings("unchecked")
    public SystemModel parseYaml(String yamlContent) {
        try {
            log.info("Parsing YAML content");

            // Parse YAML to Map first
            Map<String, Object> yamlMap = yamlMapper.readValue(yamlContent, Map.class);

            // Extract system object if it exists
            SystemYaml systemYaml;
            if (yamlMap.containsKey("system")) {
                // YAML has system: wrapper
                Object systemObj = yamlMap.get("system");
                systemYaml = yamlMapper.convertValue(systemObj, SystemYaml.class);
            } else {
                // YAML is directly the system object
                systemYaml = yamlMapper.convertValue(yamlMap, SystemYaml.class);
            }

            return convertToSystemModel(systemYaml);
        } catch (Exception e) {
            log.error("Failed to parse YAML", e);
            throw new RuntimeException("Failed to parse YAML: " + e.getMessage(), e);
        }
    }

    /**
     * Convert SystemYaml DTO to domain SystemModel
     */
    private SystemModel convertToSystemModel(SystemYaml systemYaml) {
        SystemModel model = new SystemModel(systemYaml.getName());
        model.setDescription(systemYaml.getDescription());

        // Add components
        if (systemYaml.getComponents() != null) {
            for (ComponentYaml componentYaml : systemYaml.getComponents()) {
                Component component = convertComponent(componentYaml);
                model.addComponent(component);
            }
        }

        // Add relationships
        if (systemYaml.getRelationships() != null) {
            for (RelationshipYaml relationshipYaml : systemYaml.getRelationships()) {
                Relationship relationship = convertRelationship(relationshipYaml);
                model.addRelationship(relationship);
            }
        }

        // Add annotations
        if (systemYaml.getAnnotations() != null) {
            for (AnnotationYaml annotationYaml : systemYaml.getAnnotations()) {
                Annotation annotation = convertAnnotation(annotationYaml);
                model.addAnnotation(annotation);
            }
        }

        // Add metadata
        if (systemYaml.getMetadata() != null) {
            systemYaml.getMetadata().forEach(model::setMetadata);
        }

        log.info(
                "Successfully converted SystemYaml to SystemModel with {} components, {} relationships, and {} annotations",
                model.getComponents().size(), model.getRelationships().size(), model.getAnnotations().size());

        return model;
    }

    /**
     * Convert ComponentYaml to Component domain object
     */
    private Component convertComponent(ComponentYaml componentYaml) {
        ComponentType type = ComponentType.valueOf(componentYaml.getType().toUpperCase().replace(" ", "_"));

        Component component = Component.builder()
                .id(componentYaml.getId())
                .type(type)
                .label(componentYaml.getLabel())
                .description(componentYaml.getDescription())
                .multipleLayers(componentYaml.isMultipleLayers())
                .layers(componentYaml.getLayers())
                .metadata(componentYaml.getMetadata())
                .color(componentYaml.getColor())
                .icon(componentYaml.getIcon())
                .build();

        return component;
    }

    /**
     * Convert RelationshipYaml to Relationship domain object
     */
    private Relationship convertRelationship(RelationshipYaml relationshipYaml) {
        LineStyle style = LineStyle.SOLID;
        if (relationshipYaml.getStyle() != null) {
            try {
                style = LineStyle.valueOf(relationshipYaml.getStyle().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown line style: {}, using SOLID", relationshipYaml.getStyle());
            }
        }

        Relationship relationship = new Relationship(
                relationshipYaml.getFrom(),
                relationshipYaml.getTo(),
                relationshipYaml.getLabel(),
                style);
        relationship.setBypass(relationshipYaml.isBypass());
        relationship.setAnnotationId(relationshipYaml.getAnnotationId());

        return relationship;
    }

    /**
     * Convert AnnotationYaml to Annotation domain object
     */
    private Annotation convertAnnotation(AnnotationYaml annotationYaml) {
        return Annotation.builder()
                .id(annotationYaml.getId())
                .text(annotationYaml.getText())
                .attachedTo(annotationYaml.getAttachedTo())
                .type(annotationYaml.getType())
                .color(annotationYaml.getColor())
                .build();
    }
}
