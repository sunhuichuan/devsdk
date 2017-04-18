package com.yao.devsdk.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.yao.devsdk.log.LoggerUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UniqueDeviceId {
    private static final String TAG = "UniqueDeviceId";

    /**
     * Get IMEI, if can't get IMEI, generate a fake imei, 15 digital unique
     * device id;
     *
     * @return String
     */
    public static String getDeviceId(Context context) {
        try {
            // 1 compute IMEI
            String imei = getIMEI(context);
            if (TextUtils.isEmpty(imei)){
                imei = "0000";
            }
            // 2 compute DEVICE ID
            String devIDShort = generateMachineId(); // 13 digits

            // 3 android ID - unreliable
            String androidId = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);

            // 4 wifi manager read MAC address - requires
            // android.permission.ACCESS_WIFI_STATE or comes as null
            String wlanMac = getWifiMac(context);

            // 5 Bluetooth MAC address android.permission.BLUETOOTH required, so
            // currenty just comment out, in case we use this method later
            String btMac = null;
//            btMac = getBlueToothMac(context);


            // 6 SUM THE IDs
//            String devIdLong = imei + devIDShort + androidId + wlanMac + btMac;
            String devIdLong = devIDShort.concat(androidId).concat(wlanMac);
            LoggerUtil.i(TAG, "拼接的id为：" + devIdLong);
            String idString  = md5IdString(devIdLong)+imei;
            LoggerUtil.i(TAG, "最终的id为：" + idString+",前30位为自己生成的设备id");
            return idString;
        } catch (Throwable t) {
            LoggerUtil.e(TAG, "最终异常", t);
        }
        return "DeviceId0";
    }


    /**
     * 获取手机IMEI
     *
     * @return
     */
    public static String getIMEI(Context context) {
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE

            if (!TextUtils.isEmpty(imei)) {
                // got imei, return it
                return imei.trim();
            }
        } catch (SecurityException e) {
            LoggerUtil.i(TAG, "没有权限异常", e);
        } catch (Exception e) {
            LoggerUtil.e(TAG, "获取IMEI异常", e);
        }
        return null;
    }


    /**
     * 获取蓝牙的mac地址
     *
     * Bluetooth MAC address android.permission.BLUETOOTH required, so
     * currenty just comment out, in case we use this method later
     * @param context
     * @return
     */
    static String getBlueToothMac(Context context) {
//        try {
//            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // Local Bluetooth
//            if (bluetoothAdapter != null) {
//                String btMac = bluetoothAdapter.getAddress();
//            }
//        } catch (Exception e) {
//            LogUtil.e(TAG, "获取蓝牙Mac地址异常", e);
//        }

        return null;
    }


    static String generateMachineId() {
        String devIDShort = "35"
                + // we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                + Build.USER.length() % 10; // 13 digits
        // #debug
        LoggerUtil.d(TAG, "devIDShort: " + devIDShort);

        return devIDShort;
    }


    /**
     * wifi manager read MAC address -
     * requires android.permission.ACCESS_WIFI_STATE or comes as null
     * @param context
     * @return
     */
    static String getWifiMac(Context context){
        try {
            WifiManager wm = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            String wlanMac = wm.getConnectionInfo().getMacAddress();
            return wlanMac;
        } catch (Exception e) {
            LoggerUtil.e(TAG, "获取wlan异常", e);
        }
        return null;
    }


    /**
     * 对生成的id做md5
     * @param idString
     * @return
     * @throws NoSuchAlgorithmException
     */
    static String md5IdString(String idString) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(idString.getBytes(), 0, idString.length());
        byte md5Data[] = m.digest();

        String uniqueId = "";
        for (int i = 0, len = md5Data.length; i < len; i++) {
            int b = (0xFF & md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper
            // padding)
            if (b <= 0xF)
                uniqueId += "0";
            // add number to string
            uniqueId += Integer.toHexString(b);
        }
        uniqueId = uniqueId.toUpperCase();
        //不用截取前15位吧
//        if (uniqueId.length() > 15) {
//            uniqueId = uniqueId.substring(0, 15);
//        }
        return uniqueId.trim();
    }

}
