package dev.wisebite.wisebite.domain;

import java.util.Date;

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
public class OpenTime implements Entity {

    private String id;
    private Date startDate;
    private Date endDate;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
