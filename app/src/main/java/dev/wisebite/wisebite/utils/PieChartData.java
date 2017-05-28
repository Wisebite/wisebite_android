package dev.wisebite.wisebite.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by albert on 28/05/17.
 *
 * @author albert
 */
@Getter
@Setter
public class PieChartData {

    private float[] yData;

    private String[] xData;

    public PieChartData(Integer size) {
        this.yData = new float[size];
        this.xData = new String[size];
    }
}
