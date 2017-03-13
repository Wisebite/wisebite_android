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
public class Menu implements Entity {

    private String id;
    private String name;
    private Double price;
    private String description;

    private Map<String, Object> dishes = new LinkedHashMap<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setDishes(List<String> dishes) {
        this.dishes = new LinkedHashMap<>();
        for (String dishKey : dishes) {
            this.dishes.put(dishKey, true);
        }
    }
}
