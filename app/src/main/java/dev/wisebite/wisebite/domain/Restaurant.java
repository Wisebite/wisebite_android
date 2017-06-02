package dev.wisebite.wisebite.domain;

import java.util.LinkedHashMap;
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
@AllArgsConstructor(suppressConstructorProperties = true)
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
    private Map<String, Object> menus = new LinkedHashMap<>();
    private Map<String, Object> dishes = new LinkedHashMap<>();
    private Map<String, Object> users = new LinkedHashMap<>();
    private Map<String, Object> externalOrders = new LinkedHashMap<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
