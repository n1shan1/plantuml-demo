package com.architecture.diagram.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the complete system architecture model.
 * Contains all components, relationships, and annotations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemModel {
    private String name;
    private String description;
    private List<Component> components;
    private List<Relationship> relationships;
    private List<Annotation> annotations;
    private Map<String, String> metadata;

    public SystemModel(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.annotations = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    public void addComponent(Component component) {
        if (this.components == null) {
            this.components = new ArrayList<>();
        }
        this.components.add(component);
    }

    public void addRelationship(Relationship relationship) {
        if (this.relationships == null) {
            this.relationships = new ArrayList<>();
        }
        this.relationships.add(relationship);
    }

    public void addAnnotation(Annotation annotation) {
        if (this.annotations == null) {
            this.annotations = new ArrayList<>();
        }
        this.annotations.add(annotation);
    }

    public Component getComponent(String id) {
        return components.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void setMetadata(String key, String value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
