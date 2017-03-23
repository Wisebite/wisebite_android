package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.DishAdapter;
import dev.wisebite.wisebite.adapter.OrderItemDishAdapter;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class CreateOrderActivity extends AppCompatActivity {

    private static final String RESTAURANT_MOCK_ID = "-KfvAq-HC6SSapHSBzsm";
    private OrderItemDishAdapter orderItemDishAdapter;

    private RestaurantService restaurantService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restaurantService = ServiceFactory.getRestaurantService(CreateOrderActivity.this);

        TextView done = (TextView) findViewById(R.id.done);
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done(v);
            }
        });

        initializeDishesItems();

    }

    private void initializeDishesItems() {
        orderItemDishAdapter = new OrderItemDishAdapter(restaurantService.getDishesOf(RESTAURANT_MOCK_ID));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderItemDishAdapter);
    }

    private void done(View v) {

    }

}
