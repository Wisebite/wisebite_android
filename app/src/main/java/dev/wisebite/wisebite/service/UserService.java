package dev.wisebite.wisebite.service;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Service;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class UserService extends Service<User> {

    private final Repository<Image> imageRepository;
    private final Repository<Restaurant> restaurantRepository;
    private final Repository<Order> orderRepository;
    private final Repository<OrderItem> orderItemRepository;

    public UserService(Repository<User> repository,
                       Repository<Image> imageRepository,
                       Repository<Restaurant> restaurantRepository,
                       Repository<Order> orderRepository,
                       Repository<OrderItem> orderItemRepository) {
        super(repository);
        this.imageRepository = imageRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public boolean logIn(GoogleSignInAccount acct) {
        String userId = Utils.skipAts(acct.getEmail());
        if (!repository.exists(userId)) {
            Uri imageUri = acct.getPhotoUrl();
            String imageId = null;
            if (imageUri != null) {
                Image image = Image.builder()
                        .imageFile(imageUri.toString()).description("Profile photo")
                        .build();
                imageId = imageRepository.insert(image).getId();
            }
            User user = User.builder()
                    .email(acct.getEmail())
                    .name(acct.getGivenName())
                    .lastName(acct.getFamilyName())
                    .location(null)
                    .imageId(imageId)
                    .build();
            user.setId(userId);
            repository.update(user);
        }
        Preferences.setCurrentUserEmail(userId);
        return true;
    }

    public String getProfilePhoto(String id) {
        Image image = imageRepository.get(repository.get(id).getImageId());
        if (image != null) return image.getImageFile();
        return null;
    }

    public String getUserName(String currentUser) {
        return repository.get(currentUser).getName();
    }

    public String getFirstRestaurantId(String currentUser) {
        User user = repository.get(currentUser);
        Map<String, Object> keys = user.getMyRestaurants();
        if (keys != null && !keys.keySet().isEmpty()) return (String) keys.keySet().toArray()[0];
        return null;
    }

    public String getFirstRestaurantName(String currentUser) {
        String restaurantId = null;
        User user = repository.get(currentUser);
        Map<String, Object> keys = user.getMyRestaurants();
        if (keys != null && !keys.keySet().isEmpty()) restaurantId = (String) keys.keySet().toArray()[0];
        if (restaurantId != null) return restaurantRepository.get(restaurantId).getName();
        return null;
    }

    public Integer getOrderCount(String currentUser) {
        User user = repository.get(currentUser);
        Map<String, Object> keys = user.getMyOrders();
        if (keys != null && !keys.keySet().isEmpty()) return keys.keySet().size();
        return null;
    }

    public boolean editUser(String id, String name, String lastName, String location) {
        User user = repository.get(id);
        user.setName(name);
        user.setLastName(lastName);
        user.setLocation(location);

        repository.update(user);
        return true;
    }

    public void editImage(String id, String uploadURL) {
        User user = repository.get(id);

        Image image = Image.builder().imageFile(uploadURL).build();
        String imageId = imageRepository.insert(image).getId();
        user.setImageId(imageId);

        repository.update(user);
    }

    public Order hasActiveOrder(String userKey) {
        User user = repository.get(userKey);

        Map<String, Object> dishesMap = new HashMap<>();
        Map<String, Object> menusMap = new HashMap<>();
        if (user != null && user.getMyRestaurants() != null) {
            Restaurant restaurant;
            for (String restaurantKey : user.getMyRestaurants().keySet()) {
                restaurant = restaurantRepository.get(restaurantKey);
                if (restaurant.getDishes() != null) dishesMap = restaurant.getDishes();
                if (restaurant.getMenus() != null) menusMap = restaurant.getMenus();
            }
        }

        if (user != null && user.getMyOrders() != null) {
            Order order;
            for (String orderKey : user.getMyOrders().keySet()) {
                order = orderRepository.get(orderKey);
                if (order != null && !isFinished(order.getOrderItems()) && !partOfYourRestaurant(order, dishesMap, menusMap))
                    return order;
            }
        }

        return null;
    }

    private boolean isFinished(Map<String, Object> orderItems) {
        if (orderItems != null) {
            OrderItem orderItem;
            for (String orderItemKey : orderItems.keySet()) {
                orderItem = orderItemRepository.get(orderItemKey);
                if (orderItem != null && !orderItem.isPaid()) return false;
            }
        }
        return true;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean partOfYourRestaurant(Order order, Map<String, Object> dishesMap, Map<String, Object> menusMap) {
        OrderItem orderItem;
        for (String orderItemKey : order.getOrderItems().keySet()) {
            orderItem = orderItemRepository.get(orderItemKey);
            return orderItem.getMenuId() != null &&
                    menusMap.containsKey(orderItem.getMenuId()) ||
                    orderItem.getMenuId() == null && dishesMap.containsKey(orderItem.getDishId());
        }

        return false;
    }

    public List<Order> getOrdersToReview(String userKey) {
        List<Order> orders = new ArrayList<>();

        User user = repository.get(userKey);
        if (user != null && user.getOrdersToReview() != null) {
            Order order;
            for (String orderKey : user.getOrdersToReview().keySet()) {
                order = orderRepository.get(orderKey);
                orders.add(order);
            }
        }

        return orders;
    }
}