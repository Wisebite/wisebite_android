package dev.wisebite.wisebite.firebase;

/**
 * Created by albert on 24/04/17.
 * @author albert
 */
public interface FirebaseStorageService {

    String upload(String path);

    void download(String path);

}
