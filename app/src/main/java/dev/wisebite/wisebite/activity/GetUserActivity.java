package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.DownloadImageTask;

public class GetUserActivity extends BaseActivity {

    public static final String USER_ID = "USER_ID";

    private UserService userService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userService = ServiceFactory.getUserService(GetUserActivity.this);
        if (getIntent().getSerializableExtra(USER_ID) != null) {
            this.user = userService.get(getIntent().getExtras().getString(USER_ID));
        }
        setTitle(user.getName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeView();

    }

    private void initializeView() {
        new DownloadImageTask((ImageView) findViewById(R.id.user_picture_nav))
                .execute(userService.getProfilePhoto(user.getId()));

        TextView nameView = (TextView) findViewById(R.id.name_refill);
        TextView lastNameView = (TextView) findViewById(R.id.last_name_refill);
        TextView emailView = (TextView) findViewById(R.id.email_refill);
        TextView locationView = (TextView) findViewById(R.id.location_refill);
        if (!isEmpty(user.getName())) nameView.setText(user.getName());
        if (!isEmpty(user.getLastName())) lastNameView.setText(user.getLastName());
        if (!isEmpty(user.getEmail())) emailView.setText(user.getEmail());
        if (!isEmpty(user.getLocation())) locationView.setText(user.getLocation());

        String myRestaurant = userService.getFirstRestaurantId(user.getId());
        if (myRestaurant == null) {
            findViewById(R.id.my_restaurant_layout).setVisibility(View.GONE);
        } else {
            TextView myRestaurantsView = (TextView) findViewById(R.id.my_restaurant_refill);
            myRestaurantsView.setText(myRestaurant);
        }

        Integer orderCount = userService.getOrderCount(user.getId());
        if (orderCount == null) {
            findViewById(R.id.order_count_layout).setVisibility(View.GONE);
        } else {
            TextView orderCountView = (TextView) findViewById(R.id.order_count_refill);
            orderCountView.setText(String.valueOf(orderCount));
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
