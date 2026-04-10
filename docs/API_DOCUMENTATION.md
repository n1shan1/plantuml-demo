# API Documentation

Architecture Diagram Generator - Complete API Reference

## Base URL

```
http://localhost:8080/api/diagram
```

## Endpoints

### 1. Generate Diagram

Generates an architecture diagram from YAML specification.

**Endpoint**: `POST /generate-diagram`

**Content-Type**: `application/json`

#### Request Body

```json
{
  "yamlContent": "string",        // YAML content defining the system
  "format": "png|svg",            // Output format (default: png)
  "returnBase64": boolean,        // Include base64 encoded image
  "returnPlantUML": boolean       // Include PlantUML DSL code
}
```

#### Request Parameters

| Parameter      | Type    | Required | Description                                 |
| -------------- | ------- | -------- | ------------------------------------------- |
| yamlContent    | string  | Yes      | Complete YAML specification                 |
| format         | string  | No       | "png" or "svg" (default: "png")             |
| returnBase64   | boolean | No       | Include base64 in response (default: false) |
| returnPlantUML | boolean | No       | Include PlantUML code (default: false)      |

#### Response (Success - 200)

```json
{
  "status": "success",
  "message": "Diagram generated successfully",
  "plantUMLCode": "@startuml\n...",
  "diagramBase64": "iVBORw0KGgoAAAANS...",
  "format": "png",
  "generationTimeMs": 1234,
  "componentCount": 6,
  "relationshipCount": 8
}
```

#### Response (Error - 400)

```json
{
  "status": "error",
  "message": "Failed to generate diagram",
  "errorDetails": "Invalid YAML: missing required field 'id' in component"
}
```

#### Response Fields

| Field             | Type   | Description                           |
| ----------------- | ------ | ------------------------------------- |
| status            | string | "success" or "error"                  |
| message           | string | Human-readable message                |
| plantUMLCode      | string | PlantUML DSL (if requested)           |
| diagramBase64     | string | Base64 encoded image (if requested)   |
| format            | string | Output format used                    |
| generationTimeMs  | number | Total generation time in milliseconds |
| componentCount    | number | Number of components                  |
| relationshipCount | number | Number of relationships               |
| errorDetails      | string | Error details (on failure)            |

#### Examples

**Basic PNG Generation**

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "system:\n  name: \"Test\"\n  components:\n    - id: c1\n      type: external\n      label: \"Client\"",
    "format": "png"
  }' -o diagram.png
```

**Get PlantUML Code Only**

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "...",
    "returnPlantUML": true
  }' | jq '.plantUMLCode'
```

**Get Base64 Encoded Image**

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "...",
    "format": "svg",
    "returnBase64": true
  }' | jq '.diagramBase64'
```

---

### 2. Get Example YAML

Returns a template YAML with a complete example system.

**Endpoint**: `GET /example-yaml`

**Content-Type**: `text/plain`

#### Request

```bash
curl http://localhost:8080/api/diagram/example-yaml
```

#### Response (200)

```yaml
system:
  name: "Order Management System"
  description: "Layered order processing architecture"
  components:
    - id: clients
      type: external
      label: "Clients - Custom Views"
      color: "#E8F4F8"
    # ... more components
```

#### Use Cases

- Get a template for new diagrams
- Learn YAML structure
- Copy and modify for your system

---

### 3. Health Check

Verifies the service is running and responsive.

**Endpoint**: `GET /health`

**Content-Type**: `text/plain`

#### Request

```bash
curl http://localhost:8080/api/diagram/health
```

#### Response (200)

```
Architecture Diagram Generator is running
```

#### Use Cases

- Load balancer health checks
- Service dependency verification
- Liveness probes in Kubernetes

---

## Request Examples

### Example 1: Simple Three-Tier System

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "system:\n  name: \"Web App\"\n  components:\n    - id: web\n      type: external\n      label: \"Web Browser\"\n    - id: app\n      type: layer\n      label: \"App Server\"\n    - id: db\n      type: database\n      label: \"Database\"\n  relationships:\n    - from: web\n      to: app\n    - from: app\n      to: db",
    "format": "png",
    "returnPlantUML": true
  }'
```

### Example 2: Complex Microservices

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d @request.json
```

Where `request.json`:

```json
{
  "yamlContent": "system:\n  name: \"E-Commerce\"\n  components:\n    - id: gateway\n      type: layer\n      label: \"API Gateway\"\n    - id: users\n      type: processing\n      label: \"User Service\"\n    - id: orders\n      type: processing\n      label: \"Order Service\"\n    - id: db\n      type: database\n      label: \"PostgreSQL\"\n  relationships:\n    - from: gateway\n      to: users\n    - from: gateway\n      to: orders\n    - from: users\n      to: db\n    - from: orders\n      to: db",
  "format": "svg",
  "returnBase64": true
}
```

### Example 3: With Bypass Flows and Annotations

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "system:\n  name: \"Advanced System\"\n  components:\n    - id: client\n      type: external\n      label: \"Client\"\n    - id: auth\n      type: security\n      label: \"Auth Layer\"\n    - id: logic\n      type: processing\n      label: \"Business Logic\"\n    - id: db\n      type: database\n      label: \"Database\"\n  relationships:\n    - from: client\n      to: auth\n    - from: auth\n      to: logic\n    - from: logic\n      to: db\n    - from: auth\n      to: db\n      bypass: true\n      style: dashed\n      label: \"Cache Hit\"\n  annotations:\n    - id: P1\n      text: \"Bypass for performance\"\n      type: performance",
    "format": "png",
    "returnPlantUML": true,
    "returnBase64": true
  }'
```

