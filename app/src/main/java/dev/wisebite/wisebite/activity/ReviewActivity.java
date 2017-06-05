package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.ReviewAdapter;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.domain.Review;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.ReviewService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class ReviewActivity extends AppCompatActivity {

    public static final String ORDER_ID = "ORDER_ID";

    private Order order;
    private OrderService orderService;
    private ReviewService reviewService;
    private Map<String, Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderService = ServiceFactory.getOrderService(ReviewActivity.this);
        reviewService = ServiceFactory.getReviewService(ReviewActivity.this);
        if (getIntent().getSerializableExtra(ORDER_ID) != null) {
            this.order = orderService.get(getIntent().getExtras().getString(ORDER_ID));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        initializeStaticData();
        initializeDynamicData();

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

    private void initializeStaticData() {
        TextView restaurantName = (TextView) findViewById(R.id.restaurant_name);
        TextView descriptionView = (TextView) findViewById(R.id.description);

        String description = orderService.getDescription(order.getId());
        description += ".\nIt would be very useful your opinion for the restaurant and more people" +
                " that want to go to that restaurant.\nCan you review the dishes or menus that you" +
                " tasted, and the restaurant?\nThank you very much!";
        restaurantName.setText(orderService.getRestaurantName(order.getId()));
        descriptionView.setText(description);

        LinearLayout ratingRestaurant = (LinearLayout) findViewById(R.id.rating_restaurant);
        ratingRestaurant.findViewById(R.id.dish_title).setVisibility(View.GONE);
    }

    private void initializeDynamicData() {
        List<OrderItem> orderItemList = orderService.getItems(order, false);
        this.reviews = createReviewMap(orderItemList);
        ReviewAdapter reviewAdapter = new ReviewAdapter(orderItemList, ReviewActivity.this, this.reviews);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dishes_menus_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(reviewAdapter);
    }

    private Map<String, Review> createReviewMap(List<OrderItem> orderItemList) {
        Map<String, Review> map = new HashMap<>();
        for (OrderItem item : orderItemList) {
            if (item.getMenuId() == null) {
                map.put(item.getDishId(), null);
            } else {
                map.put(item.getMenuId(), null);
            }
        }
        return map;
    }

    private void save(final View view) {
        LinearLayout ratingRestaurant = (LinearLayout) findViewById(R.id.rating_restaurant);

        RatingBar ratingBar = (RatingBar) ratingRestaurant.findViewById(R.id.rating_bar);
        EditText comment = (EditText) ratingRestaurant.findViewById(R.id.form_comment);
        if (ratingBar.getRating() == 0 || !isComplete()) {
            Snackbar.make(view, getResources().getString(R.string.no_review), Snackbar.LENGTH_LONG).show();
        } else {
            reviewService.addDishesReview(reviews);
            reviewService.addRestaurantReview(orderService.getRestaurantId(order.getId()),
                    ratingBar.getRating(), comment.getText().toString());
            reviewService.deleteOrderToReview(order.getId());
            finish();
        }
    }

    private boolean isComplete() {
        Review review;
        for (String key : reviews.keySet()) {
            review = reviews.get(key);
            if (review == null || review.getPoints() == 0) return false;
        }
        return true;
    }

}
