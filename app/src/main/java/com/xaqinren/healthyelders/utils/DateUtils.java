package com.xaqinren.healthyelders.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lee. on 2021/5/20.
 * 时间工具类
 */
public class DateUtils {


    public static String getRelativeTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        //转换时间戳
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(time);
            return format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMonth(String time) {
        //转换时间戳
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(time);
            return months[date.getMonth() - 1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDay(String time) {
        //转换时间戳
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(time);
            return String.valueOf(date.getDay());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getYMR(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private static String[] months = {"一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月",};

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_WEEK_AGO = "周前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    public static String format(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        //小于3天展示天
        if (delta < 3L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }

        if (delta < 4L * ONE_WEEK) {
            long days = toWeeks(delta);
            return (days <= 0 ? 1 : days) + ONE_WEEK_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toWeeks(long date) {
        return toDays(date) / 7L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

    /**
     * 获取过去时间点
     *
     * @param type
     * @return
     */
    public static long getLastPeroid(int type, Date date) {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String day = format.format(Date);
        Calendar c = Calendar.getInstance();

        if (0 == type) {
            //过去1天
            c.setTime(date);
            c.add(Calendar.DATE, -1);
            Date d = c.getTime();
            return d.getTime();
        } else if (1 == type) {
            //过去7天
            c.setTime(date);
            c.add(Calendar.DATE, -7);
            Date d = c.getTime();
            return d.getTime();
        } else if (2 == type) {
            //过去一月
            c.setTime(date);
            c.add(Calendar.MONTH, -1);
            Date m = c.getTime();
            return m.getTime();
        } else if (3 == type) {
            //过去一年
            c.setTime(date);
            c.add(Calendar.YEAR, -1);
            Date y = c.getTime();
            return y.getTime();
        } else if (4 == type) {
            //过去二十年
            c.setTime(date);
            c.add(Calendar.YEAR, -20);
            Date y = c.getTime();
            return y.getTime();
        }
        //过去1天
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        Date d = c.getTime();
        return d.getTime();
    }
}
