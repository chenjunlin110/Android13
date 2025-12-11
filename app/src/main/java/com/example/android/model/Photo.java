package com.example.android.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Photo model for Android version.
 * Stores the image URI as string and its filename for caption/display.
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String uriString;
    private final String filename;
    private final List<Tag> tags = new ArrayList<>();

    public Photo(String uriString, String filename) {
        this.uriString = uriString;
        this.filename = filename;
    }

    public String getUriString() {
        return uriString;
    }

    public String getFilename() {
        return filename;
    }

    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Add a tag if it does not already exist (case-insensitive).
     * @return true if added, false if duplicate
     */
    public boolean addTag(Tag tag) {
        if (tag == null) return false;
        if (tags.contains(tag)) {
            return false;
        }
        tags.add(tag);
        return true;
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public boolean hasTag(Tag.TagType type, String value) {
        if (value == null) return false;
        String norm = value.trim().toLowerCase(Locale.ROOT);
        for (Tag t : tags) {
            if (t.getType() == type && t.getNormalizedValue().startsWith(norm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Basic existence check if the underlying file is still present (optional).
     */
    public boolean exists() {
        try {
            return new File(uriString).exists();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Photo other = (Photo) obj;
        return uriString.equals(other.uriString);
    }

    @Override
    public int hashCode() {
        return uriString.hashCode();
    }

    @Override
    public String toString() {
        return filename;
    }
}
