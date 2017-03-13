package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.utils.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class MenuRepository extends FirebaseRepository<Menu> {

    public static final String OBJECT_REFERENCE = "menu";
    public static final String NAME_REFERENCE = "name";
    public static final String PRICE_REFERENCE = "price";
    public static final String DESCRIPTION_REFERENCE = "description";
    public static final String DISHES_REFERENCE = "dishes";

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
            } else if (d.getKey().equals(DISHES_REFERENCE)) {
                List<String> dishes = new ArrayList<>();
                for (DataSnapshot dish : d.getChildren()) {
                    dishes.add(dish.getKey());
                }
                menu.setDishes(dishes);
            }
        }
        return menu;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }
}
