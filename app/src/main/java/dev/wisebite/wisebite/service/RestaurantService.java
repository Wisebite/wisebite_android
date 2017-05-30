package dev.wisebite.wisebite.service;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import dev.wisebite.wisebite.charts.PieChartData;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;
import dev.wisebite.wisebite.utils.Utils;

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

    public boolean isPartOfTheStuff(String restaurantId, String user) {
        Restaurant restaurant = repository.get(restaurantId);
        for (String userKey : restaurant.getUsers().keySet()) {
            if (userKey.equals(user)) return true;
        }
        return false;
    }

    private List<String> getOrders(String restaurantId) {
        Restaurant restaurant = repository.get(restaurantId);
        List<String> ordersList = new ArrayList<>();

        User user;
        for (String userKey : restaurant.getUsers().keySet()) {
            user = userRepository.get(userKey);
            if (user != null && user.getMyOrders() != null) ordersList.addAll(user.getMyOrders().keySet());
        }

        return ordersList;
    }

    private boolean checkTime(Order order, int kind) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(Utils.getAnalyticsDate());
        Calendar orderCalendar = Calendar.getInstance();
        orderCalendar.setTime(order.getDate());
        switch (kind) {
            case Calendar.DATE:
                if (orderCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        orderCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                        orderCalendar.get(Calendar.DATE) == currentCalendar.get(Calendar.DAY_OF_MONTH)) {
                    return true;
                }
                break;
            case Calendar.WEEK_OF_YEAR:
                if (orderCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        orderCalendar.get(Calendar.WEEK_OF_YEAR) == currentCalendar.get(Calendar.WEEK_OF_YEAR)) {
                    return true;
                }
                break;
            case Calendar.MONTH:
                if (orderCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        orderCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private Integer getNumberOptions(Menu menu) {
        return  (!menu.getMainDishes().isEmpty() ? 1 : 0) +
                (!menu.getSecondaryDishes().isEmpty() ? 1 : 0) +
                (!menu.getOtherDishes().isEmpty() ? 1 : 0);
    }

    private double getPriceOfOrder(String id) {
        Order order = orderRepository.get(id);
        if (order == null) return 0.0;

        Map<String, List<String>> menuMap = new LinkedHashMap<>();

        double total = 0.0;
        for (String orderItemId : order.getOrderItems().keySet()) {
            OrderItem orderItem = orderItemRepository.get(orderItemId);
            if (orderItem != null) {
                if (orderItem.getMenuId() == null) {
                    total += dishRepository.get(orderItem.getDishId()).getPrice();
                } else {
                    List<String> dishes = menuMap.get(orderItem.getMenuId());
                    if (dishes == null) {
                        dishes = new ArrayList<>();
                    }
                    dishes.add(orderItem.getDishId());
                    menuMap.put(orderItem.getMenuId(), dishes);
                }
            }
        }
        Menu menu;
        for (String key : menuMap.keySet()) {
            menu = menuRepository.get(key);
            Integer numberOptions = getNumberOptions(menu);
            double totalMenus = (double) (menuMap.get(key).size() / numberOptions);
            total += totalMenus*menu.getPrice();
        }

        return total;
    }

    private Map<String, Integer> getDishesCountMap(String restaurantId, int kind) {
        List<String> ordersList = getOrders(restaurantId);
        if (ordersList.isEmpty()) return new HashMap<>();

        Map<String, Integer> map = new HashMap<>();
        for (Dish dish : getDishes(restaurantId)) {
            map.put(dish.getId(), 0);
        }

        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (order.getOrderItems() == null || !checkTime(order, kind)) continue;

            OrderItem orderItem;
            for (String orderItemKey : order.getOrderItems().keySet()) {
                orderItem = orderItemRepository.get(orderItemKey);
                if (orderItem.getMenuId() == null) {
                    Integer count = map.get(orderItem.getDishId()) + 1;
                    map.put(orderItem.getDishId(), count);
                }
            }
        }

        return map;
    }

    private Map<String, Integer> getMenusCountMap(String restaurantId, int kind) {
        List<String> ordersList = getOrders(restaurantId);
        if (ordersList.isEmpty()) return new HashMap<>();

        Map<String, Integer> map = new HashMap<>();
        for (Menu menu : getMenus(restaurantId)) {
            map.put(menu.getId(), 0);
        }

        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (order.getOrderItems() == null || !checkTime(order, kind)) continue;

            OrderItem orderItem;
            for (String orderItemKey : order.getOrderItems().keySet()) {
                orderItem = orderItemRepository.get(orderItemKey);
                if (orderItem.getMenuId() != null) {
                    Integer count = map.get(orderItem.getMenuId()) + 1;
                    map.put(orderItem.getMenuId(), count);
                }
            }
        }

        return map;
    }

    private double getPaidOfOrder(String id) {
        Order order = orderRepository.get(id);
        if (order == null) return 100.0;

        Map<String, List<Pair<String, Boolean>>> menuMap = new LinkedHashMap<>();
        double total = getPriceOfOrder(id);
        double paid = 0.0;

        for (String orderItemId : order.getOrderItems().keySet()) {
            OrderItem orderItem = orderItemRepository.get(orderItemId);
            if (orderItem != null) {
                if (orderItem.getMenuId() == null) {
                    if (orderItem.isPaid()) {
                        paid += dishRepository.get(orderItem.getDishId()).getPrice();
                    }
                } else {
                    List<Pair<String, Boolean>> dishes = menuMap.get(orderItem.getMenuId());
                    if (dishes == null) {
                        dishes = new ArrayList<>();
                    }
                    dishes.add(new Pair<>(orderItem.getDishId(), orderItem.isPaid()));
                    menuMap.put(orderItem.getMenuId(), dishes);
                }
            }
        }
        Menu menu;
        for (String key : menuMap.keySet()) {
            menu = menuRepository.get(key);

            Integer numberPaid = 0;
            for (Pair<String, Boolean> pair : menuMap.get(key)) {
                if (pair.second) ++numberPaid;
            }
            if (numberPaid != 0) {
                double totalPaid = (double) (numberPaid / menuMap.get(key).size());
                double totalMenus = (double) (menuMap.get(key).size() / getNumberOptions(menu));
                paid += totalPaid*totalMenus*menu.getPrice();
            }
        }

        return (paid/total)*100.0;
    }

    public Integer getOrdersCount(String restaurantId, int kind) {
        Integer count = 0;

        List<String> ordersList = getOrders(restaurantId);
        if (ordersList.isEmpty()) return 0;

        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (checkTime(order, kind)) ++count;
        }
        return count;
    }

    public Double getAveragePrice(String restaurantId, int kind) {
        double total = 0.0;
        double count = 0.0;

        List<String> ordersList = getOrders(restaurantId);
        if (ordersList.isEmpty()) return 0.0;

        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (checkTime(order, kind)) {
                total += getPriceOfOrder(orderKey);
                count += 1.0;
            }
        }
        return total/count;
    }

    public Double getTotalEarned(String restaurantId, int kind) {
        double total = 0.0;

        List<String> ordersList = getOrders(restaurantId);
        if (ordersList.isEmpty()) return 0.0;

        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (checkTime(order, kind)) total += getPriceOfOrder(orderKey);
        }
        return total;
    }

    public String getBestDish(String restaurantId, int kind) {
        Map<String, Integer> map = getDishesCountMap(restaurantId, kind);

        String bestId = "";
        Integer max = -1;
        for (String key : map.keySet()) {
            Integer current = map.get(key);
            if (current > max) {
                max = current;
                bestId = key;
            }
        }

        if (bestId.isEmpty()) return "---";
        else return dishRepository.get(bestId).getName() + ": " + max + " times.";
    }

    public String getWorstDish(String restaurantId, int kind) {
        Map<String, Integer> map = getDishesCountMap(restaurantId, kind);

        String bestId = "";
        Integer min = Integer.MAX_VALUE;
        for (String key : map.keySet()) {
            Integer current = map.get(key);
            if (current < min) {
                min = current;
                bestId = key;
            }
        }

        if (bestId.isEmpty()) return "---";
        else return dishRepository.get(bestId).getName() + ": " + min + " times.";
    }

    public String getBestMenu(String restaurantId, int kind) {
        Map<String, Integer> map = getMenusCountMap(restaurantId, kind);

        String bestId = "";
        Integer max = -1;
        for (String key : map.keySet()) {
            Integer current = map.get(key)/getNumberOptions(menuRepository.get(key));
            if (current > max) {
                max = current;
                bestId = key;
            }
        }

        if (bestId.isEmpty()) return "---";
        else return menuRepository.get(bestId).getName() + ": " + max + " times.";
    }

    public String getWorstMenu(String restaurantId, int kind) {
        Map<String, Integer> map = getMenusCountMap(restaurantId, kind);

        String bestId = "";
        Integer min = Integer.MAX_VALUE;
        for (String key : map.keySet()) {
            Integer current = map.get(key)/getNumberOptions(menuRepository.get(key));
            if (current < min) {
                min = current;
                bestId = key;
            }
        }

        if (bestId.isEmpty()) return "---";
        else return menuRepository.get(bestId).getName() + ": " + min + " times.";
    }

    public String getBestTimeRange(String restaurantId, int kind) {
        Integer[] hours = new Integer[24];
        for (int i = 0; i < hours.length; i++) hours[i] = 0;

        List<String> ordersList = getOrders(restaurantId);
        if (ordersList.isEmpty()) return "---";

        Calendar calendar = Calendar.getInstance();
        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (checkTime(order, kind)) {
                calendar.setTime(order.getDate());
                ++hours[calendar.get(Calendar.HOUR_OF_DAY)];
            }
        }

        int max = 0;
        int index = -1;
        for (int i = 0; i < hours.length; i++) {
            if (hours[i] > max) {
                max = hours[i];
                index = i;
            }
        }

        if (index == -1) return "---";

        String result = "";
        if (index < 10) result += '0';
        result += String.valueOf(index) + "h - ";
        ++index;
        if (index < 10) result += '0';
        result += String.valueOf(index) + "h";
        return result;
    }

    public String getAverageTime(String restaurantId, int kind) {
        long total = 0;
        long count = 0;

        List<String> ordersList = getOrders(restaurantId);
        if (ordersList.isEmpty()) return "No finished orders";

        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (checkTime(order, kind) && getPaidOfOrder(orderKey) == 100.0) {
                total += (order.getLastDate().getTime() - order.getDate().getTime());
                ++count;
            }
        }

        if (count == 0) return "No finished orders";

        long averageTime = total/count;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(averageTime);

        return  String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + "h " +
                String.valueOf(calendar.get(Calendar.MINUTE) + "min");
    }

    public PieChartData getAllDishesCount(String restaurantId, int kind) {
        Map<String, Integer> map = getDishesCountMap(restaurantId, kind);

        Integer count = 0;
        for (Integer val : map.values()) {
            if (val != 0) ++count;
        }

        PieChartData data = new PieChartData(count);
        String[] xData = data.getXData();
        float[] yData = data.getYData();

        Float total = 0.0f;
        for (Integer value : map.values()) total += value;

        Integer i = 0;
        for (String key : map.keySet()) {
            if (map.get(key) != 0) {
                String name = dishRepository.get(key).getName();
                Float percent = (map.get(key) / total) * 100.0f;
                xData[i] = name;
                yData[i] = percent;
                i++;
            }
        }

        data.setXData(xData);
        data.setYData(yData);
        return data;
    }

    public PieChartData getAllMenusCount(String restaurantId, int kind) {
        Map<String, Integer> map = getMenusCountMap(restaurantId, kind);

        Integer count = 0;
        for (Integer val : map.values()) {
            if (val != 0) ++count;
        }

        Menu menu;
        for (String key : map.keySet()) {
            menu = menuRepository.get(key);
            Integer current = map.get(key)/getNumberOptions(menu);
            map.put(key, current);
        }

        PieChartData data = new PieChartData(count);
        String[] xData = data.getXData();
        float[] yData = data.getYData();

        Float total = 0.0f;
        for (Integer value : map.values()) total += value;

        Integer i = 0;
        for (String key : map.keySet()) {
            if (map.get(key) != 0) {
                String name = menuRepository.get(key).getName();
                Float percent = (map.get(key) / total) * 100.0f;
                xData[i] = name;
                yData[i] = percent;
                i++;
            }
        }

        data.setXData(xData);
        data.setYData(yData);
        return data;
    }

    public PieChartData getAllOrdersCount(String restaurantId, int kind) {
        float[] time;
        if (kind == Calendar.DATE) time = new float[24];
        else if (kind == Calendar.WEEK_OF_YEAR) time = new float[7];
        else time = new float[31];
        for (int i = 0; i < time.length; i++) time[i] = 0;

        List<String> ordersList = getOrders(restaurantId);

        Calendar calendar = Calendar.getInstance();
        Order order;
        for (String orderKey : ordersList) {
            order = orderRepository.get(orderKey);
            if (checkTime(order, kind)) {
                calendar.setTime(order.getDate());
                if (kind == Calendar.DATE)
                    time[calendar.get(Calendar.HOUR_OF_DAY)] += 1.0f;
                else if (kind == Calendar.WEEK_OF_YEAR)
                    time[calendar.get(Calendar.DAY_OF_WEEK)-1] += 1.0f;
                else
                    time[calendar.get(Calendar.DAY_OF_MONTH)] += 1.0f;
            }
        }

        Integer count = 0;
        for (float t : time) if (t != 0) ++count;

        PieChartData data = new PieChartData(count);
        String[] xData = data.getXData();
        float[] yData = data.getYData();

        Integer j = 0;
        for (int i = 0; i < time.length; i++) {
            if (time[i] != 0) {
                yData[j] = time[i];


                String label = "";
                if (kind == Calendar.DATE) {
                    if (i < 10) label += '0';
                    label += String.valueOf(i) + "h - ";
                    if (i + 1 < 10) label += '0';
                    label += String.valueOf(i + 1) + 'h';
                } else if (kind == Calendar.WEEK_OF_YEAR) {
                    if (i == 0) label = "Sunday";
                    else if (i == 1) label = "Monday";
                    else if (i == 2) label = "Tuesday";
                    else if (i == 3) label = "Wednesday";
                    else if (i == 4) label = "Thursday";
                    else if (i == 5) label = "Friday";
                    else if (i == 6) label = "Saturday";
                } else {
                    label = "Day number " + String.valueOf(i) + " of the month.";
                }
                xData[j] = label;

                ++j;
            }
        }

        data.setXData(xData);
        data.setYData(yData);
        return data;
    }

    public boolean addNewUserToRestaurant(String restaurantId, String userToAdd) {
        Boolean found = false;
        for (User user : userRepository.all()) {
            if (user.getEmail().equals(userToAdd)) {
                found = true;
                break;
            }
        }
        if (!found) return false;

        for (Restaurant restaurant : repository.all()) {
            if (restaurant.getUsers() == null) continue;
            User user;
            for (String userKey : restaurant.getUsers().keySet()) {
                user = userRepository.get(userKey);
                if (user.getEmail().equals(userToAdd)) return false;
            }
        }

        addUserToRestaurant(restaurantId, Utils.skipAts(userToAdd));

        return true;
    }
}
