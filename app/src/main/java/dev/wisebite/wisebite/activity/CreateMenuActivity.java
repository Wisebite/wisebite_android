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

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.DishAdapter;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.service.MenuService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.Utils;

public class CreateMenuActivity extends BaseActivity {

    private ArrayList<Dish> mainDishes, secondaryDishes, otherDishes;
    private DishAdapter mainDishesAdapter, secondaryDishesAdapter, otherDishesAdapter;

    private LayoutInflater inflater;
    private FloatingActionMenu floatingActionMenu;
    private TextView mockMainDishes;
    private TextView mockSecondaryDishes;
    private TextView mockOtherDishes;

    private Menu menu;
    private MenuService menuService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inflater = LayoutInflater.from(CreateMenuActivity.this);

        menuService = ServiceFactory.getMenuService(CreateMenuActivity.this);

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab);
        mockMainDishes = (TextView) findViewById(R.id.mock_main_dishes);
        mockSecondaryDishes = (TextView) findViewById(R.id.mock_secondary_dishes);
        mockOtherDishes = (TextView) findViewById(R.id.mock_other_dishes);

        this.mainDishes = new ArrayList<>();
        this.secondaryDishes = new ArrayList<>();
        this.otherDishes = new ArrayList<>();

        initializeRecycleViewMainDishes();
        initializeRecycleViewSecondaryDishes();
        initializeRecycleViewOtherDishes();

        TextView done = (TextView) findViewById(R.id.done);
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done(v);
            }
        });

        showMenuInfoForm();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
    }

    private void showMenuInfoForm() {
        final LinearLayout form = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.dish_form), null);
        AlertDialog alertDialog = new AlertDialog.Builder(CreateMenuActivity.this)
                .setTitle(getResources().getString(R.string.title_menu_form))
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
                            menu = Menu.builder()
                                    .name(name.getText().toString().trim())
                                    .description(description.getText().toString().trim())
                                    .price(Double.valueOf(price.getText().toString().trim()))
                                    .build();
                            LinearLayout menuInfo = (LinearLayout) findViewById(R.id.menu_info);
                            TextView nameResult = (TextView) findViewById(R.id.name_form_result);
                            TextView descriptionResult = (TextView) findViewById(R.id.description_form_result);
                            TextView priceResult = (TextView) findViewById(R.id.price_form_result);
                            nameResult.setText(menu.getName());
                            descriptionResult.setText(menu.getDescription());
                            priceResult.setText(String.valueOf(menu.getPrice() + " €"));
                            menuInfo.setVisibility(View.VISIBLE);
                        } else {
                            onBackPressed();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onBackPressed();
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void initializeRecycleViewMainDishes() {
        mainDishesAdapter = new DishAdapter(mainDishes, true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_main_dishes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mainDishesAdapter);
    }

    private void initializeRecycleViewSecondaryDishes() {
        secondaryDishesAdapter = new DishAdapter(secondaryDishes, true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_secondary_dishes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(secondaryDishesAdapter);
    }

    private void initializeRecycleViewOtherDishes() {
        otherDishesAdapter = new DishAdapter(otherDishes, true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_other_dishes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(otherDishesAdapter);
    }

    public void typeMainDishes(View view) {
        floatingActionMenu.close(true);
        final LinearLayout form = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.dish_form), null);
        form.findViewById(R.id.form_layout_price).setVisibility(View.GONE);
        new AlertDialog.Builder(CreateMenuActivity.this)
                .setTitle(getResources().getString(R.string.title_dish_form))
                .setView(form)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView name = (TextView) form.findViewById(R.id.form_name);
                        TextView description = (TextView) form.findViewById(R.id.form_description);
                        if (!name.getText().toString().trim().isEmpty() &&
                                !description.getText().toString().trim().isEmpty()) {
                            mainDishes.add(Dish.builder()
                                    .name(name.getText().toString().trim())
                                    .description(description.getText().toString().trim())
                                    .build());
                            if (mainDishes.size() != 0) {
                                mockMainDishes.setVisibility(View.GONE);
                            }
                            mainDishesAdapter.notifyDataSetChanged();
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

    public void typeSecondaryDishes(View view) {
        floatingActionMenu.close(true);
        final LinearLayout form = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.dish_form), null);
        form.findViewById(R.id.form_layout_price).setVisibility(View.GONE);
        new AlertDialog.Builder(CreateMenuActivity.this)
                .setTitle(getResources().getString(R.string.title_dish_form))
                .setView(form)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView name = (TextView) form.findViewById(R.id.form_name);
                        TextView description = (TextView) form.findViewById(R.id.form_description);
                        secondaryDishes.add(Dish.builder()
                                .name(name.getText().toString().trim())
                                .description(description.getText().toString().trim())
                                .build());
                        if (secondaryDishes.size() != 0) {
                            mockSecondaryDishes.setVisibility(View.GONE);
                        }
                        secondaryDishesAdapter.notifyDataSetChanged();
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

    public void typeOtherDishes(View view) {
        floatingActionMenu.close(true);
        final LinearLayout form = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.dish_form), null);
        form.findViewById(R.id.form_layout_price).setVisibility(View.GONE);
        new AlertDialog.Builder(CreateMenuActivity.this)
                .setTitle(getResources().getString(R.string.title_dish_form))
                .setView(form)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView name = (TextView) form.findViewById(R.id.form_name);
                        TextView description = (TextView) form.findViewById(R.id.form_description);
                        otherDishes.add(Dish.builder()
                                .name(name.getText().toString().trim())
                                .description(description.getText().toString().trim())
                                .build());
                        if (otherDishes.size() != 0) {
                            mockOtherDishes.setVisibility(View.GONE);
                        }
                        otherDishesAdapter.notifyDataSetChanged();
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

    private void done(View view) {
        if (mainDishes.isEmpty() && secondaryDishes.isEmpty() && otherDishes.isEmpty()) {
            Snackbar.make(view, getResources().getString(R.string.no_select_dishes_menu), Snackbar.LENGTH_LONG).show();
        } else {
            menuService.addDishes(menu, mainDishes, secondaryDishes, otherDishes);
            Utils.setTempMenu(menu);
            finish();
        }
    }

}
