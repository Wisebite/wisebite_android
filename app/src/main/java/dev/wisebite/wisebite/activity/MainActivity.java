package dev.wisebite.wisebite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.KitchenAdapter;
import dev.wisebite.wisebite.adapter.OrderAdapter;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.DownloadImageTask;
import dev.wisebite.wisebite.utils.Preferences;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private RestaurantService restaurantService;
    private UserService userService;
    private OrderService orderService;
    private String restaurantId;

    private FloatingActionButton fab;
    private NavigationView navigationView;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        restaurantService = ServiceFactory.getRestaurantService(MainActivity.this);
        userService = ServiceFactory.getUserService(MainActivity.this);
        orderService = ServiceFactory.getOrderService(MainActivity.this);
        restaurantId = userService.getFirstRestaurantId(Preferences.getCurrentUserEmail());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        setUserInfo();

        if (restaurantId != null) {
            initFragment(R.layout.content_active_orders);
            initializeActiveOrders();
        } else {
            initFragment(R.layout.content_list_restaurants);
            initializeListRestaurants();
        }

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
        if (id == R.id.action_log_out) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra(LoginActivity.ANIMATIONS, false);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create_restaurant) {
            Intent intent = new Intent(MainActivity.this, CreateRestaurantInfoActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if (id == R.id.nav_active_orders) {
            initFragment(R.layout.content_active_orders);
            initializeActiveOrders();
        } else if (id == R.id.nav_kitchen) {
            initFragment(R.layout.content_kitchen);
            initializeKitchen();
        } else if (id == R.id.nav_see_restaurant) {
            Intent intent = new Intent(MainActivity.this, GetRestaurantActivity.class);
            intent.putExtra(GetRestaurantActivity.RESTAURANT_ID, restaurantId);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if (id == R.id.nav_list_restaurants) {
            initFragment(R.layout.content_list_restaurants);
            initializeListRestaurants();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void setUserInfo() {

        if (restaurantId == null) {
            navigationView.getMenu().findItem(R.id.nav_create_restaurant).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_active_orders).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_kitchen).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_see_restaurant).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_list_restaurants).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_create_restaurant).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_active_orders).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_kitchen).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_see_restaurant).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_list_restaurants).setVisible(true);
        }

        new DownloadImageTask((ImageView) navigationView.findViewById(R.id.user_picture_nav))
                .execute(userService.getProfilePhoto(Preferences.getCurrentUserEmail()));
        TextView userName = (TextView) navigationView.findViewById(R.id.user_name_nav);
        userName.setText(userService.getUserName(Preferences.getCurrentUserEmail()));
        TextView restaurantName = (TextView) navigationView.findViewById(R.id.restaurant_name_nav);
        restaurantName.setText((restaurantId != null ? restaurantService.get(restaurantId).getName() : ""));
        navigationView.findViewById(R.id.nav_header_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetUserActivity.class);
                intent.putExtra(GetUserActivity.USER_ID, Preferences.getCurrentUserEmail());
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

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
                intent.putExtra(CreateOrderActivity.RESTAURANT_ID, restaurantId);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        OrderAdapter orderAdapter = new OrderAdapter(orderService.getActiveOrders(restaurantId), MainActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.active_order_list);
        assert recyclerView != null;
        recyclerView.setAdapter(orderAdapter);
    }

    private void initializeKitchen() {
        setTitle(getResources().getString(R.string.kitchen));
        fab.setVisibility(View.GONE);
        KitchenAdapter kitchenAdapter = new KitchenAdapter(orderService.getNonReadyOrders(restaurantId), MainActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.kitchen_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(kitchenAdapter);
    }

    private void initializeListRestaurants() {
        setTitle(getResources().getString(R.string.list_restaurants));
        fab.setVisibility(View.GONE);
    }


}
