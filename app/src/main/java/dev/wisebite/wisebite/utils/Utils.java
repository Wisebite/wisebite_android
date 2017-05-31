package dev.wisebite.wisebite.utils;

import android.os.Build;
import android.widget.TimePicker;

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
    private static Date tempDate;
    private static Integer loaded = 0;

    public static String parseStartEndDate(TimePicker firstTimePicker, TimePicker secondTimePicker) {
        int firstHour, firstMinute, secondHour, secondMinute;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            firstHour = firstTimePicker.getHour();
            firstMinute = firstTimePicker.getMinute();
            secondHour = secondTimePicker.getHour();
            secondMinute = secondTimePicker.getMinute();
        } else {
            firstHour = firstTimePicker.getCurrentHour();
            firstMinute = firstTimePicker.getCurrentMinute();
            secondHour = secondTimePicker.getCurrentHour();
            secondMinute = secondTimePicker.getCurrentMinute();
        }

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

    public static OpenTime createOpenTimeByTimePicker(TimePicker firstTimePicker, TimePicker secondTimePicker, Integer viewId) {
        int dayOfTheWeek = parseViewIdToDayOfWeek(viewId);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(0);
        startCalendar.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startCalendar.set(Calendar.HOUR_OF_DAY, firstTimePicker.getHour());
            startCalendar.set(Calendar.MINUTE, firstTimePicker.getMinute());
        } else {
            startCalendar.set(Calendar.HOUR_OF_DAY, firstTimePicker.getCurrentHour());
            startCalendar.set(Calendar.MINUTE, firstTimePicker.getCurrentMinute());
        }


        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(0);
        endCalendar.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endCalendar.set(Calendar.HOUR_OF_DAY, secondTimePicker.getHour());
            endCalendar.set(Calendar.MINUTE, secondTimePicker.getMinute());
        } else {
            endCalendar.set(Calendar.HOUR_OF_DAY, secondTimePicker.getCurrentHour());
            endCalendar.set(Calendar.MINUTE, secondTimePicker.getCurrentMinute());
        }

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

    public static Integer getLoaded() { return loaded; }

    public static void increaseLoaded() { ++loaded; }

    public static String toStringWithTwoDecimals(double d) {
        d = Math.round(d * 100);
        d = d/100;
        return String.valueOf(d);
    }

    public static String getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String result = "";
        if (hour < 10) result += '0';
        result += String.valueOf(hour) + ':';
        if (minute < 10) result += '0';
        result += String.valueOf(minute);

        return result;
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

    public static int parseViewIdToDayOfWeek(Integer viewId) {
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

    public static String skipAts(String email) {
        return email.replaceAll("\\.", "@");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static void setAnalyticsDate(Date date) {
        tempDate = date;
    }

    public static Date getAnalyticsDate() {
        return tempDate;
    }
}
