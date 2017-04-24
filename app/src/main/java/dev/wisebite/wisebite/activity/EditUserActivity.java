package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.User;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.BaseActivity;
import dev.wisebite.wisebite.utils.DownloadImageTask;
import dev.wisebite.wisebite.utils.Utils;

public class EditUserActivity extends BaseActivity {

    public static final String USER_ID = "USER_ID";

    private UserService userService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userService = ServiceFactory.getUserService(EditUserActivity.this);
        if (getIntent().getSerializableExtra(USER_ID) != null) {
            this.user = userService.get(getIntent().getExtras().getString(USER_ID));
        }
        setTitle("Editing " + user.getName());

        initializeView();

        TextView done = (TextView) findViewById(R.id.done);
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        new DownloadImageTask((ImageView) findViewById(R.id.user_picture_nav))
                .execute(userService.getProfilePhoto(user.getId()));
        EditText nameView = (EditText) findViewById(R.id.input_name);
        EditText lastNameView = (EditText) findViewById(R.id.input_last_name);
        EditText locationView = (EditText) findViewById(R.id.input_location);

        if (!Utils.isEmpty(user.getName())) nameView.setText(user.getName());
        if (!Utils.isEmpty(user.getLastName())) lastNameView.setText(user.getLastName());
        if (!Utils.isEmpty(user.getLocation())) locationView.setText(user.getLocation());

    }

    private void done() {

    }

}
