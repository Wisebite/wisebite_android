package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.firebase.FirebaseRepository;

/**
 * Created by albert on 14/04/17.
 * @author albert
 */
public class UserRepository extends FirebaseRepository<User> {

    public static final String OBJECT_REFERENCE = "user";
    public static final String EMAIL_REFERENCE = "email";
    public static final String NAME_REFERENCE = "name";
    public static final String LAST_NAME_REFERENCE = "lastName";
    public static final String IMAGE_ID_REFERENCE = "imageId";
    public static final String LOCATION_REFERENCE = "location";

    public static final String MY_RESTAURANTS_REFERENCE = "myRestaurants";
    public static final String MY_ORDERS_REFERENCE = "myOrders";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public UserRepository(Context context) {
        super(context);
    }

    @Override
    protected User convert(DataSnapshot data) {
        User user = new User();
        user.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(EMAIL_REFERENCE)) {
                user.setEmail(d.getValue(String.class));
            } else if (d.getKey().equals(NAME_REFERENCE)) {
                user.setName(d.getValue(String.class));
            } else if (d.getKey().equals(LAST_NAME_REFERENCE)) {
                user.setLastName(d.getValue(String.class));
            } else if (d.getKey().equals(LOCATION_REFERENCE)) {
                user.setLocation(d.getValue(String.class));
            } else if (d.getKey().equals(IMAGE_ID_REFERENCE)) {
                user.setImageId(d.getValue(String.class));
            } else if (d.getKey().equals(MY_RESTAURANTS_REFERENCE)) {
                Map<String, Object> myRestaurants = new LinkedHashMap<>();
                for (DataSnapshot openTime : d.getChildren()) {
                    myRestaurants.put(openTime.getKey(), true);
                }
                user.setMyRestaurants(myRestaurants);
            } else if (d.getKey().equals(MY_ORDERS_REFERENCE)) {
                Map<String, Object> myOrders = new LinkedHashMap<>();
                for (DataSnapshot menu : d.getChildren()) {
                    myOrders.put(menu.getKey(), true);
                }
                user.setMyOrders(myOrders);
            }
        }
        return user;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }
}
