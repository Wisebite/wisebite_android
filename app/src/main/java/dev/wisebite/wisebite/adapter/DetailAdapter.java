package dev.wisebite.wisebite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.ReviewListActivity;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailHolder> {

    private final ArrayList<Menu> menus;
    private final ArrayList<Dish> dishes;
    private final Context context;
    private final RestaurantService restaurantService;

    public DetailAdapter(ArrayList<Dish> dishes, ArrayList<Menu> menus, Context context) {
        this.dishes = dishes;
        this.menus = menus;
        this.context = context;
        this.restaurantService = ServiceFactory.getRestaurantService(context);
        notifyDataSetChanged();
    }

    @Override
    public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.dishes_menus_item, parent, false);
        return new DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailHolder holder, final int position) {
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

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                final LinearLayout extra = (LinearLayout) inflater.inflate(context.getResources().getLayout(R.layout.extra_information), null);
                TextView nameDish = (TextView) extra.findViewById(R.id.name_refill);
                TextView descriptionDish = (TextView) extra.findViewById(R.id.description_refill);
                TextView orderedToday = (TextView) extra.findViewById(R.id.ordered_today_refill);
                TextView orderedWeek = (TextView) extra.findViewById(R.id.ordered_week_refill);
                TextView orderedMonth = (TextView) extra.findViewById(R.id.ordered_month_refill);
                TextView priceDish = (TextView) extra.findViewById(R.id.price_refill);
                TextView averagePunctuation = (TextView) extra.findViewById(R.id.average_punctuation_refill);

                String id = "";
                if (dishes != null) id = dishes.get(position).getId();
                else if (menus != null) id = menus.get(position).getId();

                nameDish.setText(holder.name.getText().toString());
                descriptionDish.setText(holder.description.getText().toString());
                orderedToday.setText(String.format("%s times", String.valueOf(restaurantService.getTimesOrdered(id, Calendar.DATE))));
                orderedWeek.setText(String.format("%s times", String.valueOf(restaurantService.getTimesOrdered(id, Calendar.WEEK_OF_YEAR))));
                orderedMonth.setText(String.format("%s times", String.valueOf(restaurantService.getTimesOrdered(id, Calendar.MONTH))));
                priceDish.setText(holder.price.getText().toString());
                averagePunctuation.setText(restaurantService.getAveragePunctuationOfDish(id) == -1.0 ?
                        "---" :
                        Utils.toStringWithTwoDecimals(restaurantService.getAveragePunctuationOfDish(id)) + " / 5.0");

                final String finalId = id;
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.extra_information))
                        .setView(extra)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNegativeButton(R.string.reviews, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, ReviewListActivity.class);
                                intent.putExtra(ReviewListActivity.DISH_ID, finalId);
                                context.startActivity(intent);
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
        }
    }
}
