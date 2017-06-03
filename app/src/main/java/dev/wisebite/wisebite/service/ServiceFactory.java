package dev.wisebite.wisebite.service;

import android.content.Context;

import dev.wisebite.wisebite.repository.DishRepository;
import dev.wisebite.wisebite.repository.ImageRepository;
import dev.wisebite.wisebite.repository.MenuRepository;
import dev.wisebite.wisebite.repository.OpenTimeRepository;
import dev.wisebite.wisebite.repository.OrderItemRepository;
import dev.wisebite.wisebite.repository.OrderRepository;
import dev.wisebite.wisebite.repository.RestaurantRepository;
import dev.wisebite.wisebite.repository.ReviewRepository;
import dev.wisebite.wisebite.repository.UserRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public final class ServiceFactory {

    private static DishService dishService;
    private static ImageService imageService;
    private static MenuService menuService;
    private static OpenTimeService openTimeService;
    private static OrderItemService orderItemService;
    private static OrderService orderService;
    private static RestaurantService restaurantService;
    private static ReviewService reviewService;
    private static UserService userService;

    public static DishService getDishService(Context context){
        if (dishService == null)
            dishService = new DishService(
                    new DishRepository(context));
        return dishService;
    }

    public static ImageService getImageService(Context context){
        if (imageService == null)
            imageService = new ImageService(
                    new ImageRepository(context));
        return imageService;
    }

    public static MenuService getMenuService(Context context){
        if (menuService == null)
            menuService = new MenuService(
                    new MenuRepository(context),
                    new DishRepository(context));
        return menuService;
    }

    public static OpenTimeService getOpenTimeService(Context context){
        if (openTimeService == null)
            openTimeService = new OpenTimeService(
                    new OpenTimeRepository(context));
        return openTimeService;
    }

    public static OrderItemService getOrderItemService(Context context){
        if (orderItemService == null)
            orderItemService = new OrderItemService(
                    new OrderItemRepository(context),
                    new DishRepository(context),
                    new MenuRepository(context));
        return orderItemService;
    }

    public static OrderService getOrderService(Context context){
        if (orderService == null)
            orderService = new OrderService(
                    new OrderRepository(context),
                    new OrderItemRepository(context),
                    new DishRepository(context),
                    new MenuRepository(context),
                    new RestaurantRepository(context),
                    new UserRepository(context));
        return orderService;
    }

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

    public static ReviewService getReviewService(Context context) {
        if (reviewService == null) {
            reviewService = new ReviewService(
                    new ReviewRepository(context));
        }
        return reviewService;
    }

    public static UserService getUserService(Context context){
        if (userService == null)
            userService = new UserService(
                    new UserRepository(context),
                    new ImageRepository(context),
                    new RestaurantRepository(context),
                    new OrderRepository(context),
                    new OrderItemRepository(context));
        return userService;
    }

    public static Integer getServiceCount() {
        return 9;
    }
}
