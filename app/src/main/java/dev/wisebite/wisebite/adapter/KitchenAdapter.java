package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
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
import dev.wisebite.wisebite.repository.OrderItemRepository;
import dev.wisebite.wisebite.repository.OrderRepository;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.FirebaseRepository;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Repository;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.KitchenHolder> {

    private ArrayList<Order> orders;
    private Context context;

    private OrderService orderService;
    private UserService userService;

    public KitchenAdapter(ArrayList<Order> ordersList, Context context) {
        this.orders = ordersList;
        this.context = context;
        this.orderService = ServiceFactory.getOrderService(context);
        this.userService = ServiceFactory.getUserService(context);
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
        initializeOrderItems(holder);
    }

    private void initializeOrderItems(KitchenHolder holder) {
        KitchenItemAdapter kitchenItemAdapter = new KitchenItemAdapter(orderService.getNonReadyOrderItems(holder.item),
                context, holder.item);
        RecyclerView recyclerView = (RecyclerView) holder.view.findViewById(R.id.kitchen_order_items_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(kitchenItemAdapter);
    }

    private String calculateReady(Order current) {
        return Utils.toStringWithTwoDecimals(orderService.getReadyOfOrder(current.getId()));
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
        }
    }

    private void setListener() {
        orderService.setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) {
                if (type.equals(EventType.Full)) {
                    orders = orderService.getNonReadyOrders(userService.getFirstRestaurantId(Preferences.getCurrentUserEmail()));
                    notifyDataSetChanged();
                }
            }
        });
    }
}
