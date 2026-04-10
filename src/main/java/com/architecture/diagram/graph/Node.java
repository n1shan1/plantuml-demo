package com.architecture.diagram.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the graph model.
 * Nodes are used for intermediate representation before PlantUML generation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Node {
    private String id;
    private String label;
    private String type; // component type
    private String shape; // rectangle, cylinder, circle, etc.
    private String color;
    private int layer; // hierarchical layer for layout
    private int row; // grid position row
    private int column; // grid position column
    private List<String> childIds; // for grouped/nested components

    public Node(String id, String label, String type, String shape) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.shape = shape;
        this.childIds = new ArrayList<>();
    }

    public void addChild(String childId) {
        this.childIds.add(childId);
    }

    public boolean isGroup() {
        return childIds != null && !childIds.isEmpty();
    }
}
