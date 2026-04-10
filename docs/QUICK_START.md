# Quick Start Guide

## 1. Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Graphviz

### Install Graphviz

```bash
# macOS
brew install graphviz

# Ubuntu/Debian
sudo apt-get install graphviz

# CentOS/RHEL
sudo yum install graphviz
```

## 2. Build the Project

```bash
cd plantuml-demo
mvn clean package
```

## 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 4. First Request

Get the example YAML:

```bash
curl http://localhost:8080/api/diagram/example-yaml
```

Generate a diagram:

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "system:\n  name: \"Test System\"\n  components:\n    - id: client\n      type: external\n      label: \"Client\"\n    - id: server\n      type: layer\n      label: \"Server\"\n    - id: db\n      type: database\n      label: \"Database\"\n  relationships:\n    - from: client\n      to: server\n    - from: server\n      to: db",
    "format": "png",
    "returnBase64": true
  }' > response.json

cat response.json
```

## 5. Save Diagram to File

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "...",
    "format": "png"
  }' > diagram.png

# View the image
open diagram.png
```

## 6. Using Example Files

Place your YAML files in `src/main/resources/examples/` and send them:

```bash
YAML_CONTENT=$(cat src/main/resources/examples/order-system.yaml | jq -Rs .)

curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d "{
    \"yamlContent\": $YAML_CONTENT,
    \"format\": \"svg\",
    \"returnPlantUML\": true
  }"
```

## 7. Web UI Integration

Save the base64 image and display in HTML:

```html
<!DOCTYPE html>
<html>
  <head>
    <title>Architecture Diagram Viewer</title>
  </head>
  <body>
    <h1>Architecture Diagram</h1>
    <img id="diagram" src="" style="max-width: 100%;" />
    <script>
      // Fetch and display diagram
      fetch("/api/diagram/generate-diagram", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          yamlContent: `system:\n  name: "Test"`,
          format: "svg",
          returnBase64: true,
        }),
      })
        .then((r) => r.json())
        .then((data) => {
          if (data.status === "success") {
            document.getElementById("diagram").src =
              "data:image/svg+xml;base64," + data.diagramBase64;
          }
        });
    </script>
  </body>
</html>
```

## 8. Troubleshooting

### Issue: "Graphviz not found"

Solution: Set `app.plantuml.graphviz-path` in `application.yml`:

```yaml
app:
  plantuml:
    graphviz-path: /usr/local/bin/dot # macOS
    # graphviz-path: /usr/bin/dot      # Linux
```

### Issue: "Failed to parse YAML"

Check your YAML syntax:

- Use proper indentation (2 spaces)
- Quote strings with special characters
- Validate at https://yaml-online-parser.appspot.com/

### Issue: Long diagram rendering time

- Reduce number of components
- Simplify relationships
- Use simpler layout strategy

## 9. Next Steps

- Read the full [README.md](README.md)
- Check [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- Explore example YAML files in `src/main/resources/examples/`
- Customize component types and colors
- Build a web UI wrapper

## 10. Integration Examples

### Python

```python
import requests
import base64

def generate_diagram(yaml_content):
    response = requests.post(
        'http://localhost:8080/api/diagram/generate-diagram',
        json={
            'yamlContent': yaml_content,
            'format': 'png',
            'returnBase64': True
        }
    )
    return response.json()

# Usage
yaml = """
system:
  name: "My System"
  components:
    - id: c1
      type: external
      label: "Client"
"""

result = generate_diagram(yaml)
if result['status'] == 'success':
    print(f"Generated in {result['generationTimeMs']}ms")
```

### Node.js

```javascript
const axios = require("axios");

async function generateDiagram(yamlContent) {
  const response = await axios.post(
    "http://localhost:8080/api/diagram/generate-diagram",
    {
      yamlContent,
      format: "svg",
      returnBase64: true,
    },
  );
  return response.data;
}

// Usage
const yaml = `system:\n  name: "Test"`;
generateDiagram(yaml).then((result) => {
  console.log(result);
});
```

---

**Happy diagramming! 🎨**
