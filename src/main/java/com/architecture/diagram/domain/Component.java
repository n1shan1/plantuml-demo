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
 * Represents a component in the system architecture.
 * Each component has an ID, type, label, and optional metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Component {
    private String id;
    private ComponentType type;
    private String label;
    private String description;
    private boolean multipleLayers;
    private List<String> layers;
    private Map<String, String> metadata;
    private String color;
    private String icon;

    public Component(String id, ComponentType type, String label) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.layers = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    public void addLayer(String layer) {
        if (this.layers == null) {
            this.layers = new ArrayList<>();
        }
        this.layers.add(layer);
    }

    public void setMetadata(String key, String value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
