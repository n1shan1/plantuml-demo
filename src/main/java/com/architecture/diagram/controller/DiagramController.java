package com.architecture.diagram.controller;

import com.architecture.diagram.domain.SystemModel;
import com.architecture.diagram.dto.DiagramGenerationRequest;
import com.architecture.diagram.dto.DiagramGenerationResponse;
import com.architecture.diagram.graph.Graph;
import com.architecture.diagram.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for diagram generation endpoints.
 * Orchestrates the complete pipeline: YAML → Graph → PlantUML → Image
 */
@RestController
@RequestMapping("/api/diagram")
@Slf4j
public class DiagramController {

    private final YamlParserService yamlParserService;
    private final GraphBuilderService graphBuilderService;
    private final PlantUMLGeneratorService plantUMLGeneratorService;
    private final RenderingService renderingService;

    @Autowired
    public DiagramController(
            YamlParserService yamlParserService,
            GraphBuilderService graphBuilderService,
            PlantUMLGeneratorService plantUMLGeneratorService,
            RenderingService renderingService) {
        this.yamlParserService = yamlParserService;
        this.graphBuilderService = graphBuilderService;
        this.plantUMLGeneratorService = plantUMLGeneratorService;
        this.renderingService = renderingService;
    }

    /**
     * Generate architecture diagram from YAML
     * POST /generate-diagram
     */
    @PostMapping("/generate-diagram")
    public ResponseEntity<?> generateDiagram(@RequestBody DiagramGenerationRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("Received diagram generation request");

            // Step 1: Parse YAML
            SystemModel systemModel = yamlParserService.parseYaml(request.getYamlContent());
            log.info("Parsed YAML: {} components", systemModel.getComponents().size());

            // Step 2: Build graph model
            Graph graph = graphBuilderService.buildGraph(systemModel);
            log.info("Built graph: {} nodes, {} edges", graph.getNodeCount(), graph.getEdgeCount());

            // Step 3: Generate PlantUML
            String plantUMLCode = plantUMLGeneratorService.generatePlantUML(graph, systemModel);
            log.info("Generated PlantUML: {} characters", plantUMLCode.length());

            // Step 4: Render to image
            String format = request.getFormat() != null ? request.getFormat() : "png";
            byte[] diagramBytes;
            String base64Diagram = null;

            if ("svg".equalsIgnoreCase(format)) {
                diagramBytes = renderingService.renderToSVG(plantUMLCode);
                if (request.isReturnBase64()) {
                    base64Diagram = renderingService.renderToBase64SVG(plantUMLCode);
                }
            } else {
                diagramBytes = renderingService.renderToPNG(plantUMLCode);
                if (request.isReturnBase64()) {
                    base64Diagram = renderingService.renderToBase64PNG(plantUMLCode);
                }
            }

            long generationTime = System.currentTimeMillis() - startTime;

            // Build response
            DiagramGenerationResponse response = DiagramGenerationResponse.builder()
                    .status("success")
                    .message("Diagram generated successfully")
                    .format(format)
                    .generationTimeMs(generationTime)
                    .componentCount(systemModel.getComponents().size())
                    .relationshipCount(systemModel.getRelationships().size())
                    .diagramBytes(diagramBytes)
                    .build();

            if (request.isReturnPlantUML()) {
                response.setPlantUMLCode(plantUMLCode);
            }

            if (request.isReturnBase64()) {
                response.setDiagramBase64(base64Diagram);
            }

            log.info("Diagram generated successfully in {} ms", generationTime);

            // Return appropriate content type
            if (request.isReturnBase64() || request.isReturnPlantUML()) {
                // Return JSON response
                DiagramGenerationResponse jsonResponse = DiagramGenerationResponse.builder()
                        .status("success")
                        .message("Diagram generated successfully")
                        .format(format)
                        .generationTimeMs(generationTime)
                        .componentCount(systemModel.getComponents().size())
                        .relationshipCount(systemModel.getRelationships().size())
                        .build();

                if (request.isReturnPlantUML()) {
                    jsonResponse.setPlantUMLCode(plantUMLCode);
                }
                if (request.isReturnBase64()) {
                    jsonResponse.setDiagramBase64(base64Diagram);
                }

                return ResponseEntity.ok(jsonResponse);
            } else {
                // Return binary image
                HttpHeaders headers = new HttpHeaders();
                MediaType mediaType = "svg".equalsIgnoreCase(format) ? MediaType.valueOf("image/svg+xml")
                        : MediaType.IMAGE_PNG;
                headers.setContentType(mediaType);
                headers.setContentLength(diagramBytes.length);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(diagramBytes);
            }

        } catch (Exception e) {
            log.error("Error generating diagram", e);

            long generationTime = System.currentTimeMillis() - startTime;
            DiagramGenerationResponse errorResponse = DiagramGenerationResponse.builder()
                    .status("error")
                    .message("Failed to generate diagram")
                    .errorDetails(e.getMessage())
                    .generationTimeMs(generationTime)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     * GET /health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Architecture Diagram Generator is running");
    }

    /**
     * Get example YAML template
     * GET /example-yaml
     */
    @GetMapping("/example-yaml")
    public ResponseEntity<String> getExampleYaml() {
        String exampleYaml = """
                system:
                  name: "Order Management System"
                  description: "Layered order processing architecture"
                  components:
                    - id: clients
                      type: external
                      label: "Clients - Custom Views"
                      color: "#E8F4F8"
                    - id: controller
                      type: layer
                      label: "REST Controller Layer"
                      color: "#D4E6F1"
                    - id: auth
                      type: security
                      label: "Authorization Layer"
                      color: "#F8D7DA"
                    - id: business
                      type: processing
                      label: "Business Rule Processing"
                      color: "#D1ECF1"
                      multipleLayers: true
                      layers:
                        - "Order Validation"
                        - "Payment Processing"
                        - "Inventory Check"
                    - id: db_access
                      type: data_access
                      label: "Database Access Layer"
                      color: "#E7D4F5"
                    - id: database
                      type: database
                      label: "Order and Customer DB"
                      color: "#FDFFB6"
                  relationships:
                    - from: clients
                      to: controller
                      label: "HTTP Request"
                    - from: controller
                      to: auth
                      label: "Authenticate"
                    - from: auth
                      to: business
                      label: "Process Order"
                    - from: business
                      to: db_access
                      label: "Query Data"
                    - from: db_access
                      to: database
                      label: "SQL"
                    - from: auth
                      to: database
                      label: "Direct access"
                      bypass: true
                      style: dashed
                  annotations:
                    - id: P1
                      text: "Bypass layers for authorization performance"
                      type: performance
                    - id: S1
                      text: "JWT token validation"
                      type: security
                    - id: R1
                      text: "Transaction rollback on failure"
                      type: recovery
                """;
        return ResponseEntity.ok(exampleYaml);
    }
}
