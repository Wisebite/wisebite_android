package dev.wisebite.wisebite.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.domain.Review;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.repository.RestaurantRepository;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class ReviewService extends Service<Review> {

    private Repository<Restaurant> restaurantRepository;
    private Repository<Dish> dishRepository;
    private Repository<Menu> menuRepository;
    private Repository<User> userRepository;

    public ReviewService(Repository<Review> repository,
                         Repository<Restaurant> restaurantRepository,
                         Repository<Dish> dishRepository,
                         Repository<Menu> menuRepository,
                         Repository<User> userRepository) {
        super(repository);
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
    }

    public void addDishesReview(Map<String, Review> reviews) {
        for (String key : reviews.keySet()) {
            if (dishRepository.get(key) != null) createDishReview(key, reviews.get(key));
            else if (menuRepository.get(key) != null) createMenuReview(key, reviews.get(key));
        }
    }

    public void addRestaurantReview(String restaurantId, float points, String comment) {
        Review review = Review.builder()
                .points(points)
                .comment(comment)
                .userId(Preferences.getCurrentUserEmail())
                .date(new Date())
                .build();
        String newId = repository.insert(review).getId();

        Restaurant restaurant = restaurantRepository.get(restaurantId);
        Map<String, Object> reviews = restaurant.getReviews();
        if (reviews == null) reviews = new LinkedHashMap<>();
        reviews.put(newId, true);
        restaurant.setReviews(reviews);
        restaurantRepository.update(restaurant);
    }

    public void deleteOrderToReview(String orderId) {
        User user = userRepository.get(Preferences.getCurrentUserEmail());
        Map<String, Object> ordersToReview = user.getOrdersToReview();
        if (ordersToReview != null) {
            ordersToReview.remove(orderId);
            user.setOrdersToReview(ordersToReview);
            userRepository.update(user);
        }
    }

    /** PRIVATE FUNCTIONS **/

    private void createDishReview(String dishId, Review review) {
        review.setUserId(Preferences.getCurrentUserEmail());
        review.setDate(new Date());
        String newId = repository.insert(review).getId();

        Dish dish = dishRepository.get(dishId);
        Map<String, Object> reviews = dish.getReviews();
        if (reviews == null) reviews = new LinkedHashMap<>();
        reviews.put(newId, true);
        dish.setReviews(reviews);
        dishRepository.update(dish);
    }

    private void createMenuReview(String menuId, Review review) {
        review.setUserId(Preferences.getCurrentUserEmail());
        review.setDate(new Date());
        String newId = repository.insert(review).getId();

        Menu menu = menuRepository.get(menuId);
        Map<String, Object> reviews = menu.getReviews();
        if (reviews == null) reviews = new LinkedHashMap<>();
        reviews.put(newId, true);
        menu.setReviews(reviews);
        menuRepository.update(menu);
    }
}