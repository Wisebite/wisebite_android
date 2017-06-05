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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.ReviewAdapter;
import dev.wisebite.wisebite.adapter.ReviewOrderAdapter;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.Preferences;

public class ReviewActivity extends AppCompatActivity {

    public static final String ORDER_ID = "ORDER_ID";

    private Order order;
    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderService = ServiceFactory.getOrderService(ReviewActivity.this);
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
        ReviewAdapter reviewAdapter = new ReviewAdapter(orderItemList, ReviewActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dishes_menus_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(reviewAdapter);
    }

    private void save(View view) {

    }

}
