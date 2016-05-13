package com.terry.samples.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToolbarTestFragment extends BaseFragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_toolbar_test, container, false);
        return view;
    }

    @Override
    protected void setUpActionBar() {
        MainActivity activity = (MainActivity) getActivity();
        activity.getDrawerToggle().setDrawerIndicatorEnabled(false);
        activity.setCustomToolbarTitle("CustomToolbarTest");
        activity.setCollapsedImage(R.drawable.green_sea);
        activity.setToolbarExpanded(true);
    }

    @Override
    public void onDestroyView() {
        setBackActionBar();
        super.onDestroyView();
    }

    private void setBackActionBar() {
        MainActivity activity = (MainActivity) getActivity();
        activity.getDrawerToggle().setDrawerIndicatorEnabled(true);
        activity.setCollapsedImage(android.R.color.transparent);
        activity.setToolbarExpanded(false);
    }
}
