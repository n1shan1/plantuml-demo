package com.architecture.diagram.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;

/**
 * Service for rendering PlantUML DSL to image formats (PNG, SVG).
 * Uses PlantUML library with Graphviz for layout.
 */
@Service
@Slf4j
public class RenderingService {

    @Value("${app.plantuml.output-format:png}")
    private String defaultOutputFormat;

    /**
     * Render PlantUML DSL to PNG
     */
    public byte[] renderToPNG(String plantUMLCode) {
        log.info("Rendering to PNG");
        return render(plantUMLCode, FileFormat.PNG);
    }

    /**
     * Render PlantUML DSL to SVG
     */
    public byte[] renderToSVG(String plantUMLCode) {
        log.info("Rendering to SVG");
        return render(plantUMLCode, FileFormat.SVG);
    }

    /**
     * Render PlantUML DSL to specified format
     */
    private byte[] render(String plantUMLCode, FileFormat format) {
        try {
            SourceStringReader reader = new SourceStringReader(plantUMLCode);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            reader.outputImage(baos, new FileFormatOption(format));

            byte[] result = baos.toByteArray();
            baos.close();

            log.info("Rendered successfully: {} bytes", result.length);
            return result;

        } catch (IOException e) {
            log.error("Failed to render: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to render diagram: " + e.getMessage(), e);
        }
    }

    /**
     * Render and get base64 encoded string
     */
    public String renderToBase64PNG(String plantUMLCode) {
        byte[] pngBytes = renderToPNG(plantUMLCode);
        return Base64.getEncoder().encodeToString(pngBytes);
    }

    /**
     * Render and get base64 encoded string for SVG
     */
    public String renderToBase64SVG(String plantUMLCode) {
        byte[] svgBytes = renderToSVG(plantUMLCode);
        return Base64.getEncoder().encodeToString(svgBytes);
    }

    /**
     * Save rendered diagram to file
     */
    public void saveToFile(String plantUMLCode, String filePath, String format) {
        try {
            FileFormat fileFormat = "svg".equalsIgnoreCase(format) ? FileFormat.SVG : FileFormat.PNG;
            SourceStringReader reader = new SourceStringReader(plantUMLCode);

            FileOutputStream fos = new FileOutputStream(filePath);
            reader.outputImage(fos, new FileFormatOption(fileFormat));
            fos.close();

            log.info("Diagram saved to: {}", filePath);

        } catch (IOException e) {
            log.error("Failed to save diagram to file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save diagram: " + e.getMessage(), e);
        }
    }
}
