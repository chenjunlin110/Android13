package com.example.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Photo album model for the Android port.
 * Keeps only data/logic (no JavaFX fields).
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private final List<Photo> photos = new ArrayList<>();

    public Album(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    /** Add photo if not already present. */
    public void addPhoto(Photo photo) {
        if (photo != null && !photos.contains(photo)) {
            photos.add(photo);
        }
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public int getPhotoCount() {
        return photos.size();
    }

    public boolean containsPhoto(Photo photo) {
        return photos.contains(photo);
    }

    /**
     * Move a photo from this album to another.
     * No-op if invalid or target already contains the photo.
     */
    public void movePhotoTo(Photo photo, Album target) {
        if (photo == null || target == null) {
            return;
        }
        if (!photos.contains(photo)) {
            return;
        }
        if (target.containsPhoto(photo)) {
            return;
        }
        photos.remove(photo);
        target.addPhoto(photo);
    }

    @Override
    public String toString() {
        return name + " (" + getPhotoCount() + " photos)";
    }
}
