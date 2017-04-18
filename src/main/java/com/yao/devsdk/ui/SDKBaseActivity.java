package com.yao.devsdk.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.constants.SdkConst;
import com.yao.devsdk.utils.SdkUtil;

public abstract class SDKBaseActivity extends AppCompatActivity implements SDKBaseFragment.OnFragmentInteractionListener {

    //@Bind fields must not be private or static  这都咋在编译的时候就能报错呢？

    protected Context appContext = SdkConfig.getAppContext();
    protected SDKBaseActivity thisContext;
    //主线程的handler
    protected Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
    }

    //被fragment通知通信
    @Override
    public abstract void onFragmentInteraction(Uri uri);


    /**
     * 点击返回键会走此方法
     */
    @Override
    public void onBackPressed() {
        finishPage();
    }


    /**
     * 原则上Activity要Finish都调用此方法，方便部分Activity有特殊处理时重写此方法
     * 例如：登录页面点「返回键」只需要返回到桌面即可，不用真的finish
     */
    public void finishPage(){
        super.finish();
    }

    /**
     * 有直接需求finish自己，则调用此方法，避免子类重写的干扰
     * 主要是为了替换调用finish方法
     */
    public final void finishSelf(){
        super.finish();
    }

    @Override
    public final void finish() {
        if (SdkConst.DEBUG){
            String message = "不要调Finish,"+this.getClass().getName();
            SdkUtil.showDebugToast(message);
            throw new RuntimeException("不要直接调用BaseActivity的 finish 方法, from-->"+message);
        }
    }

}
