package dev.wisebite.wisebite.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Dish;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class OrderItemDishAdapter extends RecyclerView.Adapter<OrderItemDishAdapter.OrderItemDishHolder> {

    private ArrayList<Dish> dishes;
    private ArrayList<Dish> selectedDishes;
    private TextView totalPriceView;

    public OrderItemDishAdapter(ArrayList<Dish> dishes, TextView totalPriceView, ArrayList<Dish> selectedDishes) {
        this.dishes = dishes;
        this.totalPriceView = totalPriceView;
        this.selectedDishes = selectedDishes;
        notifyDataSetChanged();
    }

    @Override
    public OrderItemDishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_dish_item, parent, false);
        return new OrderItemDishHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderItemDishHolder holder, int position) {
        final Dish current = dishes.get(position);
        holder.name.setText(current.getName());
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer currentCounter = Integer.valueOf(holder.counter.getText().toString());
                if (currentCounter != 0) {
                    --currentCounter;
                    double totalPrice = Double.parseDouble(totalPriceView.getText().toString().split(" ")[0]);
                    totalPrice -= current.getPrice();
                    totalPriceView.setText(String.format("%s €", Utils.toStringWithTwoDecimals(totalPrice)));
                    selectedDishes.remove(current);
                }
                holder.counter.setText(String.valueOf(currentCounter));
            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer currentCounter = Integer.valueOf(holder.counter.getText().toString());
                ++currentCounter;
                double totalPrice = Double.parseDouble(totalPriceView.getText().toString().split(" ")[0]);
                totalPrice += current.getPrice();
                totalPriceView.setText(String.format("%s €", Utils.toStringWithTwoDecimals(totalPrice)));
                selectedDishes.add(current);
                holder.counter.setText(String.valueOf(currentCounter));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    class OrderItemDishHolder extends RecyclerView.ViewHolder {

        public View view;
        TextView name;
        TextView counter;
        Button minus;
        Button plus;

        OrderItemDishHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.counter = (TextView) itemView.findViewById(R.id.counter);
            this.minus = (Button) itemView.findViewById(R.id.minus);
            this.plus = (Button) itemView.findViewById(R.id.plus);
        }
    }
}
