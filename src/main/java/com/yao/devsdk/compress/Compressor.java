package com.yao.devsdk.compress;

import android.os.AsyncTask;

import com.yao.devsdk.compress.util.CompressWay1;
import com.yao.devsdk.compress.util.PathUtil;

import java.io.File;


/**
 *
 * Created by fanyu on 16/1/26.
 */
public class Compressor {
    int compressQuality;
    String oldPath;
    String newPath;

    public interface CompleteListener{
        void onSuccess(String newPath);
    }

    public Compressor(int compressQuality, String oldPath) {
        this.compressQuality = compressQuality;
        this.oldPath = oldPath;
        this.newPath = PathUtil.generateNewPath(oldPath);
    }

    public void doCompress(CompleteListener listener) {
        new CompressTask(listener).execute();
    }

    /**
     * 同步压缩
     * @return
     */
    public String doCompress() {
        compress();
        return newPath;
    }


    void compress(){
        File newFile = new File(newPath);
        if (newFile != null && newFile.exists()){
            //当压缩过的文件存在，则删除
            newFile.delete();
        }

        CompressWay1.bitmapToString(oldPath, compressQuality, newPath);
    }

    class CompressTask extends AsyncTask<String,Integer,String>{
        CompleteListener listener;

        public CompressTask(CompleteListener listener) {
            this.listener = listener;
        }


        @Override
        protected String doInBackground(String... params) {
            compress();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (listener!=null){
                listener.onSuccess(newPath);
            }
        }
    }
}
