package dev.wisebite.wisebite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.KitchenAdapter;
import dev.wisebite.wisebite.adapter.OrderAdapter;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.DownloadImageTask;
import dev.wisebite.wisebite.utils.Preferences;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RestaurantService restaurantService;
    private String restaurantId;

    private NavigationView navigationView;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        restaurantService = ServiceFactory.getRestaurantService(MainActivity.this);
        restaurantId = "-KfvAq-HC6SSapHSBzsm";

        fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        setUserInfo(navigationView);

        initFragment(R.layout.content_active_orders);
        initializeActiveOrders();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_active_orders) {
            initFragment(R.layout.content_active_orders);
            initializeActiveOrders();
        } else if (id == R.id.nav_kitchen) {
            initFragment(R.layout.content_kitchen);
            initializeKitchen();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUserInfo(NavigationView navigationView) {
        // TODO set user info when we have user model
        TextView restaurantName = (TextView) navigationView.findViewById(R.id.restaurant_name_nav);
        restaurantName.setText(restaurantService.get(restaurantId).getName());
        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetRestaurantActivity.class);
                intent.putExtra(GetRestaurantActivity.RESTAURANT_ID, restaurantId);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        new DownloadImageTask((ImageView) navigationView.findViewById(R.id.user_picture_nav))
                .execute(restaurantService.getProfilePhoto());
        TextView userName = (TextView) navigationView.findViewById(R.id.user_name_nav);
        userName.setText(restaurantService.getUserName(Preferences.getCurrentUserEmail()));

    }

    private void initFragment(int id) {
        LayoutInflater inflater = getLayoutInflater();
        View v;
        if (id != -1) v = inflater.inflate(id, null);
        else v = new View(getApplicationContext());
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_frame_layout);
        frameLayout.removeAllViews();
        frameLayout.addView(v);
    }

    private void initializeActiveOrders() {
        setTitle(getResources().getString(R.string.active_orders));
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        OrderAdapter orderAdapter = new OrderAdapter(restaurantService.getActiveOrders(), restaurantService);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.active_order_list);
        assert recyclerView != null;
        recyclerView.setAdapter(orderAdapter);
    }

    private void initializeKitchen() {
        setTitle(getResources().getString(R.string.kitchen));
        fab.setVisibility(View.GONE);
        KitchenAdapter kitchenAdapter = new KitchenAdapter(restaurantService.getNonReadyOrders(), restaurantService, MainActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.kitchen_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(kitchenAdapter);
    }

}
