package com.yao.devsdk.cardview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

//@SuppressLint({
//        "InflateParams", "RtlHardcoded"
//})

/**
 * 在ListView或者RecyclerView中显示的cardView
 * @param <T> card显示的对象
 * @param <PV> card所在的父View,可能是ListView，可能是RecyclerView
 */
public abstract class BaseCardView<T,PV> extends FrameLayout {

    private static final String TAG = "BaseCardView";

    protected Context context;
    protected BaseCardView thisView;
    protected T mCardInfo;
    private PV mParentView;

    // 用于判断是那个页面的item，决定点击item后通过那个event跳转
    protected String pageId;

    //card在ListView中的位置
    protected int mPosition = -1;

    public BaseCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCardView(context);
    }

    public BaseCardView(Context context) {
        super(context);
        initCardView(context);
    }




    protected void setCardInfo(T cardInfo) {
        this.mCardInfo = cardInfo;
    }


    /**
     * 返回此CardView对象内显示的对象
     * @return
     */
    public T getCardInfo() {
        return mCardInfo;
    }



    private void initCardView(Context context) {
        this.context = context;
        this.thisView = this;
        initLayout();

    }




    public void setParentView(PV parentView) {
        this.mParentView = parentView;
    }

    /**
     * 获取父View的引用
     * @return
     */
    public PV getParentView() {
        return mParentView;
    }



    /**
     * 更新Card界面显示
     *
     * @param updateInfo 更新需要的info
     */
    public final void update(UpdateInfo<T> updateInfo) {
        if (updateInfo == null || updateInfo.cardInfo == null) {
            return;
        }

        this.mPosition = updateInfo.position;
        this.pageId = updateInfo.pageId;
        this.mCardInfo = updateInfo.cardInfo;

        updateView(updateInfo);

    }

    protected abstract void initLayout();

    /**
     * 更新View
     * @param updateInfo
     */
    protected abstract void updateView(UpdateInfo<T> updateInfo);

    /**
     * 是否读过状态设置
     * @param cardInfo
     */
    protected abstract void updateReadState(T cardInfo);






//    /**
//     * 获取BaseCardView所在的adapter
//     * @return
//     */
//    public AceAdapter getAdapter(){
//        ListAdapter listAdapter = mParentView.getAdapter();
//        if (listAdapter instanceof HeaderViewListAdapter){
//            HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) listAdapter;
//            AceAdapter wrappedAdapter = (AceAdapter) headerAdapter.getWrappedAdapter();
//            return wrappedAdapter;
//        }
//        return null;
//    }


}
