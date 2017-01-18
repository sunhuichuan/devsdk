package com.yao.devsdk.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.yao.devsdk.constants.SdkConst;
import com.yao.devsdk.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Android 6.0 运行时权限的申请和管理
 * Created by huichuan on 16/10/23.
 */
public class PermissionManager {
    private static final String TAG = "Permissions";

    public static final String PERMISSION_STORAGE =
            ManifestCompat.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * 请求权限默认的requestCode
     */
    public static final int PERMISSIONS_REQUEST_DEFAULT = 0;


    private static PermissionManager ourInstance = new PermissionManager();

    public static PermissionManager getInstance() {
        return ourInstance;
    }

    private PermissionManager() {
    }

    /**
     * 单一权限申请Listener集合
     */
    private List<PermissionGrantListener> permissionListeners = new ArrayList<>();

    /**
     * 一组权限申请Listener集合
     */
    private List<PermissionGroupGrantListener> permissionGroupListeners = new ArrayList<>();


    /**
     * 是否获得了指定权限
     * @param activity
     * @return
     */
    public boolean isGrant(Activity activity,String permission) {
        boolean isDeny = ContextCompat.checkSelfPermission(activity, permission) !=
                PackageManager.PERMISSION_GRANTED;
        LogUtil.i(TAG,"权限--"+permission+"--是否被拒绝："+isDeny);
        return !isDeny;
    }

    /**
     * 请求读取sd卡存储权限
     * @param activity
     */
    public void requestStoragePermission(Activity activity,PermissionGrantListener listener){
        permissionListeners.add(listener);
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,PERMISSION_STORAGE)) {
            LogUtil.i(TAG,"需要弹出解释的弹框");

        }else{
            //其他情况，申请权限
            requestPermissions(activity,PERMISSIONS_REQUEST_DEFAULT, PERMISSION_STORAGE);

        }

    }
    /**
     * 请求麦克风的权限
     * @param activity
     */
    public void requestAudioPermission(Activity activity,PermissionGrantListener listener){
        permissionListeners.add(listener);

        requestPermissions(activity,PERMISSIONS_REQUEST_DEFAULT,Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 请求申请权限
     * @param activity
     * @param requestCode 此值必须 >= 0,具体要求参见{@link FragmentActivity#requestPermissions(String[], int)}
     * @param permission
     */
    public void requestPermissions(Activity activity,int requestCode,String... permission){

        if (permission.length==1){
            //只申请一个权限
            boolean isGranted = isGrant(activity, permission[0]);
            if (isGranted){
                //已经授权直接回调
                int permissionCount = permission.length;
                int[] grantResult = new int[permissionCount];
                for (int i=0;i<grantResult.length;i++){
                    grantResult[i] = PackageManager.PERMISSION_GRANTED;
                }
                onRequestPermissionsResult(requestCode,permission,grantResult);
                return;
            }
        }

        //其他情况，申请权限
        ActivityCompat.requestPermissions(activity,
                permission,requestCode);


    }

    /**
     * 此方法会被Activity的onRequestPermissionsResult回调方法调用
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults){

        if (permissions==null || permissions.length<1){
            if (SdkConst.DEBUG){
                throw new RuntimeException("onRequestPermissionsResult--权限回调数组长度为空，不正常");
            }
            return;
        }

        if (permissions.length == 1){
            //请求了一个权限
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                for (PermissionGrantListener listener:permissionListeners){
                    //通知Listener
                    listener.onGranted(requestCode,permissions[0]);
                }

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                for (PermissionGrantListener listener:permissionListeners){
                    //通知Listener
                    listener.onDenied(requestCode,permissions[0]);
                }
            }
            //清空Listener
            permissionListeners.clear();

        }else if (permissions.length>1){
            //请求了一组权限
            for (PermissionGroupGrantListener groupListener:permissionGroupListeners){
                //通知Listener
                groupListener.onRequestResult(requestCode,permissions,grantResults);
            }
            //清空GroupListener
            permissionGroupListeners.clear();
        }

//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_DEFAULT: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//        }

    }



    public interface PermissionGrantListener{

        /**
         * 用户授权了权限
         * @param requestCode {@link PermissionManager#PERMISSIONS_REQUEST_DEFAULT}等
         * @param permission {@link android.Manifest.permission#RECORD_AUDIO}等
         */
        void onGranted(int requestCode,String permission);
        /**
         * 用户拒绝了权限
         * @param requestCode {@link PermissionManager#PERMISSIONS_REQUEST_DEFAULT}等
         * @param permission {@link android.Manifest.permission#RECORD_AUDIO}等
         */
        void onDenied(int requestCode,String permission);
    }

    /**
     * 一组权限申请的结果listener
     */
    public interface PermissionGroupGrantListener{

        /**
         * 通知用户授权结果
         */
        void onRequestResult(int requestCode,String permissions[], int[] grantResults);
    }



}
