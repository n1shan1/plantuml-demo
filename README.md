# Architecture Diagram Generator

A Spring Boot application that generates PlantUML-based architecture diagrams from YAML specifications using Graphviz layout engine.

## Features

- **YAML-based DSL**: Define system architecture in simple, readable YAML
- **Clean Architecture**: Layered components (external, controller, security, business, data access, database)
- **Advanced Layout**: Hierarchical layout using topological sort for proper flow visualization
- **Bypass Flows**: Support for bypass arrows to show alternative flows
- **Annotations**: Add tactical notes and security considerations
- **Multiple Output Formats**: PNG, SVG, Base64 encoded
- **PlantUML Integration**: Uses PlantUML with Graphviz for professional diagrams
- **Extensible Design**: Easy to add new component types and styling

## Project Structure

```
architecture-diagrams/
├── src/main/java/com/architecture/diagram/
│   ├── ArchitectureDiagramApplication.java    # Main entry point
│   ├── controller/
│   │   └── DiagramController.java             # REST API endpoints
│   ├── domain/
│   │   ├── SystemModel.java                   # Core domain model
│   │   ├── Component.java                     # Component entity
│   │   ├── Relationship.java                  # Relationship entity
│   │   ├── Annotation.java                    # Annotation entity
│   │   ├── ComponentType.java                 # Enum for component types
│   │   └── LineStyle.java                     # Enum for line styles
│   ├── service/
│   │   ├── YamlParserService.java            # YAML parsing
│   │   ├── GraphBuilderService.java          # Graph model building
│   │   ├── PlantUMLGeneratorService.java     # PlantUML DSL generation
│   │   └── RenderingService.java             # Image rendering
│   ├── graph/
│   │   ├── Graph.java                        # Graph model
│   │   ├── Node.java                         # Graph node
│   │   └── Edge.java                         # Graph edge
│   ├── dto/
│   │   ├── DiagramGenerationRequest.java     # Request DTO
│   │   └── DiagramGenerationResponse.java    # Response DTO
│   └── config/yaml/
│       ├── SystemYaml.java                   # YAML DTO
│       ├── ComponentYaml.java                # Component YAML DTO
│       ├── RelationshipYaml.java             # Relationship YAML DTO
│       └── AnnotationYaml.java               # Annotation YAML DTO
├── src/main/resources/
│   ├── application.yml                       # Spring Boot config
│   └── examples/
│       ├── order-system.yaml                 # Example: Order system
│       ├── ecommerce-system.yaml             # Example: E-commerce
│       └── simple-blog.yaml                  # Example: Blog app
└── pom.xml                                   # Maven dependencies
```

## Pipeline Architecture

```
YAML Input
    ↓
YamlParserService (Parsing)
    ↓
SystemModel (Domain Model)
    ↓
GraphBuilderService (Graph Construction)
    ↓
Graph (Intermediate Representation)
    ├─ Nodes (components)
    ├─ Edges (relationships)
    └─ Layout hints
    ↓
PlantUMLGeneratorService (DSL Generation)
    ↓
PlantUML Code
    ↓
RenderingService (Image Rendering)
    ↓
PNG/SVG Output
```

## YAML DSL Specification

### Basic Structure

```yaml
system:
  name: "System Name"
  description: "System description"

  components:
    - id: component_id
      type: component_type
      label: "Display Label"
      color: "#RRGGBB"
      description: "Optional description"

  relationships:
    - from: source_id
      to: target_id
      label: "Optional label"
      style: line_style
      bypass: false

  annotations:
    - id: annotation_id
      text: "Annotation text"
      type: annotation_type
```

### Component Types

- **external**: External actors/clients
- **layer**: Application layers
- **security**: Security/validation layers
- **processing**: Business logic
- **data_access**: Data access layers
- **database**: Databases/storage
- **service**: Microservices

### Line Styles

- **solid**: Normal flow (default)
- **dashed**: Bypass or alternative flow
- **dotted**: Optional or conditional
- **bold**: Critical flow

### Example YAML

