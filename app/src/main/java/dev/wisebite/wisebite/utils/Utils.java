package dev.wisebite.wisebite.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.TimePicker;

import com.firebase.client.Firebase;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.OpenTime;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class Utils {

    private static Menu tempMenu;

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

        return datesToString(firstHour, firstMinute, secondHour, secondMinute);
    }

    public static String parseStartEndDate(Date startDate, Date endDate) {
        int firstHour, firstMinute, secondHour, secondMinute;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        firstHour = calendar.get(Calendar.HOUR_OF_DAY);
        firstMinute = calendar.get(Calendar.MINUTE);

        calendar.setTime(endDate);
        secondHour = calendar.get(Calendar.HOUR_OF_DAY);
        secondMinute = calendar.get(Calendar.MINUTE);

        return datesToString(firstHour, firstMinute, secondHour, secondMinute);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static OpenTime createOpenTimeByTimePicker(TimePicker firstTimePicker, TimePicker secondTimePicker, Integer viewId) {
        int dayOfTheWeek = parseViewIdToDayOfWeek(viewId);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(0);
        startCalendar.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
        startCalendar.set(Calendar.HOUR_OF_DAY, firstTimePicker.getHour());
        startCalendar.set(Calendar.MINUTE, firstTimePicker.getMinute());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(0);
        endCalendar.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
        endCalendar.set(Calendar.HOUR_OF_DAY, secondTimePicker.getHour());
        endCalendar.set(Calendar.MINUTE, secondTimePicker.getMinute());

        return OpenTime.builder()
                    .startDate(startCalendar.getTime())
                    .endDate(endCalendar.getTime())
                    .build();
    }

    public static void setTempMenu(Menu menu) {
        tempMenu = menu;
    }

    public static Menu getTempMenu() {
        return tempMenu;
    }

    public static String toStringWithTwoDecimals(double d) {
        d = Math.round(d * 100);
        d = d/100;
        return String.valueOf(d);
    }

    private static String datesToString(int firstHour, int firstMinute, int secondHour, int secondMinute) {
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

    private static int parseViewIdToDayOfWeek(Integer viewId) {
        switch (viewId) {
            case R.id.monday_date_picker:
                return Calendar.MONDAY;
            case R.id.tuesday_date_picker:
                return Calendar.TUESDAY;
            case R.id.wednesday_date_picker:
                return Calendar.WEDNESDAY;
            case R.id.thursday_date_picker:
                return Calendar.THURSDAY;
            case R.id.friday_date_picker:
                return Calendar.FRIDAY;
            case R.id.saturday_date_picker:
                return Calendar.SATURDAY;
            case R.id.sunday_date_picker:
                return Calendar.SUNDAY;
            default:
                return 0;
        }
    }

}
