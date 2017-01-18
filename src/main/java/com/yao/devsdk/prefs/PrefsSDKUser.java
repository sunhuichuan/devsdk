package com.yao.devsdk.prefs;

import android.content.Context;

import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.prefs.base.CachedPrefs;

/**
 * 用户设置相关的类
 * <p>
 *     User 是一个特殊的类，放在SDK管理，避免数据不一致
 * </p>
 */
public class PrefsSDKUser extends CachedPrefs {
    private static final String USER_PREFS = "sdk_user";

    private PrefsSDKUser(Context context) {
        super(context
                .getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE));
    }

    private static PrefsSDKUser ourInstance = new PrefsSDKUser(SdkConfig.getAppContext());

    public static PrefsSDKUser getInstance() {
        return ourInstance;
    }

    /**
     * 登录用户的uid
     */
    public StringVal login_uid = new StringVal("login_uid", "");
    /**
     * 登录用户的token
     */
    public StringVal login_token = new StringVal("login_token", "");

}
