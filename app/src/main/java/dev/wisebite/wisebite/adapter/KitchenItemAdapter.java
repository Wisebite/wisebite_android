package dev.wisebite.wisebite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.CollectOrderActivity;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.service.RestaurantService;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class KitchenItemAdapter extends RecyclerView.Adapter<KitchenItemAdapter.KitchenItemHolder> {

    private final RestaurantService restaurantService;
    private ArrayList<OrderItem> orderItems;
    private final Context context;
    private final Order order;

    public KitchenItemAdapter(ArrayList<OrderItem> orderItems, RestaurantService restaurantService, Context context, Order order) {
        this.orderItems = orderItems;
        this.restaurantService = restaurantService;
        this.context = context;
        this.order = order;
        notifyDataSetChanged();
    }

    @Override
    public KitchenItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.kitchen_item_description, parent, false);
        return new KitchenItemHolder(view);
    }

    @Override
    public void onBindViewHolder(KitchenItemHolder holder, int position) {
        final OrderItem current = orderItems.get(position);
        holder.item = current;
        holder.descriptionItem.setText(restaurantService.getDishNameOf(current));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    class KitchenItemHolder extends RecyclerView.ViewHolder {

        public View view;
        OrderItem item;
        TextView descriptionItem;

        KitchenItemHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.descriptionItem = (TextView) itemView.findViewById(R.id.description_item);
            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getResources().getString(R.string.title_finish_item))
                            .setMessage(context.getResources().getString(R.string.message_finish_item))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    restaurantService.setReady(item, true, order);
                                    orderItems = restaurantService.getNonReadyOrderItems(order);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                }
            });
        }
    }

}
