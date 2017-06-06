package dev.wisebite.wisebite.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.GetOrderActivity;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.service.OrderItemService;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.ServiceFactory;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemHolder> {

    private ArrayList<OrderItem> orderItems;
    private final Order order;

    private OrderService orderService;
    private OrderItemService orderItemService;
    private Context context;

    public OrderItemAdapter(ArrayList<OrderItem> orderItems, Context context, Order order) {
        this.orderItems = orderItems;
        this.order = order;

        this.orderService = ServiceFactory.getOrderService(context);
        this.orderItemService = ServiceFactory.getOrderItemService(context);
        this.context = context;
        notifyDataSetChanged();
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
        holder.name.setText(orderItemService.getDishName(current));
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
                    orderService.setDelivered(item, true, order);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void setFlags(OrderItemHolder holder) {
        Activity activity = (Activity) context;

        if (holder.item.isReady()) {
            holder.ready.setVisibility(View.VISIBLE);

            if (activity instanceof GetOrderActivity) {
                if (!holder.item.isDelivered()) {
                    holder.mark.setVisibility(View.VISIBLE);
                } else {
                    holder.mark.setVisibility(View.GONE);
                }
            }

        }
        if (holder.item.isDelivered()) {
            holder.delivered.setVisibility(View.VISIBLE);
        }
        if (holder.item.isPaid()) {
            holder.paid.setVisibility(View.VISIBLE);
        }
    }

}