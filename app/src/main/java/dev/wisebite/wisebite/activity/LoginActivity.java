package dev.wisebite.wisebite.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Utils;

public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static final String ANIMATIONS = "ANIMATIONS";
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private ProgressDialog mProgressDialog;

    private UserService userService;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private boolean translation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        showProgressDialog();
        initializeService();

        if (getIntent().getSerializableExtra(ANIMATIONS) != null) {
            this.translation = getIntent().getExtras().getBoolean(ANIMATIONS);
        }

        // Set the dimensions of the sign-in button.
        this.mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (translation) {
            startAnimations();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            new Thread() {
                @Override
                public void run() {
                    try {
                        while (Utils.getLoaded() < ServiceFactory.getServiceCount()) {
                            sleep(1000);
                        }
                    } catch (InterruptedException ex) {
                        // Catching exception
                    } finally {
                        if (userService.logIn(acct)) {
                            initApp();
                        }
                    }
                }
            }.start();
        }

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void startAnimations() {
        Animation alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate);
        alpha.reset();
        translate.reset();
        ImageView logo = (ImageView) findViewById(R.id.splash);
        assert logo != null;
        assert mSignInButton != null;
        logo.clearAnimation();
        mSignInButton.clearAnimation();

        logo.startAnimation(translate);
        this.mSignInButton.startAnimation(alpha);
    }

    private void initializeService() {
        this.userService = ServiceFactory.getUserService(LoginActivity.this);
        this.userService.setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }});

        ServiceFactory.getDishService(LoginActivity.this).setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }
        });
        ServiceFactory.getImageService(LoginActivity.this).setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }});
        ServiceFactory.getMenuService(LoginActivity.this).setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }});
        ServiceFactory.getOpenTimeService(LoginActivity.this).setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }});
        ServiceFactory.getOrderItemService(LoginActivity.this).setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }});
        ServiceFactory.getOrderService(LoginActivity.this).setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }});
        ServiceFactory.getRestaurantService(LoginActivity.this).setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) { increaseLoaded(type); }});

    }

    private void initApp() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void increaseLoaded(Repository.OnChangedListener.EventType type) {
        if (type.equals(Repository.OnChangedListener.EventType.Full)) {
            Utils.increaseLoaded();
        }
    }

}
