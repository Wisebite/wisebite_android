package dev.wisebite.wisebite.service;

import java.util.ArrayList;
import java.util.Map;

import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class DishService extends Service<Dish> {

    public DishService(Repository<Dish> repository) {
        super(repository);
    }

    public ArrayList<Dish> parseDishMapToDishModel(Map<String, Object> dishesMap) {
        ArrayList<Dish> dishes = new ArrayList<>();
        if (dishesMap != null) {
            for (String dishKey : dishesMap.keySet()) {
                dishes.add(repository.get(dishKey));
            }
        }
        return dishes;
    }

    public String getName(OrderItem orderItem) {
        return repository.get(orderItem.getDishId()).getName();
    }

}