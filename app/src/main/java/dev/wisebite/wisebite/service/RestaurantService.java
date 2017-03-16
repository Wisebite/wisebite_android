package dev.wisebite.wisebite.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.domain.Menu;
import dev.wisebite.wisebite.domain.OpenTime;
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

    public RestaurantService(Repository<Restaurant> repository,
                             Repository<Menu> menuRepository,
                             Repository<Dish> dishRepository,
                             Repository<Image> imageRepository,
                             Repository<OpenTime> openTimeRepository) {
        super(repository);
        this.menuRepository = menuRepository;
        this.dishRepository = dishRepository;
        this.imageRepository = imageRepository;
        this.openTimeRepository = openTimeRepository;
    }

    public void addOpenTimesToRestaurant(Restaurant restaurant, List<OpenTime> openTimeList) {
        Map<String, Object> openTimeMap = new LinkedHashMap<>();
        for (OpenTime time : openTimeList) {
            String insertedId = openTimeRepository.insert(time).getId();
            openTimeMap.put(insertedId, true);
        }
        restaurant.setOpenTimes(openTimeMap);
    }
}
