package dev.wisebite.wisebite.comparator;

import java.util.Comparator;

import dev.wisebite.wisebite.domain.Order;

/**
 * Created by albert on 23/03/17.
 *
 * @author albert
 */
public class OrderLastDateComparator implements Comparator<Order> {

    @Override
    public int compare(Order lhs, Order rhs) {
        return (int) (lhs.getLastDate().getTime() - rhs.getLastDate().getTime());
    }

}
