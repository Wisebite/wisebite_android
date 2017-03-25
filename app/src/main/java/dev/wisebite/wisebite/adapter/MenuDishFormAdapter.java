package dev.wisebite.wisebite.adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Dish;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class MenuDishFormAdapter extends RecyclerView.Adapter<MenuDishFormAdapter.MenuDishFormHolder> {

    private ArrayList<Dish> dishes;
    private ArrayList<Dish> selectedDish;

    public MenuDishFormAdapter(ArrayList<Dish> dishes, ArrayList<Dish> selectedDish) {
        this.dishes = dishes;
        this.selectedDish = selectedDish;
        notifyDataSetChanged();
    }

    @Override
    public MenuDishFormHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.menu_dish_form_item, parent, false);
        return new MenuDishFormHolder(view);
    }

    @Override
    public void onBindViewHolder(final MenuDishFormHolder holder, int position) {
        final Dish current = dishes.get(position);
        holder.name.setText(current.getName());
        holder.description.setText(current.getDescription());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Before change dish", selectedDish.toString());
                selectedDish.clear();
                selectedDish.add(current);
                Log.d("After change dish", selectedDish.toString());
                Snackbar.make(v, "You have selected " + holder.name.getText().toString() + " as a dish.",
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    class MenuDishFormHolder extends RecyclerView.ViewHolder {

        public View view;
        LinearLayout mainLayout;
        TextView name;
        TextView description;
        TextView price;

        MenuDishFormHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.mainLayout = (LinearLayout) itemView.findViewById(R.id.main_layout);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
