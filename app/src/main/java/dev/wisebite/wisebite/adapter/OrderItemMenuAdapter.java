package dev.wisebite.wisebite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class OrderItemMenuAdapter extends RecyclerView.Adapter<OrderItemMenuAdapter.OrderItemMenuHolder> {

    private ArrayList<Menu> menus;
    private ArrayList<Menu> selectedMenus;
    private TextView totalPriceView;
    private Context context;

    private ArrayList<Dish> mainDishSelected, secondaryDishSelected, otherDishSelected;

    private LayoutInflater inflater;

    private RestaurantService restaurantService;

    public OrderItemMenuAdapter(ArrayList<Menu> menus, TextView totalPriceView, ArrayList<Menu> selectedMenus, Context context) {
        this.menus = menus;
        this.totalPriceView = totalPriceView;
        this.selectedMenus = selectedMenus;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.restaurantService = ServiceFactory.getRestaurantService(context);
        notifyDataSetChanged();
    }

    @Override
    public OrderItemMenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_dish_menu_item, parent, false);
        return new OrderItemMenuHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderItemMenuHolder holder, int position) {
        final Menu current = menus.get(position);
        holder.name.setText(current.getName());
        holder.description.setText(current.getDescription());
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer currentCounter = Integer.valueOf(holder.counter.getText().toString());
                if (currentCounter != 0) {
                    --currentCounter;
                    double totalPrice = Double.parseDouble(totalPriceView.getText().toString().split(" ")[0]);
                    totalPrice -= current.getPrice();
                    totalPriceView.setText(String.format("%s €", Utils.toStringWithTwoDecimals(totalPrice)));
                    removeSafely(current);
                }
                holder.counter.setText(String.valueOf(currentCounter));
            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout menuDishesForm = (LinearLayout) inflater.inflate(context.getResources().getLayout(R.layout.menu_dishes_form), null);
                initializeMenuDishesForm(current, menuDishesForm);
                new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.title_menu_dishes_form))
                        .setView(menuDishesForm)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setSelectedMenu(current);
                                Integer currentCounter = Integer.valueOf(holder.counter.getText().toString());
                                ++currentCounter;
                                double totalPrice = Double.parseDouble(totalPriceView.getText().toString().split(" ")[0]);
                                totalPrice += current.getPrice();
                                totalPriceView.setText(String.format("%s €", Utils.toStringWithTwoDecimals(totalPrice)));
                                selectedMenus.add(current);
                                holder.counter.setText(String.valueOf(currentCounter));
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
        });
    }

    private void setSelectedMenu(Menu current) {
        Map<String, Object> mainMap = new LinkedHashMap<>();
        Map<String, Object> secondaryMap = new LinkedHashMap<>();
        Map<String, Object> otherMap = new LinkedHashMap<>();
        for (Dish dish : mainDishSelected) mainMap.put(dish.getId(), true);
        for (Dish dish : secondaryDishSelected) secondaryMap.put(dish.getId(), true);
        for (Dish dish : otherDishSelected) otherMap.put(dish.getId(), true);

        current.setMainDishes(mainMap);
        current.setSecondaryDishes(secondaryMap);
        current.setOtherDishes(otherMap);
    }

    private void initializeMenuDishesForm(Menu current, LinearLayout menuDishesForm) {
        MenuDishFormAdapter mainForm, secondaryForm, otherForm;
        mainDishSelected = new ArrayList<>();
        secondaryDishSelected = new ArrayList<>();
        otherDishSelected = new ArrayList<>();
        mainForm = new MenuDishFormAdapter(restaurantService.parseDishMapToDishModel(current.getMainDishes()), mainDishSelected);
        secondaryForm = new MenuDishFormAdapter(restaurantService.parseDishMapToDishModel(current.getSecondaryDishes()), secondaryDishSelected);
        otherForm = new MenuDishFormAdapter(restaurantService.parseDishMapToDishModel(current.getOtherDishes()), otherDishSelected);

        RecyclerView recyclerViewMainDishes = (RecyclerView) menuDishesForm.findViewById(R.id.recycler_view_main_dishes);
        LinearLayoutManager linearLayoutManagerMain = new LinearLayoutManager(context);
        assert recyclerViewMainDishes != null;
        recyclerViewMainDishes.setLayoutManager(linearLayoutManagerMain);
        recyclerViewMainDishes.setAdapter(mainForm);

        RecyclerView recyclerViewSecondaryDishes = (RecyclerView) menuDishesForm.findViewById(R.id.recycler_view_secondary_dishes);
        LinearLayoutManager linearLayoutManagerSecondary = new LinearLayoutManager(context);
        assert recyclerViewSecondaryDishes != null;
        recyclerViewSecondaryDishes.setLayoutManager(linearLayoutManagerSecondary);
        recyclerViewSecondaryDishes.setAdapter(secondaryForm);

        RecyclerView recyclerViewOtherDishes = (RecyclerView) menuDishesForm.findViewById(R.id.recycler_view_other_dishes);
        LinearLayoutManager linearLayoutManagerOther = new LinearLayoutManager(context);
        assert recyclerViewOtherDishes != null;
        recyclerViewOtherDishes.setLayoutManager(linearLayoutManagerOther);
        recyclerViewOtherDishes.setAdapter(otherForm);

    }

    private void removeSafely(Menu current) {
        int index = 0;
        for (Menu menu : selectedMenus) {
            if (menu.getId().equals(current.getId())) {
                index = selectedMenus.indexOf(menu);
            }
        }
        selectedMenus.remove(index);
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    class OrderItemMenuHolder extends RecyclerView.ViewHolder {

        public View view;
        TextView name;
        TextView description;
        TextView counter;
        Button minus;
        Button plus;

        OrderItemMenuHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.different_feature);
            this.counter = (TextView) itemView.findViewById(R.id.counter);
            this.minus = (Button) itemView.findViewById(R.id.minus);
            this.plus = (Button) itemView.findViewById(R.id.plus);
        }
    }
}
