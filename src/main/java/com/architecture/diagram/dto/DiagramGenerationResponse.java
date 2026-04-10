package com.architecture.diagram.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for diagram generation endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagramGenerationResponse {
    private String status; // success, error
    private String message;
    private String plantUMLCode; // PlantUML DSL
    private String diagramBase64; // Base64 encoded image
    private byte[] diagramBytes; // Raw image bytes
    private String format; // png, svg
    private long generationTimeMs;
    private int componentCount;
    private int relationshipCount;
    private String errorDetails;
}
