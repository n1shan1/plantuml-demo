# Developer Guide

Comprehensive guide for developers working with the Architecture Diagram Generator.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Project Structure](#project-structure)
3. [Development Setup](#development-setup)
4. [Core Concepts](#core-concepts)
5. [Adding Features](#adding-features)
6. [Testing Strategy](#testing-strategy)
7. [Debugging](#debugging)
8. [Performance Optimization](#performance-optimization)
9. [Contributing](#contributing)

---

## Architecture Overview

### Layered Architecture

```
┌─────────────────────────────────────┐
│      REST Controller Layer          │
│  (DiagramController)                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Service Layer                  │
│  - YamlParserService                │
│  - GraphBuilderService              │
│  - PlantUMLGeneratorService         │
│  - RenderingService                 │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Domain Model Layer             │
│  - SystemModel                      │
│  - Component, Relationship          │
│  - Graph, Node, Edge                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      External Libraries             │
│  - PlantUML                         │
│  - Jackson YAML                     │
│  - Graphviz                         │
└─────────────────────────────────────┘
```

### Data Flow Pipeline

```
YAML Input
    ↓
YamlParserService.parseYaml()
    ↓
SystemModel (domain objects)
    ↓
GraphBuilderService.buildGraph()
    ↓
Graph (intermediate representation)
    ├─ nodes (with layout hints)
    ├─ edges (with styles)
    └─ layout strategy
    ↓
PlantUMLGeneratorService.generatePlantUML()
    ↓
PlantUML DSL String
    ↓
RenderingService.render*()
    ↓
PNG/SVG bytes
    ↓
HTTP Response
```

---

## Project Structure

### Directory Layout

```
src/
├── main/
│   ├── java/com/architecture/diagram/
│   │   ├── ArchitectureDiagramApplication.java
│   │   ├── controller/
│   │   │   └── DiagramController.java
│   │   ├── service/
│   │   │   ├── YamlParserService.java
│   │   │   ├── GraphBuilderService.java
│   │   │   ├── PlantUMLGeneratorService.java
│   │   │   └── RenderingService.java
│   │   ├── domain/
│   │   │   ├── SystemModel.java
│   │   │   ├── Component.java
│   │   │   ├── Relationship.java
│   │   │   ├── Annotation.java
│   │   │   ├── ComponentType.java
│   │   │   └── LineStyle.java
│   │   ├── graph/
│   │   │   ├── Graph.java
│   │   │   ├── Node.java
│   │   │   └── Edge.java
│   │   ├── dto/
│   │   │   ├── DiagramGenerationRequest.java
│   │   │   └── DiagramGenerationResponse.java
│   │   ├── config/
│   │   │   ├── ApplicationConfig.java
│   │   │   └── yaml/
│   │   │       ├── SystemYaml.java
│   │   │       ├── ComponentYaml.java
│   │   │       ├── RelationshipYaml.java
│   │   │       └── AnnotationYaml.java
│   │   └── util/
│   │       ├── StyleUtil.java
│   │       └── ValidationUtil.java
│   └── resources/
│       ├── application.yml
│       └── examples/
│           ├── order-system.yaml
│           ├── ecommerce-system.yaml
│           └── simple-blog.yaml
├── test/
│   ├── java/com/architecture/diagram/
│   │   ├── service/
│   │   │   └── YamlParserServiceTest.java
│   │   └── ...
│   └── resources/
│       └── application.yml
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Development Setup

### Prerequisites

- Java 17+
- Maven 3.6+
- Graphviz
- Git

### Initial Setup

```bash
# Clone repository
git clone <repository-url>
cd architecture-diagrams

# Install dependencies
mvn clean install

# Build project
mvn clean package

# Run application
mvn spring-boot:run

# Run tests
mvn test
```

### IDE Setup

#### IntelliJ IDEA

1. Open project as Maven project
2. Configure JDK 17
3. Enable annotation processing for Lombok
4. Run `ArchitectureDiagramApplication.main()`

#### VS Code

1. Install Extension Pack for Java
2. Open project folder
3. Spring Boot Dashboard will auto-detect
4. Click "Run" button in dashboard

#### Eclipse

1. Import as Maven project
2. Configure JDK 17
3. Right-click project → Run As → Spring Boot App

---

## Core Concepts

### 1. System Model

The `SystemModel` is the core domain entity containing:

- `name`: System name
- `components`: List of components
- `relationships`: Connections between components
- `annotations`: Tactical notes

```java
SystemModel model = new SystemModel("My System");
model.addComponent(new Component("id1", ComponentType.EXTERNAL, "Client"));
model.addRelationship(new Relationship("id1", "id2", "Request"));
```

### 2. Component Types

Each component has a `ComponentType` that determines:

- Visual shape
- Default color
- Semantic meaning

```java
public enum ComponentType {
    EXTERNAL,      // External actors
    LAYER,         // Application layers
    SECURITY,      // Security components
    PROCESSING,    // Business logic
    DATA_ACCESS,   // Data access layer
    DATABASE,      // Databases
    SERVICE        // Microservices
}
```

### 3. Relationship Flow

Relationships define how components interact:

- `from`: Source component
- `to`: Target component
- `style`: Line style (solid, dashed, etc.)
- `bypass`: Alternative flow indicator

```java
Relationship rel = new Relationship(
    "auth",
    "database",
    "Direct Access",
    LineStyle.DASHED
);
rel.setBypass(true);
```

### 4. Graph Model

The `Graph` is an intermediate representation:

- `nodes`: Components with layout hints
- `edges`: Relationships with styling
- `layoutStrategy`: How to arrange components

Used for:

- Deterministic layout
- Cycle detection
- Flow analysis

### 5. PlantUML Generation

Converts graph to PlantUML DSL:

- Uses package notation for grouping
- Annotations for notes
- Skinparam for styling
- Deterministic ordering

### 6. Rendering

PlantUML to Image conversion:

- PNG: Raster image
- SVG: Scalable vector graphics
- Base64 encoding for embedding

---

## Adding Features

### Adding a New Component Type

1. **Update Enum in `ComponentType.java`**:

```java
public enum ComponentType {
    CACHE("Cache"),  // Add new type
    // ... existing types
}
```

2. **Update Styling in `StyleUtil.java`**:

```java
static {
    COMPONENT_COLORS.put(ComponentType.CACHE, "#FF6B6B");
    COMPONENT_SHAPES.put(ComponentType.CACHE, "rectangle");
}
```

3. **Update GraphBuilder in `GraphBuilderService.java`**:

```java
private String getShapeForComponentType(ComponentType type) {
    return switch (type) {
        case CACHE -> "rectangle",
        // ...
    };
}
```

4. **Update PlantUML Generator**:

```java
private String generateComponentNode(Node node) {
    if ("cache".equalsIgnoreCase(node.getType())) {
        // Custom rendering for cache nodes
        return ...;
    }
    // ...
}
```

5. **Test the Change**:

```java
@Test
public void testCacheComponentType() {
    Component cache = new Component("cache1", ComponentType.CACHE, "Redis Cache");
    assertEquals(ComponentType.CACHE, cache.getType());
}
```

### Adding a New Output Format

1. **Extend `RenderingService.java`**:

```java
public byte[] renderToPDF(String plantUMLCode) {
    SourceStringReader reader = new SourceStringReader(plantUMLCode);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    reader.outputImage(baos, new FileFormatOption(FileFormat.PDF));
    return baos.toByteArray();
}
```

2. **Update Controller**:

```java
if ("pdf".equalsIgnoreCase(format)) {
    diagramBytes = renderingService.renderToPDF(plantUMLCode);
}
```

3. **Update Tests**:

```java
@Test
public void testPDFRendering() {
    byte[] pdf = renderingService.renderToPDF(plantUMLCode);
    assertTrue(pdf.length > 0);
}
```

### Implementing a New Layout Strategy

1. **Add Strategy to Graph Enum**:

```java
public enum LayoutStrategy {
    FORCE_DIRECTED("Physics-based"),
    // ... existing strategies
}
```

2. **Implement in GraphBuilder**:

```java
private void applyForceDirectedLayout(Graph graph) {
    // Use physics simulation to position nodes
    for (Node node : graph.getNodes().values()) {
        // Calculate forces between connected nodes
        // Update node position
    }
}
```

3. **Call in buildGraph**:

```java
public Graph buildGraph(SystemModel systemModel) {
    // ...
    if (graph.getLayoutStrategy() == LayoutStrategy.FORCE_DIRECTED) {
        applyForceDirectedLayout(graph);
    }
}
```

---

## Testing Strategy

### Unit Testing

Test individual methods in isolation:

```java
@Test
public void testYamlParsing() {
    YamlParserService service = new YamlParserService();
    String yaml = "system:\n  name: \"Test\"";
    SystemModel model = service.parseYaml(yaml);
    assertEquals("Test", model.getName());
}
```

### Integration Testing

Test component interactions:

```java
@WithMockMvc
@SpringBootTest
public class DiagramControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGenerateDiagram() throws Exception {
        String request = "{\"yamlContent\": \"...\"}";
        mockMvc.perform(post("/api/diagram/generate-diagram")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isOk());
    }
}
```

### End-to-End Testing

Test complete pipeline:

```bash
# Start application
mvn spring-boot:run &

# Test complete flow
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{"yamlContent": "..."}'

# Verify response
echo "Tests passed!"
```

### Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=YamlParserServiceTest

# Specific test method
mvn test -Dtest=YamlParserServiceTest#testParseSimpleYaml

# Skip tests during build
mvn package -DskipTests
```

---

## Debugging

### Enable Debug Logging

In `application.yml`:

```yaml
logging:
  level:
    com.architecture.diagram: DEBUG
    org.springframework: INFO
```

### Debug in IDE

1. Set breakpoints in code
2. Run in debug mode
3. Use Variables panel to inspect state
4. Step through execution

### Common Debug Points

```java
// YamlParserService
break at: convertToSystemModel()

// GraphBuilderService
break at: assignLayers()

// PlantUMLGeneratorService
break at: generateEdges()

// RenderingService
break at: render()
```

### Inspect Diagram Generation

Save intermediate artifacts:

```java
// Save PlantUML code
Files.write(Paths.get("debug.puml"), plantUMLCode.getBytes());

// Save graph structure
log.debug("Graph: {} nodes, {} edges", graph.getNodeCount(), graph.getEdgeCount());

// Inspect layout
for (Node node : graph.getNodes().values()) {
    log.debug("Node: {} at ({}, {})", node.getId(), node.getRow(), node.getColumn());
}
```

---

## Performance Optimization

### Measure Performance

```java
long start = System.currentTimeMillis();
// ... operation
long duration = System.currentTimeMillis() - start;
log.info("Operation took {}ms", duration);
```

### Optimize Parsing

```java
// Cache ObjectMapper instance
private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

// Reuse buffers
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
```

### Optimize Graph Building

```java
// Use LinkedHashMap for preserving order
Map<String, Node> nodes = new LinkedHashMap<>();

// Use stream efficiently
graph.getOutgoing(nodeId).stream()
    .filter(e -> !processed.contains(e.getToNodeId()))
    .forEach(...);
```

### Optimize Rendering

```java
// Cache rendered diagrams
@Cacheable(value = "diagrams", key = "#yamlHash")
public byte[] generateDiagram(String yaml) {
    // ...
}

// Use async rendering for large diagrams
@Async
public CompletableFuture<byte[]> renderAsync(String code) {
    // ...
}
```

### Profile Application

```bash
# Generate flamegraph
mvn clean package -P dev

# Run with profiler
java -jar -XX:+UnlockDiagnosticVMOptions \
    -XX:+TraceClassLoading target/app.jar

# Monitor with jvisualvm
jvisualvm
```

---

## Contributing

### Code Style

Follow Google Java Style Guide:

- 2 space indentation
- Max line length: 100 characters
- Use meaningful variable names
- Add Javadoc for public methods

```java
/**
 * Generates PlantUML DSL from graph model.
 *
 * @param graph the graph to convert
 * @return PlantUML DSL string
 */
public String generatePlantUML(Graph graph) {
    // ...
}
```

### Commit Messages

```
feat: Add support for custom colors in components
fix: Resolve cycle detection algorithm
docs: Update API documentation
test: Add unit tests for graph builder
refactor: Simplify PlantUML generation logic
```

### Pull Request Process

1. Create feature branch: `git checkout -b feature/new-feature`
2. Make changes and commit
3. Write tests for new code
4. Push and create PR
5. Address review feedback
6. Merge when approved

---

## Useful Resources

- [PlantUML Documentation](http://plantuml.com/)
- [Graphviz Documentation](https://graphviz.org/)
- [Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)
- [Jackson YAML](https://github.com/FasterXML/jackson-dataformats-text)
- [Java Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)

---

## FAQ

**Q: How to add a custom color scheme?**
A: Update `StyleUtil.java` COMPONENT_COLORS map and provide a theme configuration.

**Q: Can I support other layout engines besides Graphviz?**
A: Yes, extend `LayoutStrategy` enum and implement layout algorithm in `GraphBuilderService`.

**Q: How to cache diagram generation?**
A: Use Spring Cache annotation on `RenderingService.render*()` methods.

**Q: How to add reverse engineering (PlantUML → YAML)?**
A: Implement parser for PlantUML syntax, create reverse mapping, and add new service.

---

**Happy coding! 🚀**
