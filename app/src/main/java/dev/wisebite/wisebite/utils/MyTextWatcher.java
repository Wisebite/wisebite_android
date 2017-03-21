package dev.wisebite.wisebite.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import dev.wisebite.wisebite.R;

/**
 * Created by albert on 14/03/17.
 * @author albert
 */
public class MyTextWatcher implements TextWatcher {

    private View view;

    public MyTextWatcher(View view) {
        this.view = view;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {
        switch (view.getId()) {
            case R.id.input_name:
                validateName();
                break;
            case R.id.input_location:
                validateLocation();
                break;
            case R.id.input_phone:
                validatePhone();
                break;
            case R.id.input_description:
                validateDescription();
                break;
            case R.id.input_website:
                validateWebsite();
                break;
            case R.id.input_number_of_tables:
                validateNumberOfTables();
                break;
        }
    }

    private boolean validateName() {
        return true;
    }

    private boolean validateLocation() {
        return true;
    }

    private boolean validatePhone() {
        return true;
    }

    private boolean validateDescription() {
        return true;
    }

    private boolean validateWebsite() {
        return true;
    }

    private boolean validateNumberOfTables() {
        return true;
    }

}
