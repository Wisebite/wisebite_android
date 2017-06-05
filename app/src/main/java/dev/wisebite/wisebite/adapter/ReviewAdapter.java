package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.domain.Review;
import dev.wisebite.wisebite.service.OrderItemService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.Preferences;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.OrderItemHolder> {

    private List<OrderItem> orderItems;
    private Map<String, Review> reviews;
    private OrderItemService orderItemService;

    public ReviewAdapter(List<OrderItem> orderItemList, Context context, Map<String, Review> reviewMap) {
        this.orderItems = orderItemList;
        this.reviews = reviewMap;
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
        EditText comment;

        OrderItemHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.dish_title);
            this.ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            this.comment = (EditText) itemView.findViewById(R.id.form_comment);

            this.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    String id;
                    if (item.getMenuId() == null) id = item.getDishId();
                    else id = item.getMenuId();

                    Review review = reviews.get(id);
                    if (review == null) review = Review.builder().build();
                    review.setPoints(rating);

                    reviews.put(id, review);
                }
            });
            this.comment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // do nothing
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String id;
                    if (item.getMenuId() == null) id = item.getDishId();
                    else id = item.getMenuId();

                    Review review = reviews.get(id);
                    if (review == null) review = Review.builder().build();
                    review.setComment(s.toString());

                    reviews.put(id, review);
                }
            });
        }
    }

}
