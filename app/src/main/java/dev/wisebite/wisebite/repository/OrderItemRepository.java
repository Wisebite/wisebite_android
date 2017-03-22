package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.utils.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class OrderItemRepository extends FirebaseRepository<OrderItem> {

    public static final String OBJECT_REFERENCE = "orderItem";
    public static final String DIFFERENT_FEATURE_REFERENCE = "differentFeature";
    public static final String PAYED_REFERENCE = "paid";
    public static final String READY_REFERENCE = "ready";
    public static final String DELIVERED_REFERENCE = "delivered";
    public static final String DISH_ID_REFERENCE = "dishId";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public OrderItemRepository(Context context) {
        super(context);
    }

    @Override
    protected OrderItem convert(DataSnapshot data) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(DIFFERENT_FEATURE_REFERENCE)) {
                orderItem.setDifferentFeature(d.getValue(String.class));
            } else if (d.getKey().equals(PAYED_REFERENCE)) {
                orderItem.setPaid(d.getValue(Boolean.class));
            } else if (d.getKey().equals(READY_REFERENCE)) {
                orderItem.setReady(d.getValue(Boolean.class));
            } else if (d.getKey().equals(DELIVERED_REFERENCE)) {
                orderItem.setDelivered(d.getValue(Boolean.class));
            } else if (d.getKey().equals(DISH_ID_REFERENCE)) {
                orderItem.setDishId(d.getValue(String.class));
            }
        }
        return orderItem;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }

}
