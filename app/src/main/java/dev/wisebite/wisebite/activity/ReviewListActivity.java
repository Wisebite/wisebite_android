package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.ReviewListAdapter;
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
        initializeData();
    }

    private List<Review> getReviews() {
        List<Review> reviewList = new ArrayList<>();

        Restaurant restaurant = restaurantService.get(id);
        Dish dish = dishService.get(id);
        Menu menu = menuService.get(id);

        List<String> reviewKeys = new ArrayList<>();
        if (restaurant != null && restaurant.getReviews() != null) {
            reviewKeys.addAll(restaurant.getReviews().keySet());
            setTitle(restaurant.getName() + " reviews");
        } else if (dish != null && dish.getReviews() != null) {
            reviewKeys.addAll(dish.getReviews().keySet());
            setTitle(dish.getName() + " reviews");
        } else if (menu != null && menu.getReviews() != null) {
            reviewKeys.addAll(menu.getReviews().keySet());
            setTitle(menu.getName() + " reviews");
        }

        Review review;
        for (String key : reviewKeys) {
            review = reviewService.get(key);
            if (review != null) reviewList.add(review);
        }

        return reviewList;
    }

    private void initializeData() {
        if (reviews.size() != 0) findViewById(R.id.review_list_mock).setVisibility(View.GONE);
        else findViewById(R.id.review_list_mock).setVisibility(View.VISIBLE);

        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(reviews, ReviewListActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.review_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(reviewListAdapter);
    }

}
