package cn.com.egova.module1;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hsy on 2017/8/14.
 */

public class DateUtils {
    public static Calendar calendar = null;
    public static String FORMAT_Y = "yyyy";
    public static String FORMAT_HM = "HH:mm";
    public static String FORMAT_MDHM = "MM-dd HH:mm";
    public static String FORMAT_MD_CN = "MM月dd日";
    public static String FORMAT_YMD = "yyyy-MM-dd";
    public static String FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
    public static String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
    public static String FORMAT_FULL_SN = "yyyyMMddHHmmssS";
    public static String FORMAT_YMD_CN = "yyyy年MM月dd日";
    public static String FORMAT_YMDH_CN = "yyyy年MM月dd日 HH时";
    public static String FORMAT_YMDHM_CN = "yyyy年MM月dd日 HH时mm分";
    public static String FORMAT_YMDHMS_CN = "yyyy年MM月dd日  HH时mm分ss秒";
    public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

    public DateUtils() {
    }

    public static String getDatePattern() {
        return FORMAT_YMDHMS;
    }

    public static String getNow() {
        return format(new Date());
    }

    public static String getNow(String format) {
        return format(new Date(), format);
    }

    public static String format(Date date) {
        return format(date, getDatePattern());
    }

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if(date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }

        return returnValue;
    }

    public static Date parse(String strDate) {
        return parse(strDate, getDatePattern());
    }

    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);

        try {
            return df.parse(strDate);
        } catch (ParseException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    @SuppressLint("WrongConstant")
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(2, n);
        return cal.getTime();
    }

    @SuppressLint("WrongConstant")
    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, n);
        return cal.getTime();
    }

    public static String getNextHour(String format, int h) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        date.setTime(date.getTime() + (long)(h * 60 * 60 * 1000));
        return sdf.format(date);
    }

    public static String getTimeString() {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    public static String getYear(Date date) {
        return format(date).substring(0, 4);
    }

    @SuppressLint("WrongConstant")
    public static int getMonth(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(2) + 1;
    }

    @SuppressLint("WrongConstant")
    public static int getDay(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(5);
    }

    @SuppressLint("WrongConstant")
    public static int getHour(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(11);
    }

    @SuppressLint("WrongConstant")
    public static int getMinute(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(12);
    }

    @SuppressLint("WrongConstant")
    public static int getSecond(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(13);
    }

    public static long getMillis(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public static int countDays(String date) {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date));
        long t1 = c.getTime().getTime();
        return (int)(t / 1000L - t1 / 1000L) / 3600 / 24;
    }

    public static int countDays(String date, String format) {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date, format));
        long t1 = c.getTime().getTime();
        return (int)(t / 1000L - t1 / 1000L) / 3600 / 24;
    }
}
