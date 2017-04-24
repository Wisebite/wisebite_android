package dev.wisebite.wisebite.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private static final int RESULT_LOAD_IMAGE = 1;

    private UserService userService;
    private User user;

    private EditText nameView, lastNameView, locationView;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("Loaded picture", picturePath);

            ImageView imageView = (ImageView) findViewById(R.id.user_picture_nav);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    private void initializeView() {
        ImageView imageView = (ImageView) findViewById(R.id.user_picture_nav);
        new DownloadImageTask(imageView)
                .execute(userService.getProfilePhoto(user.getId()));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        nameView = (EditText) findViewById(R.id.input_name);
        lastNameView = (EditText) findViewById(R.id.input_last_name);
        locationView = (EditText) findViewById(R.id.input_location);

        if (!Utils.isEmpty(user.getName())) nameView.setText(user.getName());
        if (!Utils.isEmpty(user.getLastName())) lastNameView.setText(user.getLastName());
        if (!Utils.isEmpty(user.getLocation())) locationView.setText(user.getLocation());

    }

    private void done() {
        if (userService.editUser(user.getId(),
                nameView.getText().toString(),
                lastNameView.getText().toString(),
                locationView.getText().toString())) {
            onBackPressed();
        }
    }

}
