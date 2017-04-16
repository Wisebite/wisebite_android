package dev.wisebite.wisebite.service;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Map;
import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Repository;
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

    public UserService(Repository<User> repository,
                       Repository<Image> imageRepository,
                       Repository<Restaurant> restaurantRepository,
                       Repository<Order> orderRepository) {
        super(repository);
        this.imageRepository = imageRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
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
                    .email(userId)
                    .name(acct.getDisplayName())
                    .lastName(acct.getFamilyName())
                    .location(null)
                    .imageId(imageId)
                    .build();
            repository.update(user);
        }
        Preferences.setCurrentUserEmail(userId);
        return true;
    }

    public String getProfilePhoto() {
        return imageRepository.get(
                repository.get(Preferences.getCurrentUserEmail()).getImageId())
                .getImageFile();
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
}