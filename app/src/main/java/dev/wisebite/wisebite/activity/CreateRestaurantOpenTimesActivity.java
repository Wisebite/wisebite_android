package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
                restaurantService.removeOpenTimes(restaurant.getId());
                restaurantService.addOpenTimes(restaurant, openTimes);
                Intent intent = new Intent(CreateRestaurantOpenTimesActivity.this, CreateRestaurantDishesActivity.class);
                intent.putExtra(CreateRestaurantDishesActivity.RESTAURANT, restaurant);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

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
}
