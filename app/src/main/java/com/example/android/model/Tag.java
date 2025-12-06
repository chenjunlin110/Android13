package com.example.android.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * Tag with fixed types: person or location.
 * Values are stored case-insensitively.
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TagType {
        PERSON, LOCATION
    }

    private final TagType type;
    private final String value;       // original casing for display
    private final String normalized;  // lower-case for comparisons

    public Tag(TagType type, String value) {
        this.type = type;
        this.value = value == null ? "" : value.trim();
        this.normalized = this.value.toLowerCase(Locale.ROOT);
    }

    public TagType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getNormalizedValue() {
        return normalized;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag other = (Tag) obj;
        return type == other.type && normalized.equals(other.normalized);
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + normalized.hashCode();
    }

    @Override
    public String toString() {
        return type.name().toLowerCase(Locale.ROOT) + "=" + value;
    }
}
