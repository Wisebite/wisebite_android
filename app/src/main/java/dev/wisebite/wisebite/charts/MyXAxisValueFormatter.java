package dev.wisebite.wisebite.charts;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by albert on 29/05/17.
 *
 * @author albert
 */
public class MyXAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;
    private Map<Integer, Boolean> mMap;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
        this.mMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) mMap.put(i, false);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (!mMap.get((int) value)) {
            mMap.put((int) value, true);
            return mValues[(int) value];
        } else {
            return "";
        }
    }
}
