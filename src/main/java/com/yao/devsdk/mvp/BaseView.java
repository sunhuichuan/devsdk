package com.yao.devsdk.mvp;

/**
 * MVP模式下的view
 * Created by huichuan on 16/4/27.
 */
public interface BaseView<T> {

    void setPresenter(T presenter);

}
