package com.project.backend.model;

import com.google.cloud.firestore.annotation.DocumentId;

// Abstract class representing a Firestore document model
public abstract class Model {
    // Document ID field annotated with @DocumentId to automatically populate the document's ID
    // when the POJO is created from a Firestore document
    @DocumentId
    private String id;

    // Getter for the document ID
    public String getId() {
        return id;
    }

    // Setter for the document ID
    public void setId(String id) {
        this.id = id;
    }
}