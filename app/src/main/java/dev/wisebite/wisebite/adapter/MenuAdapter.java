package dev.wisebite.wisebite.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Menu;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {

    private ArrayList<Menu> menus;

    public MenuAdapter(ArrayList<Menu> menus) {
        this.menus = menus;
        notifyDataSetChanged();
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.dishes_menus_item, parent, false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        Menu current = menus.get(position);
        holder.name.setText(current.getName());
        holder.description.setText(current.getDescription());
        holder.price.setText(String.valueOf(current.getPrice() + " €"));
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    class MenuHolder extends RecyclerView.ViewHolder {

        public View view;
        TextView name;
        TextView description;
        TextView price;

        MenuHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
