package com.yao.devsdk.utils;

import android.text.format.Time;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * Created by huichuan on 16/4/8.
 */
public class TimeUtil {



    /**
     * 这个时间是否比今天早
     */
    public static boolean isBeforeToday(long timeMillions) {
        if (timeMillions <= 0) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timeMillions));
        int year = cal.get(Calendar.YEAR);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(new Date());
        if (year < cal.get(Calendar.YEAR)) {
            // 这个时间是去年
            return true;
        } else if (year == cal.get(Calendar.YEAR)) {
            // 这个时间是今年
            if (dayOfYear < cal.get(Calendar.DAY_OF_YEAR)) {
                // 这个时间比今天早
                return true;
            }
        }
        return false;
    }

    /**
     * 这个时间是否是今天
     */
    public static boolean isToday(long timeMillions) {
        if (timeMillions <= 0) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillions);
        int year = cal.get(Calendar.YEAR);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTimeInMillis(System.currentTimeMillis());
        if (year < cal.get(Calendar.YEAR)) {
            // 这个时间是去年
            return false;
        } else if (year == cal.get(Calendar.YEAR)) {
            // 这个时间是今年
            if (dayOfYear == cal.get(Calendar.DAY_OF_YEAR)) {
                // 这个时间是今天
                return true;
            }
        }
        return false;
    }


    public static boolean isYeaterday(long oldTime) throws ParseException {
        return  compareYeaterday(oldTime, 0) == 0;
    }

    /**
     * @author LuoB.
     * @param oldTime 较小的时间
     * @param newTime 较大的时间 (如果为空   默认当前时间 ,表示和当前时间相比)
     * @return -1 ：同一天.    0：昨天 .   1 ：至少是前天.
     * @throws ParseException 转换异常
     */
    public static int compareYeaterday(long oldTime,long newTime) throws ParseException{
        if (newTime == 0){
            newTime = System.currentTimeMillis();
        }
        //将下面的 理解成  yyyy-MM-dd 00：00：00 更好理解点
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(new Date(newTime));
        Date today = format.parse(todayStr);//将新日期改为当天的0点0分0秒
        //昨天 86400000=24*60*60*1000 一天
        if((today.getTime()-oldTime)>0 && (today.getTime()-oldTime)<=86400000) {
            return 0;
        }
        else if((today.getTime()-oldTime)<=0){ //至少是今天
            return -1;
        }
        else{ //至少是前天
            return 1;
        }

    }

    /**
     * 是否超过给定的时间
     */
    public static boolean isOverTheTime(int hour) {
        if (hour < 0 || hour > 24) {
            throw new RuntimeException("请给出正确的小时时间");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        return hourOfDay >= hour;
    }


    /**
     *
     * @return
     */
    public static String getTimeString(long createTime) throws Exception {

        String timeString = null;

        long currentTime = System.currentTimeMillis();
        int createTimeLength = String.valueOf(createTime).length();
        if (createTimeLength == 10){
            //秒制，服务器时间
            createTime  = createTime * 1000;
        }else if (createTimeLength == 13){
            //毫秒制，和客户端一致
        }else{
            throw new RuntimeException("时间格式不正确");
        }

        if (createTime > currentTime){
            //创建时间比现在还要晚，例如：明天
            throw new RuntimeException("创建时间晚于现在，不正确！");
        }


        long timeOffset = currentTime - createTime;

        boolean isToday = isToday(createTime);
        if (isToday){
            //今天
            timeString = parseTimeString(createTime);
        }else{
            boolean isYesterday = isYeaterday(createTime);
            if(isYesterday){
                //昨天
                timeString = "昨天"+parseTimeString(createTime);
            }else{

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(createTime);
                cal.get(Calendar.DAY_OF_WEEK);


            }


        }
        return timeString;
    }



    public static String parseTimeString(long createTime) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("HH:mm",
                Locale.CHINA);
        return format.format(new Date(createTime));
    }


    /**
     * 判断当前日期是星期几
     *
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public  static String getWeek(Calendar calendar) {
        String Week = "";

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            Week += "日";
        }else if (dayOfWeek == 2) {
            Week += "一";
        }else if (dayOfWeek == 3) {
            Week += "二";
        }else if (dayOfWeek == 4) {
            Week += "三";
        }else if (dayOfWeek == 5) {
            Week += "四";
        }else if (dayOfWeek == 6) {
            Week += "五";
        }else if (dayOfWeek == 7) {
            Week += "六";
        }
        return Week;
    }
    public static boolean isSameDay(Date d1,Date d2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return  sameDay;
    }
    public static boolean isYesterday(Date d1,Date d2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        int day1_of_year=cal1.get(Calendar.DAY_OF_YEAR);
        int day2_of_year=cal2.get(Calendar.DAY_OF_YEAR);
        boolean yesterday = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                (Math.abs(day1_of_year-day2_of_year)==1);
        return  yesterday;
    }
    public static boolean isInOneWeek(Date d1,Date d2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        int day1_of_year=cal1.get(Calendar.DAY_OF_YEAR);
        int day2_of_year=cal2.get(Calendar.DAY_OF_YEAR);
        boolean inOneWeek = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                (Math.abs(day1_of_year-day2_of_year)<=6);
        return  inOneWeek;
    }



    public static String getTimeLength(long curTime, long originTime) {
        final String JUST_NOW = "刚刚";
        final String INNER_ONE_HOUR = "分钟前";
        final String INNER_HOUR = "小时前";
        final String YESTERDAY = "昨天";


        String timeLength = "";
        long timeOffset = curTime - originTime;
        if (timeOffset < 60) {
            timeLength = JUST_NOW;

        } else if (timeOffset > 60 && timeOffset < 60 * 60) {
            long time = timeOffset / 60;
            String prefx = INNER_ONE_HOUR;
            timeLength = time + prefx;

        } else if (timeOffset > 60 * 60 && timeOffset < 60 * 60 * 24) {
            long time = timeOffset / 3600;
            String prefx = INNER_HOUR;
            timeLength = time + prefx;

        } else if (timeOffset > 60 * 60 * 24
                && timeOffset < (curTime - getZeroTime())) {
            timeLength = YESTERDAY;

        } else {
            timeLength = parserDataFromSecondInDisplayType(String
                    .valueOf(originTime));

        }
        return timeLength;
    }


    /**
     * 获取某天的零时时刻
     *
     * @return
     */
    public static long getZeroTime() {
        Calendar ca = Calendar.getInstance(Locale.CHINA);
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        int minute = ca.get(Calendar.MINUTE);
        int second = ca.get(Calendar.SECOND);
        long current = getCurrentTime();
        return current - (hour * 3600 + minute * 60 + second) - 60 * 60 * 24;
    }

    /**
     * 获取当前的时间，以秒为单位
     *
     * @return
     */
    public static long getCurrentTime() {
        Time t = new Time();
        t.setToNow();
        return t.toMillis(false) / 1000;
    }

    public static String parserDataFromSecondInDisplayType(String str) {
        SimpleDateFormat COMMENT_DATE_FORMAT = new SimpleDateFormat(
                "MM月dd日", Locale.CHINA);
        SimpleDateFormat sdf = COMMENT_DATE_FORMAT;
        StringBuffer sb = new StringBuffer();
        if (str != null && !str.equals("")) {
            long millsecond = Long.valueOf(str) * 1000;
            Date date = new Date(millsecond);
            sdf.format(date, sb, new FieldPosition(1));
        }
        return sb.toString();
    }

}
