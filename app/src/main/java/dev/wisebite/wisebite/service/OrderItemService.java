package dev.wisebite.wisebite.service;

import java.util.ArrayList;

import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.utils.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class OrderItemService extends Service<OrderItem> {

    private final Repository<Dish> dishRepository;
    private final Repository<Menu> menuRepository;

    public OrderItemService(Repository<OrderItem> repository,
                            Repository<Dish> dishRepository,
                            Repository<Menu> menuRepository) {
        super(repository);
        this.dishRepository = dishRepository;
        this.menuRepository = menuRepository;
    }

    public String getDishName(OrderItem current) {
        return dishRepository.get(current.getDishId()).getName();
    }

    public String getName(OrderItem current) {
        if (current.getMenuId() == null) {
            return dishRepository.get(current.getDishId()).getName();
        } else {
            return menuRepository.get(current.getMenuId()).getName();
        }
    }

    public String getDescription(OrderItem current) {
        if (current.getMenuId() == null) {
            return dishRepository.get(current.getDishId()).getDescription();
        } else {
            return menuRepository.get(current.getMenuId()).getDescription();
        }
    }

    public double getPrice(OrderItem current) {
        if (current.getMenuId() == null) {
            return dishRepository.get(current.getDishId()).getPrice();
        } else {
            return menuRepository.get(current.getMenuId()).getPrice();
        }
    }

    public double getPriceOfOrderItems(ArrayList<OrderItem> selectedItems) {
        double total = 0.0;
        for (OrderItem orderItem : selectedItems) {
            if (orderItem.getMenuId() == null) {
                total += dishRepository.get(orderItem.getDishId()).getPrice();
            } else {
                total += menuRepository.get(orderItem.getMenuId()).getPrice();
            }
        }
        return total;
    }

}
