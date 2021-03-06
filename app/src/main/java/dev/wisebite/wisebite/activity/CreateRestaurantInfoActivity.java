package dev.wisebite.wisebite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.BaseActivity;

public class CreateRestaurantInfoActivity extends BaseActivity {

    private EditText inputName, inputLocation, inputPhone,
            inputDescription, inputWebsite, inputNumberOfTables;
    private TextInputLayout inputLayoutName, inputLayoutLocation, inputLayoutPhone,
            inputLayoutDescription, inputLayoutWebsite, inputLayoutNumberOfTables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ServiceFactory.getRestaurantService(CreateRestaurantInfoActivity.this);

        initializeForm();
        initializeSubmit();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    /**
     * Initialize form inputs
     */
    private void initializeForm() {
        inputName = (EditText) findViewById(R.id.input_name);
        inputLocation = (EditText) findViewById(R.id.input_location);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputDescription = (EditText) findViewById(R.id.input_description);
        inputWebsite = (EditText) findViewById(R.id.input_website);
        inputNumberOfTables = (EditText) findViewById(R.id.input_number_of_tables);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutLocation = (TextInputLayout) findViewById(R.id.input_layout_location);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_description);
        inputLayoutWebsite = (TextInputLayout) findViewById(R.id.input_layout_website);
        inputLayoutNumberOfTables = (TextInputLayout) findViewById(R.id.input_layout_number_of_tables);

    }

    /**
     * Initialize form submit button
     */
    private void initializeSubmit() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAll()) {
                    Restaurant restaurant = Restaurant.builder()
                                            .name(inputName.getText().toString())
                                            .location(inputLocation.getText().toString())
                                            .phone(Integer.valueOf(inputPhone.getText().toString()))
                                            .description(inputDescription.getText().toString())
                                            .website(inputWebsite.getText().toString())
                                            .numberOfTables(Integer.valueOf(inputNumberOfTables.getText().toString()))
                                            .build();
                    Intent intent = new Intent(CreateRestaurantInfoActivity.this, CreateRestaurantOpenTimesActivity.class);
                    intent.putExtra(CreateRestaurantOpenTimesActivity.RESTAURANT, restaurant);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            }
        });
    }

    /**
     * Action to do when the user clicks to some input of the form
     * @param view view where the input will be shown
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Validate all data
     * @return true if it's validated correctly, false otherwise
     */
    private boolean validateAll() {
        return  validateName() && validateLocation() && validatePhone() && validateDescription() &&
                validateWebsite() && validateNumberOfTables();
    }

    /**
     * Validate restaurant name
     * @return true if it's validated correctly, false otherwise
     */
    private boolean validateName() {
        String name = inputName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Validate restaurant location
     * @return true if it's validated correctly, false otherwise
     */
    private boolean validateLocation() {
        String location = inputLocation.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            inputLayoutLocation.setError(getString(R.string.err_msg_location));
            requestFocus(inputLocation);
            return false;
        } else {
            inputLayoutLocation.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Validate restaurant phone
     * @return true if it's validated correctly, false otherwise
     */
    private boolean validatePhone() {
        String phone = inputPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || !TextUtils.isDigitsOnly(phone)) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Validate restaurant description
     * @return true if it's validated correctly, false otherwise
     */
    private boolean validateDescription() {
        String description = inputDescription.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            inputLayoutDescription.setError(getString(R.string.err_msg_description));
            requestFocus(inputDescription);
            return false;
        } else {
            inputLayoutDescription.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Validate restaurant website
     * @return true if it's validated correctly, false otherwise
     */
    private boolean validateWebsite() {
        String website = inputWebsite.getText().toString().trim();
        if (TextUtils.isEmpty(website)) {
            inputLayoutWebsite.setError(getString(R.string.err_msg_website));
            requestFocus(inputWebsite);
            return false;
        } else {
            inputLayoutWebsite.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Validate number of tables
     * @return true if it's validated correctly, false otherwise
     */
    private boolean validateNumberOfTables() {
        String numberOfTables = inputNumberOfTables.getText().toString().trim();
        if (TextUtils.isEmpty(numberOfTables) || !TextUtils.isDigitsOnly(numberOfTables)) {
            inputLayoutNumberOfTables.setError(getString(R.string.err_msg_number_of_tables));
            requestFocus(inputNumberOfTables);
            return false;
        } else if (Integer.valueOf(numberOfTables) > 1000) {
            inputLayoutNumberOfTables.setError(getString(R.string.err_msg_number_of_tables_too_much_tables));
            requestFocus(inputNumberOfTables);
            return false;
        } else {
            inputLayoutNumberOfTables.setErrorEnabled(false);
        }
        return true;
    }

}
