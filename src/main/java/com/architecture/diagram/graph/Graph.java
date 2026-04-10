package com.architecture.diagram.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Represents the complete graph model with nodes and edges.
 * This is the intermediate representation between SystemModel and PlantUML.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Graph {
    private String id;
    private String title;
    private Map<String, Node> nodes; // nodeId -> Node
    private List<Edge> edges;
    private String direction; // "left to right", "top to bottom", etc.
    private LayoutStrategy layoutStrategy; // Strategy for node positioning

    public Graph(String id, String title) {
        this.id = id;
        this.title = title;
        this.nodes = new LinkedHashMap<>();
        this.edges = new ArrayList<>();
        this.direction = "left to right";
        this.layoutStrategy = LayoutStrategy.HIERARCHICAL;
    }

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public List<Edge> getOutgoing(String nodeId) {
        return edges.stream()
                .filter(e -> e.getFromNodeId().equals(nodeId))
                .toList();
    }

    public List<Edge> getIncoming(String nodeId) {
        return edges.stream()
                .filter(e -> e.getToNodeId().equals(nodeId))
                .toList();
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Layout strategy enum for diagram generation
     */
    public enum LayoutStrategy {
        HIERARCHICAL("Hierarchical - layered approach"),
        CIRCULAR("Circular - arranged in circle"),
        TREE("Tree - hierarchical tree layout"),
        GRID("Grid - grid-based layout"),
        FORCE_DIRECTED("Force-directed - physics-based");

        private final String description;

        LayoutStrategy(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
