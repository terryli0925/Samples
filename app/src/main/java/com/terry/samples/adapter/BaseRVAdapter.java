package com.terry.samples.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by terry on 2016/4/20.
 */
public abstract class BaseRVAdapter<T, H extends BaseRVAdapterHelper> extends
        RecyclerView.Adapter<BaseRVAdapterHelper> implements View.OnClickListener,
        View.OnLongClickListener {

    protected final Context mContext;
    protected int mLayoutResId;
    protected final List<T> mList;

    private OnItemClickListener mOnItemClickListener = null;
    private OnItemLongClickListener mOnItemLongClickListener = null;

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //define interface
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * Create a BaseRVAdapter.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     */
    public BaseRVAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    /**
     * Same as BaseRVAdapter#BaseRVAdapter(Context,int) but with
     * some initialization list.
     *
     * @param context     The mContext.
     * @param layoutResId The layout resource id of each item.
     * @param list        A new list is created out of this one to avoid mutable list
     */
    public BaseRVAdapter(Context context, int layoutResId, List<T> list) {
        this.mList = list == null ? new ArrayList<T>() : list;
        this.mContext = context;
        this.mLayoutResId = layoutResId;
    }

    /**
     * Create a BaseRVAdapter. For Multiple type.
     *
     * @param context     The context.
     */
    public BaseRVAdapter(Context context) {
        this(context, null);
    }

    /**
     * For Multiple type.
     *
     * @param context     The mContext.
     * @param list        A new list is created out of this one to avoid mutable list
     */
    public BaseRVAdapter(Context context, List<T> list) {
        this.mList = list == null ? new ArrayList<T>() : list;
        this.mContext = context;
    }

    @Override
    public BaseRVAdapterHelper onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutResId,
                parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        BaseRVAdapterHelper vh = new BaseRVAdapterHelper(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(BaseRVAdapterHelper holder, int position) {
        holder.itemView.setTag(position);
        T item = getItem(position);
        convert((H) holder, item);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(H holder, T item);

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public T getItem(int position) {
        if (position >= mList.size()) return null;
        return mList.get(position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(v, (int) v.getTag());
        }
        return true;
    }

    public void setOnItemLognClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public void add(T item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    public void set(T oldItem, T newItem) {
        set(mList.indexOf(oldItem), newItem);
    }

    public void set(int index, T item) {
        mList.set(index, item);
        notifyDataSetChanged();
    }

    public void remove(T item) {
        mList.remove(item);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mList.remove(index);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> item) {
        mList.clear();
        mList.addAll(item);
        notifyDataSetChanged();
    }

    public boolean contains(T item) {
        return mList.contains(item);
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }
}
