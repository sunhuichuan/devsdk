package com.yao.devsdk.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.yao.devsdk.SdkConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NashLegend on 2014/9/18 0018
 */
public abstract class AceAdapter<T> extends BaseAdapter implements IListAdapter<T>{

    protected Context appContext = SdkConfig.getAppContext();

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

    @Override
    public long getItemId(int position) {
        return position;
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

}
