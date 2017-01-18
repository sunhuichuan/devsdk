package com.yao.devsdk.utils;

import android.os.Build;
import android.text.TextUtils;

import com.yao.devsdk.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * 判断Cpu架构的工具类
 * Created by huichuan on 16/1/5.
 */
public class CpuUtil {
    public static final String TAG = "CpuUtils";


    public static final String AARCH64 = "AArch64";
    public static final String ARM = "ARM";


    /**
     * 三个默认值，第一、三个设置为字符串，第二个设置为 数字
     */
    public static Object[] mArmArchitecture = new Object[]{"-1",-1,"-1"};



    public boolean isArmV8a(){
        return false;
    }


    public boolean is64bitCpu(){

        String arch = System.getProperty("os.arch");

        if("arch64".equals(arch)||"aarch64".equals(arch)||"x86_64".equals(arch)){
            return true;
        }
        Object[] mArmArchitecture = getCpuArchitecture();
        String processor = (String) mArmArchitecture[0];
        Integer version = (Integer) mArmArchitecture[1];
        if (TextUtils.equals(processor, AARCH64)){
            return true;
        }else{
            if (TextUtils.equals(processor,ARM)&&(version!=null &&version == 7)){
                //Arm v7
                return false;
            }
        }

        //不可识别，都判定为64位
        return true;
    }








    /**
     *
     * [获取cpu类型和架构]
     *
     * @return
     * 三个参数类型的数组，第一个参数标识是不是ARM架构，第二个参数标识是V6还是V7架构，第三个参数标识是不是neon指令集
     */
    public static Object[] getCpuArchitecture() {
        if ((Integer) mArmArchitecture[1] != -1) {
            return mArmArchitecture;
        }
        try {
            InputStream is = new FileInputStream("/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            try {
                String nameProcessor = "Processor";
                String nameFeatures = "Features";
                String nameModel = "model name";
                String nameCpuFamily = "cpu family";
                while (true) {
                    String line = br.readLine();
                    LogUtil.i(TAG, "cpuInfo:" + line);
                    String[] pair = null;
                    if (line == null) {
                        break;
                    }
                    pair = line.split(":");
                    if (pair.length != 2)
                        continue;
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    if (key.compareTo(nameProcessor) == 0) {
                        String n = "";
                        int indexOfARMv = val.indexOf("ARMv");
                        if (indexOfARMv!=-1){
                            //找到了ARMv
                            for (int i = indexOfARMv + 4; i < val.length(); i++) {
                                String temp = val.charAt(i) + "";
                                if (temp.matches("\\d")) {
                                    //匹配数字
                                    n += temp;
                                } else {
                                    break;
                                }
                            }
                            mArmArchitecture[0] = CpuUtil.ARM;
                            mArmArchitecture[1] = Integer.parseInt(n);
                        }else{
                            int indexOfAArc64 = val.indexOf(CpuUtil.AARCH64);
                            if (indexOfAArc64!=-1){
                                //含有64位标识
                                mArmArchitecture[0] = CpuUtil.AARCH64;
                            }
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameFeatures) == 0) {
                        if (val.contains("neon")) {
                            mArmArchitecture[2] = "neon";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameModel) == 0) {
                        if (val.contains("Intel")) {
                            mArmArchitecture[0] = "INTEL";
                            mArmArchitecture[2] = "atom";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameCpuFamily) == 0) {
                        mArmArchitecture[1] = Integer.parseInt(val);
                        continue;
                    }
                }
            } finally {
                br.close();
                ir.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mArmArchitecture;
    }









    private static String[] sCpuInfo = null;


    /**
     * 是否是arm架构cpu arm armv7
     *
     * @return
     */
    public static boolean isArmCpu() {
        String cpu = getCpuType();
        return !TextUtils.isEmpty(cpu) && cpu.contains("arm");
    }

    public static String getCpuType() {
        String cpu = Build.CPU_ABI;
        if (TextUtils.isEmpty(cpu)) {
            String[] cpuInfo = getCpuInfo();
            if (cpuInfo != null && !TextUtils.isEmpty(cpuInfo[0])) {
                cpu = cpuInfo[0].toLowerCase();
            }
        } else {
            cpu = cpu.toLowerCase();
        }
        LogUtil.d(TAG, "cpu:" + cpu);
        return cpu;
    }

    /**
     * 获取cpu信息 基本上耗时 几毫秒 0型号 1频率 ARMv7
     *
     * @return
     */
    public static String[] getCpuInfo() {
        if (sCpuInfo == null) {
            String path = "/proc/cpuinfo";
            String data = "";
            String[] cpuInfo = {"", ""};
            String[] arrayOfString;
            try {
                FileReader file = new FileReader(path);
                BufferedReader localBufferedReader = new BufferedReader(file, 8192);
                data = localBufferedReader.readLine();
                arrayOfString = data.split("\\s+");
                for (int i = 2; i < arrayOfString.length; i++) {
                    cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
                }
                data = localBufferedReader.readLine();
                arrayOfString = data.split("\\s+");
                cpuInfo[1] += arrayOfString[2];
                localBufferedReader.close();

                sCpuInfo = cpuInfo;
            } catch (IOException e) {
            }
        }
        return sCpuInfo;
    }





    /**
     * 获取CPU核心数
     *
     * @return
     */
    public static int getNumCores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            // Default to return 1 core
            return 1;
        }
    }




}
