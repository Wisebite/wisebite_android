package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.utils.Utils;

public class CreateRestaurantOpenTimesActivity extends AppCompatActivity {

    public static final String RESTAURANT = "INTENT_RESTAURANT";

    private Restaurant restaurant;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant_open_times);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inflater = LayoutInflater.from(CreateRestaurantOpenTimesActivity.this);

        if (getIntent().getSerializableExtra(RESTAURANT) != null) {
            this.restaurant = (Restaurant) getIntent().getSerializableExtra(RESTAURANT);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, restaurant.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
}
