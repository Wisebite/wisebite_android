package dev.wisebite.wisebite.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.charts.MyXAxisValueFormatter;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.charts.PieChartData;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 24/05/17.
 * @author albert
 */
@SuppressLint("ValidFragment")
public class AnalyticsMonthFragment extends Fragment {

    private RestaurantService restaurantService;
    private String restaurantId;

    public AnalyticsMonthFragment(Context context, String restaurantId) {
        this.restaurantService = ServiceFactory.getRestaurantService(context);
        this.restaurantId = restaurantId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analytics_fragment, container, false);

        LinearLayout mainFragmentLayout = (LinearLayout) view.findViewById(R.id.main_fragment_layout);
        mainFragmentLayout.setBackgroundColor(getResources().getColor(R.color.month_color));

        TextView analyticsDate = (TextView) view.findViewById(R.id.analytics_date_refill);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        analyticsDate.setText(simpleDateFormat.format(Utils.getAnalyticsDate()));

        initializeValues(view);
        initializePieCharts(view);
        initializeBarCharts(view);

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

        numberOfOrders.setText(String.valueOf(restaurantService.getOrdersCount(restaurantId, Calendar.MONTH)));
        averagePrice.setText(String.format("%s €", Utils.toStringWithTwoDecimals(restaurantService.getAveragePrice(restaurantId, Calendar.MONTH))));
        totalEarned.setText(String.format("%s €", Utils.toStringWithTwoDecimals(restaurantService.getTotalEarned(restaurantId, Calendar.MONTH))));
        bestDish.setText(restaurantService.getBestDish(restaurantId, Calendar.MONTH));
        worstDish.setText(restaurantService.getWorstDish(restaurantId, Calendar.MONTH));
        bestMenu.setText(restaurantService.getBestMenu(restaurantId, Calendar.MONTH));
        worstMenu.setText(restaurantService.getWorstMenu(restaurantId, Calendar.MONTH));
        bestTimeRange.setText(restaurantService.getBestTimeRange(restaurantId, Calendar.MONTH));
        averageTime.setText(restaurantService.getAverageTime(restaurantId, Calendar.MONTH));

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

    private void createBarChart(final View view, PieChartData data, int id) {
        float[] yData = data.getYData();
        final String[] xData = data.getXData();

        BarChart barChart = (BarChart) view.findViewById(id);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < yData.length; i++) barEntries.add(new BarEntry(i, yData[i]));

        BarDataSet barDataSet;
        barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        YAxis left = barChart.getAxisLeft();
        left.setDrawLabels(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xData));

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        barChart.getLegend().setEnabled(false);

        barChart.animateY(1000);
        barChart.setHorizontalScrollBarEnabled(true);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Snackbar.make(view, "Time range: " + xData[(int) e.getX()], Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void initializePieCharts(final View view) {
        PieChartData dishesData = restaurantService.getAllDishesCount(restaurantId, Calendar.MONTH);
        if (!dishesData.isEmpty()) {
            view.findViewById(R.id.mock_first_pie).setVisibility(View.GONE);
            createPieChart(view, dishesData, R.id.best_dishes_pie_chart);
        } else {
            view.findViewById(R.id.best_dishes_pie_chart).setVisibility(View.GONE);
        }

        PieChartData menusData = restaurantService.getAllMenusCount(restaurantId, Calendar.MONTH);
        if (!menusData.isEmpty()) {
            view.findViewById(R.id.mock_second_pie).setVisibility(View.GONE);
            createPieChart(view, menusData, R.id.best_menus_pie_chart);
        } else {
            view.findViewById(R.id.best_menus_pie_chart).setVisibility(View.GONE);
        }
    }

    private void initializeBarCharts(final View view) {
        PieChartData ordersData = restaurantService.getAllOrdersCount(restaurantId, Calendar.MONTH);
        if (!ordersData.isEmpty()) {
            view.findViewById(R.id.mock_third_pie).setVisibility(View.GONE);
            createBarChart(view, ordersData, R.id.orders_time_range);
        } else {
            view.findViewById(R.id.orders_time_range).setVisibility(View.GONE);
        }
    }

}
