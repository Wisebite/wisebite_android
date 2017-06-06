package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.domain.Review;
import dev.wisebite.wisebite.service.DishService;
import dev.wisebite.wisebite.service.MenuService;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ReviewService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class ReviewListActivity extends AppCompatActivity {

    public static final String DISH_ID = "DISH_ID";

    private String id;
    private RestaurantService restaurantService;
    private DishService dishService;
    private MenuService menuService;
    private ReviewService reviewService;

    private List<Review> reviews;

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
        reviewService = ServiceFactory.getReviewService(ReviewListActivity.this);
        if (getIntent().getSerializableExtra(DISH_ID) != null) {
            this.id = getIntent().getExtras().getString(DISH_ID);
        }

        this.reviews = getReviews();

    }

    private List<Review> getReviews() {
        List<Review> reviewList = new ArrayList<>();

        Restaurant restaurant = restaurantService.get(id);
        Dish dish = dishService.get(id);
        Menu menu = menuService.get(id);

        List<String> reviewKeys = new ArrayList<>();
        if (restaurant != null && restaurant.getReviews() != null) reviewKeys.addAll(restaurant.getReviews().keySet());
        else if (dish != null && dish.getReviews() != null) reviewKeys.addAll(dish.getReviews().keySet());
        else if (menu != null && menu.getReviews() != null) reviewKeys.addAll(menu.getReviews().keySet());

        Review review;
        for (String key : reviewKeys) {
            review = reviewService.get(key);
            if (review != null) reviewList.add(review);
        }

        return reviewList;
    }

}
