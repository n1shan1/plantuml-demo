package com.architecture.diagram.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an edge/connection between two nodes in the graph.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Edge {
    private String fromNodeId;
    private String toNodeId;
    private String label;
    private String style; // solid, dashed, dotted, bold
    private String color;
    private boolean isBypass;
    private String annotationId;

    public Edge(String fromNodeId, String toNodeId) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.style = "solid";
        this.isBypass = false;
    }

    public Edge(String fromNodeId, String toNodeId, String label, String style) {
        this(fromNodeId, toNodeId);
        this.label = label;
        this.style = style;
    }
}
