package com.architecture.diagram.service;

import com.architecture.diagram.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for YamlParserService
 */
public class YamlParserServiceTest {

    private YamlParserService yamlParserService;

    @BeforeEach
    public void setUp() {
        yamlParserService = new YamlParserService();
    }

    @Test
    public void testParseSimpleYaml() {
        String yaml = """
                system:
                  name: "Test System"
                  components:
                    - id: comp1
                      type: external
                      label: "Component 1"
                    - id: comp2
                      type: database
                      label: "Database"
                  relationships:
                    - from: comp1
                      to: comp2
                      label: "Query"
                """;

        SystemModel model = yamlParserService.parseYaml(yaml);

        assertNotNull(model);
        assertEquals("Test System", model.getName());
        assertEquals(2, model.getComponents().size());
        assertEquals(1, model.getRelationships().size());
    }

    @Test
    public void testParseComponentTypes() {
        String yaml = """
                system:
                  name: "Type Test"
                  components:
                    - id: ext
                      type: external
                      label: "External"
                    - id: layer
                      type: layer
                      label: "Layer"
                    - id: sec
                      type: security
                      label: "Security"
                    - id: proc
                      type: processing
                      label: "Processing"
                    - id: data
                      type: data_access
                      label: "Data"
                    - id: db
                      type: database
                      label: "DB"
                  relationships: []
                """;

        SystemModel model = yamlParserService.parseYaml(yaml);

        assertEquals(6, model.getComponents().size());
        assertEquals(ComponentType.EXTERNAL, model.getComponents().get(0).getType());
        assertEquals(ComponentType.DATABASE, model.getComponents().get(5).getType());
    }

    @Test
    public void testParseWithAnnotations() {
        String yaml = """
                system:
                  name: "Annotation Test"
                  components:
                    - id: c1
                      type: external
                      label: "Component"
                  relationships: []
                  annotations:
                    - id: A1
                      text: "Test annotation"
                      type: security
                """;

        SystemModel model = yamlParserService.parseYaml(yaml);

        assertEquals(1, model.getAnnotations().size());
        assertEquals("A1", model.getAnnotations().get(0).getId());
        assertEquals("Test annotation", model.getAnnotations().get(0).getText());
    }

    @Test
    public void testParseBypassFlows() {
        String yaml = """
                system:
                  name: "Bypass Test"
                  components:
                    - id: c1
                      type: external
                      label: "C1"
                    - id: c2
                      type: database
                      label: "C2"
                  relationships:
                    - from: c1
                      to: c2
                      bypass: true
                      style: dashed
                """;

        SystemModel model = yamlParserService.parseYaml(yaml);

        Relationship rel = model.getRelationships().get(0);
        assertTrue(rel.isBypass());
        assertEquals(LineStyle.DASHED, rel.getStyle());
    }

    @Test
    public void testInvalidYamlThrowsException() {
        String invalidYaml = "not valid yaml {{{";

        assertThrows(RuntimeException.class, () -> {
            yamlParserService.parseYaml(invalidYaml);
        });
    }

    @Test
    public void testEmptyYamlThrowsException() {
        String emptyYaml = "";

        assertThrows(RuntimeException.class, () -> {
            yamlParserService.parseYaml(emptyYaml);
        });
    }
}
