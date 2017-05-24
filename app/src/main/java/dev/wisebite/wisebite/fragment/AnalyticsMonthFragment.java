package dev.wisebite.wisebite.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.wisebite.wisebite.R;

/**
 * Created by albert on 24/05/17.
 * @author albert
 */
public class AnalyticsMonthFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.analytics_fragment, container, false);
    }

}
