package dev.wisebite.wisebite.firebase;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

/**
 * Created by albert on 24/04/17.
 * @author albert
 */
public interface FirebaseStorageService {

    void upload(Uri path, Activity activity);

    void download(String path);

}
