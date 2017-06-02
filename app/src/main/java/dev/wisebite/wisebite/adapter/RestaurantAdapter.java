package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.GetRestaurantActivity;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.OrderHolder> {

    private List<Restaurant> restaurants;
    private RestaurantService restaurantService;

    public RestaurantAdapter(List<Restaurant> restaurantsList, Context context) {
        this.restaurants = restaurantsList;
        this.restaurantService = ServiceFactory.getRestaurantService(context);
        notifyDataSetChanged();
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.restaurant_item, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.item = restaurant;
        holder.name.setText(restaurant.getName());
        holder.description.setText(restaurant.getDescription());
        holder.daysOpen.setText(String.valueOf(calculateDaysOpen(restaurant) + " open days"));
        holder.dishesCount.setText(String.valueOf(calculateDishesCount(restaurant) + " different dishes"));
        holder.menusCount.setText(String.valueOf(calculateMenusCount(restaurant) + " different menus"));
    }

    private String calculateDaysOpen(Restaurant current) {
        return String.valueOf(restaurantService.getDaysOpen(current));
    }

    private String calculateDishesCount(Restaurant current) {
        return String.valueOf(restaurantService.getDishesCount(current));
    }

    private String calculateMenusCount(Restaurant current) {
        return String.valueOf(restaurantService.getMenusCount(current));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        public View view;
        Restaurant item;
        TextView name;
        TextView description;
        TextView daysOpen;
        TextView dishesCount;
        TextView menusCount;

        OrderHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.daysOpen = (TextView) itemView.findViewById(R.id.days_open);
            this.dishesCount = (TextView) itemView.findViewById(R.id.dishes_count);
            this.menusCount = (TextView) itemView.findViewById(R.id.menus_count);

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), GetRestaurantActivity.class);
                    intent.putExtra(GetRestaurantActivity.RESTAURANT_ID, item.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

}
