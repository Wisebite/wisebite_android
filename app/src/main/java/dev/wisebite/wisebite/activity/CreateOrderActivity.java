package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.OrderItemDishAdapter;
import dev.wisebite.wisebite.adapter.OrderItemMenuAdapter;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.BaseActivity;

public class CreateOrderActivity extends BaseActivity {

    public static final String RESTAURANT_ID = "RESTAURANT_ID";
    public static final String TABLE_NUMBER = "TABLE_NUMBER";

    private RestaurantService restaurantService;
    private OrderService orderService;

    private TextView totalPriceView;
    private ArrayList<Dish> selectedDishes;
    private ArrayList<Menu> selectedMenusDishes;
    private Integer tableNumber;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getSerializableExtra(RESTAURANT_ID) != null) {
            this.restaurantId = getIntent().getExtras().getString(RESTAURANT_ID);
        }

        if (getIntent().getSerializableExtra(TABLE_NUMBER) != null) {
            this.tableNumber = getIntent().getExtras().getInt(TABLE_NUMBER);
            TextView titleTableNumber = (TextView) findViewById(R.id.table_number_title);
            titleTableNumber.setText(String.valueOf("at table " + tableNumber));
        }

        restaurantService = ServiceFactory.getRestaurantService(CreateOrderActivity.this);
        orderService = ServiceFactory.getOrderService(CreateOrderActivity.this);

        TextView done = (TextView) findViewById(R.id.done);
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done(v);
            }
        });

        initializeDishesItems();
        initializeMenusItems();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
    }

    private void initializeDishesItems() {
        selectedDishes = new ArrayList<>();
        totalPriceView = (TextView) findViewById(R.id.total_price);
        OrderItemDishAdapter orderItemDishAdapter = new OrderItemDishAdapter(restaurantService.getDishes(restaurantId),
                totalPriceView,
                selectedDishes);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item_dish);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderItemDishAdapter);
    }

    private void initializeMenusItems() {
        selectedMenusDishes = new ArrayList<>();
        totalPriceView = (TextView) findViewById(R.id.total_price);
        OrderItemMenuAdapter orderItemMenuAdapter = new OrderItemMenuAdapter(restaurantService.getMenus(restaurantId),
                totalPriceView,
                selectedMenusDishes,
                CreateOrderActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item_menu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderItemMenuAdapter);
    }

    private void done(View view) {
        if (selectedDishes.isEmpty() && selectedMenusDishes.isEmpty()) {
            Snackbar.make(view, getResources().getString(R.string.no_select_dishes), Snackbar.LENGTH_LONG).show();
        } else {
            orderService.addOrder(selectedDishes, tableNumber, selectedMenusDishes);
            finish();
        }
    }

}
