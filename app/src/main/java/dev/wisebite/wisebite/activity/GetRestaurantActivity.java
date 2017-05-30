package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.DetailAdapter;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.OpenTime;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Utils;

public class GetRestaurantActivity extends BaseActivity {

    public static final String RESTAURANT_ID = "RESTAURANT_ID";

    private RestaurantService restaurantService;
    private Restaurant restaurant;
    private String restaurantId;

    private FloatingActionButton fab;
    private LayoutInflater inflater;

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
        inflater = LayoutInflater.from(GetRestaurantActivity.this);

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserToSomeRestaurant(view);
            }
        });

        initializeGeneralInfo();
        initializeOpenTimes();
        initializeDishes();
        initializeMenus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeGeneralInfo() {
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

        if (!restaurantService.isPartOfTheStuff(restaurantId, Preferences.getCurrentUserEmail())) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
        }
    }

    private void initializeOpenTimes() {
        for (OpenTime openTime : restaurantService.getOpenTimes(restaurant)) {
            initializeOpenTimeTextView(openTime);
        }
    }

    private void initializeOpenTimeTextView(OpenTime openTime) {
        TextView textView = (TextView) findViewById(getTextView(openTime.getStartDate()));
        textView.setText(Utils.parseStartEndDate(openTime.getStartDate(), openTime.getEndDate()));
    }

    private int getTextView(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return R.id.monday_open_time;
            case Calendar.TUESDAY:
                return R.id.tuesday_open_time;
            case Calendar.WEDNESDAY:
                return R.id.wednesday_open_time;
            case Calendar.THURSDAY:
                return R.id.thursday_open_time;
            case Calendar.FRIDAY:
                return R.id.friday_open_time;
            case Calendar.SATURDAY:
                return R.id.saturday_open_time;
            case Calendar.SUNDAY:
                return R.id.sunday_open_time;
        }
        return 0;
    }

    private void initializeDishes() {
        ArrayList<Dish> dishes = restaurantService.getDishes(restaurantId);
        if (dishes != null && !dishes.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.mock_dishes);
            textView.setVisibility(View.GONE);
        }
        DetailAdapter detailAdapter = new DetailAdapter(dishes, null);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dishes_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(detailAdapter);
    }

    private void initializeMenus() {
        ArrayList<Menu> menus = restaurantService.getMenus(restaurantId);
        if (menus != null && !menus.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.mock_menus);
            textView.setVisibility(View.GONE);
        }
        DetailAdapter detailAdapter = new DetailAdapter(null, menus);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menus_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(detailAdapter);
    }

    private void addUserToSomeRestaurant(final View view) {
        if (!restaurantService.isPartOfTheStuff(restaurantId, Preferences.getCurrentUserEmail())) return;
        final LinearLayout form = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.email_form), null);
        AlertDialog alertDialog = new AlertDialog.Builder(GetRestaurantActivity.this)
                .setTitle(getResources().getString(R.string.email_form_title))
                .setMessage(getResources().getString(R.string.email_form_message))
                .setView(form)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView email = (TextView) form.findViewById(R.id.form_email);
                        if (restaurantService.addNewUserToRestaurant(restaurantId, email.getText().toString())) {
                            Snackbar.make(view, getResources().getString(R.string.add_user_correct), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(view, getResources().getString(R.string.add_user_uncorrect), Snackbar.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do nothing
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}
