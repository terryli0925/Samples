package com.terry.samples.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.terry.samples.R;
import com.terry.samples.adapter.BaseRVAdapter;
import com.terry.samples.adapter.BaseRVAdapterHelper;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {
    private ArrayList<String> testItems = new ArrayList<>();
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle("Title");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();
    }

    private void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        for (int i = 0; i < 100; i++) {
            testItems.add(Integer.toString(i));
        }

        BaseRVAdapter mStringAdapter = new BaseRVAdapter<String, BaseRVAdapterHelper>(
                this, R.layout.layout_textview, testItems) {
            @Override
            protected void convert(BaseRVAdapterHelper holder, String item, int position) {
                holder.getTextView(R.id.text).setText(item);
            }
        };
        mRecyclerView.setAdapter(mStringAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
