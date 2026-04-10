package com.architecture.diagram.util;

import com.architecture.diagram.domain.SystemModel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility for validating system models
 */
@Slf4j
public class ValidationUtil {

    /**
     * Validate system model for consistency
     */
    public static void validateSystemModel(SystemModel model) {
        if (model == null) {
            throw new IllegalArgumentException("SystemModel cannot be null");
        }

        if (model.getName() == null || model.getName().isEmpty()) {
            throw new IllegalArgumentException("System name is required");
        }

        if (model.getComponents() == null || model.getComponents().isEmpty()) {
            throw new IllegalArgumentException("System must have at least one component");
        }

        // Validate component IDs are unique
        Set<String> componentIds = new HashSet<>();
        for (var component : model.getComponents()) {
            if (component.getId() == null || component.getId().isEmpty()) {
                throw new IllegalArgumentException("Component ID cannot be empty");
            }
            if (!componentIds.add(component.getId())) {
                throw new IllegalArgumentException("Duplicate component ID: " + component.getId());
            }
            if (component.getLabel() == null || component.getLabel().isEmpty()) {
                throw new IllegalArgumentException("Component label cannot be empty: " + component.getId());
            }
        }

        // Validate relationships reference existing components
        if (model.getRelationships() != null) {
            for (var relationship : model.getRelationships()) {
                if (!componentIds.contains(relationship.getFrom())) {
                    throw new IllegalArgumentException(
                            "Relationship references non-existent component: " + relationship.getFrom());
                }
                if (!componentIds.contains(relationship.getTo())) {
                    throw new IllegalArgumentException(
                            "Relationship references non-existent component: " + relationship.getTo());
                }
            }
        }

        log.info("SystemModel validation passed: {} components, {} relationships",
                model.getComponents().size(),
                model.getRelationships() != null ? model.getRelationships().size() : 0);
    }

    /**
     * Validate YAML content format
     */
    public static void validateYAMLFormat(String yamlContent) {
        if (yamlContent == null || yamlContent.trim().isEmpty()) {
            throw new IllegalArgumentException("YAML content cannot be empty");
        }

        if (!yamlContent.contains("system:")) {
            throw new IllegalArgumentException("YAML must contain 'system:' root element");
        }

        if (!yamlContent.contains("components:")) {
            throw new IllegalArgumentException("YAML must contain 'components:' section");
        }
    }

    /**
     * Check for cycles in relationships
     */
    public static boolean hasCycle(SystemModel model) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (var component : model.getComponents()) {
            if (!visited.contains(component.getId())) {
                if (hasCycleUtil(component.getId(), model, visited, recursionStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasCycleUtil(String nodeId, SystemModel model, Set<String> visited,
            Set<String> recursionStack) {
        visited.add(nodeId);
        recursionStack.add(nodeId);

        // Get all outgoing edges
        var outgoing = model.getRelationships().stream()
                .filter(r -> r.getFrom().equals(nodeId))
                .toList();

        for (var rel : outgoing) {
            if (!visited.contains(rel.getTo())) {
                if (hasCycleUtil(rel.getTo(), model, visited, recursionStack)) {
                    return true;
                }
            } else if (recursionStack.contains(rel.getTo())) {
                return true;
            }
        }

        recursionStack.remove(nodeId);
        return false;
    }
}
