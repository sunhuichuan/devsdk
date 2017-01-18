package com.yao.devsdk.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装常规数据操作的adapter
 * Created by huichuan on 16/4/29.
 */
public interface IListAdapter<T>{

    void add(T t);

    void add(int index, T t);

    void addAll(List<T> list);

    void addAll(int index, List<T> list);

    void addAllReversely(List<T> list);

    void addAllReversely(List<T> list, int index);

    void remove(int index);

    void remove(T t);

    void clear();

    void setList(List<T> list);

    ArrayList<T> getList();

    //其实和Adapter的getCount方法重复，但RecyclerView的adapter没有此方法
    int getCount();
}
