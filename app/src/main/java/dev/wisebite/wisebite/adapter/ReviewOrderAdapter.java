package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.GetRestaurantActivity;
import dev.wisebite.wisebite.activity.ReviewActivity;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.Preferences;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class ReviewOrderAdapter extends RecyclerView.Adapter<ReviewOrderAdapter.OrderHolder> {

    private List<Order> orders;
    private OrderService orderService;
    private UserService userService;

    public ReviewOrderAdapter(List<Order> orderList, Context context) {
        this.orders = orderList;
        this.orderService = ServiceFactory.getOrderService(context);
        this.userService = ServiceFactory.getUserService(context);
        notifyDataSetChanged();
        setListener();
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_order_item, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        Order order = orders.get(position);
        holder.item = order;
        holder.name.setText(orderService.getRestaurantName(order.getId()));
        holder.description.setText(orderService.getDescription(order.getId()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        public View view;
        Order item;
        TextView name;
        TextView description;

        OrderHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ReviewActivity.class);
                    intent.putExtra(ReviewActivity.ORDER_ID, item.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private void setListener() {
        userService.setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) {
                if (type.equals(EventType.Full)) {
                    orders = userService.getOrdersToReview(Preferences.getCurrentUserEmail());
                    notifyDataSetChanged();
                }
            }
        });
    }

}
