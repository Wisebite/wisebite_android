package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.firebase.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class MenuRepository extends FirebaseRepository<Menu> {

    public static final String OBJECT_REFERENCE = "menu";
    public static final String NAME_REFERENCE = "name";
    public static final String PRICE_REFERENCE = "price";
    public static final String DESCRIPTION_REFERENCE = "description";
    public static final String MAIN_DISHES_REFERENCE = "mainDishes";
    public static final String SECONDARY_DISHES_REFERENCE = "secondaryDishes";
    public static final String OTHER_DISHES_REFERENCE = "otherDishes";
    public static final String REVIEWS_REFERENCE = "reviews";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public MenuRepository(Context context) {
        super(context);
    }

    @Override
    protected Menu convert(DataSnapshot data) {
        Menu menu = new Menu();
        menu.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(NAME_REFERENCE)) {
                menu.setName(d.getValue(String.class));
            } else if (d.getKey().equals(PRICE_REFERENCE)) {
                menu.setPrice(d.getValue(Double.class));
            } else if (d.getKey().equals(DESCRIPTION_REFERENCE)) {
                menu.setDescription(d.getValue(String.class));
            } else if (d.getKey().equals(MAIN_DISHES_REFERENCE)) {
                Map<String, Object> dishes = new LinkedHashMap<>();
                for (DataSnapshot dish : d.getChildren()) {
                    dishes.put(dish.getKey(), true);
                }
                menu.setMainDishes(dishes);
            } else if (d.getKey().equals(SECONDARY_DISHES_REFERENCE)) {
                Map<String, Object> dishes = new LinkedHashMap<>();
                for (DataSnapshot dish : d.getChildren()) {
                    dishes.put(dish.getKey(), true);
                }
                menu.setSecondaryDishes(dishes);
            } else if (d.getKey().equals(OTHER_DISHES_REFERENCE)) {
                Map<String, Object> dishes = new LinkedHashMap<>();
                for (DataSnapshot dish : d.getChildren()) {
                    dishes.put(dish.getKey(), true);
                }
                menu.setOtherDishes(dishes);
            } else if (d.getKey().equals(REVIEWS_REFERENCE)) {
                Map<String, Object> reviews = new LinkedHashMap<>();
                for (DataSnapshot review : d.getChildren()) {
                    reviews.put(review.getKey(), true);
                }
                menu.setReviews(reviews);
            }
        }
        return menu;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }

}
