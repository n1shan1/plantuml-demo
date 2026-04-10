# Web UI Guide - Architecture Diagram Generator

## Overview

A modern, responsive web interface for generating architecture diagrams. Access it at **http://localhost:8080** after starting the application.

## Features

### 🎨 Beautiful Interface

- Split-panel design with YAML editor on left, diagram viewer on right
- Dark/light theme support
- Responsive layout (works on desktop, tablet, mobile)
- Real-time feedback with success/error messages

### 📝 YAML Editor

- Syntax-highlighted YAML input
- One-click template loading (3 examples included)
- Copy/paste support
- Auto-formatting hints

### 🎯 Quick Templates

Click buttons to instantly load example architectures:

1. **Simple** - Basic 3-tier web app
2. **E-Commerce** - Full microservices platform
3. **Order** - Order management system

### 📊 Diagram Viewer

Three tabs for different views:

1. **Diagram** - Visual architecture diagram
   - Zoom and pan support
   - Click to fullscreen
   - Download as PNG or SVG

2. **PlantUML Code** - Raw DSL code
   - Copy to clipboard
   - Export for external rendering
   - Syntax reference

3. **Info** - Metadata about the diagram
   - Generation time
   - Component count
   - Relationship count
   - Format used

### 🎛️ Controls

**Format Options:**

- PNG (compressed raster image)
- SVG (scalable vector graphic)

**Checkboxes:**

- ✓ Base64 - Embed image in responses
- ✓ Show Code - Display PlantUML DSL

**Action Buttons:**

- ✨ Generate Diagram - Create from YAML
- ⬇️ Download - Save diagram to file
- 📋 Copy - Copy diagram to clipboard

## Usage Workflow

### 1. Start Application

```bash
cd /Users/nishantdev/Downloads/plantuml-demo
mvn spring-boot:run
```

Application starts at `http://localhost:8080`

### 2. Open Web UI

Open browser to: **http://localhost:8080**

You'll see the Architecture Diagram Generator interface.

### 3. Load Template (Optional)

Click one of the template buttons:

- Simple, E-Commerce, or Order

YAML will auto-populate and diagram generates automatically.

### 4. Customize YAML

Edit the YAML in the left panel:

```yaml
system:
  name: "My Custom System"
  components:
    - id: client
      type: external
      label: "Client"
    - id: server
      type: layer
      label: "Server"
  relationships:
    - from: client
      to: server
```

### 5. Generate Diagram

Click **✨ Generate Diagram** button

The diagram appears in the right panel within 1-3 seconds.

### 6. Export

**Download Options:**

1. Click **⬇️ Download** to save as PNG/SVG
2. Click **📋 Copy** to copy to clipboard
3. View **PlantUML Code** tab for DSL code

## YAML Format Guide

### Minimal Example

```yaml
system:
  name: "My App"
  components:
    - id: web
      type: external
      label: "Web Client"
    - id: api
      type: layer
      label: "API Server"
    - id: db
      type: database
      label: "Database"
  relationships:
    - from: web
      to: api
    - from: api
      to: db
```

### Full Example with Annotations

```yaml
system:
  name: "E-Commerce Platform"
  description: "Complete system architecture"

  components:
    - id: clients
      type: external
      label: "Clients"
      color: "#E8F4F8"

    - id: auth
      type: security
      label: "Auth Service"
      color: "#F8D7DA"

    - id: products
      type: processing
      label: "Products"

    - id: db
      type: database
      label: "PostgreSQL"

  relationships:
    - from: clients
      to: auth
      label: "Login"

    - from: auth
      to: products
      label: "Check permissions"

    - from: products
      to: db
      label: "Query"

    - from: auth
      to: db
      label: "User check"
      bypass: true
      style: dashed

  annotations:
    - id: PERF
      text: "Bypass improves performance"
    - id: SEC
      text: "JWT authentication"
```

### Component Types

