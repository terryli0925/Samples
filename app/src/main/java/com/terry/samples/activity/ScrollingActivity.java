package com.terry.samples.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.terry.samples.R;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {
    private ArrayList<String> testItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ItemAdapter mItemAdapter;

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

        mItemAdapter = new ItemAdapter(this, testItems);
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
        private ArrayList<String> testItems;
        private LayoutInflater inflater;
        private Context context;

        public ItemAdapter(Context context, ArrayList<String> items) {
            this.context = context;
            this.testItems = items;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_textview, parent, false);
            ItemViewHolder pvh = new ItemViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder productViewHolder, int i) {
            productViewHolder.mText.setText(testItems.get(i));
        }

        @Override
        public int getItemCount() {
            return testItems.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView mText;

            public ItemViewHolder(View itemView) {
                super(itemView);
                mText = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}
