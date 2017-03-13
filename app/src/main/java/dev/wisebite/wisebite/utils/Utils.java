package dev.wisebite.wisebite.utils;

import com.firebase.client.Firebase;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class Utils {

    public static String generateId() {
        return new Firebase("https://wisebite-f7a53.firebaseio.com/").push().getKey();
    }

}
