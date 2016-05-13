package com.terry.samples.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;
import com.terry.samples.activity.ScrollingActivity;
import com.terry.samples.activity.TabActivity;
import com.terry.samples.activity.ToolbarActivity;

/**
 * Created by terry on 2016/4/18.
 */
public class LayoutFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void setUpActionBar() {
        MainActivity activity = (MainActivity) getActivity();
        activity.setCustomToolbarTitle("ToolbarTest");
        activity.setToolbarExpanded(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.btn1).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.btn2).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.btn3).setOnClickListener(mOnClickListener);
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn1:
                    startActivity(new Intent(getActivity(), ScrollingActivity.class));
                    break;
                case R.id.btn2:
                    startActivity(new Intent(getActivity(), ToolbarActivity.class));
                    break;
                case R.id.btn3:
                    startActivity(new Intent(getActivity(), TabActivity.class));
                    break;
            }
        }
    };

}
