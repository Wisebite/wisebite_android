package dev.wisebite.wisebite.domain;

import java.util.Map;

import dev.wisebite.wisebite.utils.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by albert on 14/04/17.
 * @author albert
 */
@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
@ToString
@Builder
public class User implements Entity {

    private String id;
    private String email;
    private String name;
    private String lastName;
    private String location;
    private String imageId;

    private Map<String, Object> myRestaurants;
    private Map<String, Object> myOrders;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
