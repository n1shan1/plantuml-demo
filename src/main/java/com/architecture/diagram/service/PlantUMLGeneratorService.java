package com.architecture.diagram.service;

import com.architecture.diagram.domain.Annotation;
import com.architecture.diagram.domain.SystemModel;
import com.architecture.diagram.graph.Edge;
import com.architecture.diagram.graph.Graph;
import com.architecture.diagram.graph.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating PlantUML DSL from Graph model.
 * Produces PlantUML syntax optimized for Graphviz layout engine.
 */
@Service
@Slf4j
public class PlantUMLGeneratorService {

    /**
     * Generate PlantUML DSL from graph model
     */
    public String generatePlantUML(Graph graph, SystemModel systemModel) {
        log.info("Generating PlantUML from graph: {}", graph.getTitle());

        StringBuilder uml = new StringBuilder();

        // Header
        uml.append("@startuml\n");
        uml.append("title ").append(graph.getTitle()).append("\n");
        uml.append("\n");

        // Direction and skinparam
        uml.append("!define DIRECTION ").append(graph.getDirection()).append("\n");
        uml.append("left to right direction\n");
        uml.append("skinparam linetype ortho\n");
        uml.append("skinparam padding 10\n");
        uml.append("skinparam arrowColor #333333\n");
        uml.append("skinparam backgroundColor #FFFFFF\n");
        uml.append("\n");

        // Generate legend
        generateLegend(uml);

        // Group components into layers
        generateComponentGroups(uml, graph, systemModel);

        // Generate edges/relationships
        generateEdges(uml, graph, systemModel);

        // Generate annotations
        generateAnnotations(uml, systemModel);

        // Footer
        uml.append("\n@enduml\n");

        String result = uml.toString();
        log.debug("PlantUML generated: {} characters", result.length());
        return result;
    }

    /**
     * Generate legend
     */
    private void generateLegend(StringBuilder uml) {
        uml.append("legend right\n");
        uml.append("  <size:11><b>Legend</b>\n");
        uml.append("  <color:E8F4F8>External Actor</color>\n");
        uml.append("  <color:D4E6F1>Application Layer</color>\n");
        uml.append("  <color:F8D7DA>Security/Authorization</color>\n");
        uml.append("  <color:D1ECF1>Business Logic</color>\n");
        uml.append("  <color:E7D4F5>Data Access</color>\n");
        uml.append("  <color:FDFFB6>Database</color>\n");
        uml.append("endlegend\n");
        uml.append("\n");
    }

    /**
     * Generate component groups/packages
     */
    private void generateComponentGroups(StringBuilder uml, Graph graph, SystemModel systemModel) {
        // Identify layers of components
        Map<Integer, List<Node>> nodesByLayer = graph.getNodes().values().stream()
                .collect(Collectors.groupingBy(Node::getLayer));

        // Generate main system boundary
        uml.append("package \"").append(graph.getTitle()).append("\" {\n");

        // Group nodes by layer
        TreeMap<Integer, List<Node>> sortedLayers = new TreeMap<>(nodesByLayer);

        boolean isFirstLayer = true;
        for (var layerEntry : sortedLayers.entrySet()) {
            int layer = layerEntry.getKey();
            List<Node> layerNodes = layerEntry.getValue();

            // Group internal layers
            boolean isInternalLayer = layerNodes.stream()
                    .anyMatch(n -> !n.getType().equals("EXTERNAL"));

            if (isInternalLayer && !isFirstLayer) {
                // Generate sub-package for internal layers
                generateLayerPackage(uml, layerNodes, layer);
            } else {
                // Generate individual components
                for (Node node : layerNodes) {
                    generateNode(uml, node);
                }
            }

            isFirstLayer = false;
        }

        uml.append("}\n\n");
    }

    /**
     * Generate package/group for a layer
     */
    private void generateLayerPackage(StringBuilder uml, List<Node> nodes, int layer) {
        if (nodes.isEmpty()) {
            return;
        }

        // Get layer name from first node's type
        String layerName = nodes.get(0).getType();

        uml.append("  package \"").append(layerName).append(" Layer\" {\n");
        for (Node node : nodes) {
            generateNode(uml, node);
        }
        uml.append("  }\n");
    }

    /**
     * Generate PlantUML node definition
     */
    private void generateNode(StringBuilder uml, Node node) {
        // Determine shape and syntax
        String plantUMLDef = switch (node.getShape()) {
            case "cylinder" -> generateDatabaseNode(node);
            case "rectangle" -> generateComponentNode(node);
            default -> generateComponentNode(node);
        };

        uml.append("  ").append(plantUMLDef).append("\n");
    }

    /**
     * Generate regular component node
     */
    private String generateComponentNode(Node node) {
        String colorStr = node.getColor() != null ? "#" + node.getColor().replaceFirst("^#", "") : "#D4E6F1";

        return String.format("[%s] <<Business>> #%s",
                node.getLabel(),
                colorStr.replaceFirst("^#", ""));
    }

    /**
     * Generate database node (cylinder shape)
     */
    private String generateDatabaseNode(Node node) {
        String colorStr = node.getColor() != null ? "#" + node.getColor().replaceFirst("^#", "") : "#FDFFB6";

        return String.format("((%s)) #%s",
                node.getLabel(),
                colorStr.replaceFirst("^#", ""));
    }

    /**
     * Generate edges/relationships
     */
    private void generateEdges(StringBuilder uml, Graph graph, SystemModel systemModel) {
        Set<String> processedEdges = new HashSet<>();

        for (Edge edge : graph.getEdges()) {
            // Avoid duplicate edge definitions
            String edgeKey = edge.getFromNodeId() + "->" + edge.getToNodeId();
            if (processedEdges.contains(edgeKey)) {
                continue;
            }
            processedEdges.add(edgeKey);

            generateEdge(uml, edge);
        }
    }

    /**
     * Generate single edge definition
     */
    private void generateEdge(StringBuilder uml, Edge edge) {
        String fromId = formatNodeId(edge.getFromNodeId());
        String toId = formatNodeId(edge.getToNodeId());
        String arrow = edge.isBypass() ? "..>" : "-->";

        StringBuilder edgeDef = new StringBuilder();
        edgeDef.append(fromId).append(" ").append(arrow).append(" ").append(toId);

        if (edge.getLabel() != null && !edge.getLabel().isEmpty()) {
            edgeDef.append(" : ").append(edge.getLabel());
        }

        uml.append("  ").append(edgeDef).append("\n");
    }

    /**
     * Format node ID for PlantUML
     */
    private String formatNodeId(String nodeId) {
        // Use bracket notation for node references
        return "[" + nodeId + "]";
    }

    /**
     * Generate annotations
     */
    private void generateAnnotations(StringBuilder uml, SystemModel systemModel) {
        if (systemModel.getAnnotations() == null || systemModel.getAnnotations().isEmpty()) {
            return;
        }

        uml.append("\n");
        uml.append("note right\n");
        uml.append("  **Annotations:**\n");

        for (Annotation annotation : systemModel.getAnnotations()) {
            uml.append("  - ")
                    .append(annotation.getId())
                    .append(": ")
                    .append(annotation.getText())
                    .append("\n");
        }

        uml.append("end note\n");
    }
}
