package com.architecture.diagram.domain;

/**
 * Enum representing line styles for relationships between components.
 */
public enum LineStyle {
    SOLID("normal", "-->"),
    DASHED("dashed", "..>"),
    DOTTED("dotted", "...>"),
    BOLD("bold", "==>"),
    HIDDEN("hidden", "[hidden]");

    private final String plantUMLName;
    private final String symbol;

    LineStyle(String plantUMLName, String symbol) {
        this.plantUMLName = plantUMLName;
        this.symbol = symbol;
    }

    public String getPlantUMLName() {
        return plantUMLName;
    }

    public String getSymbol() {
        return symbol;
    }
}
