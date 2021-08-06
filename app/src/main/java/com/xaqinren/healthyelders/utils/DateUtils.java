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

    public static int getAgeByTime(String str) throws ParseException {
        // 使用默认时区和语言环境获得一个日历
        Calendar cal = Calendar.getInstance();
//        int a = Integer.parseInt((str.subSequence(0, 4).toString())); // 输入的年
//        int a1 = Integer.parseInt((str.subSequence(4, 6).toString())); // 输入的月
//        int a2 = Integer.parseInt((str.subSequence(6, 8).toString())); // 输入的日
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = simpleDateFormat.parse(str);
        Calendar bCal = Calendar.getInstance();
        bCal.setTime(parse);
        int a = bCal.get(Calendar.YEAR);
        int a1 = bCal.get(Calendar.MONTH) + 1;
        int a2 = bCal.get(Calendar.DAY_OF_MONTH);
        int b = cal.get(Calendar.YEAR); // 当前的年
        int b1 = cal.get(Calendar.MONDAY) + 1; // 当前的月
        int b2 = cal.get(Calendar.DAY_OF_MONTH); // 当前的日

        // 粗略判断
        if (a > b || a < 1000 || a1 > 12 || a1 <= 0 || a2 > 31 || a2 <= 0) {
            return -1;
        }

        // 进一步判断
        if ((a % 4 == 0 && a1 == 2 && a2 > 29) || (a % 4 != 0 && a1 == 2 && a2 > 28)
                || ((a1 == 4 || a1 == 6 || a1 == 9 || a1 == 11) && a2 > 30)
                || (a == b && (a1 < b1 || (a1 == b1 && a2 > b2)))) {
            return -1;
        }

        // 计算年龄
        int age = b - a;
        if (b1 < a1 || (b1 == a1 && b2 <= a2)) {
            age--;
        }
        return age;
    }

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
            return months[date.getMonth()];
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
