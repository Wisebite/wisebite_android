package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.OpenTime;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.Utils;

public class CreateRestaurantOpenTimesActivity extends BaseActivity {

    public static final String RESTAURANT = "INTENT_RESTAURANT";

    private Restaurant restaurant;
    private LayoutInflater inflater;
    private Menu menu;

    private List<OpenTime> openTimes;
    private RestaurantService restaurantService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant_open_times);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inflater = LayoutInflater.from(CreateRestaurantOpenTimesActivity.this);

        if (getIntent().getSerializableExtra(RESTAURANT) != null) {
            this.restaurant = (Restaurant) getIntent().getSerializableExtra(RESTAURANT);
        }

        openTimes = new ArrayList<>();
        restaurantService = ServiceFactory.getRestaurantService(CreateRestaurantOpenTimesActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openTimes.isEmpty()) {
                    Snackbar.make(view, getResources().getString(R.string.empty_open_times), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    restaurantService.removeOpenTimes(restaurant.getId());
                    restaurantService.addOpenTimes(restaurant, openTimes);
                    Intent intent = new Intent(CreateRestaurantOpenTimesActivity.this, CreateRestaurantDishesActivity.class);
                    intent.putExtra(CreateRestaurantDishesActivity.RESTAURANT, restaurant);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(CreateRestaurantOpenTimesActivity.this)
                .setTitle(getResources().getString(R.string.back))
                .setMessage(getResources().getString(R.string.back_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreateRestaurantOpenTimesActivity.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.create_open_time_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_copy) {
            copyOpenTimes();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show time pickers for selecting restaurant open times
     * @param view view where the time picker will be shown
     */
    public void showTimePicker(final View view) {
        final TimePicker firstTimePicker = (TimePicker) inflater.inflate(getResources().getLayout(R.layout.time_picker), null);
        firstTimePicker.setIs24HourView(true);
        new AlertDialog.Builder(CreateRestaurantOpenTimesActivity.this)
                .setTitle(getResources().getString(R.string.title_first_time_picker))
                .setMessage(getResources().getString(R.string.message_first_time_picker))
                .setView(firstTimePicker)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final TimePicker secondTimePicker = (TimePicker) inflater.inflate(getResources().getLayout(R.layout.time_picker), null);
                        secondTimePicker.setIs24HourView(true);
                        new AlertDialog.Builder(CreateRestaurantOpenTimesActivity.this)
                                .setTitle(getResources().getString(R.string.title_second_time_picker))
                                .setMessage(getResources().getString(R.string.message_second_time_picker))
                                .setView(secondTimePicker)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TextView textView = (TextView) view;
                                        textView.setText(Utils.parseStartEndDate(firstTimePicker, secondTimePicker));
                                        addOpenTimeToList(Utils.createOpenTimeByTimePicker(firstTimePicker, secondTimePicker, view.getId()));
                                        setClosedVisible(view.getId());
                                        setCopyVisible();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    /**
     * Action to do when the user wants to cancel a open time
     * @param view view where the button will be shown
     */
    public void setClosed(View view) {
        int datePickerId = getDatePickerIdByCloseId(view.getId());
        TextView textView = (TextView) findViewById(datePickerId);
        textView.setText(getResources().getString(R.string.time_picker_test));

        removeOpenTimeByDay(Utils.parseViewIdToDayOfWeek(datePickerId));

        view.setVisibility(View.INVISIBLE);
        setCopyVisible();
    }

    /**
     * Get date pickers by close button id
     * @param id close button id
     * @return date picker id
     */
    private int getDatePickerIdByCloseId(int id) {
        switch (id) {
            case R.id.monday_close:
                return R.id.monday_date_picker;
            case R.id.tuesday_close:
                return R.id.tuesday_date_picker;
            case R.id.wednesday_close:
                return R.id.wednesday_date_picker;
            case R.id.thursday_close:
                return R.id.thursday_date_picker;
            case R.id.friday_close:
                return R.id.friday_date_picker;
            case R.id.saturday_close:
                return R.id.saturday_date_picker;
            case R.id.sunday_close:
                return R.id.sunday_date_picker;
        }
        return -1;
    }

    /**
     * Show close button with id `id`
     * @param id close button id
     */
    private void setClosedVisible(int id) {
        switch (id) {
            case R.id.monday_date_picker:
                findViewById(R.id.monday_close).setVisibility(View.VISIBLE); break;
            case R.id.tuesday_date_picker:
                findViewById(R.id.tuesday_close).setVisibility(View.VISIBLE); break;
            case R.id.wednesday_date_picker:
                findViewById(R.id.wednesday_close).setVisibility(View.VISIBLE); break;
            case R.id.thursday_date_picker:
                findViewById(R.id.thursday_close).setVisibility(View.VISIBLE); break;
            case R.id.friday_date_picker:
                findViewById(R.id.friday_close).setVisibility(View.VISIBLE); break;
            case R.id.saturday_date_picker:
                findViewById(R.id.saturday_close).setVisibility(View.VISIBLE); break;
            case R.id.sunday_date_picker:
                findViewById(R.id.sunday_close).setVisibility(View.VISIBLE); break;
        }
    }

    /**
     * Add open time to the current restaurant list
     * @param openTime open time to add
     */
    private void addOpenTimeToList(OpenTime openTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(openTime.getStartDate());

        Integer dayOfOpenTime = calendar.get(Calendar.DAY_OF_WEEK);
        for (OpenTime time : this.openTimes) {
            calendar.setTime(time.getStartDate());
            if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfOpenTime) {
                this.openTimes.remove(time);
                break;
            }
        }

        this.openTimes.add(openTime);
    }

    /**
     * Remove open time to the current restaurant list by day
     * @param day day of the open time to remove
     */
    private void removeOpenTimeByDay(int day) {
        Calendar calendar = Calendar.getInstance();
        for (OpenTime time : this.openTimes) {
            calendar.setTime(time.getStartDate());
            if (calendar.get(Calendar.DAY_OF_WEEK) == day) {
                this.openTimes.remove(time);
                return;
            }
        }
    }

    /**
     * Show copy button visible
     */
    private void setCopyVisible() {
        if (openTimes.isEmpty()) {
            this.menu.findItem(R.id.action_copy).setVisible(false);
        } else {
            if (sameHour()) {
                this.menu.findItem(R.id.action_copy).setVisible(true);
            } else {
                this.menu.findItem(R.id.action_copy).setVisible(false);
            }
        }
    }

    /**
     * Check if all open times in the list have the same hour
     * @return true if all open times have the same hour, false otherwise
     */
    private boolean sameHour() {
        if (this.openTimes.size() == 1) return true;
        else if (this.openTimes.size() == 7) return false;

        Calendar calendar = Calendar.getInstance();

        OpenTime first = this.openTimes.get(0);
        calendar.setTime(first.getStartDate());
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);
        calendar.setTime(first.getEndDate());
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);

        for (OpenTime openTime : this.openTimes) {
            calendar.setTime(openTime.getStartDate());
            if (calendar.get(Calendar.HOUR_OF_DAY) != startHour || calendar.get(Calendar.MINUTE) != startMinute)
                return false;
            calendar.setTime(openTime.getEndDate());
            if (calendar.get(Calendar.HOUR_OF_DAY) != endHour || calendar.get(Calendar.MINUTE) != endMinute)
                return false;
        }

        return true;
    }

    /**
     * Make all open times with same hour
     */
    private void copyOpenTimes() {
        OpenTime openTimeToCopy = this.openTimes.get(0);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(openTimeToCopy.getStartDate());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(openTimeToCopy.getEndDate());

        this.openTimes.clear();
        for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
            startCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            endCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            this.openTimes.add(OpenTime.builder()
                    .startDate(startCalendar.getTime())
                    .endDate(endCalendar.getTime())
                    .build());
        }

        TextView monday = (TextView) findViewById(R.id.monday_date_picker);
        TextView tuesday = (TextView) findViewById(R.id.tuesday_date_picker);
        TextView wednesday = (TextView) findViewById(R.id.wednesday_date_picker);
        TextView thursday = (TextView) findViewById(R.id.thursday_date_picker);
        TextView friday = (TextView) findViewById(R.id.friday_date_picker);
        TextView saturday = (TextView) findViewById(R.id.saturday_date_picker);
        TextView sunday = (TextView) findViewById(R.id.sunday_date_picker);

        String text = Utils.parseStartEndDate(startCalendar.getTime(), endCalendar.getTime());

        monday.setText(text); setClosedVisible(monday.getId());
        tuesday.setText(text); setClosedVisible(tuesday.getId());
        wednesday.setText(text); setClosedVisible(wednesday.getId());
        thursday.setText(text); setClosedVisible(thursday.getId());
        friday.setText(text); setClosedVisible(friday.getId());
        saturday.setText(text); setClosedVisible(saturday.getId());
        sunday.setText(text); setClosedVisible(sunday.getId());

        setCopyVisible();
    }

}
