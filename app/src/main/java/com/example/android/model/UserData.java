package com.example.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Single-user container for albums and search logic.
 */
public class UserData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Album> albums = new ArrayList<>();

    public List<Album> getAlbums() {
        return albums;
    }

    public Album getAlbum(String name) {
        if (name == null) return null;
        for (Album a : albums) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    public boolean addAlbum(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        if (getAlbum(name) != null) return false;
        albums.add(new Album(name.trim()));
        return true;
    }

    public boolean renameAlbum(String oldName, String newName) {
        Album target = getAlbum(oldName);
        if (target == null) return false;
        if (newName == null || newName.trim().isEmpty()) return false;
        // disallow duplicate names
        Album dup = getAlbum(newName);
        if (dup != null && dup != target) return false;
        target.setName(newName.trim());
        return true;
    }

    public boolean removeAlbum(String name) {
        Album a = getAlbum(name);
        if (a == null) return false;
        return albums.remove(a);
    }

    /**
     * Search photos across all albums with optional 2-tag AND/OR logic.
     * Values compared case-insensitively; tag types limited to person/location.
     */
    public List<Photo> search(Tag.TagType type1, String value1,
                              String connector, Tag.TagType type2, String value2) {
        List<Photo> results = new ArrayList<>();
        if (type1 == null || value1 == null) {
            return results;
        }
        String norm1 = value1.trim().toLowerCase(Locale.ROOT);
        String norm2 = value2 == null ? null : value2.trim().toLowerCase(Locale.ROOT);
        String op = connector == null ? "" : connector.trim().toUpperCase(Locale.ROOT);

        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                boolean m1 = photo.hasTag(type1, norm1);
                boolean match;
                if (type2 == null || norm2 == null || norm2.isEmpty()) {
                    match = m1;
                } else {
                    boolean m2 = photo.hasTag(type2, norm2);
                    match = "AND".equals(op) ? (m1 && m2) : (m1 || m2); // default OR
                }
                if (match && !results.contains(photo)) {
                    results.add(photo);
                }
            }
        }
        return results;
    }

    /**
     * Collect distinct tag values (case-insensitive unique) for auto-complete.
     */
    public List<String> getTagValues(Tag.TagType type) {
        Set<String> seen = new HashSet<>();
        List<String> values = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    if (tag.getType() == type) {
                        String norm = tag.getNormalizedValue();
                        if (seen.add(norm)) {
                            values.add(tag.getValue());
                        }
                    }
                }
            }
        }
        return values;
    }

    /**
     * Auto-complete values by prefix (case-insensitive).
     */
    public List<String> autocomplete(Tag.TagType type, String prefix) {
        if (prefix == null) return new ArrayList<>();
        String normPrefix = prefix.trim().toLowerCase(Locale.ROOT);
        return getTagValues(type).stream()
                .filter(v -> v.toLowerCase(Locale.ROOT).startsWith(normPrefix))
                .collect(Collectors.toList());
    }
}
