package com.yao.devsdk.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.log.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yao on 2016/4/27
 */
public abstract class AceRecyclerAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements IListAdapter<T>{
    private static final String TAG = "AceRecyclerAdapter";

    protected Context appContext = SdkConfig.getAppContext();
    protected Activity thisContext;

    public AceRecyclerAdapter(){}

    //adapter经常需要Activity,故增加此构造方法
    public AceRecyclerAdapter(Activity context){
        thisContext = context;
    }


    public final ArrayList<T> mList = new ArrayList<T>();

    public void addAll(List<T> list) {
        this.mList.addAll(list);
    }

    public void addAll(int index, List<T> list) {
        this.mList.addAll(index, list);
    }

    public void addAllReversely(List<T> list) {
        if (list.size() > 0) {
            for (int i = list.size() - 1; i >= 0; i--) {
                this.mList.add(list.get(i));
            }
        }
    }

    public void addAllReversely(List<T> list, int index) {
        for (int i = 0; i < list.size(); i++) {
            this.mList.add(index, list.get(i));
        }
    }

    public void clear() {
        mList.clear();
    }

    public void add(T t) {
        mList.add(t);
    }

    public void add(int index, T t) {
        mList.add(index, t);
    }

    public void remove(T t) {
        mList.remove(t);
    }

    public void remove(int index) {
        mList.remove(index);
    }

    public T getItem(int index){
        if (mList.size()>0){
            return mList.get(index);
        }
        return null;
    }

    public ArrayList<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    @Override
    public int getCount() {
        return mList.size();
    }



    protected OnItemClickListener<T> mOnItemClickListener;


    @Override
    public final void onBindViewHolder(final VH viewHolder, final int position) {
        LoggerUtil.i(TAG, "Element " + position + " set.");
        final T obj = mList.get(position);
        if (viewHolder.itemView!=null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoggerUtil.i(TAG, "click:" + (obj == null ? "nuuullll" : obj.toString()));
                    int n = viewHolder.getLayoutPosition();
                    if (mOnItemClickListener!=null && obj!=null){
                        mOnItemClickListener.onItemClick(v, n, obj);
                    }
                }
            });
        }

        onInnerBindViewHolder(viewHolder,obj,position);

    }

    /**
     * 此抽象方法是为了让子类必须实现，以保证所有的RecyclerAdapter都能响应onItemClickListener
     */
    public abstract void onInnerBindViewHolder(VH viewHolder,T item, int position);




    public void setOnItemClickListener(OnItemClickListener<T> listener){
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener<T>{

        void onItemClick(View view, int position, T item);
    }




}
