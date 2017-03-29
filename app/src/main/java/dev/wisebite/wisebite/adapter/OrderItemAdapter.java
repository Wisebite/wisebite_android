package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
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
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.FirebaseRepository;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemHolder> {

    private RestaurantService restaurantService;
    private ArrayList<OrderItem> orderItems;
    private final Order order;
    private final String typeSearch;

    public OrderItemAdapter(ArrayList<OrderItem> orderItems, RestaurantService restaurantService, Order order, String typeSearch) {
        this.orderItems = orderItems;
        this.restaurantService = restaurantService;
        this.order = order;
        this.typeSearch = typeSearch;
        notifyDataSetChanged();
        setListener();
    }

    @Override
    public OrderItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_dish_item, parent, false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderItemHolder holder, int position) {
        final OrderItem current = orderItems.get(position);
        holder.item = current;
        holder.name.setText(restaurantService.getDishNameOf(current));
        setFlags(holder);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    class OrderItemHolder extends RecyclerView.ViewHolder {

        public View view;
        OrderItem item;
        TextView name;
        TextView ready;
        TextView delivered;
        TextView paid;
        FloatingActionButton mark;

        OrderItemHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.ready = (TextView) itemView.findViewById(R.id.ready);
            this.delivered = (TextView) itemView.findViewById(R.id.delivered);
            this.paid = (TextView) itemView.findViewById(R.id.paid);
            this.mark = (FloatingActionButton) itemView.findViewById(R.id.fab);
            this.mark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restaurantService.setDelivered(item, true, order);
                }
            });
        }
    }

    private void setFlags(OrderItemHolder holder) {
        if (holder.item.isReady()) {
            holder.ready.setVisibility(View.VISIBLE);
            if (!holder.item.isDelivered()) {
                holder.mark.setVisibility(View.VISIBLE);
            }
        }
        if (holder.item.isDelivered()) {
            holder.delivered.setVisibility(View.VISIBLE);
        }
        if (holder.item.isPaid()) {
            holder.paid.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        Firebase firebase;
        for (final OrderItem orderItem : this.orderItems) {
            firebase = new Firebase(FirebaseRepository.FIREBASE_URI +
                    OrderItemRepository.OBJECT_REFERENCE + '/' +
                    orderItem.getId());
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (typeSearch.equals(DishRepository.OBJECT_REFERENCE)) {
                        orderItems = restaurantService.getOnlyDishItemsOf(order);
                    } else {
                        orderItems = restaurantService.getOnlyMenuItemsOf(order);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // do nothing
                }
            });
        }
    }

}