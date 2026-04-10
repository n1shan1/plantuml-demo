package com.architecture.diagram.service;

import com.architecture.diagram.domain.*;
import com.architecture.diagram.graph.Edge;
import com.architecture.diagram.graph.Graph;
import com.architecture.diagram.graph.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for building graph model from SystemModel.
 * Converts domain model to graph representation with proper layout hints.
 */
@Service
@Slf4j
public class GraphBuilderService {

    /**
     * Build graph model from SystemModel
     */
    public Graph buildGraph(SystemModel systemModel) {
        log.info("Building graph from SystemModel: {}", systemModel.getName());

        Graph graph = new Graph(systemModel.getName().toLowerCase().replace(" ", "-"), systemModel.getName());

        // Add nodes for each component
        for (Component component : systemModel.getComponents()) {
            Node node = convertComponentToNode(component);
            graph.addNode(node);
        }

        // Add edges for each relationship
        for (Relationship relationship : systemModel.getRelationships()) {
            Edge edge = convertRelationshipToEdge(relationship);
            graph.addEdge(edge);
        }

        // Apply layout strategy to assign layers and positions
        applyHierarchicalLayout(graph, systemModel);

        log.info("Graph built with {} nodes and {} edges", graph.getNodeCount(), graph.getEdgeCount());

        return graph;
    }

    /**
     * Convert Component to Node
     */
    private Node convertComponentToNode(Component component) {
        String shape = getShapeForComponentType(component.getType());
        String color = component.getColor() != null ? component.getColor() : getDefaultColor(component.getType());

        Node node = Node.builder()
                .id(component.getId())
                .label(component.getLabel())
                .type(component.getType().name())
                .shape(shape)
                .color(color)
                .layer(0)
                .build();

        // Add child nodes if multiple layers
        if (component.isMultipleLayers() && component.getLayers() != null) {
            for (String layer : component.getLayers()) {
                node.addChild(layer);
            }
        }

        return node;
    }

    /**
     * Convert Relationship to Edge
     */
    private Edge convertRelationshipToEdge(Relationship relationship) {
        String style = relationship.getStyle().name().toLowerCase();
        if (relationship.getStyle() == LineStyle.DASHED) {
            style = "dashed";
        }

        Edge edge = Edge.builder()
                .fromNodeId(relationship.getFrom())
                .toNodeId(relationship.getTo())
                .label(relationship.getLabel())
                .style(style)
                .isBypass(relationship.isBypass())
                .annotationId(relationship.getAnnotationId())
                .build();

        // Use dashed style for bypass flows
        if (relationship.isBypass()) {
            edge.setStyle("dashed");
        }

        return edge;
    }

    /**
     * Determine shape based on component type
     */
    private String getShapeForComponentType(ComponentType type) {
        return switch (type) {
            case EXTERNAL -> "rectangle";
            case LAYER -> "rectangle";
            case SECURITY -> "rectangle";
            case PROCESSING -> "rectangle";
            case DATA_ACCESS -> "rectangle";
            case DATABASE -> "cylinder";
            case SERVICE -> "rectangle";
        };
    }

    /**
     * Get default color for component type
     */
    private String getDefaultColor(ComponentType type) {
        return switch (type) {
            case EXTERNAL -> "#E8F4F8"; // Light blue for external
            case LAYER -> "#D4E6F1"; // Medium blue for layers
            case SECURITY -> "#F8D7DA"; // Light red for security
            case PROCESSING -> "#D1ECF1"; // Light teal for processing
            case DATA_ACCESS -> "#E7D4F5"; // Light purple for data access
            case DATABASE -> "#FDFFB6"; // Light yellow for database
            case SERVICE -> "#D5F4E6"; // Light green for service
        };
    }

    /**
     * Apply hierarchical layout to assign layers and positions
     */
    private void applyHierarchicalLayout(Graph graph, SystemModel systemModel) {
        log.debug("Applying hierarchical layout");

        // Use topological sort to assign layers
        Map<String, Integer> layerMap = assignLayers(graph);

        // Assign positions based on layer
        Map<Integer, List<String>> nodesByLayer = new TreeMap<>();
        for (var entry : layerMap.entrySet()) {
            nodesByLayer.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }

        // Position nodes in grid
        int rowCounter = 0;
        for (var layer : nodesByLayer.entrySet()) {
            int colCounter = 0;
            for (String nodeId : layer.getValue()) {
                Node node = graph.getNode(nodeId);
                node.setLayer(layer.getKey());
                node.setRow(rowCounter);
                node.setColumn(colCounter);
                colCounter++;
            }
            rowCounter++;
        }

        log.debug("Layout applied: {} layers", nodesByLayer.size());
    }

    /**
     * Assign layer numbers using topological sort
     */
    private Map<String, Integer> assignLayers(Graph graph) {
        Map<String, Integer> layers = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // Initialize in-degrees
        for (String nodeId : graph.getNodes().keySet()) {
            inDegree.put(nodeId, 0);
        }

        for (Edge edge : graph.getEdges()) {
            inDegree.put(edge.getToNodeId(), inDegree.getOrDefault(edge.getToNodeId(), 0) + 1);
        }

        // Queue for topological sort
        Queue<String> queue = new LinkedList<>();
        for (var entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
                layers.put(entry.getKey(), 0);
            }
        }

        // Process queue and assign layers
        while (!queue.isEmpty()) {
            String nodeId = queue.poll();
            int currentLayer = layers.get(nodeId);

            for (Edge edge : graph.getOutgoing(nodeId)) {
                String nextNodeId = edge.getToNodeId();
                int newLayer = currentLayer + 1;
                layers.put(nextNodeId, Math.max(layers.getOrDefault(nextNodeId, 0), newLayer));

                inDegree.put(nextNodeId, inDegree.get(nextNodeId) - 1);
                if (inDegree.get(nextNodeId) == 0) {
                    queue.offer(nextNodeId);
                }
            }
        }

        // For nodes not reached, assign layer 0
        for (String nodeId : graph.getNodes().keySet()) {
            layers.putIfAbsent(nodeId, 0);
        }

        return layers;
    }
}
