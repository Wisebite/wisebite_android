package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.utils.FirebaseRepository;

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
    public static final String IMAGES_REFERENCE = "images";
    public static final String MENUS_REFERENCE = "menus";
    public static final String DISHES_REFERENCE = "dishes";

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
                List<String> openTimes = new ArrayList<>();
                for (DataSnapshot openTime : d.getChildren()) {
                    openTimes.add(openTime.getKey());
                }
                restaurant.setOpenTimes(openTimes);
            } else if (d.getKey().equals(IMAGES_REFERENCE)) {
                List<String> images = new ArrayList<>();
                for (DataSnapshot image : d.getChildren()) {
                    images.add(image.getKey());
                }
                restaurant.setImages(images);
            } else if (d.getKey().equals(MENUS_REFERENCE)) {
                List<String> menus = new ArrayList<>();
                for (DataSnapshot menu : d.getChildren()) {
                    menus.add(menu.getKey());
                }
                restaurant.setMenus(menus);
            } else if (d.getKey().equals(DISHES_REFERENCE)) {
                List<String> dishes = new ArrayList<>();
                for (DataSnapshot dish : d.getChildren()) {
                    dishes.add(dish.getKey());
                }
                restaurant.setDishes(dishes);
            }
        }
        return restaurant;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }
}
