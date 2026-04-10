package com.architecture.diagram.domain;

/**
 * Enum representing different types of components in the system architecture.
 */
public enum ComponentType {
    EXTERNAL("External Actor"),
    LAYER("Application Layer"),
    SECURITY("Security/Authorization"),
    PROCESSING("Business Logic"),
    DATA_ACCESS("Data Access"),
    DATABASE("Database"),
    SERVICE("Service");

    private final String displayName;

    ComponentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
