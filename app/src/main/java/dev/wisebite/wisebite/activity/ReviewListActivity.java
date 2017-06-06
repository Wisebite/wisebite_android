package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.service.DishService;
import dev.wisebite.wisebite.service.MenuService;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class ReviewListActivity extends AppCompatActivity {

    public static final String DISH_ID = "DISH_ID";

    private String id;
    private RestaurantService restaurantService;
    private DishService dishService;
    private MenuService menuService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restaurantService = ServiceFactory.getRestaurantService(ReviewListActivity.this);
        dishService = ServiceFactory.getDishService(ReviewListActivity.this);
        menuService = ServiceFactory.getMenuService(ReviewListActivity.this);
        if (getIntent().getSerializableExtra(DISH_ID) != null) {
            this.id = getIntent().getExtras().getString(DISH_ID);
        }

    }

}
