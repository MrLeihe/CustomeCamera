package com.code.library.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    private TimeUtils() {
    }

    /**
     * 返回时间戳的描述
     *
     * @param timestamp
     * @return
     */
    public static String getTimeDescription(long timestamp) {
        long currentSeconds = System.currentTimeMillis() / 1000;
        long timeGap = currentSeconds - timestamp;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > 24 * 60 * 60 * 3) {
            timeStr = getTime(timestamp,"yyyy/MM/dd HH:mm");
        } else if (timeGap > 24 * 60 * 60) {// 1天以上
            timeStr = timeGap / (24 * 60 * 60) + "天前";
        } else if (timeGap > 60 * 60) {// 1小时-24小时
            timeStr = timeGap / (60 * 60) + "小时前";
        } else if (timeGap > 60) {// 1分钟-59分钟
            timeStr = timeGap / 60 + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 返回当前时间的文本
     *
     * @return
     */
    public static String getTime() {
        return getTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 根据格式返回当前时间
     *
     * @param format
     * @return
     */
    public static String getTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 将时间戳转为当前时间字符
     *
     * @param timestamp
     * @return
     */
    public static String getTime(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    public static String getTime(long timestamp,String format) {
        Date date = new Date(timestamp * 1000L);
        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }
}
