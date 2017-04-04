package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class GetRestaurantActivity extends AppCompatActivity {

    public static final String RESTAURANT_ID = "RESTAURANT_ID";

    private RestaurantService restaurantService;
    private Restaurant restaurant;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getSerializableExtra(RESTAURANT_ID) != null) {
            this.restaurantId = getIntent().getExtras().getString(RESTAURANT_ID);
        }

        restaurantService = ServiceFactory.getRestaurantService(GetRestaurantActivity.this);
        restaurant = restaurantService.get(restaurantId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRestaurant();
            }
        });

        initializeView();
    }

    private void initializeView() {
        setTitle(restaurant.getName());
        TextView description, location, phone, website, numberOfTables;
        description = (TextView) findViewById(R.id.description_refill);
        location = (TextView) findViewById(R.id.location_refill);
        phone = (TextView) findViewById(R.id.phone_refill);
        website = (TextView) findViewById(R.id.website_refill);
        numberOfTables = (TextView) findViewById(R.id.number_of_tables_refill);
        description.setText(restaurant.getDescription());
        location.setText(restaurant.getLocation());
        phone.setText(String.valueOf(restaurant.getPhone()));
        website.setText(restaurant.getWebsite());
        numberOfTables.setText(String.valueOf(restaurant.getNumberOfTables() + " tables"));
    }

    private void editRestaurant() {

    }
}
