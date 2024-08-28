package com.project.backend.security;

import java.util.List;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.project.backend.exceptionhandler.ExceptionLog;
import com.project.backend.repository.FirestoreRepository;

@Service
public class BackendDetailsService {
    // Autowired instance of FirestoreRepository for interacting with Firestore
    @Autowired
    private FirestoreRepository repository;

    // Autowired instance of ExceptionLog for logging exceptions
    @Autowired
    private ExceptionLog exceptionLog;

    // Method to load user details by email from Firestore
    public AuthenticationDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        // Get the Firestore collection reference for AuthenticationDetails
        CollectionReference collection = repository.getCollection(AuthenticationDetails.class);
        // Check if the collection or email is null
        if (collection == null || email == null) {
            // Log the exception and return null if the collection or email is null
            exceptionLog.log(new UsernameNotFoundException(this.getClass().getName()));
            return null;
        }
        try {
            // Query Firestore for a document with the specified email
            Query query = collection.whereEqualTo("email", email)
                                    .limit(1);
            // Execute the query and get the result
            QuerySnapshot querySnapshot = query.get()
                                               .get();
            // Retrieve the list of document snapshots from the query result
            List<QueryDocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
            // Check if the query returned any documents
            if (!documentSnapshots.isEmpty()) {
                // Convert the first document snapshot to an AuthenticationDetails object and return it
                return documentSnapshots.get(0)
                                        .toObject(AuthenticationDetails.class);
            } else {
                // Log the exception and return null if no document was found
                exceptionLog.log(new UsernameNotFoundException(this.getClass().getName()));
                return null;
            }
        } catch (Exception e) {
            // Log any exceptions that occur during the process
            exceptionLog.log(e);
            return null;
        }
    }
    @Nullable
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext()
                                         .getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getName();
    }
}
