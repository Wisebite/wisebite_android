package dev.wisebite.wisebite.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class MenuService extends Service<Menu> {

    private final Repository<Dish> dishRepository;

    public MenuService(Repository<Menu> repository,
                       Repository<Dish> dishRepository) {
        super(repository);
        this.dishRepository = dishRepository;
    }

    public void addDishes(Menu menu, ArrayList<Dish> mainDishes, ArrayList<Dish> secondaryDishes, ArrayList<Dish> otherDishes) {
        Map<String, Object> mainDishesMap = new LinkedHashMap<>();
        for (Dish dish : mainDishes) {
            String insertedId = dishRepository.insert(dish).getId();
            mainDishesMap.put(insertedId, true);
        }
        menu.setMainDishes(mainDishesMap);

        Map<String, Object> secondaryDishesMap = new LinkedHashMap<>();
        for (Dish dish : secondaryDishes) {
            String insertedId = dishRepository.insert(dish).getId();
            secondaryDishesMap.put(insertedId, true);
        }
        menu.setSecondaryDishes(secondaryDishesMap);

        Map<String, Object> otherDishesMap = new LinkedHashMap<>();
        for (Dish dish : otherDishes) {
            String insertedId = dishRepository.insert(dish).getId();
            otherDishesMap.put(insertedId, true);
        }
        menu.setOtherDishes(otherDishesMap);
    }

}