```yaml
system:
  name: "E-Commerce Platform"

  components:
    - id: clients
      type: external
      label: "Web & Mobile Clients"
      color: "#E8F4F8"

    - id: api_gateway
      type: layer
      label: "API Gateway"

    - id: auth
      type: security
      label: "Authentication"

    - id: order_service
      type: processing
      label: "Order Service"

    - id: db
      type: database
      label: "Database"

  relationships:
    - from: clients
      to: api_gateway
      label: "HTTPS"

    - from: api_gateway
      to: auth
      label: "Token"

    - from: auth
      to: order_service
      label: "Request"

    - from: order_service
      to: db
      label: "SQL"

    - from: auth
      to: db
      label: "User Check"
      bypass: true
      style: dashed

  annotations:
    - id: P1
      text: "Bypass for performance"
      type: performance
```

## Building and Running

### Prerequisites

- Java 17+
- Maven 3.6+
- Graphviz (for PlantUML rendering)

### Installation

```bash
# On macOS (Homebrew)
brew install graphviz

# On Ubuntu/Debian
sudo apt-get install graphviz

# On CentOS/RHEL
sudo yum install graphviz
```

### Build

```bash
mvn clean package
```

### Run

```bash
# Development mode
mvn spring-boot:run

# Production
java -jar target/architecture-diagrams-1.0.0.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Generate Diagram

**Endpoint**: `POST /api/diagram/generate-diagram`

**Request Body**:

```json
{
  "yamlContent": "system:\n  name: \"My System\"\n  ...",
  "format": "png",
  "returnBase64": false,
  "returnPlantUML": false
}
```

**Query Parameters**:

- `format`: "png" or "svg" (default: "png")
- `returnBase64`: Include base64 encoded image
- `returnPlantUML`: Include PlantUML DSL code

**Success Response** (200):

```json
{
  "status": "success",
  "message": "Diagram generated successfully",
  "format": "png",
  "generationTimeMs": 1234,
  "componentCount": 6,
  "relationshipCount": 8,
  "plantUMLCode": "@startuml\n...",
  "diagramBase64": "iVBORw0KGgoAAAANS..."
}
```

**Error Response** (400):

```json
{
  "status": "error",
  "message": "Failed to generate diagram",
  "errorDetails": "..."
}
```

### Get Example YAML

**Endpoint**: `GET /api/diagram/example-yaml`

Returns a complete example YAML for testing.

### Health Check

**Endpoint**: `GET /api/diagram/health`

Verifies the service is running.

## Usage Examples

### Using cURL

```bash
# Generate PNG and return as base64
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "system:\n  name: \"Test\"\n  components:\n    - id: c1\n      type: external\n      label: \"Client\"",
    "format": "png",
    "returnBase64": true
  }'

# Get PlantUML code only
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "...",
    "returnPlantUML": true
  }' | jq '.plantUMLCode'

# Get raw PNG bytes
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "...",
    "format": "png"
  }' > diagram.png
```

### Using Python

```python
import requests
import json
import base64

url = "http://localhost:8080/api/diagram/generate-diagram"

yaml_content = """
system:
  name: "My System"
  components:
    - id: client
      type: external
      label: "Client"
    - id: db
      type: database
      label: "Database"
  relationships:
    - from: client
      to: db
      label: "Query"
"""

payload = {
    "yamlContent": yaml_content,
    "format": "png",
    "returnBase64": True
}

response = requests.post(url, json=payload)
data = response.json()

# Save image
if data['status'] == 'success':
    image_data = base64.b64decode(data['diagramBase64'])
    with open('diagram.png', 'wb') as f:
        f.write(image_data)
    print("Diagram saved to diagram.png")
```

### Using JavaScript

```javascript
const yamlContent = `
system:
  name: "My System"
  components:
    - id: client
      type: external
      label: "Client"
    - id: db
      type: database
      label: "Database"
  relationships:
    - from: client
      to: db
`;

fetch("http://localhost:8080/api/diagram/generate-diagram", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    yamlContent: yamlContent,
    format: "svg",
    returnPlantUML: true,
  }),
})
  .then((r) => r.json())
  .then((data) => {
    if (data.status === "success") {
      console.log(data.plantUMLCode);
      // Display SVG in img tag
      document.querySelector("img").src =
        "data:image/svg+xml;base64," + data.diagramBase64;
    }
  });
