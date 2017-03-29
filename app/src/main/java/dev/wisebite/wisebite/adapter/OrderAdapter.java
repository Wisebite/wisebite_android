package dev.wisebite.wisebite.adapter;

import android.content.Intent;
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
import dev.wisebite.wisebite.activity.GetOrderActivity;
import dev.wisebite.wisebite.activity.MainActivity;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.repository.OrderItemRepository;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.FirebaseRepository;
import dev.wisebite.wisebite.utils.Repository;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private ArrayList<Order> orders;

    private RestaurantService restaurantService;

    public OrderAdapter(ArrayList<Order> ordersList, final RestaurantService restaurantService) {
        this.orders = ordersList;
        this.restaurantService = restaurantService;
        notifyDataSetChanged();
        setListener();
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_item, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        Order current = orders.get(position);
        holder.item = current;
        holder.numberTable.setText(String.valueOf("Table " + current.getTableNumber()));
        holder.price.setText(String.valueOf(calculatePrice(current) + " €"));
        holder.ready.setText(String.valueOf("Ready: " + calculateReady(current) + "%"));
        holder.delivered.setText(String.valueOf("Delivered: " + calculateDelivered(current) + "%"));
        holder.paid.setText(String.valueOf("Paid: " + calculatePaid(current) + "%"));
    }

    private String calculatePrice(Order current) {
        return Utils.toStringWithTwoDecimals(restaurantService.getPriceOfOrder(current.getId()));
    }

    private String calculateReady(Order current) {
        return Utils.toStringWithTwoDecimals(restaurantService.getReadyOfOrder(current.getId()));
    }

    private String calculateDelivered(Order current) {
        return Utils.toStringWithTwoDecimals(restaurantService.getDeliveredOfOrder(current.getId()));
    }

    private String calculatePaid(Order current) {
        return Utils.toStringWithTwoDecimals(restaurantService.getPaidOfOrder(current.getId()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        public View view;
        Order item;
        TextView numberTable;
        TextView price;
        TextView ready;
        TextView delivered;
        TextView paid;

        OrderHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.numberTable = (TextView) itemView.findViewById(R.id.table_number);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.ready = (TextView) itemView.findViewById(R.id.ready);
            this.delivered = (TextView) itemView.findViewById(R.id.delivered);
            this.paid = (TextView) itemView.findViewById(R.id.paid);

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
        for (Order order : this.orders) {
            for (String key : order.getOrderItems().keySet()) {
                firebase = new Firebase(FirebaseRepository.FIREBASE_URI +
                                        OrderItemRepository.OBJECT_REFERENCE + '/' +
                                        key);
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
            }
        }
    }
}
