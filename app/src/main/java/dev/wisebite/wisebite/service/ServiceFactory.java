package dev.wisebite.wisebite.service;

import android.content.Context;

import dev.wisebite.wisebite.repository.DishRepository;
import dev.wisebite.wisebite.repository.ImageRepository;
import dev.wisebite.wisebite.repository.MenuRepository;
import dev.wisebite.wisebite.repository.OpenTimeRepository;
import dev.wisebite.wisebite.repository.OrderItemRepository;
import dev.wisebite.wisebite.repository.OrderRepository;
import dev.wisebite.wisebite.repository.RestaurantRepository;
import dev.wisebite.wisebite.repository.UserRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public final class ServiceFactory {

    private static RestaurantService restaurantService;

    public static RestaurantService getRestaurantService(Context context){
        if (restaurantService == null)
            restaurantService = new RestaurantService(
                    new RestaurantRepository(context),
                    new MenuRepository(context),
                    new DishRepository(context),
                    new ImageRepository(context),
                    new OpenTimeRepository(context),
                    new OrderRepository(context),
                    new OrderItemRepository(context),
                    new UserRepository(context));
        return restaurantService;
    }

}
