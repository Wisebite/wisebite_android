package dev.wisebite.wisebite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailHolder> {

    private final ArrayList<Menu> menus;
    private final ArrayList<Dish> dishes;
    private final Context context;

    public DetailAdapter(ArrayList<Dish> dishes, ArrayList<Menu> menus, Context context) {
        this.dishes = dishes;
        this.menus = menus;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.dishes_menus_item, parent, false);
        return new DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailHolder holder, int position) {
        if (this.dishes != null) {
            Dish current = this.dishes.get(position);
            holder.name.setText(current.getName());
            holder.description.setText(current.getDescription());
            holder.price.setText(String.valueOf(current.getPrice() + " €"));
        } else if (this.menus != null) {
            Menu current = this.menus.get(position);
            holder.name.setText(current.getName());
            holder.description.setText(current.getDescription());
            holder.price.setText(String.valueOf(current.getPrice() + " €"));
        }
    }

    @Override
    public int getItemCount() {
        if (dishes != null) {
            return dishes.size();
        } else if (menus != null) {
            return menus.size();
        }
        return 0;
    }

    class DetailHolder extends RecyclerView.ViewHolder {

        public View view;
        TextView name;
        TextView description;
        TextView price;

        DetailHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final LinearLayout extra = (LinearLayout) inflater.inflate(context.getResources().getLayout(R.layout.email_form), null);
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle(context.getResources().getString(R.string.extra_information) + " " + name.getText().toString())
                            .setView(extra)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    // do nothing
                                }
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                    return true;
                }
            });
        }
    }
}
