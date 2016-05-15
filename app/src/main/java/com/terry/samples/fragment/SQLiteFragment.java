package com.terry.samples.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;
import com.terry.samples.adapter.BaseRVAdapter;
import com.terry.samples.adapter.BaseRVAdapterHelper;
import com.terry.samples.model.Person;
import com.terry.samples.utils.SampleDBHelper;

import java.util.List;

/**
 * Created by terry on 2016/4/19.
 */
public class SQLiteFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private EditText mInsertPersonView;

    private BaseRVAdapter mPersonListAdapter;
    private SampleDBHelper mSampleDBHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sqlite, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mInsertPersonView = (EditText) view.findViewById(R.id.insertAccount);
        view.findViewById(R.id.clear).setOnClickListener(this);
        view.findViewById(R.id.refresh).setOnClickListener(this);
        view.findViewById(R.id.insert).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSampleDBHelper = SampleDBHelper.getInstance(getActivity());

        setAdapter(mSampleDBHelper.queryAll());
    }

    @Override
    protected void setUpActionBar() {
        MainActivity activity = (MainActivity) getActivity();
        activity.setCustomToolbarTitle("SQLiteTest");
        activity.setToolbarExpanded(false);
    }

    private void setAdapter(List<Person> list) {
        if (mPersonListAdapter == null) {
            mPersonListAdapter = new BaseRVAdapter<Person, BaseRVAdapterHelper>(getActivity(),
                    R.layout.layout_textview, null) {

                @Override
                protected void convert(BaseRVAdapterHelper holder, Person item, int position) {
                    holder.getTextView(R.id.text).setText(item.getName());
                }
            };
            mRecyclerView.setAdapter(mPersonListAdapter);
            mPersonListAdapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    position = mRecyclerView.getChildAdapterPosition(view);
                    final Person person = (Person) mPersonListAdapter.getItem(position);
                    final EditText editText = new EditText(getActivity());

                    new AlertDialog.Builder(getActivity())
                            .setTitle("更名")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    String newName = editText.getText().toString();
                                    if (!newName.isEmpty()) {
                                        mSampleDBHelper.update(person, newName);
                                        setAdapter(mSampleDBHelper.queryAll());
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).show();
                }
            });
            mPersonListAdapter.setOnItemLognClickListener(new BaseRVAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, int position) {
                    position = mRecyclerView.getChildAdapterPosition(view);
                    final Person person = (Person) mPersonListAdapter.getItem(position);

                    new AlertDialog.Builder(getActivity())
                            .setTitle("刪除")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    mSampleDBHelper.delete(person);
                                    setAdapter(mSampleDBHelper.queryAll());
                                }
                            })
                            .setNegativeButton("取消", null).show();
                }
            });
        }
        mPersonListAdapter.replaceAll(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                mSampleDBHelper.deleteDatabase();
//                mItemAdapter.cleanList();
                mPersonListAdapter.clear();
                break;
            case R.id.refresh:
                setAdapter(mSampleDBHelper.queryAll());
                break;
            case R.id.insert:
                String person = mInsertPersonView.getText().toString();
                if (!person.isEmpty()) {
                    mSampleDBHelper.insert(person);
                    setAdapter(mSampleDBHelper.queryAll());
                    mInsertPersonView.setText("");
                }
                break;
        }
    }
}
