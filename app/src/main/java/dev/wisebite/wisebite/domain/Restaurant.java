package dev.wisebite.wisebite.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.wisebite.wisebite.utils.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Restaurant implements Entity {

    private String id;
    private String name;
    private String location;
    private Integer phone;
    private String description;
    private String website;
    private Integer numberOfTables;

    private Map<String, Object> openTimes = new LinkedHashMap<>();
    private Map<String, Object> images = new LinkedHashMap<>();
    private Map<String, Object> menus = new LinkedHashMap<>();
    private Map<String, Object> dishes = new LinkedHashMap<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setOpenTimes(List<String> openTimes) {
        this.openTimes = new LinkedHashMap<>();
        for (String openTimeKey : openTimes) {
            this.openTimes.put(openTimeKey, true);
        }
    }

    public void setImages(List<String> images) {
        this.images = new LinkedHashMap<>();
        for (String imageKey : images) {
            this.images.put(imageKey, true);
        }
    }

    public void setMenus(List<String> menus) {
        this.menus = new LinkedHashMap<>();
        for (String menuKey : menus) {
            this.menus.put(menuKey, true);
        }
    }

    public void setDishes(List<String> dishes) {
        this.dishes = new LinkedHashMap<>();
        for (String dishKey : dishes) {
            this.dishes.put(dishKey, true);
        }
    }
}
