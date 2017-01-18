package com.yao.devsdk.utils.file;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.constants.FolderPath;
import com.yao.devsdk.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtil {

    private static final String TAG = "FileUtil";



    private static final String SDCARD_ROOT = Environment
            .getExternalStorageDirectory().toString();

    static {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File logDir = new File(SDCARD_ROOT + FolderPath.PATH_APP_ROOT + FolderPath.PATH_ERROR);
                if (!logDir.exists()) {
                    logDir.mkdir();
                }
            } catch (Exception e) {
                LogUtil.w(TAG, "创建文件夹异常", e);
            }
        }
    }




    public static String getRootFolderPath() {
        String appRootPath = SDCARD_ROOT + FolderPath.PATH_APP_ROOT;
        LogUtil.i(TAG,"app根目录："+appRootPath);
        return appRootPath;
    }



    public static void mediaScanFile(Context context,Uri path) {
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        context.sendBroadcast(localIntent);
    }

    public static File createDir(Context context,String path) {
        File dir;
        if (isSDCardMounted()) {
            dir = Environment.getExternalStorageDirectory();
            String tempDir = dir.getPath() + path;
            dir = new File(tempDir);
        } else {
            dir = context.getFilesDir();
        }
        makDirs(dir);
        return dir;
    }

    public static byte[] inputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        byte[] data = null;
        try {
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            data = bytestream.toByteArray();
        } finally {
            CloseUtil.close(bytestream);
        }
        return data;
    }

    public static String getStringFromAssertsFile(Context context,String fileName) {
        String ret = null;// "file:///android_asset/" + fileName;
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.getClass().getResourceAsStream(
                    "/assets/" + fileName);
            if (null != is) {
                ret = new String(inputStreamToByte(is));
            }
            // AssetFileDescriptor descriptor = assetManager.openFd(fileName);
        } catch (Exception e) {
            // #debug
            LogUtil.e(TAG, "getAssertsPath(): e=", e);
        } finally {
            CloseUtil.close(is);
        }
        return ret;
    }

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static void makeSureFileExist(File file) {
        if (file != null && !file.exists()) {
            makDirs(file.getParentFile());
            try {
                file.createNewFile();
            } catch (IOException e) {
                LogUtil.e(TAG, e.getMessage(), e.getCause());
            }
        }
    }

    public static void makDirs(File file) {
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean isExist(File file) {
        return file != null && file.exists();
    }

    /**
     * 删除文件
     * @param fileAbsolutePath
     */
    public static void deleteFile(String fileAbsolutePath) {
        deleteFile(new File(fileAbsolutePath));
    }
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public static void renameFile(File oldFile, File newFile) {
        if (oldFile != null && newFile != null && oldFile.exists()) {
            deleteFile(newFile);
            oldFile.renameTo(newFile);
        }
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dest
     */
    public static void copyFile(File src, File dest) {
        if (src != null && dest != null && isExist(src)) {
            makeSureFileExist(dest);
            FileInputStream fileInputStream;
            FileOutputStream fileOutputStream;
            try {
                fileInputStream = new FileInputStream(src);
                fileOutputStream = new FileOutputStream(dest);
                copy(fileInputStream, fileOutputStream);
            } catch (Exception e) {
                LogUtil.e(TAG, "copyFile 异常", e);
            }
        }
    }

    public static boolean copy(InputStream inputStream,
                               OutputStream outputStream) {
        if (inputStream != null && outputStream != null) {
            int size = 512 * 1024;
            if (!(inputStream instanceof BufferedInputStream)) {
                inputStream = new BufferedInputStream(inputStream, size);
            }
            if (!(outputStream instanceof BufferedOutputStream)) {
                outputStream = new BufferedOutputStream(outputStream, size);
            }
            byte buffer[] = new byte[size];
            int len = 0;
            try {
                while ((len = inputStream.read(buffer)) >= 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                // #debug
                LogUtil.e(TAG, e.getMessage(), e.getCause());
            } finally {
                CloseUtil.close(inputStream);
                CloseUtil.close(outputStream);
            }
        }
        return false;
    }

    public static void saveToSDCard(String filename, String content)
            throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(),
                filename);
        FileOutputStream outStream = new FileOutputStream(file);
        outStream.write(content.getBytes());
        outStream.close();
    }


    /**
     * 清理文件夹
     *
     * @param folderPath    要清理的文件夹路径
     * @param maintainCount 保留的文件个数
     */
    public static void cleanSDFolderFile(String folderPath, final String fileNamePrefix,int maintainCount, Comparator comparator) {
        File folderFile = new File(Environment.getExternalStorageDirectory(),
                folderPath);
        if (folderFile.exists()) {
            //文件夹存在
            File[] crashLogs = folderFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(fileNamePrefix);
                }
            });

            if (crashLogs!=null && crashLogs.length > maintainCount) {
                //当前log数量大于要保留的数量
                //从要求从最老到最新排列
                Arrays.sort(crashLogs, comparator);
                for (int i = 0, size = crashLogs.length - maintainCount; i < size; i++) {
                    crashLogs[i].delete();
                }
            } else {
                LogUtil.i(TAG, "当前文件夹总数：" + crashLogs.length + ",不够要保留的总数：" + maintainCount + ",不删除");
            }

        } else {
            LogUtil.i(TAG, "文件夹不存在：" + folderPath);
        }
    }


    /**
     * 本地文件是否存在
     * @param fileName
     * @return
     */
    public static boolean isInnerFileExist(Context context,String fileName){
        try {
            File innerFile = context.getFileStreamPath(fileName);
            if (innerFile!=null && innerFile.exists()){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 删除文件
     * @param fileName
     * @return
     */
    public static File getInnerFile(Context context,String fileName){
        try {
            File innerFile = context.getFileStreamPath(fileName);
            return innerFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 向本地写数据,以追加形式
     */
    public static void saveString2Inner(Context context,String fileName, String content) {
        try {
            FileOutputStream fos = context.openFileOutput(
                    fileName, Context.MODE_APPEND);
            PrintStream ps = new PrintStream(fos);
            ps.print(content);
            ps.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "saveString2Inner FileNotFoundException", e);
        }
    }

    /**
     * 根据文件路径读取文件内容feed流顺序表
     *
     * @return
     */
    public static synchronized String readInnerFile(Context context,String fileName) {
        StringBuilder sb = new StringBuilder(20);
        try {
            if (context.getFileStreamPath(fileName).exists()){
                //文件存在
                FileInputStream fis = context.openFileInput(
                        fileName);
                byte[] buff = new byte[1024];
                int hasRead = 0;
                while ((hasRead = fis.read(buff)) > 0) {
                    sb.append(new String(buff, 0, hasRead));
                }
            }else{
                LogUtil.e(TAG, "文件【" + context.getFileStreamPath(fileName).getAbsolutePath() + "】不存在！！！");
            }
        } catch (FileNotFoundException e) {
            LogUtil.d(TAG, "readInnerFile FileNotFoundException", e);
        } catch (IOException e) {
            LogUtil.e(TAG, "readInnerFile IOException", e);
        }
        return sb.toString();
    }
    /**
     * 根据文件路径读取文件内容feed流顺序表
     *
     * @return
     */
    public static synchronized String readFileContent(File file) {
        if (file == null){
            return "";
        }
        StringBuilder sb = new StringBuilder(20);
        try {
            if (file.exists()){
                //文件存在
                FileInputStream fis = new FileInputStream(file);
                byte[] buff = new byte[1024];
                int hasRead = 0;
                while ((hasRead = fis.read(buff)) > 0) {
                    sb.append(new String(buff, 0, hasRead));
                }
            }else{
                LogUtil.e(TAG, "文件【" + file.getAbsolutePath() + "】不存在！！！");
            }
        } catch (FileNotFoundException e) {
            LogUtil.d(TAG, "readInnerFile FileNotFoundException", e);
        } catch (IOException e) {
            LogUtil.e(TAG, "readInnerFile IOException", e);
        }
        return sb.toString();
    }





    /**
     * 根据文件路径读取文件内容feed流顺序表
     *
     * @return
     */
    public static synchronized boolean deleteInnerFile(Context context,String fileName) {
        boolean result = false;
        try {
            result = context.deleteFile(fileName);
        } catch (Exception e) {
            LogUtil.e(TAG, "deleteInnerFile Exception", e);
        }

        return result;
    }


    /**
     * 清除当前时间以前的数据
     * clear the cache before time numDays
     * @param dir
     * @param numDays
     * @return
     */
    public static int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }



    /**
     * 删除某个文件夹下的所有文件夹和文件
     *
     * @param folderPth
     *            String
     * @throws FileNotFoundException
     * @throws IOException
     * @return boolean
     */
    public static boolean deleteFiles(String folderPth) throws Exception {
        try {

            File file = new File(folderPth);
            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(folderPth + "//" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                        LogUtil.i(TAG, delfile.getAbsolutePath() + "删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deleteFiles(folderPth + "//" + filelist[i]);
                    }
                }
                LogUtil.i(TAG, file.getAbsolutePath() + "删除成功");
                file.delete();
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "deleteFile() Exception", e);
        }
        return true;
    }


    /**
     * 写字符串到sd卡的文件上
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void writeStringToSDFile(String fileName, String content) {

        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            String folderPath = Environment.getExternalStorageDirectory() + "/" + FolderPath.PATH_LOG;
            File folderFile = new File(folderPath);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            writer = new FileWriter(folderPath + "/" + fileName, true);
            writer.write(content);
        } catch (IOException e) {
            LogUtil.e(TAG, "写文件异常", e);
        } finally {

            CloseUtil.close(writer);

        }
    }

    public static void copyDBToSDCard() {
        final String folderName = "/DebugRomFolder";
        File romFolderFile = new File(FileUtil.getRootFolderPath(), folderName);
        if (!romFolderFile.exists()) {
            romFolderFile.mkdir();
        }

        Context context = SdkConfig.getAppContext();
        String packageName = context.getPackageName();
        File cacheDir = context.getCacheDir();
        File parentFile = cacheDir.getParentFile();
        if (TextUtils.equals(parentFile.getName(), packageName)) {
            LogUtil.i(TAG, "缓存目录：" + parentFile);
            copyFolderFiles(parentFile, romFolderFile);

        }

    }


    public static void copyFolderFiles(File folderSrc, File folderDes){
        if (folderSrc==null || folderDes==null){
            return;
        }

        if (!folderSrc.exists()){
            return;
        }
        //目标目录不存在则创建
        if(!folderDes.exists()){
            folderDes.mkdir();
        }


        if (!folderSrc.isDirectory() || !folderDes.isDirectory()){
            return;
        }


        //文件夹中有内容
        File[] listFiles = folderSrc.listFiles();
        for (File fileItem : listFiles){
            if (fileItem.isDirectory()){
                //是个目录
                File desItem = new File(folderDes,fileItem.getName());
                copyFolderFiles(fileItem,desItem);
            }else{
                //是个文件
                File desItem = new File(folderDes,fileItem.getName());
                FileUtil.copyFile(fileItem,desItem);
            }
        }

        LogUtil.i(TAG,"Done "+folderSrc+" 到 "+folderDes+" , 拷贝完成");

    }



}
