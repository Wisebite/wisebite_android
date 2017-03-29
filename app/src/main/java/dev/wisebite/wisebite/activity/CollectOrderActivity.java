package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;

public class CollectOrderActivity extends AppCompatActivity {

    public static final String INTENT_ORDER = "INTENT_ORDER";

    private Order order;
    private RestaurantService restaurantService;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
                        Snackbar.make((View) getResources().getLayout(R.layout.activity_collect_order), "Select some dishes to collect", Snackbar.LENGTH_SHORT);
                    }
                })
                .show();

    }

    private String getCollectAllMessage() {
        return "Do you have already collected " + restaurantService.getPriceOfOrder(order.getId()) + "€?";
    }

}
