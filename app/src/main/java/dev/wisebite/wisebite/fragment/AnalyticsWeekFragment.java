package dev.wisebite.wisebite.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.MainActivity;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.PieChartData;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 24/05/17.
 * @author albert
 */
@SuppressLint("ValidFragment")
public class AnalyticsWeekFragment extends Fragment {

    private RestaurantService restaurantService;
    private String restaurantId;

    public AnalyticsWeekFragment(Context context, String restaurantId) {
        this.restaurantService = ServiceFactory.getRestaurantService(context);
        this.restaurantId = restaurantId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analytics_fragment, container, false);

        LinearLayout mainFragmentLayout = (LinearLayout) view.findViewById(R.id.main_fragment_layout);
        mainFragmentLayout.setBackgroundColor(getResources().getColor(R.color.week_color));

        initializeValues(view);
        initializePieCharts(view);

        return view;

    }

    private void initializeValues(View view) {
        TextView numberOfOrders = (TextView) view.findViewById(R.id.number_of_orders_refill);
        TextView averagePrice = (TextView) view.findViewById(R.id.average_price_refill);
        TextView totalEarned = (TextView) view.findViewById(R.id.total_earned_refill);
        TextView bestDish = (TextView) view.findViewById(R.id.best_dish_refill);
        TextView worstDish = (TextView) view.findViewById(R.id.worst_dish_refill);
        TextView bestMenu = (TextView) view.findViewById(R.id.best_menu_refill);
        TextView worstMenu = (TextView) view.findViewById(R.id.worst_menu_refill);
        TextView bestTimeRange = (TextView) view.findViewById(R.id.best_time_range_refill);
        TextView averageTime = (TextView) view.findViewById(R.id.average_time_refill);

        numberOfOrders.setText(String.valueOf(restaurantService.getOrdersCount(restaurantId, Calendar.WEEK_OF_YEAR)));
        averagePrice.setText(String.format("%s €", Utils.toStringWithTwoDecimals(restaurantService.getAveragePrice(restaurantId, Calendar.WEEK_OF_YEAR))));
        totalEarned.setText(String.format("%s €", Utils.toStringWithTwoDecimals(restaurantService.getTotalEarned(restaurantId, Calendar.WEEK_OF_YEAR))));
        bestDish.setText(restaurantService.getBestDish(restaurantId, Calendar.WEEK_OF_YEAR));
        worstDish.setText(restaurantService.getWorstDish(restaurantId, Calendar.WEEK_OF_YEAR));
        bestMenu.setText(restaurantService.getBestMenu(restaurantId, Calendar.WEEK_OF_YEAR));
        worstMenu.setText(restaurantService.getWorstMenu(restaurantId, Calendar.WEEK_OF_YEAR));
        bestTimeRange.setText(restaurantService.getBestTimeRange(restaurantId, Calendar.WEEK_OF_YEAR));
        averageTime.setText(restaurantService.getAverageTime(restaurantId, Calendar.WEEK_OF_YEAR));

    }

    private void createPieChart(View view, PieChartData data, int id) {
        float[] yData = data.getYData();
        final String[] xData = data.getXData();

        PieChart pieChart = (PieChart) view.findViewById(id);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(0);
        pieChart.setTransparentCircleAlpha(0);

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);

        ArrayList<PieEntry> yEntries = new ArrayList<>();
        for (int i = 0; i < yData.length; i++) yEntries.add(new PieEntry(yData[i], xData[i]));

        PieDataSet pieDataSet = new PieDataSet(yEntries, "");
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);

        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setWordWrapEnabled(true);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS) colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.setEntryLabelTextSize(0);
        pieChart.invalidate();
    }

    private void initializePieCharts(final View view) {
        PieChartData dishesData = restaurantService.getAllDishesCount(restaurantId, Calendar.WEEK_OF_YEAR);
        if (!dishesData.isEmpty()) {
            view.findViewById(R.id.mock_first_pie).setVisibility(View.GONE);
            createPieChart(view, dishesData, R.id.best_dishes_pie_chart);
        } else {
            view.findViewById(R.id.best_dishes_pie_chart).setVisibility(View.GONE);
        }

        PieChartData menusData = restaurantService.getAllMenusCount(restaurantId, Calendar.WEEK_OF_YEAR);
        if (!menusData.isEmpty()) {
            view.findViewById(R.id.mock_second_pie).setVisibility(View.GONE);
            createPieChart(view, menusData, R.id.best_menus_pie_chart);
        } else {
            view.findViewById(R.id.best_menus_pie_chart).setVisibility(View.GONE);
        }
    }

}
