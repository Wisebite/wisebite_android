package dev.wisebite.wisebite.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by albert on 24/04/17.
 *
 * @author albert
 */
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    private FirebaseStorage storage;
    private StorageReference reference;

    public FirebaseStorageServiceImpl() {
        this.storage = FirebaseStorage.getInstance("gs://wisebite-f7a53.appspot.com");
        this.reference = storage.getReference();
    }

    @Override
    public String upload(String path) {
        final String[] uploadURL = {null};
        InputStream stream;
        try {
            stream = new FileInputStream(path);
            UploadTask uploadTask = reference.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Upload fails: ", e.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    assert taskSnapshot.getDownloadUrl() != null;
                    uploadURL[0] = taskSnapshot.getDownloadUrl().toString();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return uploadURL[0];
    }

    @Override
    public void download(String path) {

    }
}
