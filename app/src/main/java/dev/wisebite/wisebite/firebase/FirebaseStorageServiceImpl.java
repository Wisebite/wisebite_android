package dev.wisebite.wisebite.firebase;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dev.wisebite.wisebite.activity.EditUserActivity;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.Preferences;

/**
 * Created by albert on 24/04/17.
 *
 * @author albert
 */
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    private StorageReference reference;
    private UserService userService;

    public FirebaseStorageServiceImpl(Context context) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://wisebite-f7a53.appspot.com");
        this.reference = storage.getReference();
        this.userService = ServiceFactory.getUserService(context);
    }

    @Override
    public void upload(Uri path, final Activity activity) {
        if (path != null) {
            String[] split = path.toString().split("/");
            String name = split[split.length - 1];
            StorageReference childRef = reference.child(name);

            UploadTask uploadTask = childRef.putFile(path);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    assert taskSnapshot.getDownloadUrl() != null;
                    userService.editImage(Preferences.getCurrentUserEmail(),
                            taskSnapshot.getDownloadUrl().toString());
                    if (activity instanceof EditUserActivity) {
                        EditUserActivity editUserActivity = (EditUserActivity) activity;
                        editUserActivity.processDialog.hide();
                    }
                    activity.onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Error uploading image", e.getMessage());
                }
            });
        }
    }

}
