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

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.OrderItemAdapter;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.repository.DishRepository;
import dev.wisebite.wisebite.repository.MenuRepository;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class GetOrderActivity extends AppCompatActivity {

    public static final String INTENT_ORDER = "INTENT_ORDER";

    private RestaurantService restaurantService;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restaurantService = ServiceFactory.getRestaurantService(GetOrderActivity.this);

        if (getIntent().getSerializableExtra(INTENT_ORDER) != null) {
            this.order = restaurantService.getOrder(getIntent().getExtras().getString(INTENT_ORDER));
        }

        setTitle(String.valueOf("Order at Table " + order.getTableNumber()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeDishes();
        initializeMenus();
    }

    private void initializeDishes() {
        ArrayList<OrderItem> orderItems = restaurantService.getOnlyDishItemsOf(order);
        if (orderItems != null && !orderItems.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.mock_dishes);
            textView.setVisibility(View.GONE);
        }
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(orderItems,
                restaurantService, this.order, DishRepository.OBJECT_REFERENCE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item_dish);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderItemAdapter);
    }

    private void initializeMenus() {
        ArrayList<OrderItem> orderItems = restaurantService.getOnlyMenuItemsOf(order);
        if (orderItems != null && !orderItems.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.mock_menus);
            textView.setVisibility(View.GONE);
        }
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(orderItems,
                restaurantService, this.order, MenuRepository.OBJECT_REFERENCE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item_menu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderItemAdapter);
    }

}
