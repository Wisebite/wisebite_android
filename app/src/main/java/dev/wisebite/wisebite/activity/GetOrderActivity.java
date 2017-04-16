package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import dev.wisebite.wisebite.utils.BaseActivity;

public class GetOrderActivity extends BaseActivity {

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

        setTitle("Order at Table " + (order != null ? String.valueOf(order.getTableNumber()) : "" ));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CollectOrderActivity.class);
                intent.putExtra(CollectOrderActivity.INTENT_ORDER, order.getId());
                view.getContext().startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        initializeDishes();
        initializeMenus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeDishes();
        initializeMenus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.get_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel) {
            new AlertDialog.Builder(GetOrderActivity.this)
                    .setTitle(getResources().getString(R.string.action_cancel_order))
                    .setMessage(getResources().getString(R.string.cancel_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restaurantService.cancelOrder(order);
                            onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();

            return true;
        } else if (id == R.id.action_edit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