```

## Component Type Styling

| Type        | Color      | Shape     |
| ----------- | ---------- | --------- |
| EXTERNAL    | Light Blue | Rectangle |
| LAYER       | Blue       | Rectangle |
| SECURITY    | Red        | Rectangle |
| PROCESSING  | Teal       | Rectangle |
| DATA_ACCESS | Purple     | Rectangle |
| DATABASE    | Yellow     | Cylinder  |
| SERVICE     | Green      | Rectangle |

## Advanced Features

### 1. Multiple Layers

Define complex business logic with multiple layers:

```yaml
components:
  - id: business
    type: processing
    label: "Business Logic"
    multipleLayers: true
    layers:
      - "Validation"
      - "Processing"
      - "Notification"
```

### 2. Bypass Flows

Show alternative paths:

```yaml
relationships:
  - from: auth
    to: database
    label: "Direct Access"
    bypass: true
    style: dashed
```

### 3. Annotations

Add architectural notes:

```yaml
annotations:
  - id: P1
    text: "Cache for performance"
    type: performance
  - id: S1
    text: "Requires encryption"
    type: security
```

## Extensibility

### Adding New Component Types

1. Update `ComponentType` enum:

```java
public enum ComponentType {
    MESSAGE_QUEUE("Message Queue"),
    CACHE("Cache"),
    // ... existing types
}
```

2. Update shape mapping in `GraphBuilderService`:

```java
private String getShapeForComponentType(ComponentType type) {
    return switch (type) {
        case MESSAGE_QUEUE -> "queue",
        // ...
    };
}
```

3. Update color mapping:

```java
private String getDefaultColor(ComponentType type) {
    return switch (type) {
        case MESSAGE_QUEUE -> "#FFC857",
        // ...
    };
}
```

### Custom Rendering Formats

Extend `RenderingService` to support additional formats:

```java
public byte[] renderToPDF(String plantUMLCode) {
    SourceStringReader reader = new SourceStringReader(plantUMLCode);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    reader.outputImage(baos, new FileFormatOption(FileFormat.PDF));
    return baos.toByteArray();
}
```

### Custom Layout Strategies

Implement new layout algorithms in `GraphBuilderService`:

```java
private void applyCircularLayout(Graph graph) {
    // Implement circular layout
}

private void applyForceDirectedLayout(Graph graph) {
    // Implement force-directed layout
}
```

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
# Start server
mvn spring-boot:run

# In another terminal
curl -X GET http://localhost:8080/api/diagram/health
```

## Performance Considerations

- **Caching**: Consider caching generated diagrams for identical inputs
- **Async Processing**: For large diagrams, use async endpoints
- **Streaming**: Stream large SVG outputs to avoid memory issues
- **GraphViz Optimization**: Use layout hints to optimize rendering time

## Troubleshooting

### PlantUML Rendering Issues

1. Verify Graphviz is installed:

```bash
which dot
dot -V
```

2. Check PlantUML logs in console

### YAML Parsing Errors

1. Validate YAML syntax at [yaml-online-parser.appspot.com](https://yaml-online-parser.appspot.com/)
2. Check component IDs match in relationships
3. Ensure all required fields are present

## Future Enhancements

1. **Reverse Engineering**: Generate YAML from PlantUML diagrams
2. **Import/Export**: Support for other formats (Visio, Draw.io)
3. **Web UI**: Drag-and-drop diagram builder
4. **Themes**: Custom color schemes and styling
5. **Validation**: Schema validation and constraint checking
6. **Version Control**: Diff diagrams over time
7. **Collaboration**: Real-time diagram editing
8. **Analytics**: Component dependency analysis

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! Please see CONTRIBUTING.md for guidelines.

## Support

- Issues: GitHub Issues
- Discussions: GitHub Discussions
- Documentation: See docs/ directory

---

**Built with Spring Boot, PlantUML, and Graphviz**
