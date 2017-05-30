package dev.wisebite.wisebite.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.DishAdapter;
import dev.wisebite.wisebite.adapter.MenuAdapter;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.Utils;

public class CreateRestaurantDishesActivity extends BaseActivity {

    public static final String RESTAURANT = "INTENT_RESTAURANT";

    private ArrayList<Menu> menus;
    private ArrayList<Dish> dishes;

    private MenuAdapter menuAdapter;
    private DishAdapter dishAdapter;

    private Restaurant restaurant;
    private LayoutInflater inflater;
    private FloatingActionMenu floatingActionMenu;
    private TextView mockMenus;
    private TextView mockDishes;

    private RestaurantService restaurantService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant_dishes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restaurantService = ServiceFactory.getRestaurantService(CreateRestaurantDishesActivity.this);

        inflater = LayoutInflater.from(CreateRestaurantDishesActivity.this);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab);
        mockMenus = (TextView) findViewById(R.id.mock_menus);
        mockDishes = (TextView) findViewById(R.id.mock_dishes);

        if (getIntent().getSerializableExtra(RESTAURANT) != null) {
            this.restaurant = (Restaurant) getIntent().getSerializableExtra(RESTAURANT);
        }

        this.menus = new ArrayList<>();
        this.dishes = new ArrayList<>();

        initializeRecycleViewMenu();
        initializeRecycleViewDish();

        TextView done = (TextView) findViewById(R.id.done);
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Menu tempMenu = Utils.getTempMenu();
        if (tempMenu != null) {
            Log.d("onResume DishesActivity", tempMenu.toString());
            menus.add(tempMenu);
            if (menus.size() != 0) {
                mockMenus.setVisibility(View.GONE);
            }
            menuAdapter.notifyDataSetChanged();
            Utils.setTempMenu(null);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(CreateRestaurantDishesActivity.this)
                .setTitle(getResources().getString(R.string.back))
                .setMessage(getResources().getString(R.string.back_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreateRestaurantDishesActivity.this.finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeRecycleViewMenu() {
        menuAdapter = new MenuAdapter(menus);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_menu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(menuAdapter);
    }

    private void initializeRecycleViewDish() {
        dishAdapter = new DishAdapter(dishes, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_dish);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(dishAdapter);
    }

    public void typeDish(View view) {
        floatingActionMenu.close(true);
        final LinearLayout form = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.dish_form), null);
        new AlertDialog.Builder(CreateRestaurantDishesActivity.this)
                .setTitle(getResources().getString(R.string.title_dish_form))
                .setView(form)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView name = (TextView) form.findViewById(R.id.form_name);
                        TextView description = (TextView) form.findViewById(R.id.form_description);
                        TextView price = (TextView) form.findViewById(R.id.form_price);
                        if (    !name.getText().toString().trim().isEmpty() &&
                                !description.getText().toString().trim().isEmpty() &&
                                !price.getText().toString().trim().isEmpty()) {
                            dishes.add(Dish.builder()
                                    .name(name.getText().toString().trim())
                                    .description(description.getText().toString().trim())
                                    .price(Double.valueOf(price.getText().toString().trim()))
                                    .build());
                            if (dishes.size() != 0) {
                                mockDishes.setVisibility(View.GONE);
                            }
                            dishAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public void redirectToMenuActivity(View view) {
        floatingActionMenu.close(true);
        Intent intent = new Intent(CreateRestaurantDishesActivity.this, CreateMenuActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void done(View view) {
        if (dishes.isEmpty() && menus.isEmpty()) {
            Snackbar.make(view, getResources().getString(R.string.empty_dishes_menus), Snackbar.LENGTH_LONG)
                    .show();
        } else {
            restaurantService.addDishesAndMenus(restaurant, dishes, menus);
            Intent intent = new Intent(CreateRestaurantDishesActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }
}
