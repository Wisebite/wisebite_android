package dev.wisebite.wisebite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.DownloadImageTask;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Utils;

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
                Intent intent = new Intent(GetUserActivity.this, EditUserActivity.class);
                intent.putExtra(EditUserActivity.USER_ID, user.getId());
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        initializeView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeView() {
        String url = userService.getProfilePhoto(user.getId());
        if (url != null) {
            new DownloadImageTask((ImageView) findViewById(R.id.user_picture_nav))
                    .execute(url);
        }

        TextView nameView = (TextView) findViewById(R.id.name_refill);
        TextView lastNameView = (TextView) findViewById(R.id.last_name_refill);
        TextView emailView = (TextView) findViewById(R.id.email_refill);
        TextView locationView = (TextView) findViewById(R.id.location_refill);
        if (!Utils.isEmpty(user.getName())) nameView.setText(user.getName());
        if (!Utils.isEmpty(user.getLastName())) lastNameView.setText(user.getLastName());
        if (!Utils.isEmpty(user.getEmail())) emailView.setText(user.getEmail());
        if (!Utils.isEmpty(user.getLocation())) locationView.setText(user.getLocation());

        String myRestaurant = userService.getFirstRestaurantName(user.getId());

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

}
