package com.architecture.diagram.util;

import com.architecture.diagram.domain.ComponentType;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for styling components based on their type
 */
public class StyleUtil {

    private static final Map<ComponentType, String> COMPONENT_COLORS = new HashMap<>();
    private static final Map<ComponentType, String> COMPONENT_SHAPES = new HashMap<>();
    private static final Map<String, String> LINE_STYLE_MAPPING = new HashMap<>();

    static {
        // Initialize colors for component types
        COMPONENT_COLORS.put(ComponentType.EXTERNAL, "#E8F4F8");
        COMPONENT_COLORS.put(ComponentType.LAYER, "#D4E6F1");
        COMPONENT_COLORS.put(ComponentType.SECURITY, "#F8D7DA");
        COMPONENT_COLORS.put(ComponentType.PROCESSING, "#D1ECF1");
        COMPONENT_COLORS.put(ComponentType.DATA_ACCESS, "#E7D4F5");
        COMPONENT_COLORS.put(ComponentType.DATABASE, "#FDFFB6");
        COMPONENT_COLORS.put(ComponentType.SERVICE, "#D5F4E6");

        // Initialize shapes for component types
        COMPONENT_SHAPES.put(ComponentType.EXTERNAL, "rectangle");
        COMPONENT_SHAPES.put(ComponentType.LAYER, "rectangle");
        COMPONENT_SHAPES.put(ComponentType.SECURITY, "rectangle");
        COMPONENT_SHAPES.put(ComponentType.PROCESSING, "rectangle");
        COMPONENT_SHAPES.put(ComponentType.DATA_ACCESS, "rectangle");
        COMPONENT_SHAPES.put(ComponentType.DATABASE, "cylinder");
        COMPONENT_SHAPES.put(ComponentType.SERVICE, "rectangle");

        // Initialize line style mapping
        LINE_STYLE_MAPPING.put("SOLID", "-->");
        LINE_STYLE_MAPPING.put("DASHED", "..>");
        LINE_STYLE_MAPPING.put("DOTTED", "...>");
        LINE_STYLE_MAPPING.put("BOLD", "==>");
        LINE_STYLE_MAPPING.put("HIDDEN", "[hidden]");
    }

    /**
     * Get color for component type
     */
    public static String getColorForType(ComponentType type) {
        return COMPONENT_COLORS.getOrDefault(type, "#D4E6F1");
    }

    /**
     * Get shape for component type
     */
    public static String getShapeForType(ComponentType type) {
        return COMPONENT_SHAPES.getOrDefault(type, "rectangle");
    }

    /**
     * Get PlantUML arrow symbol for line style
     */
    public static String getArrowForStyle(String style) {
        return LINE_STYLE_MAPPING.getOrDefault(style, "-->");
    }

    /**
     * Format color for PlantUML (remove # if present)
     */
    public static String formatColorForPlantUML(String color) {
        if (color == null || color.isEmpty()) {
            return "000000";
        }
        return color.replaceFirst("^#", "");
    }

    /**
     * Generate PlantUML color directive
     */
    public static String generateColorDirective(String color) {
        return "#" + formatColorForPlantUML(color);
    }
}
