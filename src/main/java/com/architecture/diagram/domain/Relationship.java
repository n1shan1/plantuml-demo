package com.architecture.diagram.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a relationship between two components.
 * Relationships can be normal flows or bypass flows.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relationship {
    private String from;
    private String to;
    private String label;
    private LineStyle style;
    private boolean isBypass;
    private String annotationId;

    public Relationship(String from, String to) {
        this.from = from;
        this.to = to;
        this.style = LineStyle.SOLID;
        this.isBypass = false;
    }

    public Relationship(String from, String to, String label) {
        this(from, to);
        this.label = label;
    }

    public Relationship(String from, String to, String label, LineStyle style) {
        this(from, to, label);
        this.style = style;
    }
}
