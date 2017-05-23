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
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class RestaurantService extends Service<Restaurant> {

    private final Repository<Menu> menuRepository;
    private final Repository<Dish> dishRepository;
    private final Repository<Image> imageRepository;
    private final Repository<OpenTime> openTimeRepository;
    private final Repository<Order> orderRepository;
    private final Repository<OrderItem> orderItemRepository;
    private final Repository<User> userRepository;

    public RestaurantService(Repository<Restaurant> repository,
                             Repository<Menu> menuRepository,
                             Repository<Dish> dishRepository,
                             Repository<Image> imageRepository,
                             Repository<OpenTime> openTimeRepository,
                             Repository<Order> orderRepository,
                             Repository<OrderItem> orderItemRepository,
                             Repository<User> userRepository) {
        super(repository);
        this.menuRepository = menuRepository;
        this.dishRepository = dishRepository;
        this.imageRepository = imageRepository;
        this.openTimeRepository = openTimeRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    public void addOpenTimes(Restaurant restaurant, List<OpenTime> openTimeList) {
        Map<String, Object> openTimeMap = new LinkedHashMap<>();
        for (OpenTime time : openTimeList) {
            String insertedId = openTimeRepository.insert(time).getId();
            openTimeMap.put(insertedId, true);
        }
        restaurant.setOpenTimes(openTimeMap);
    }

    public void removeOpenTimes(String restaurantId) {
        Restaurant restaurant = repository.get(restaurantId);
        if (restaurant == null || restaurant.getOpenTimes() == null || restaurant.getOpenTimes().isEmpty()) return;
        for (String id : restaurant.getOpenTimes().keySet()) {
            openTimeRepository.delete(id);
        }
    }

    public void addDishesAndMenus(Restaurant restaurant, ArrayList<Dish> dishes, ArrayList<Menu> menus) {
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

        String newId = repository.insert(restaurant).getId();
        addUserToRestaurant(newId, Preferences.getCurrentUserEmail());
    }

    public ArrayList<Dish> getDishes(String restaurantId) {
        ArrayList<Dish> dishes = new ArrayList<>();
        Restaurant restaurant = repository.get(restaurantId);
        if (restaurant != null) {
            for (String dishId : restaurant.getDishes().keySet()) {
                dishes.add(dishRepository.get(dishId));
            }
        }
        return dishes;
    }

    public ArrayList<Menu> getMenus(String restaurantId) {
        ArrayList<Menu> menus = new ArrayList<>();
        Restaurant restaurant = repository.get(restaurantId);
        if (restaurant != null) {
            for (String menuId : restaurant.getMenus().keySet()) {
                menus.add(menuRepository.get(menuId));
            }
        }
        return menus;
    }

    public List<OpenTime> getOpenTimes(Restaurant restaurant) {
        List<OpenTime> result = new ArrayList<>();
        for (String key : restaurant.getOpenTimes().keySet()) {
            result.add(openTimeRepository.get(key));
        }
        return result;
    }

    public void addUserToRestaurant(String restaurantId, String userId) {
        Restaurant restaurant = repository.get(restaurantId);
        Map<String, Object> users = restaurant.getUsers();
        if (users == null) {
            users = new LinkedHashMap<>();
        }
        users.put(userId, true);
        restaurant.setUsers(users);
        repository.update(restaurant);

        User user = userRepository.get(userId);
        Map<String, Object> restaurants = user.getMyRestaurants();
        if (restaurants == null) {
            restaurants = new LinkedHashMap<>();
        }
        restaurants.put(restaurantId, true);
        user.setMyRestaurants(restaurants);
        userRepository.update(user);
    }

    public Integer getDaysOpen(Restaurant current) {
        return current.getOpenTimes().size();
    }

    public Integer getDishesCount(Restaurant current) {
        return current.getDishes().size();
    }

    public Integer getMenusCount(Restaurant current) {
        return current.getMenus().size();
    }
}
