package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private static final String RESTAURANT_MOCK_ID = "-KfvAq-HC6SSapHSBzsm";

    private LayoutInflater inflater;

    private RestaurantService restaurantService;
    private OrderService orderService;

    private TextView totalPriceView;
    private ArrayList<Dish> selectedDishes;
    private ArrayList<Menu> selectedMenusDishes;
    private Integer tableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inflater = LayoutInflater.from(CreateOrderActivity.this);

        restaurantService = ServiceFactory.getRestaurantService(CreateOrderActivity.this);
        orderService = ServiceFactory.getOrderService(CreateOrderActivity.this);

        TextView done = (TextView) findViewById(R.id.done);
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        initializeDishesItems();
        initializeMenusItems();

        showTableNumberForm();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
    }

    private void showTableNumberForm() {
        final LinearLayout tableNumberForm = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.table_number_form), null);
        new AlertDialog.Builder(CreateOrderActivity.this)
                .setTitle(getResources().getString(R.string.title_table_number_form))
                .setView(tableNumberForm)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tableNumberView = (TextView) tableNumberForm.findViewById(R.id.form_number);
                        tableNumber = Integer.valueOf(tableNumberView.getText().toString());

                        TextView titleTableNumber = (TextView) findViewById(R.id.table_number_title);
                        titleTableNumber.setText(String.valueOf("at table " + tableNumber));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .show();

    }

    private void initializeDishesItems() {
        selectedDishes = new ArrayList<>();
        totalPriceView = (TextView) findViewById(R.id.total_price);
        OrderItemDishAdapter orderItemDishAdapter = new OrderItemDishAdapter(restaurantService.getDishes(RESTAURANT_MOCK_ID),
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
        OrderItemMenuAdapter orderItemMenuAdapter = new OrderItemMenuAdapter(restaurantService.getMenus(RESTAURANT_MOCK_ID),
                totalPriceView,
                selectedMenusDishes,
                CreateOrderActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item_menu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderItemMenuAdapter);
    }

    private void done() {
        orderService.addOrder(selectedDishes, tableNumber, selectedMenusDishes);
        finish();
    }

}
