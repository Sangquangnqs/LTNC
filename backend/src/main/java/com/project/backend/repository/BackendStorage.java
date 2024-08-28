package com.project.backend.repository;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Bucket.BlobWriteOption;
import com.google.firebase.cloud.StorageClient;
import com.project.backend.exceptionhandler.ExceptionLog;

/**
 * Provides methods for interacting with a configured storage backend, such as
 * Google Cloud Storage or Firebase Storage.
 *
 * This class provides methods for saving, deleting, retrieving, and updating
 * file blobs in the configured storage backend.
 */
@Repository
public class BackendStorage {
    @Autowired
    private StorageClient storage;
    @Autowired
    private ExceptionLog exceptionLog;

    /**
     * Saves a file blob to the configured storage backend.
     *
     * @param file The file to be saved.
     * @param path The path where the file should be saved, as a list of directory
     *             names.
     * @return {@code true} if the file was saved successfully, {@code false}
     *         otherwise.
     */
    public boolean saveBlob(MultipartFile file, List<String> path) {
        StringJoiner joiner = new StringJoiner("/");
        path.forEach(p -> joiner.add(p));
        try {
            storage.bucket()
                    .create(joiner.toString(),
                            file.getInputStream(),
                            BlobWriteOption.doesNotExist());
            return true;
        } catch (Exception e) {
            exceptionLog.log(e);
            return false;
        }
    }

    /**
     * Deletes a file blob from the configured storage backend.
     *
     * @param path The path where the file is stored, as a list of directory names.
     * @return {@code true} if the file was deleted successfully, {@code false}
     *         otherwise.
     */
    public boolean deleteBlob(List<String> path) {
        Blob blob = this.getBlob(path);
        if (blob == null) {
            return false;
        }
        blob.delete();
        return true;
    }

    /**
     * Retrieves a file from the configured storage backend.
     *
     * @param path The path where the file is stored, as a list of directory names.
     * @return A {@link Map.Entry} containing the file name and the file contents as
     *         a {@link Resource}, or {@code null} if the file could not be
     *         retrieved.
     */
    @Nullable
    public Map.Entry<String, Resource> getFile(List<String> path) {
        if (path == null || path.isEmpty()) {
            exceptionLog.log(new NullPointerException(this.getClass().getName()));
            return null;
        }
        StringJoiner joiner = new StringJoiner("/");
        path.forEach(p -> joiner.add(p));
        Blob blob = storage.bucket()
                .get(joiner.toString());
        try {
            return Map.entry(blob.getName(), new ByteArrayResource(blob.getContent()));
        } catch (Exception e) {
            exceptionLog.log(e, this.getClass().getName());
            return null;
        }
    }

    /**
     * Retrieves a blob from the configured storage backend.
     *
     * @param path The path where the file is stored, as a list of directory names.
     * @return The blob at the specified path, or {@code null} if the blob could not
     *         be retrieved.
     */
    @Nullable
    public Blob getBlob(List<String> path) {
        if (path == null || path.isEmpty()) {
            exceptionLog.log(new NullPointerException(this.getClass().getName()));
            return null;
        }
        StringJoiner joiner = new StringJoiner("/");
        path.forEach(p -> joiner.add(p));
        Blob blob = storage.bucket()
                .get(joiner.toString());
        return blob;
    }

    /**
     * Updates a blob in the configured storage backend with the provided file.
     *
     * @param file The file to be uploaded.
     * @param path The path where the file should be stored, as a list of directory
     *             names.
     * @return {@code true} if the file was successfully uploaded, {@code false}
     *         otherwise.
     */
    public boolean updateBlob(MultipartFile file, List<String> path) {
        StringJoiner joiner = new StringJoiner("/");
        path.forEach(p -> joiner.add(p));
        try {
            storage.bucket()
                    .create(joiner.toString(),
                            file.getInputStream())
                    .createAcl(Acl.of(User.ofAllUsers(), Role.READER));
            return true;
        } catch (Exception e) {
            exceptionLog.log(e, this.getClass().getName());
            return false;
        }
    }
}
