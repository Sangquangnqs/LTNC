package com.project.backend.firebase;

import java.io.FileInputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

@Configuration
public class FirebaseConfiguration {
    // Bean for initializing FirebaseApp with credentials from a service account file
    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        // Load service account credentials from a file
        FileInputStream serviceAccount = new FileInputStream("firebase.json");

        // Configure Firebase options with the service account credentials
        FirebaseOptions options = FirebaseOptions.builder()
                                                 .setCredentials(
                                                    GoogleCredentials.fromStream(serviceAccount)
                                                    )
                                                 .setStorageBucket("database-6cded.appspot.com")
                                                 .build();
        // Initialize FirebaseApp with the configured options
        return FirebaseApp.initializeApp(options);
    }

    // Bean for creating a Firestore instance using the initialized FirebaseApp
    @Bean
    public Firestore firestore(FirebaseApp app) {
        // Get Firestore instance associated with the FirebaseApp
        return FirestoreClient.getFirestore(app);
    }
    @Bean
    public StorageClient storage(FirebaseApp app) {
        // Get Storage instance associated with the FirebaseApp
        return StorageClient.getInstance(app);
    }
}
