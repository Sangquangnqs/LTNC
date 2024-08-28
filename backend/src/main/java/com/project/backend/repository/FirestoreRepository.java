package com.project.backend.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.project.backend.exceptionhandler.ExceptionLog;
import com.project.backend.firebase.CollectionName;
import com.project.backend.model.Model;

@Repository
public class FirestoreRepository {
    // Firestore instance for database operations
    @Autowired
    private Firestore repository;
    // Exception logging utility
    @Autowired
    private ExceptionLog exceptionLog;

    // Method to get a Firestore collection reference based on the model class
    @Nullable
    public CollectionReference getCollection(Class<? extends Model> type) {
        // Check if the model class has a CollectionName annotation
        return type.isAnnotationPresent(CollectionName.class)
                ? repository.collection(type.getAnnotation(CollectionName.class).value())
                : null;
    }

    // Method to save a model object with specific documentID to Firestore and return the document ID
    public <T extends Model> String saveDocument(T model, String documentId) {
        // Check if the model is null
        if (model == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Get the collection reference for the model
        CollectionReference collection = getCollection(model.getClass());
        if (collection != null) {
            Object object = getDocumentById(model.getClass(), documentId);
            if (object != null) {
                exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
                return null;
            }
            // Add the model to the collection and get the document reference
            DocumentReference documentReference = collection.document(documentId);
            // model.setId(documentId);
            ApiFuture<WriteResult> api = documentReference.set(model);
            try {
                // Return the document ID
                return api.get().getClass().getName();
            } catch (Exception e) {
                exceptionLog.log(e, this.getClass().getName());
                return null;
            }
        } else {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
    }
    public <T extends Model> String saveDocument(T model) {
        // Check if the model is null
        if (model == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Get the collection reference for the model
        CollectionReference collection = getCollection(model.getClass());
        if (collection != null) {
            // Add the model to the collection and get the document reference
            ApiFuture<DocumentReference> api = collection.add(model);
            try {
                // Return the document ID
                return api.get().getId();
            } catch (Exception e) {
                exceptionLog.log(e, this.getClass().getName());
                return null;
            }
        } else {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
    }

    // Method to save a model object to Firestore using a map and return the
    // document ID
    public String saveDocument(Class<? extends Model> type, Map<String, Object> model) {
        // Check if the model map is null
        if (model == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        if (collection != null) {
            // Add the model map to the collection and get the document reference
            ApiFuture<DocumentReference> api = collection.add(model);
            try {
                // Return the document ID
                return api.get().getId();
            } catch (Exception e) {
                exceptionLog.log(e, this.getClass().getName());
                return null;
            }
        } else {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
    }

    // Method to get a document by ID from Firestore
    @Nullable
    public DocumentSnapshot getDocumentById(Class<? extends Model> type, String id) {
        // Check if the ID is null
        if (id == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        if (collection != null) {
            // Get the document reference by ID
            DocumentReference documentReference = collection.document(id);
            try {
                // Get the document snapshot
                DocumentSnapshot documentSnapshot = documentReference.get().get();
                // Return the document snapshot if it exists
                return documentSnapshot.exists() ? documentSnapshot : null;
            } catch (Exception e) {
                exceptionLog.log(e);
                return null;
            }
        } else {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
    }

    @Nullable
    public List<DocumentSnapshot> getAllDocuments(Class<? extends Model> type){
        CollectionReference collection = getCollection(type);
        ApiFuture<QuerySnapshot> query = collection.get();
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (Exception e) {
            exceptionLog.log(e);
        }
        List<DocumentSnapshot> documentSnapshots = new ArrayList<>();
        if (querySnapshot != null) {
            List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshot.getDocuments();
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                documentSnapshots.add(queryDocumentSnapshot);
            }
        }
        return documentSnapshots;
    }
    // Method to get all documents by a specific field value from Firestore
    @Nullable
    public List<DocumentSnapshot> getAllDocumentsByField(Class<? extends Model> type,
            String fieldName,
            Object value) {
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        if (collection == null || fieldName == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Create a query to find documents with the specified field value
        Query query = collection.whereEqualTo(fieldName, value);
        try {
            // Execute the query and get the documents
            ApiFuture<QuerySnapshot> futureQuerySnapshot = query.get();
            List<DocumentSnapshot> snapshots = new ArrayList<>();
            QuerySnapshot querySnapshot = futureQuerySnapshot.get();
            querySnapshot.getDocuments().forEach(doc -> snapshots.add(doc));
            return snapshots;
        } catch (Exception e) {
            exceptionLog.log(e);
            return null;
        }
    }

    // Method to get documents by a specific field value with a limit
    public List<DocumentSnapshot> getDocumentsByField(Class<? extends Model> type,
            String fieldName,
            Object value,
            int limit) {
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        if (collection == null || fieldName == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Create a query to find documents with the specified field value and limit
        Query query = collection.whereEqualTo(fieldName, value).limit(limit);
        ApiFuture<QuerySnapshot> apiQuerySnapshot = query.get();
        List<DocumentSnapshot> snapshots = new ArrayList<DocumentSnapshot>();
        try {
            QuerySnapshot querySnapshot = apiQuerySnapshot.get();
            querySnapshot.getDocuments().forEach(doc -> snapshots.add(doc));
            return snapshots;
        } catch (Exception e) {
            exceptionLog.log(e);
            return null;
        }
    }

    // Method to get documents by a specific field value with ordering and a limit
    public List<DocumentSnapshot> getDocumentsByField(Class<? extends Model> type,
            String fieldName,
            Object value,
            String orderBy,
            int limit) {
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        // Check if the collection, orderBy, or fieldName is null
        if (collection == null || orderBy == null || fieldName == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Create a query to find documents with the specified field value, order, and
        // limit
        Query query = collection.orderBy(orderBy).whereEqualTo(fieldName, value).limit(limit);
        // Execute the query and get the documents
        ApiFuture<QuerySnapshot> apiQuerySnapshot = query.get();
        List<DocumentSnapshot> snapshots = new ArrayList<DocumentSnapshot>();
        try {
            QuerySnapshot querySnapshot = apiQuerySnapshot.get();
            querySnapshot.getDocuments().forEach(doc -> snapshots.add(doc));
            return snapshots;
        } catch (Exception e) {
            exceptionLog.log(e);
            return null;
        }
    }

    // Method to get documents by a specific field value with ordering, pagination,
    // and a limit
    public List<DocumentSnapshot> getDocumentsByField(Class<? extends Model> type,
            String fieldName,
            Object value,
            DocumentSnapshot lastVisible,
            String orderBy,
            int limit) {
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        // Check if the collection, orderBy, fieldName, or lastVisible is null
        if (collection == null || orderBy == null || fieldName == null || lastVisible == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return null;
        }
        // Create a query to find documents with the specified field value, order,
        // pagination, and limit
        Query query = collection.orderBy(orderBy).whereEqualTo(fieldName, value).startAfter(lastVisible).limit(limit);
        // Execute the query and get the documents
        ApiFuture<QuerySnapshot> apiQuerySnapshot = query.get();
        List<DocumentSnapshot> snapshots = new ArrayList<DocumentSnapshot>();
        try {
            QuerySnapshot querySnapshot = apiQuerySnapshot.get();
            querySnapshot.getDocuments().forEach(doc -> snapshots.add(doc));
            return snapshots;
        } catch (Exception e) {
            exceptionLog.log(e);
            return null;
        }
    }

    // Method to delete a document by ID from Firestore
    public boolean deleteDocumentById(Class<? extends Model> type, String id) {
        // Check if the ID is null
        if (id == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return false;
        }
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        if (collection != null) {
            // Get the document reference by ID
            DocumentReference documentReference = collection.document(id);
            // Delete the document and get the result
            ApiFuture<WriteResult> result = documentReference.delete();
            try {
                result.get();
                return true;
            } catch (Exception e) {
                exceptionLog.log(e);
                return false;
            }
        } else {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return false;
        }
    }

    // Method to update a document by ID with a model object
    public <T extends Model> boolean updateDocumentById(T model) {
        // Check if the model is null
        if (model == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return false;
        }
        // Get the collection reference for the model class
        CollectionReference collection = getCollection(model.getClass());
        // Check if the collection is not null and the model has an ID
        if (collection != null && model.getId() != null) {
            // Get the document reference by ID
            DocumentReference documentReference = collection.document(model.getId());
            // Update the document with the model object and get the result
            ApiFuture<WriteResult> result = documentReference.set(model);
            try {
                result.get();
                return true;
            } catch (Exception e) {
                exceptionLog.log(e);
                return false;
            }
        } else {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return false;
        }
    }

    // Method to update a document by ID with specific attributes
    public <T extends Model> boolean updateDocumentById(Class<? extends Model> type, String id,
            Map<String, Object> attributes) {
        // Check if the ID or attributes map is null
        if (id == null || attributes == null) {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return false;
        }
        // Get the collection reference for the model type
        CollectionReference collection = getCollection(type);
        if (collection != null) {
            // Get the document reference by ID
            DocumentReference documentReference = collection.document(id);
            // Update the document with the specified attributes and get the result
            ApiFuture<WriteResult> result = documentReference.update(attributes);
            try {
                result.get();
                return true;
            } catch (Exception e) {
                exceptionLog.log(e);
                return false;
            }
        } else {
            exceptionLog.log(new IllegalArgumentException(this.getClass().getName()));
            return false;
        }
    }

}
