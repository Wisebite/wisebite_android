package dev.wisebite.wisebite.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.TimePicker;

import com.firebase.client.Firebase;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class Utils {

    public static String generateId() {
        return new Firebase("https://wisebite-f7a53.firebaseio.com/").push().getKey();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static String parseStartEndDate(TimePicker firstTimePicker, TimePicker secondTimePicker) {
        int firstHour, firstMinute, secondHour, secondMinute;
        firstHour = firstTimePicker.getHour();
        firstMinute = firstTimePicker.getMinute();
        secondHour = secondTimePicker.getHour();
        secondMinute = secondTimePicker.getMinute();

        String parsed = "";
        if (firstHour < 10) parsed += '0';
        parsed += String.valueOf(firstHour) + ':';
        if (firstMinute < 10) parsed += '0';
        parsed += String.valueOf(firstMinute);

        parsed += " - ";

        if (secondHour < 10) parsed += '0';
        parsed += String.valueOf(secondHour) + ':';
        if (secondMinute < 10) parsed += '0';
        parsed += String.valueOf(secondMinute);

        return parsed;
    }
}
