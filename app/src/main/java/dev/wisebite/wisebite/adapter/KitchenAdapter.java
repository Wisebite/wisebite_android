package dev.wisebite.wisebite.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import dev.wisebite.wisebite.activity.GetOrderActivity;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.repository.OrderItemRepository;
import dev.wisebite.wisebite.repository.OrderRepository;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.utils.FirebaseRepository;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.KitchenHolder> {

    private ArrayList<Order> orders;

    private RestaurantService restaurantService;

    public KitchenAdapter(ArrayList<Order> ordersList, final RestaurantService restaurantService) {
        this.orders = ordersList;
        this.restaurantService = restaurantService;
        notifyDataSetChanged();
        setListener();
    }

    @Override
    public KitchenHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.kitchen_item, parent, false);
        return new KitchenHolder(view);
    }

    @Override
    public void onBindViewHolder(KitchenHolder holder, int position) {
        Order current = orders.get(position);
        holder.item = current;
        holder.date.setText(Utils.getHour(current.getDate()));
        holder.ready.setText(String.format("%s%%", calculateReady(current)));
    }

    private String calculateReady(Order current) {
        return Utils.toStringWithTwoDecimals(restaurantService.getReadyOfOrder(current.getId()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class KitchenHolder extends RecyclerView.ViewHolder {

        public View view;
        Order item;
        TextView date;
        TextView ready;
        RecyclerView recyclerView;

        KitchenHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.date = (TextView) itemView.findViewById(R.id.date_order);
            this.ready = (TextView) itemView.findViewById(R.id.ready);
            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.kitchen_order_items_list);

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), GetOrderActivity.class);
                    intent.putExtra(GetOrderActivity.INTENT_ORDER, item.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private void setListener() {
        Firebase firebase;
        firebase = new Firebase(FirebaseRepository.FIREBASE_URI + OrderRepository.OBJECT_REFERENCE);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orders = restaurantService.getActiveOrders();
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // do nothing
            }
        });

        for (Order order : this.orders) {
            for (String key : order.getOrderItems().keySet()) {
                firebase = new Firebase(FirebaseRepository.FIREBASE_URI +
                                        OrderItemRepository.OBJECT_REFERENCE + '/' +
                                        key);
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        orders = restaurantService.getNonReadyOrders();
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
}
