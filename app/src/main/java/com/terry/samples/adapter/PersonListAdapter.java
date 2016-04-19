package com.terry.samples.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.terry.samples.R;
import com.terry.samples.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by terry on 2016/4/19.
 */
public class PersonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Person> testItems;
    private Context context;

    /**
     * The listener that receives notifications when an item is clicked.
     */
    OnItemClickListener mOnItemClickListener;

    /**
     * The listener that receives notifications when an item is long clicked.
     */
    OnItemLongClickListener mOnItemLongClickListener;

    public PersonListAdapter(Context context, ArrayList<Person> items) {
        this.context = context;
        this.testItems = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_textview,
                parent, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mText.setText(testItems.get(i).getName());
        itemViewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        return testItems.size();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    public ArrayList<Person> getList() {
        return testItems;
    }

    public Object getItemAtPosition(int position) {
        return (testItems == null || position < 0) ? null : testItems.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(v, (Integer) v.getTag());
                    }

                    return true;
                }
            });
        }
    }

    public void addAll(List<Person> list) {
        testItems.addAll(list);
        notifyDataSetChanged();
    }

    public void cleanList() {
        testItems.clear();
        notifyDataSetChanged();
    }
}
