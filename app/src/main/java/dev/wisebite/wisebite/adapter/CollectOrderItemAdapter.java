package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.repository.DishRepository;
import dev.wisebite.wisebite.repository.OrderItemRepository;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.utils.FirebaseRepository;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class CollectOrderItemAdapter extends RecyclerView.Adapter<CollectOrderItemAdapter.CollectOrderItemHolder> {

    private RestaurantService restaurantService;
    private ArrayList<OrderItem> orderItems, selectedItems;
    private final Order order;
    private final Context context;

    public CollectOrderItemAdapter(ArrayList<OrderItem> orderItems, RestaurantService restaurantService, Order order, ArrayList<OrderItem> selectedOrderItems, Context context) {
        this.orderItems = orderItems;
        this.restaurantService = restaurantService;
        this.order = order;
        this.selectedItems = selectedOrderItems;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public CollectOrderItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.dishes_menus_item, parent, false);
        return new CollectOrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final CollectOrderItemHolder holder, int position) {
        final OrderItem current = orderItems.get(position);
        holder.item = current;
        holder.name.setText(restaurantService.getName(current));
        holder.description.setText(restaurantService.getDescription(current));
        holder.price.setText(String.valueOf(restaurantService.getPrice(current) + " €"));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    class CollectOrderItemHolder extends RecyclerView.ViewHolder {

        public View view;
        OrderItem item;
        TextView name;
        TextView description;
        TextView price;

        CollectOrderItemHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item);
                        v.setBackgroundColor(context.getResources().getColor(R.color.white));
                        Snackbar.make(v, "You've removed the item.", Snackbar.LENGTH_SHORT).show();
                    } else {
                        selectedItems.add(item);
                        v.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                        Snackbar.make(v, "You've added the item.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}