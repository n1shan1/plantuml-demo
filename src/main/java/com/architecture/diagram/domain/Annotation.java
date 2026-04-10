package com.architecture.diagram.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an annotation that can be attached to components or relationships.
 * Annotations include tactics, security notes, performance hints, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Annotation {
    private String id;
    private String text;
    private String attachedTo; // Component or Relationship ID
    private String type; // security, performance, note, warning, etc.
    private String color;

    public Annotation(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public Annotation(String id, String text, String attachedTo) {
        this(id, text);
        this.attachedTo = attachedTo;
    }
}
