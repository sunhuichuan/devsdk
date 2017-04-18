package com.yao.devsdk.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yao.devsdk.constants.FolderPath;
import com.yao.devsdk.constants.SdkConst;
import com.yao.devsdk.utils.file.CloseUtil;
import com.yao.devsdk.utils.file.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * crashlog 文件记录的Manager
 * Created by huichuan on 16/1/4.
 */
public class CrashLogManager {
    private static final String TAG = "CrashLogManager";
    private static CrashLogManager ourInstance = new CrashLogManager();

    public static CrashLogManager getInstance() {
        return ourInstance;
    }

    private CrashLogManager() {
    }



    /**
     * crashLog的时间命名pattern
     */
    public static final String CRASH_LOG_NAME_PATTERN = "yyyy-MM-dd__HH-mm-ss";

    private static final String SDCARD_ROOT = Environment
            .getExternalStorageDirectory().toString();

    /**
     * crash log 文件的前缀
     */
    public static final String CRASH_LOG_PREFIX = "crash_";
    /**
     * crash log 文件需要保留的个数
     */
    public static final int CRASH_LOG_MAINTAIN_COUNT = 10;


    /**
     * 清理崩溃log文件夹
     */
    public void cleanCrashLogFolder() {
        /**只保留log文件夹下最新的10个Crash文件，剩下的删除*/
        Comparator<File> cleanComparator = new Comparator<File>() {

            SimpleDateFormat format = new SimpleDateFormat(CRASH_LOG_NAME_PATTERN,
                    Locale.CHINA);

            @Override
            public int compare(File lhs, File rhs) {
                String lName = lhs.getName().substring(CRASH_LOG_PREFIX.length());
                String rName = rhs.getName().substring(CRASH_LOG_PREFIX.length());
                try {
                    Date lDate = format.parse(lName);
                    Date rDate = format.parse(rName);
                    return lDate.compareTo(rDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        FileUtil.cleanSDFolderFile(FolderPath.PATH_APP_ROOT + FolderPath.PATH_ERROR, CRASH_LOG_PREFIX, CRASH_LOG_MAINTAIN_COUNT, cleanComparator);
    }


    /**
     * 获取crashLog的集合，一般用于上传到服务器
     */
    public List<String> getCrashLogList(){
        List<String> logList = new ArrayList<>();
        //遍历log文件
        String folderPath = FolderPath.PATH_APP_ROOT + FolderPath.PATH_ERROR;
        File folderFile = new File(Environment.getExternalStorageDirectory(),
                folderPath);
        if (folderFile.exists()) {
            //文件夹存在
            File[] crashLogs = folderFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(CRASH_LOG_PREFIX);
                }
            });

            if (crashLogs!=null){
                for (int i=0;i<crashLogs.length;i++){
                    final File logItem = crashLogs[i];
                    String fileContent = FileUtil.readFileContent(logItem);
                    if (!TextUtils.isEmpty(fileContent)){
                        //不为空，则上传到服务器
                        LoggerUtil.i(TAG, "要上传到服务器的为log为：" + fileContent);
                        logList.add(fileContent);

//                        final FeedBackRequest request = new FeedBackRequest(FeedBackRequest.TYPE_CRASH_LOG,fileContent,new Response.Listener<Result>() {
//                            @Override
//                            public void onResponse(Result response) {
//                                LogPrinter.i(TAG,"上传log后的结果："+response);
//                                if (response.isSucess()){
//                                    LogPrinter.i(TAG,"删除文件："+logItem.getAbsolutePath());
//                                    logItem.delete();
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                LogPrinter.e(TAG,"上传log后的错误结果：",error);
//                            }
//                        });
//
//                        request.addToRequestQueue(TAG);
                    }
                }
            }

        } else {
            LoggerUtil.i(TAG, "文件夹不存在：" + folderPath);
        }
        return logList;


    }




    /**
     * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
     *
     * @param context
     * @param ex
     * @return
     */
    public String saveInfoToSD(Context context, Throwable ex) {
        LoggerUtil.e(TAG,"saveInfoToSD,保存异常信息");
        ex.printStackTrace();
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : obtainSimpleInfo(context)
                .entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }

        //获取系统未捕捉的错误信息
        String exceptionStackInfo = CustomCrashHandler.getExceptionStackInfo(ex);
        sb.append(exceptionStackInfo);

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(SDCARD_ROOT + FolderPath.PATH_APP_ROOT + FolderPath.PATH_ERROR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos=null;
            try {
                String suffix = ".log";
                if (SdkConst.DEBUG){
                    //debug状态下，改后缀名，方便开发点击文件直接打开查看log
                    suffix = ".txt";
                }
                fileName = dir.toString() + File.separator + CRASH_LOG_PREFIX
                        + parserTime(System.currentTimeMillis()) + suffix;
                fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
            } catch (Exception e) {
                Log.e(TAG,"保存log异常",e);
            }finally {
                CloseUtil.close(fos);
            }

        }

        return fileName;

    }






    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     *
     * @param context
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> map = new LinkedHashMap<String, String>();
        PackageManager mPackageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            map.put("versionName", packageInfo.versionName);
            map.put("versionCode", "" + packageInfo.versionCode);
//            map.put("WMValue", "" + CommonUtils.getAppWM());
//            map.put("FROMValue", "" + CommonUtils.getAppFrom());

            map.put("MODEL", "" + Build.MODEL);
            map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
            map.put("PRODUCT", "" + Build.PRODUCT);
        } catch (PackageManager.NameNotFoundException e) {
            LoggerUtil.w(TAG, "获取设备信息异常", e);
        }

        return map;
    }



    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     *
     * @param milliseconds
     * @return
     */
    private String parserTime(long milliseconds) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat(CRASH_LOG_NAME_PATTERN,
                Locale.CHINA);
        return format.format(new Date(milliseconds));
    }



}
