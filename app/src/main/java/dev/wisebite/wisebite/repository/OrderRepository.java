package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.firebase.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class OrderRepository extends FirebaseRepository<Order> {

    public static final String OBJECT_REFERENCE = "order";
    public static final String DATE_REFERENCE = "date";
    public static final String TABLE_NUMBER_REFERENCE = "tableNumber";
    public static final String LAST_DATE_REFERENCE = "lastDate";

    public static final String ORDER_ITEMS_REFERENCE = "orderItems";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public OrderRepository(Context context) {
        super(context);
    }

    @Override
    protected Order convert(DataSnapshot data) {
        Order order = new Order();
        order.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(DATE_REFERENCE)) {
                order.setDate(d.getValue(Date.class));
            } else if (d.getKey().equals(TABLE_NUMBER_REFERENCE)) {
                order.setTableNumber(d.getValue(Integer.class));
            } else if (d.getKey().equals(LAST_DATE_REFERENCE)) {
                order.setLastDate(d.getValue(Date.class));
            } else if (d.getKey().equals(ORDER_ITEMS_REFERENCE)) {
                Map<String, Object> orderItems = new LinkedHashMap<>();
                for (DataSnapshot orderItem : d.getChildren()) {
                    orderItems.put(orderItem.getKey(), true);
                }
                order.setOrderItems(orderItems);
            }
        }
        return order;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }

}
