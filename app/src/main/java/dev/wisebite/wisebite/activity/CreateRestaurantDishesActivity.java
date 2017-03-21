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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.DishAdapter;
import dev.wisebite.wisebite.adapter.MenuAdapter;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.Utils;

public class CreateRestaurantDishesActivity extends AppCompatActivity {

    public static final String RESTAURANT = "INTENT_RESTAURANT";

    private ArrayList<Menu> menus;
    private ArrayList<Dish> dishes;

    private MenuAdapter menuAdapter;
    private DishAdapter dishAdapter;

    private Restaurant restaurant;
    private LayoutInflater inflater;
    private FloatingActionMenu floatingActionMenu;
    private TextView mockMenus, mockDishes, done;

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

        done = (TextView) findViewById(R.id.done);
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done(v);
            }
        });
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
        dishAdapter = new DishAdapter(dishes);
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


    }

    public void done(View view) {
        restaurantService.addDishesAndMenusToRestaurant(restaurant, dishes, menus);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
