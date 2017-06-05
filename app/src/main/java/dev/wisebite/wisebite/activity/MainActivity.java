package dev.wisebite.wisebite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.adapter.FragmentAdapter;
import dev.wisebite.wisebite.adapter.KitchenAdapter;
import dev.wisebite.wisebite.adapter.OrderAdapter;
import dev.wisebite.wisebite.adapter.OrderItemAdapter;
import dev.wisebite.wisebite.adapter.RestaurantAdapter;
import dev.wisebite.wisebite.adapter.ReviewOrderAdapter;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.domain.OrderItem;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.fragment.AnalyticsDayFragment;
import dev.wisebite.wisebite.fragment.AnalyticsMonthFragment;
import dev.wisebite.wisebite.fragment.AnalyticsWeekFragment;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Utils;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private RestaurantService restaurantService;
    private UserService userService;
    private OrderService orderService;
    private String restaurantId;
    private Order currentOrder;
    private LayoutInflater inflater;

    private FloatingActionButton fab;
    private NavigationView navigationView;
    private GoogleApiClient mGoogleApiClient;
    private TabLayout tabs;
    private AppBarLayout appBar;
    private Menu menu;
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
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        inflater = LayoutInflater.from(MainActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        setUserInfo();

        if (restaurantId != null) {
            initFragment(R.layout.content_active_orders);
            initializeActiveOrders();
            navigationView.getMenu().getItem(1).setChecked(true);
        } else {
            initFragment(R.layout.content_list_restaurants);
            initializeListRestaurants();
            navigationView.getMenu().getItem(4).setChecked(true);
        }

        Utils.setAnalyticsDate(new Date());

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
        checkListsSize();
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
        this.menu = menu;
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
        } else if (id == R.id.action_change_day) {
            final DatePicker datePicker = new DatePicker(MainActivity.this);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.getAnalyticsDate());
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.change_date))
                    .setMessage(getResources().getString(R.string.change_date_message))
                    .setView(datePicker)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(0);
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            Utils.setAnalyticsDate(calendar.getTime());
                            initializeAnalytics();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // do nothing
                        }
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
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
        } else if (id == R.id.nav_analytics) {
            initFragment(R.layout.content_analytics);
            initializeAnalytics();
        } else if (id == R.id.nav_current_order) {
            initFragment(R.layout.current_order);
            initializeCurrentOrder();
        } else if (id == R.id.nav_pending_reviews) {
            initFragment(R.layout.pending_reviews);
            initializePendingReviews();
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
            navigationView.getMenu().findItem(R.id.nav_analytics).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.nav_create_restaurant).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_active_orders).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_kitchen).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_see_restaurant).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_list_restaurants).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_analytics).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_current_order).setVisible(false);
        }

        this.currentOrder = userService.hasActiveOrder(Preferences.getCurrentUserEmail()); 
        if (this.currentOrder != null) {
            navigationView.getMenu().findItem(R.id.nav_current_order).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_current_order).setVisible(false);
        }

        String url = userService.getProfilePhoto(Preferences.getCurrentUserEmail());
        if (url != null) {
            Picasso.with(MainActivity.this).load(url)
                    .into((ImageView) navigationView.findViewById(R.id.user_picture_nav));
        }
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

    private void checkListsSize() {
        if (findViewById(R.id.mock_active_orders) != null)
            checkList(R.id.mock_active_orders, orderService.getActiveOrders(restaurantId).size());
        if (findViewById(R.id.mock_kitchen) != null)
            checkList(R.id.mock_kitchen, orderService.getNonReadyOrders(restaurantId).size());
        if (findViewById(R.id.restaurant_mock) != null)
            checkList(R.id.restaurant_mock, restaurantService.getAll().size());
    }

    private void checkList(int resourceId, int size) {
        if (size != 0) findViewById(resourceId).setVisibility(View.GONE);
        else findViewById(resourceId).setVisibility(View.VISIBLE);
    }

    private void removeTabs() {
        if (tabs != null) {
            appBar.removeView(tabs);
        }
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

    private void showTableNumberForm(final View view) {
        final LinearLayout tableNumberForm = (LinearLayout) inflater.inflate(getResources().getLayout(R.layout.table_number_form), null);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.title_table_number_form))
                .setView(tableNumberForm)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tableNumberView = (TextView) tableNumberForm.findViewById(R.id.form_number);
                        if (!tableNumberView.getText().toString().trim().isEmpty()) {
                            Integer tableNumber = Integer.valueOf(tableNumberView.getText().toString());
                            Integer maxTableNumber = restaurantService.get(restaurantId).getNumberOfTables();

                            if (tableNumber <= 0 || tableNumber > maxTableNumber) {
                                Snackbar.make(view, getResources().getString(R.string.no_exists_table), Snackbar.LENGTH_LONG).show();
                            } else {
                                Intent intent = new Intent(MainActivity.this, CreateOrderActivity.class);
                                intent.putExtra(CreateOrderActivity.RESTAURANT_ID, restaurantId);
                                intent.putExtra(CreateOrderActivity.TABLE_NUMBER, tableNumber);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            }

                        } else {
                            Snackbar.make(view, getResources().getString(R.string.no_select_table), Snackbar.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do nothing
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void initializeActiveOrders() {
        setTitle(getResources().getString(R.string.active_orders));
        removeTabs();
        if (this.menu != null) this.menu.findItem(R.id.action_change_day).setVisible(false);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTableNumberForm(view);
            }
        });
        ArrayList<Order> ordersList = orderService.getActiveOrders(restaurantId);
        checkList(R.id.mock_active_orders, ordersList.size());
        OrderAdapter orderAdapter = new OrderAdapter(ordersList, MainActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.active_order_list);
        assert recyclerView != null;
        recyclerView.setAdapter(orderAdapter);
    }

    private void initializeKitchen() {
        setTitle(getResources().getString(R.string.kitchen));
        removeTabs();
        if (this.menu != null) this.menu.findItem(R.id.action_change_day).setVisible(false);
        fab.setVisibility(View.GONE);
        ArrayList<Order> ordersList = orderService.getNonReadyOrders(restaurantId);
        checkList(R.id.mock_kitchen, ordersList.size());
        KitchenAdapter kitchenAdapter = new KitchenAdapter(ordersList, MainActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.kitchen_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(kitchenAdapter);
    }

    private void initializeListRestaurants() {
        setTitle(getResources().getString(R.string.list_restaurants));
        removeTabs();
        if (this.menu != null) this.menu.findItem(R.id.action_change_day).setVisible(false);
        fab.setVisibility(View.GONE);
        List<Restaurant> restaurantsList = restaurantService.getAll();
        checkList(R.id.restaurant_mock, restaurantsList.size());
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(restaurantsList, MainActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.restaurant_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(restaurantAdapter);
    }

    private void initializeAnalytics() {
        setTitle(getResources().getString(R.string.analytics));
        removeTabs();
        if (this.menu != null) this.menu.findItem(R.id.action_change_day).setVisible(true);
        fab.setVisibility(View.GONE);

        tabs = new TabLayout(MainActivity.this);
        tabs.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        appBar.addView(tabs);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new AnalyticsDayFragment(MainActivity.this, restaurantId), getString(R.string.per_day));
        adapter.addFragment(new AnalyticsWeekFragment(MainActivity.this, restaurantId), getString(R.string.per_week));
        adapter.addFragment(new AnalyticsMonthFragment(MainActivity.this, restaurantId), getString(R.string.per_month));
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }

    private void initializeCurrentOrder() {
        setTitle(getResources().getString(R.string.current_order));
        removeTabs();
        if (this.menu != null) this.menu.findItem(R.id.action_change_day).setVisible(false);
        fab.setVisibility(View.GONE);

        TextView restaurantName = (TextView) findViewById(R.id.restaurant_name);
        restaurantName.setText(orderService.getRestaurantName(currentOrder.getId()));

        TextView currentTableNumber = (TextView) findViewById(R.id.current_table_number);
        currentTableNumber.setText(String.format("at table %s", String.valueOf(currentOrder.getTableNumber())));

        TextView totalPrice = (TextView) findViewById(R.id.current_total_price);
        totalPrice.setText(String.format("%s €", String.valueOf(orderService.getPriceOfOrder(currentOrder.getId()))));

        ArrayList<OrderItem> dishesOrderItems = orderService.getOnlyDishItemsOf(currentOrder);
        if (dishesOrderItems != null && !dishesOrderItems.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.mock_dishes);
            textView.setVisibility(View.GONE);
        }
        OrderItemAdapter dishesOrderItemAdapter = new OrderItemAdapter(dishesOrderItems,
                MainActivity.this, this.currentOrder);
        RecyclerView dishesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item_dish);
        LinearLayoutManager dishesLinearLayoutManager = new LinearLayoutManager(this);
        assert dishesRecyclerView != null;
        dishesRecyclerView.setLayoutManager(dishesLinearLayoutManager);
        dishesRecyclerView.setAdapter(dishesOrderItemAdapter);

        ArrayList<OrderItem> menusOrderItems = orderService.getOnlyMenuItemsOf(currentOrder);
        if (menusOrderItems != null && !menusOrderItems.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.mock_menus);
            textView.setVisibility(View.GONE);
        }
        OrderItemAdapter menusOrderItemAdapter = new OrderItemAdapter(menusOrderItems,
                MainActivity.this, this.currentOrder);
        RecyclerView menusRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_order_item_menu);
        LinearLayoutManager menusLinearLayoutManager = new LinearLayoutManager(this);
        assert menusRecyclerView != null;
        menusRecyclerView.setLayoutManager(menusLinearLayoutManager);
        menusRecyclerView.setAdapter(menusOrderItemAdapter);
    }

    private void initializePendingReviews() {
        setTitle(getResources().getString(R.string.pending_reviews));
        removeTabs();
        if (this.menu != null) this.menu.findItem(R.id.action_change_day).setVisible(false);
        fab.setVisibility(View.GONE);
        List<Order> orderList = userService.getOrdersToReview(Preferences.getCurrentUserEmail());
        checkList(R.id.review_mock, orderList.size());
        ReviewOrderAdapter reviewOrderAdapter = new ReviewOrderAdapter(orderList, MainActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.order_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        assert recyclerView != null;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(reviewOrderAdapter);
    }

}
