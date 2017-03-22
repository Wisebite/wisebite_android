package dev.wisebite.wisebite.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.OpenTime;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.utils.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class RestaurantService extends Service<Restaurant> {

    public static final String TAG = RestaurantService.class.getSimpleName();
    private final Repository<Menu> menuRepository;
    private final Repository<Dish> dishRepository;
    private final Repository<Image> imageRepository;
    private final Repository<OpenTime> openTimeRepository;
    private final Repository<Order> orderRepository;
    private final Repository<OrderItem> orderItemRepository;

    public RestaurantService(Repository<Restaurant> repository,
                             Repository<Menu> menuRepository,
                             Repository<Dish> dishRepository,
                             Repository<Image> imageRepository,
                             Repository<OpenTime> openTimeRepository,
                             Repository<Order> orderRepository,
                             Repository<OrderItem> orderItemRepository) {
        super(repository);
        this.menuRepository = menuRepository;
        this.dishRepository = dishRepository;
        this.imageRepository = imageRepository;
        this.openTimeRepository = openTimeRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public void addOpenTimesToRestaurant(Restaurant restaurant, List<OpenTime> openTimeList) {
        Map<String, Object> openTimeMap = new LinkedHashMap<>();
        for (OpenTime time : openTimeList) {
            String insertedId = openTimeRepository.insert(time).getId();
            openTimeMap.put(insertedId, true);
        }
        restaurant.setOpenTimes(openTimeMap);
    }

    public void removeOpenTimesOf(String restaurantId) {
        Restaurant restaurant = repository.get(restaurantId);
        if (restaurant == null || restaurant.getOpenTimes() == null || restaurant.getOpenTimes().isEmpty()) return;
        for (String id : restaurant.getOpenTimes().keySet()) {
            openTimeRepository.delete(id);
        }
    }

    public void addDishesAndMenusToRestaurant(Restaurant restaurant, ArrayList<Dish> dishes, ArrayList<Menu> menus) {
        Map<String, Object> dishesMap = new LinkedHashMap<>();
        for (Dish dish : dishes) {
            String insertedId = dishRepository.insert(dish).getId();
            dishesMap.put(insertedId, true);
        }
        restaurant.setDishes(dishesMap);

        Map<String, Object> menusMap = new LinkedHashMap<>();
        for (Menu menu : menus) {
            String insertedId = menuRepository.insert(menu).getId();
            menusMap.put(insertedId, true);
        }
        restaurant.setMenus(menusMap);

        repository.insert(restaurant);
    }

    public void addDishesToMenu(Menu menu, ArrayList<Dish> mainDishes, ArrayList<Dish> secondaryDishes, ArrayList<Dish> otherDishes) {
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