---

## YAML Schema Reference

### System Definition

```yaml
system:
  name: string # Required: System name
  description: string # Optional: System description
  components: Component[] # Required: List of components
  relationships: Relationship[] # Required: List of relationships
  annotations: Annotation[] # Optional: List of annotations
  metadata: object # Optional: Custom metadata
```

### Component Definition

```yaml
components:
  - id: string # Required: Unique component ID
    type: string # Required: Component type
    label: string # Required: Display label
    description: string # Optional: Description
    color: string # Optional: Hex color code
    icon: string # Optional: Icon reference
    multipleLayers: boolean # Optional: Has multiple layers
    layers: string[] # Optional: Layer names
    metadata: object # Optional: Custom metadata
```

### Component Types

Valid values for `type`:

- `external` - External actors/clients
- `layer` - Application layers
- `security` - Security/authorization
- `processing` - Business logic
- `data_access` - Data access
- `database` - Databases/storage
- `service` - Microservices

### Relationship Definition

```yaml
relationships:
  - from: string # Required: Source component ID
    to: string # Required: Target component ID
    label: string # Optional: Relationship label
    style: string # Optional: Line style
    bypass: boolean # Optional: Is bypass flow
    annotationId: string # Optional: Annotation reference
```

### Line Styles

Valid values for `style`:

- `solid` - Normal flow (default)
- `dashed` - Alternative/bypass flow
- `dotted` - Optional/conditional flow
- `bold` - Critical flow
- `hidden` - Hidden edge for layout

### Annotation Definition

```yaml
annotations:
  - id: string # Required: Unique annotation ID
    text: string # Required: Annotation text
    attachedTo: string # Optional: Component/relationship ID
    type: string # Optional: Annotation type
    color: string # Optional: Highlight color
```

### Annotation Types

- `security` - Security-related notes
- `performance` - Performance optimization notes
- `reliability` - Reliability/fault tolerance
- `scalability` - Scaling considerations
- `business` - Business logic notes
- `note` - General notes
- `warning` - Warning/caution

---

## Error Handling

### Error Response Format

All errors return HTTP 400 with JSON:

```json
{
  "status": "error",
  "message": "Human-readable error message",
  "errorDetails": "Technical error details"
}
```

### Common Errors

| Error                    | Cause                       | Solution                     |
| ------------------------ | --------------------------- | ---------------------------- |
| "Failed to parse YAML"   | Invalid YAML syntax         | Validate YAML structure      |
| "Component not found"    | Referenced ID doesn't exist | Check component ID spelling  |
| "Invalid component type" | Unknown component type      | Use valid type from list     |
| "Failed to render"       | Graphviz error              | Install Graphviz, check PATH |

---

## Response Formats

### PNG Response

When `returnBase64=false` and no PlantUML return requested:

- Content-Type: `image/png`
- Body: Binary PNG image data

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{"yamlContent": "...", "format": "png"}' \
  --output diagram.png
```

### SVG Response

Similar to PNG but with Content-Type: `image/svg+xml`

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{"yamlContent": "...", "format": "svg"}' \
  --output diagram.svg
```

### JSON Response

When `returnBase64=true` or `returnPlantUML=true`:

- Content-Type: `application/json`
- Body: JSON with `diagramBase64` and/or `plantUMLCode` fields

---

## Rate Limiting

Currently no rate limiting is implemented. For production:

- Implement rate limiting based on IP
- Consider async processing for large diagrams
- Cache identical requests

---

## Performance Tips

1. **Reduce complexity**: Fewer components = faster rendering
2. **Simplify layout**: Use hierarchical layout for best results
3. **Batch requests**: Generate multiple diagrams asynchronously
4. **Cache results**: Cache identical YAML inputs
5. **Monitor**: Track generation times in metrics

---

## Integration Guides

### React Component

```jsx
import { useState } from "react";

export function DiagramGenerator() {
  const [yaml, setYaml] = useState("");
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(false);

  const generate = async () => {
    setLoading(true);
    const res = await fetch("/api/diagram/generate-diagram", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        yamlContent: yaml,
        format: "svg",
        returnBase64: true,
      }),
    });
    const data = await res.json();
    if (data.status === "success") {
      setImage(`data:image/svg+xml;base64,${data.diagramBase64}`);
    }
    setLoading(false);
  };

  return (
    <div>
      <textarea value={yaml} onChange={(e) => setYaml(e.target.value)} />
      <button onClick={generate} disabled={loading}>
        {loading ? "Generating..." : "Generate Diagram"}
      </button>
      {image && <img src={image} alt="Diagram" />}
    </div>
  );
}
```

---

## Changelog

### Version 1.0.0 (Initial Release)

- YAML parsing
- Component types
- Relationship modeling
- PlantUML generation
- PNG/SVG rendering
- Annotation support
- REST API endpoints

---

## Support

- **Issues**: GitHub Issues
- **Documentation**: See README.md
- **Examples**: See src/main/resources/examples/

---

**API Version: 1.0.0**  
**Last Updated**: 2024
