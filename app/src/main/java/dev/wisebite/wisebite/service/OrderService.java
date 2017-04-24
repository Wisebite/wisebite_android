package dev.wisebite.wisebite.service;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.wisebite.wisebite.comparator.OrderLastDateComparator;
import dev.wisebite.wisebite.comparator.OrderStartDateComparator;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class OrderService extends Service<Order> {

    private final Repository<OrderItem> orderItemRepository;
    private final Repository<Dish> dishRepository;
    private final Repository<Menu> menuRepository;
    private final Repository<Restaurant> restaurantRepository;
    private final Repository<User> userRepository;

    public OrderService(Repository<Order> repository,
                        Repository<OrderItem> orderItemRepository,
                        Repository<Dish> dishRepository,
                        Repository<Menu> menuRepository,
                        Repository<Restaurant> restaurantRepository,
                        Repository<User> userRepository) {
        super(repository);
        this.orderItemRepository = orderItemRepository;
        this.dishRepository = dishRepository;
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public double getPriceOfOrder(String id) {
        Order order = repository.get(id);
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

    public double getReadyOfOrder(String id) {
        Order order = repository.get(id);
        if (order == null) return 100.0;

        double total = order.getOrderItems().size();
        double ready = 0.0;

        for (String orderItemId : order.getOrderItems().keySet()) {
            OrderItem orderItem = orderItemRepository.get(orderItemId);
            if (orderItem == null) return 100.0;
            if (orderItem.isReady()) ++ready;
        }

        return (ready/total)*100.0;
    }

    public double getDeliveredOfOrder(String id) {
        Order order = repository.get(id);
        if (order == null) return 100.0;

        double total = order.getOrderItems().size();
        double delivered = 0.0;

        for (String orderItemId : order.getOrderItems().keySet()) {
            OrderItem orderItem = orderItemRepository.get(orderItemId);
            if (orderItem == null) return 100.0;
            if (orderItem.isDelivered()) ++delivered;
        }

        return (delivered/total)*100.0;
    }

    public double getPaidOfOrder(String id) {
        Order order = repository.get(id);
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

    public ArrayList<Order> getActiveOrders(String restaurantId) {
        ArrayList<String> orderKeys = getOrdersOf(restaurantId);

        ArrayList<Order> orders = new ArrayList<>();
        for (String id : orderKeys) {
            Order order = repository.get(id);
            if (order == null) addNullActiveOrder(id, orders);
            else if (getPaidOfOrder(order.getId()) < 100.0) orders.add(order);
        }
        Collections.sort(orders, new OrderLastDateComparator());
        return orders;
    }

    public void addOrder(ArrayList<Dish> selectedDishes, Integer tableNumber, ArrayList<Menu> selectedMenus) {
        Map<String, Object> orderItems = new LinkedHashMap<>();
        for (Dish dish : selectedDishes) {
            String insertedId = orderItemRepository.insert(OrderItem.builder()
                    .dishId(dish.getId())
                    .ready(false)
                    .delivered(false)
                    .paid(false)
                    .build()).getId();
            orderItems.put(insertedId, true);
        }
        for (Menu menu : selectedMenus) {
            ArrayList<String> dishesId = new ArrayList<>();
            for (String key : menu.getMainDishes().keySet()) dishesId.add(key);
            for (String key : menu.getSecondaryDishes().keySet()) dishesId.add(key);
            for (String key : menu.getOtherDishes().keySet()) dishesId.add(key);
            for (String dishId : dishesId) {
                Dish dish = dishRepository.get(dishId);
                String insertedId = orderItemRepository.insert(OrderItem.builder()
                        .dishId(dish.getId())
                        .menuId(menu.getId())
                        .ready(false)
                        .delivered(false)
                        .paid(false)
                        .build()).getId();
                orderItems.put(insertedId, true);
            }
        }
        String newId = repository.insert(Order.builder()
                .date(new Date())
                .tableNumber(tableNumber)
                .lastDate(new Date())
                .orderItems(orderItems)
                .build()).getId();

        User user = userRepository.get(Preferences.getCurrentUserEmail());
        Map<String, Object> myOrders = user.getMyOrders();
        if (myOrders == null) {
            myOrders = new LinkedHashMap<>();
        }
        myOrders.put(newId, true);
        user.setMyOrders(myOrders);
        userRepository.update(user);

    }

    public void setDelivered(OrderItem item, boolean b, Order order) {
        item.setDelivered(b);
        orderItemRepository.update(item);
        order.setLastDate(new Date());
        repository.update(order);
    }

    public void setReady(OrderItem item, boolean b, Order order) {
        item.setReady(b);
        orderItemRepository.update(item);
        order.setLastDate(new Date());
        repository.update(order);
    }

    public ArrayList<OrderItem> getOnlyDishItemsOf(Order order) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem;
        for (String orderItemId : order.getOrderItems().keySet()) {
            orderItem = orderItemRepository.get(orderItemId);
            if (orderItem != null) {
                if (orderItem.getMenuId() == null) {
                    orderItems.add(orderItem);
                }
            }
        }
        return orderItems;
    }

    public ArrayList<OrderItem> getOnlyMenuItemsOf(Order order) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem;
        for (String orderItemId : order.getOrderItems().keySet()) {
            orderItem = orderItemRepository.get(orderItemId);
            if (orderItem != null) {
                if (orderItem.getMenuId() != null) {
                    orderItems.add(orderItem);
                }
            }
        }
        return orderItems;
    }

    public void collectAll(Order order) {
        OrderItem orderItem;
        for (String key : order.getOrderItems().keySet()) {
            orderItem = orderItemRepository.get(key);
            orderItem.setPaid(true);
            orderItemRepository.update(orderItem);
        }
        order.setLastDate(new Date());
        repository.update(order);
    }

    public ArrayList<OrderItem> getItemsToCollect(Order order) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();

        Map<String, List<OrderItem>> menuIds = new LinkedHashMap<>();
        OrderItem orderItem;
        for (String key : order.getOrderItems().keySet()) {
            orderItem = orderItemRepository.get(key);
            if (!orderItem.isPaid()) {
                if (orderItem.getMenuId() == null) {
                    orderItems.add(orderItem);
                } else {
                    List<OrderItem> count = menuIds.get(orderItem.getMenuId());
                    if (count == null) {
                        count = new ArrayList<>();
                    }
                    count.add(orderItem);
                    menuIds.put(orderItem.getMenuId(), count);
                }
            }
        }
        for (String key : menuIds.keySet()) {
            List<OrderItem> count = menuIds.get(key);
            Menu menu = menuRepository.get(key);
            Integer numberOptions = getNumberOptions(menu);
            for (int i = 0; i < count.size()/numberOptions; ++i) {
                orderItems.add(count.get(i));
            }
        }

        return orderItems;
    }

    public void collectSomeItems(ArrayList<OrderItem> selectedItems, Order order) {
        List<String> menus = new ArrayList<>();
        for (OrderItem item : selectedItems) {
            if (item.getMenuId() == null) {
                item.setPaid(true);
                orderItemRepository.update(item);
            } else {
                menus.add(item.getMenuId());
            }
        }
        for (String id : menus) {
            Integer numberOptions = getNumberOptions(menuRepository.get(id));
            List<OrderItem> orderItems = new ArrayList<>();
            for (String key : order.getOrderItems().keySet()) {
                if (!orderItemRepository.get(key).isPaid()) {
                    orderItems.add(orderItemRepository.get(key));
                }
            }
            for (OrderItem item : orderItems) {
                if (item.getMenuId() != null && item.getMenuId().equals(id)) {
                    item.setPaid(true);
                    orderItemRepository.update(item);
                    if (--numberOptions == 0) break;
                }
            }
        }
    }

    public boolean isPartially(Order order) {
        for (String key : order.getOrderItems().keySet()) {
            if (orderItemRepository.get(key).isPaid()) return true;
        }
        return false;
    }

    public void cancelOrder(Order order) {
        for (String key : order.getOrderItems().keySet()) {
            orderItemRepository.delete(key);
        }
        repository.delete(order.getId());
    }

    public ArrayList<Order> getNonReadyOrders(String restaurantId) {
        ArrayList<String> orderKeys = getOrdersOf(restaurantId);

        ArrayList<Order> orders = new ArrayList<>();
        for (String id : orderKeys) {
            Order order = repository.get(id);
            if (order == null) addNullNonReadyOrder(id, orders);
            else if (getReadyOfOrder(order.getId()) < 100.0) orders.add(order);
        }
        Collections.sort(orders, new OrderStartDateComparator());
        return orders;
    }

    public ArrayList<OrderItem> getNonReadyOrderItems(Order order) {
        ArrayList<OrderItem> result = new ArrayList<>();
        for (String key : order.getOrderItems().keySet()) {
            OrderItem orderItem = orderItemRepository.get(key);
            if (orderItem != null && !orderItem.isReady()) {
                result.add(orderItem);
            }
        }
        return result;
    }

    /** PRIVATE FUNCTIONS */

    private Integer getNumberOptions(Menu menu) {
        return  (!menu.getMainDishes().isEmpty() ? 1 : 0) +
                (!menu.getSecondaryDishes().isEmpty() ? 1 : 0) +
                (!menu.getOtherDishes().isEmpty() ? 1 : 0);
    }

    private ArrayList<String> getOrdersOf(String restaurantId) {
        ArrayList<String> orderKeys = new ArrayList<>();
        Restaurant restaurant = restaurantRepository.get(restaurantId);
        for (String userId : restaurant.getUsers().keySet()) {
            User user = userRepository.get(userId);
            Map<String, Object> orderIds = user.getMyOrders();
            if (orderIds != null && !orderIds.isEmpty()) {
                orderKeys.addAll(orderIds.keySet());
            }
        }
        return orderKeys;
    }

    private void addNullNonReadyOrder(final String id, final ArrayList<Order> orders) {
        new Thread(){
            @Override
            public void run() {
                Order order = repository.get(id);
                try {
                    while (order == null) {
                        sleep(1000);
                        order = repository.get(id);
                    }
                } catch (InterruptedException ex) {
                    // Catching exception
                } finally {
                    if (getReadyOfOrder(id) < 100.0) orders.add(order);
                }
            }
        }.start();
    }

    private void addNullActiveOrder(final String id, final ArrayList<Order> orders) {
        new Thread(){
            @Override
            public void run() {
                Order order = repository.get(id);
                try {
                    while (order == null) {
                        sleep(1000);
                        order = repository.get(id);
                    }
                } catch (InterruptedException ex) {
                    // Catching exception
                } finally {
                    if (getPaidOfOrder(id) < 100.0) orders.add(order);
                }
            }
        }.start();
    }

}
