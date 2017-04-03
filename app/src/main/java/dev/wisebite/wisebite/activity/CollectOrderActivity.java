package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.CollectOrderItemAdapter;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class CollectOrderActivity extends AppCompatActivity {

    public static final String INTENT_ORDER = "INTENT_ORDER";

    private Order order;
    private RestaurantService restaurantService;
    private ArrayList<OrderItem> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.restaurantService = ServiceFactory.getRestaurantService(CollectOrderActivity.this);

        if (getIntent().getSerializableExtra(INTENT_ORDER) != null) {
            this.order = restaurantService.getOrder(getIntent().getExtras().getString(INTENT_ORDER));
        }

        setTitle("Collect order of table " + String.valueOf(order.getTableNumber()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CollectOrderActivity.this)
                        .setTitle(getResources().getString(R.string.collect_all_title))
                        .setMessage(getCollectInGroupsMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restaurantService.collectSomeItems(selectedItems, order);
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
            }
        });

        initializeOrderItems();

        showFirstDialog();
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

    private void showFirstDialog() {
        if (!restaurantService.isPartially(order)) {
            new AlertDialog.Builder(CollectOrderActivity.this)
                    .setTitle(getResources().getString(R.string.title_collect_order_form))
                    .setMessage(getResources().getString(R.string.message_collect_order_form))
                    .setPositiveButton(R.string.all, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(CollectOrderActivity.this)
                                    .setTitle(getResources().getString(R.string.collect_all_title))
                                    .setMessage(getCollectAllMessage())
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            restaurantService.collectAll(order);
                                            onBackPressed();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    })
                                    .show();
                        }
                    })
                    .setNegativeButton(R.string.in_groups, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }

    }

    private String getCollectAllMessage() {
        return "Do you have already collected " + restaurantService.getPriceOfOrder(order.getId()) + "€?";
    }

    private String getCollectInGroupsMessage() {
        return "Do you have already collected " + restaurantService.getPriceOfOrderItems(selectedItems) + "€?";
    }

    private void initializeOrderItems() {
        this.selectedItems = new ArrayList<>();
        ArrayList<OrderItem> orderItems = restaurantService.getItemsToCollect(order);
        if (orderItems != null && !orderItems.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.mock_order_items);
            textView.setVisibility(View.GONE);
        }
        CollectOrderItemAdapter collectOrderItemAdapter = new CollectOrderItemAdapter(orderItems,
                restaurantService, this.order, this.selectedItems, getApplicationContext());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(collectOrderItemAdapter);
    }

}
