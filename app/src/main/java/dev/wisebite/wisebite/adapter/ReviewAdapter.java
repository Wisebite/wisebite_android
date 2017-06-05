package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.ReviewActivity;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.service.OrderItemService;
import dev.wisebite.wisebite.service.ServiceFactory;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.OrderItemHolder> {

    private List<OrderItem> orderItems;
    private OrderItemService orderItemService;

    public ReviewAdapter(List<OrderItem> orderItemList, Context context) {
        this.orderItems = orderItemList;
        this.orderItemService = ServiceFactory.getOrderItemService(context);
        notifyDataSetChanged();
    }

    @Override
    public OrderItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_item, parent, false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderItemHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.item = orderItem;
        holder.name.setText(orderItemService.getName(orderItem));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    class OrderItemHolder extends RecyclerView.ViewHolder {

        public View view;
        OrderItem item;
        TextView name;
        RatingBar ratingBar;
        EditText description;

        OrderItemHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.dish_title);
            this.ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            this.description = (EditText) itemView.findViewById(R.id.form_comment);
        }
    }

}
