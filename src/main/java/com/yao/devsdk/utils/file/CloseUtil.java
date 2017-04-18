package com.yao.devsdk.utils.file;

import android.os.ParcelFileDescriptor;

import com.yao.devsdk.log.LoggerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * 关闭文件流的Utils，与FileUtils关系紧密，放在一个独立包中
 */
public final class CloseUtil {

    public static final String TAG_CLOSE = "CloseUtils";

    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // #debug
                LoggerUtil.e(TAG_CLOSE, e.getMessage(), e.getCause());
            }
        }
    }

    public static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                // #debug
                LoggerUtil.e(TAG_CLOSE, "写文件final异常", e);
            }
        }

    }

    public static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                // #debug
                LoggerUtil.e(TAG_CLOSE, e.getMessage(), e.getCause());
            }
        }
    }

    public static void close(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                // #debug
                LoggerUtil.e(TAG_CLOSE, e.getMessage(), e.getCause());
            }
        }
    }

    public static void close(ParcelFileDescriptor pfd) {
        if (pfd != null) {
            try {
                pfd.close();
            } catch (IOException e) {
                // #debug
                LoggerUtil.e(TAG_CLOSE, e.getMessage(), e.getCause());
            }
        }
    }

    public static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // #debug
                LoggerUtil.e(TAG_CLOSE, e.getMessage(), e.getCause());
            }
        }
    }

    public static void close(DatagramSocket datagramSocket) {
        if (datagramSocket != null) {
            try {
                datagramSocket.close();
            } catch (Exception e) {
                // #debug
                LoggerUtil.e(TAG_CLOSE, e.getMessage(), e.getCause());
            }
        }
    }
}
