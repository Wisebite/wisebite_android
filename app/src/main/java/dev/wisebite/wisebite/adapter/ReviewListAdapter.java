package dev.wisebite.wisebite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Review;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewHolder> {

    private List<Review> reviews;
    private UserService userService;

    public ReviewListAdapter(List<Review> reviewList, Context context) {
        this.reviews = reviewList;
        this.userService = ServiceFactory.getUserService(context);
        notifyDataSetChanged();
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.item = review;
        holder.ratingBar.setRating((float) review.getPoints());
        holder.comment.setText(review.getComment());
        holder.description.setText(getDescription(review));
    }

    private String getDescription(Review review) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        User user = userService.get(review.getUserId());
        return "Review by " + (user.getName().isEmpty() ? "user" : user.getName()) +
                (review.getDate() == null ? "" : " on " + simpleDateFormat.format(review.getDate())) + ".";
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        public View view;
        Review item;
        RatingBar ratingBar;
        TextView comment;
        TextView description;

        ReviewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            this.comment = (TextView) itemView.findViewById(R.id.comment);
            this.description = (TextView) itemView.findViewById(R.id.description);
        }
    }

}
