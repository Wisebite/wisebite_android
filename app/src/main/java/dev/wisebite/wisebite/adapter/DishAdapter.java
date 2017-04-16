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
public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishHolder> {

    private ArrayList<Dish> dishes;
    private boolean menu;

    public DishAdapter(ArrayList<Dish> dishes, boolean menu) {
        this.dishes = dishes;
        this.menu = menu;
        notifyDataSetChanged();
    }

    @Override
    public DishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.dishes_menus_item, parent, false);
        return new DishHolder(view);
    }

    @Override
    public void onBindViewHolder(DishHolder holder, int position) {
        Dish current = dishes.get(position);
        holder.name.setText(current.getName());
        holder.description.setText(current.getDescription());
        if (!menu) holder.price.setText(String.valueOf(current.getPrice() + " €"));
        else holder.price.setText("");
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    class DishHolder extends RecyclerView.ViewHolder {

        public View view;
        TextView name;
        TextView description;
        TextView price;

        DishHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.price = (TextView) itemView.findViewById(R.id.price);
            if (menu) {
                this.price.setVisibility(View.GONE);
            }
        }
    }
}
