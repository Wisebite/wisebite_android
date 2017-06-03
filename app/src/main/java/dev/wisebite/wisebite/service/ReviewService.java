package dev.wisebite.wisebite.service;

import dev.wisebite.wisebite.domain.Review;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class ReviewService extends Service<Review> {

    public ReviewService(Repository<Review> repository) {
        super(repository);
    }

}