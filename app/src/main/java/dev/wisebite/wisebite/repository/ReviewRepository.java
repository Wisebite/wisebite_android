package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import dev.wisebite.wisebite.domain.Review;
import dev.wisebite.wisebite.firebase.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class ReviewRepository extends FirebaseRepository<Review> {

    public static final String OBJECT_REFERENCE = "review";
    public static final String POINTS_REFERENCE = "points";
    public static final String COMMENT_REFERENCE = "comment";
    public static final String USER_ID_REFERENCE = "userId";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public ReviewRepository(Context context) {
        super(context);
    }

    @Override
    protected Review convert(DataSnapshot data) {
        Review review = new Review();
        review.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(POINTS_REFERENCE)) {
                review.setPoints(d.getValue(Double.class));
            } else if (d.getKey().equals(COMMENT_REFERENCE)) {
                review.setComment(d.getValue(String.class));
            } else if (d.getKey().equals(USER_ID_REFERENCE)) {
                review.setUserId(d.getValue(String.class));
            }
        }
        return review;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }

}
