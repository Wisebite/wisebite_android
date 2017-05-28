package dev.wisebite.wisebite.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 24/05/17.
 * @author albert
 */
@SuppressLint("ValidFragment")
public class AnalyticsDayFragment extends Fragment {

    private RestaurantService restaurantService;
    private String restaurantId;

    public AnalyticsDayFragment(Context context, String restaurantId) {
        this.restaurantService = ServiceFactory.getRestaurantService(context);
        this.restaurantId = restaurantId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analytics_fragment, container, false);

        LinearLayout mainFragmentLayout = (LinearLayout) view.findViewById(R.id.main_fragment_layout);
        mainFragmentLayout.setBackgroundColor(getResources().getColor(R.color.day_color));

        initializeValues(view);

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

        numberOfOrders.setText(String.valueOf(restaurantService.getOrdersCount(restaurantId, Calendar.DATE)));
        averagePrice.setText(String.format("%s €", Utils.toStringWithTwoDecimals(restaurantService.getAveragePrice(restaurantId, Calendar.DATE))));
        totalEarned.setText(String.format("%s €", Utils.toStringWithTwoDecimals(restaurantService.getTotalEarned(restaurantId, Calendar.DATE))));
        bestDish.setText(restaurantService.getBestDish(restaurantId, Calendar.DATE));
        worstDish.setText(restaurantService.getWorstDish(restaurantId, Calendar.DATE));
        bestMenu.setText(restaurantService.getBestMenu(restaurantId, Calendar.DATE));
        worstMenu.setText(restaurantService.getWorstMenu(restaurantId, Calendar.DATE));
        bestTimeRange.setText(restaurantService.getBestTimeRange(restaurantId, Calendar.DATE));

    }

}