| Type          | Icon Color | Use Case                         |
| ------------- | ---------- | -------------------------------- |
| `external`    | Light Blue | External users, external systems |
| `layer`       | Blue       | HTTP API, web server             |
| `security`    | Red        | Auth, security, validation       |
| `processing`  | Teal       | Business logic, services         |
| `data_access` | Purple     | Repository, data layer           |
| `database`    | Yellow     | DB, storage, cache               |
| `service`     | Green      | Microservices                    |

### Line Styles

| Style             | Visual | Meaning               |
| ----------------- | ------ | --------------------- |
| `solid` (default) | —>     | Normal flow           |
| `dashed`          | ·>     | Bypass, alternative   |
| `dotted`          | ···>   | Optional, conditional |
| `bold`            | ==>    | Critical              |

## Tips & Tricks

### Performance

- Keep components under 20 for clarity
- Use bypass flows sparingly
- Group related services

### Naming

- Use descriptive component IDs (no spaces)
- Labels can have spaces and special chars
- Use abbreviations in IDs: `auth_svc` → "Auth Service"

### Colors

- Use hex colors: `#RRGGBB`
- Default colors auto-assigned by type
- Override with custom `color` field

### Export

- **PNG** for presentations, documents, sharing
- **SVG** for web, editing, scaling
- **Code** for documentation, version control

## Keyboard Shortcuts

| Key                | Action                          |
| ------------------ | ------------------------------- |
| `Cmd/Ctrl + Enter` | Generate diagram (in editor)    |
| `Cmd/Ctrl + C`     | Copy code (when tab is focused) |
| `Click image`      | Fullscreen view                 |
| `Escape`           | Exit fullscreen                 |

## Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Mobile browsers (iOS Safari, Chrome Mobile)

## Troubleshooting

### Diagram not generating

1. Check YAML syntax (most common)
   - Use 2-space indentation
   - Verify `system:`, `components:`, `relationships:` are present

2. Check component references
   - Ensure relationship `from` and `to` IDs exist
   - IDs are case-sensitive

3. Check browser console
   - Press F12, check Console tab for errors
   - Look for network errors in Network tab

### CORS Errors

If you see "CORS error":

1. Ensure backend is running: `http://localhost:8080`
2. Check browser console: F12 → Network tab
3. Verify API endpoint is responding

### Image not displaying

1. Try different format (PNG vs SVG)
2. Check "Base64" checkbox
3. Reduce number of components
4. Check browser cache (Cmd/Ctrl + Shift + R to hard refresh)

## Advanced Usage

### Batch Generation

You can use the API directly:

```bash
curl -X POST http://localhost:8080/api/diagram/generate-diagram \
  -H "Content-Type: application/json" \
  -d '{
    "yamlContent": "system:\n  name: \"Test\"\n...",
    "format": "png",
    "returnBase64": true
  }'
```

### Programmatic Access

From JavaScript (in your own web app):

```javascript
const response = await fetch(
  "http://localhost:8080/api/diagram/generate-diagram",
  {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      yamlContent: yaml,
      format: "svg",
      returnBase64: true,
    }),
  },
);

const data = await response.json();
document.querySelector("img").src =
  `data:image/svg+xml;base64,${data.diagramBase64}`;
```

## Customization

### Modifying the UI

Edit `/src/main/resources/static/index.html`:

- Change colors in `<style>` section
- Modify templates in `templates` object
- Add new buttons and features in JavaScript

### Adding New Templates

In `index.html`, add to `templates` object:

```javascript
templates.mytemplate = `
system:
  name: "My Template"
  ...
`;
```

Then add button:

```html
<button
  class="btn btn-secondary btn-small"
  onclick="loadTemplate('mytemplate')"
>
  My Template
</button>
```

### Custom Styling

Modify CSS variables or theme colors in `<style>` block.

## Support

- Found a bug? Check `/API_DOCUMENTATION.md`
- Need help? See `/QUICK_START.md`
- Want to extend? Read `/DEVELOPER_GUIDE.md`

---

**Happy diagramming! 🎨**

Version: 1.0.0  
Last Updated: April 9, 2026
