package com.yao.devsdk.cardview;

/**
 * card view 更新时的消息
 * Created by huichuan on 16/3/9.
 */
public class UpdateInfo<T> {

    //当前Card使用的数据
    public T cardInfo;
    //当前Card所在页面，比如首页、收藏
    //用string是为了方便看打印的log
    public String pageId;
    public int position;


    public UpdateInfo(T cardInfo, String pageId, int position){
        this.cardInfo = cardInfo;
        this.pageId = pageId;
        this.position = position;
    }
}
