package dev.wisebite.wisebite.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Dish;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class OrderItemDishAdapter extends RecyclerView.Adapter<OrderItemDishAdapter.OrderItemDishHolder> {

    private ArrayList<Dish> dishes;

    public OrderItemDishAdapter(ArrayList<Dish> dishes) {
        this.dishes = dishes;
        notifyDataSetChanged();
    }

    @Override
    public OrderItemDishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_dish_item, parent, false);
        return new OrderItemDishHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderItemDishHolder holder, int position) {
        Dish current = dishes.get(position);
        holder.name.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    class OrderItemDishHolder extends RecyclerView.ViewHolder {

        public View view;
        TextView name;

        OrderItemDishHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
