package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.utils.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class DishRepository extends FirebaseRepository<Dish> {

    public static final String OBJECT_REFERENCE = "dish";
    public static final String NAME_REFERENCE = "name";
    public static final String PRICE_REFERENCE = "price";
    public static final String DESCRIPTION_REFERENCE = "description";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public DishRepository(Context context) {
        super(context);
    }

    @Override
    protected Dish convert(DataSnapshot data) {
        Dish dish = new Dish();
        dish.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(NAME_REFERENCE)) {
                dish.setName(d.getValue(String.class));
            } else if (d.getKey().equals(PRICE_REFERENCE)) {
                dish.setPrice(d.getValue(Double.class));
            } else if (d.getKey().equals(DESCRIPTION_REFERENCE)) {
                dish.setDescription(d.getValue(String.class));
            }
        }
        return dish;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }

}
