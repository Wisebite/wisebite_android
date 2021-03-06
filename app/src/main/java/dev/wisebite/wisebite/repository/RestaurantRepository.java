package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.firebase.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class RestaurantRepository extends FirebaseRepository<Restaurant> {

    public static final String OBJECT_REFERENCE = "restaurant";
    public static final String NAME_REFERENCE = "name";
    public static final String LOCATION_REFERENCE = "location";
    public static final String PHONE_REFERENCE = "phone";
    public static final String DESCRIPTION_REFERENCE = "description";
    public static final String WEBSITE_REFERENCE = "website";
    public static final String NUMBER_OF_TABLES_REFERENCE = "numberOfTables";

    public static final String OPEN_TIMES_REFERENCE = "openTimes";
    public static final String MENUS_REFERENCE = "menus";
    public static final String DISHES_REFERENCE = "dishes";
    public static final String USERS_REFERENCE = "users";
    public static final String EXTERNAL_ORDERS_REFERENCE = "externalOrders";
    public static final String REVIEWS_REFERENCE = "reviews";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public RestaurantRepository(Context context) {
        super(context);
    }

    @Override
    protected Restaurant convert(DataSnapshot data) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(NAME_REFERENCE)) {
                restaurant.setName(d.getValue(String.class));
            } else if (d.getKey().equals(LOCATION_REFERENCE)) {
                restaurant.setLocation(d.getValue(String.class));
            } else if (d.getKey().equals(PHONE_REFERENCE)) {
                restaurant.setPhone(d.getValue(Integer.class));
            } else if (d.getKey().equals(DESCRIPTION_REFERENCE)) {
                restaurant.setDescription(d.getValue(String.class));
            } else if (d.getKey().equals(WEBSITE_REFERENCE)) {
                restaurant.setWebsite(d.getValue(String.class));
            } else if (d.getKey().equals(NUMBER_OF_TABLES_REFERENCE)) {
                restaurant.setNumberOfTables(d.getValue(Integer.class));
            } else if (d.getKey().equals(OPEN_TIMES_REFERENCE)) {
                Map<String, Object> openTimes = new LinkedHashMap<>();
                for (DataSnapshot openTime : d.getChildren()) {
                    openTimes.put(openTime.getKey(), true);
                }
                restaurant.setOpenTimes(openTimes);
            } else if (d.getKey().equals(MENUS_REFERENCE)) {
                Map<String, Object> menus = new LinkedHashMap<>();
                for (DataSnapshot menu : d.getChildren()) {
                    menus.put(menu.getKey(), true);
                }
                restaurant.setMenus(menus);
            } else if (d.getKey().equals(DISHES_REFERENCE)) {
                Map<String, Object> dishes = new LinkedHashMap<>();
                for (DataSnapshot dish : d.getChildren()) {
                    dishes.put(dish.getKey(), true);
                }
                restaurant.setDishes(dishes);
            } else if (d.getKey().equals(USERS_REFERENCE)) {
                Map<String, Object> users = new LinkedHashMap<>();
                for (DataSnapshot dish : d.getChildren()) {
                    users.put(dish.getKey(), true);
                }
                restaurant.setUsers(users);
            } else if (d.getKey().equals(EXTERNAL_ORDERS_REFERENCE)) {
                Map<String, Object> externalOrders = new LinkedHashMap<>();
                for (DataSnapshot dish : d.getChildren()) {
                    externalOrders.put(dish.getKey(), true);
                }
                restaurant.setExternalOrders(externalOrders);
            } else if (d.getKey().equals(REVIEWS_REFERENCE)) {
                Map<String, Object> reviews = new LinkedHashMap<>();
                for (DataSnapshot review : d.getChildren()) {
                    reviews.put(review.getKey(), true);
                }
                restaurant.setReviews(reviews);
            }
        }
        return restaurant;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }

}
