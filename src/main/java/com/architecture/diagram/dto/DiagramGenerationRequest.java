package com.architecture.diagram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for diagram generation endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagramGenerationRequest {
    private String yamlContent;
    private String format; // png or svg
    private boolean returnBase64;
    private boolean returnPlantUML;
}
