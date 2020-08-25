package com.zb.lib_base.utils;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    // HH24小时制  hh12小时制
    public final static String yyyy_MM_dd = "yyyy-MM-dd";
    public final static String yyyy_MM_dd_nyr = "yyyy年MM月dd日";
    public final static String CN_MM_dd = "MM月dd日";
    public final static String MM_dd_HH_mm = "MM/dd HH:mm";
    public final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public final static String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public final static String HH_mm = "HH:mm";
    public final static String US_yyyy_MMM_dd_HH_mm_ss = "EEE MMM dd HH:mm:ss 'CST' yyyy";


    /**
     * 当前日期
     *
     * @param pattern
     * @return
     */
    public static String getNow(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(Calendar.getInstance().getTime());
    }

    /**
     * 前90天日期
     *
     * @return
     */
    public static String getNow_90() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        Date date = calendar.getTime();
        return DateUtil.dateToStr(date, DateUtil.yyyy_MM_dd_nyr);
    }

    /**
     * 将日期时间型转成字符串
     *
     * @param date
     * @param pattern yyyy_MM_dd_HH_mm_ss
     * @return
     */
    public static String dateToStr(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 将字符串型(中文格式)转成日期型
     *
     * @param str     "2002-07-01 22:09:55"
     * @param pattern yyyy_MM_dd_HH_mm_ss
     * @return
     */
    public static Date strToDate(String str, String pattern) {
        Date date = null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.CHINA);
            date = fmt.parse(str);
            return date;
        } catch (Exception e) {
            return date;
        }
    }

    /**
     * 将字符型转化成字符型
     *
     * @param userTime
     * @return
     */
    public static String strToStr(String userTime) {
        Date date;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            date = fmt.parse(userTime);
        } catch (Exception e) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(MM_dd_HH_mm);
        return sdf.format(date);
    }

    /**
     * 将字符串型(英文格式)转成日期型 如: "Tue Dec 26 14:45:20 CST 2000"
     *
     * @param USStr 字符串 "Tue Dec 26 14:45:20 CST 2000"
     * @return Date 日期
     */
    public static Date USStrToDate(String USStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(US_yyyy_MMM_dd_HH_mm_ss, Locale.US);
            return sdf.parse(USStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 判断一年的第几周
     *
     * @param datetime
     * @return
     * @throws java.text.ParseException
     */
    public static int whatWeek(String datetime) throws java.text.ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(datetime);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 比较2个日期毫秒大小
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static long compareTime(String DATE1, String DATE2, String dateType) {
        Date dt1 = strToDate(DATE1, dateType);
        Date dt2 = strToDate(DATE2, dateType);
        return dt1.getTime() - dt2.getTime();
    }

    /**
     * 比较2个日期毫秒大小
     *
     * @param DATE1
     * @param time2
     * @return
     */
    public static long compareTime(String DATE1, long time2, String dateType) {
        Date dt1 = strToDate(DATE1, dateType);
        return dt1.getTime() - time2;
    }

    /**
     * 比较2个日期
     *
     * @param DATE1
     * @param DATE2
     * @param type  相隔天数 1000f * 3600f * 24f;
     *              相隔小时 1000f * 3600f;
     *              相隔分钟 1000f * 60f;
     * @return
     */
    public static int getDateCount(String DATE1, String DATE2, String dateType, float type) {
        return (int) (compareTime(DATE1, DATE2, dateType) / type);
    }

    /**
     * 得到几天后或几天前的时间
     *
     * @param day 几天后 >0;
     *            几天前 <0;
     * @return
     */
    public static String getOtherDate(String dateStr, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(strToDate(dateStr, yyyy_MM_dd));
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return dateToStr(now.getTime(), yyyy_MM_dd);
    }

    /**
     * 转换util.date-->sql.date
     *
     * @param inDate
     * @return
     */
    public static java.sql.Date UtilDateToSqlDate(Date inDate) {
        return new java.sql.Date(getTimeByDate(inDate));
    }

    /**
     * 根据日期类型计算毫秒
     *
     * @param date
     * @return
     */
    public static long getTimeByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        cal.set(year, month, day, 0, 0, 0);
        long result = cal.getTimeInMillis();
        result = result / 1000 * 1000;
        return result;
    }

    /**
     * 根据String类型计算毫秒
     *
     * @param dateStr
     * @return
     */
    public static long getTimeByString(String dateStr) {
        SimpleDateFormat sdr = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, Locale.CHINA);
        Date date = null;
        try {
            date = sdr.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 倒计时
     *
     * @param text
     * @param textInfo
     * @param finishInfo
     * @param startDate
     * @param hour
     */
    public void countDownTimer(final TextView text, final String textInfo, final String finishInfo, String startDate, int hour) {
//        textInfo = "订单将预留一天时间，请尽快付款（%d时%d分%d秒）";
        new CountDownTimer(getTimeByString(startDate) + hour * 60 * 60 * 1000
                - getTimeByString(getNow(yyyy_MM_dd_HH_mm_ss)), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int HH = (int) (millisUntilFinished / (60 * 60 * 1000));
                int mm = (int) ((millisUntilFinished - (HH * 60 * 60 * 1000)) / (60 * 1000));
                int ss = (int) ((millisUntilFinished - (HH * 60 * 60 * 1000) - (mm * 60 * 1000)) / 1000);
                text.setText(String.format(textInfo, HH, mm, ss));
            }

            @Override
            public void onFinish() {
                text.setText(finishInfo);
            }
        }.start();
    }


    /**
     * 遍历刚从数据库里查出来的Map，将里面Timestamp格式化成指定的pattern
     *
     * @param target  目标map,就是一般是刚从数据库里查出来的
     * @param pattern 格式化规则，从自身取
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Deprecated
    public static void formatMapDate(Map target, String pattern) {
        for (Object item : target.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            if (entry.getValue() instanceof Timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                entry.setValue(sdf.format((Timestamp) entry.getValue()));
            }
        }
    }

    /**
     * 日期转化为大小写 chenjiandong 20090609 add
     *
     * @param date
     * @param type 1;2两种样式1为简体中文，2为繁体中文
     * @return
     */
    public static String dataToUpper(Date date, int type) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH) + 1;
        int day = ca.get(Calendar.DAY_OF_MONTH);
        return numToUpper(year, type) + "年" + monthToUppder(month, type) + "月" + dayToUppder(day, type) + "日";
    }

    /**
     * 将数字转化为大写
     *
     * @param num
     * @param type
     * @return
     */
    public static String numToUpper(int num, int type) {// type为样式1;2
        String[] u1 = {"〇", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] u2 = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        char[] str = String.valueOf(num).toCharArray();
        String rstr = "";
        if (type == 1) {
            for (int i = 0; i < str.length; i++) {
                rstr = rstr + u1[Integer.parseInt(str[i] + "")];
            }
        } else if (type == 2) {
            for (int i = 0; i < str.length; i++) {
                rstr = rstr + u2[Integer.parseInt(str[i] + "")];
            }
        }
        return rstr;
    }

    /**
     * 月转化为大写
     *
     * @param month
     * @param type
     * @return
     */
    public static String monthToUppder(int month, int type) {
        if (month < 10) {
            return numToUpper(month, type);
        } else if (month == 10) {
            return "十";
        } else {
            return "十" + numToUpper((month - 10), type);
        }
    }

    /**
     * 日转化为大写
     *
     * @param day
     * @param type
     * @return
     */
    public static String dayToUppder(int day, int type) {
        if (day < 20) {
            return monthToUppder(day, type);
        } else {
            char[] str = String.valueOf(day).toCharArray();
            if (str[1] == '0') {
                return numToUpper(Integer.parseInt(str[0] + ""), type) + "十";
            } else {
                return numToUpper(Integer.parseInt(str[0] + ""), type) + "十"
                        + numToUpper(Integer.parseInt(str[1] + ""), type);
            }
        }
    }

    /**
     * 根据日期取得星期几
     *
     * @param datestr
     * @return
     */
    public static String getWeek(String datestr) {
        Date date = strToDate(datestr, yyyy_MM_dd);
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 聊天记录时间
     *
     * @param datestr
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getChatTime(String datestr) {
        String chatTime = "";
        Date date = strToDate(datestr, yyyy_MM_dd_HH_mm_ss);
        double dayCount = getDateCount(getNow(yyyy_MM_dd), datestr.substring(0, 10), yyyy_MM_dd, 1000f * 3600f * 24f);
        if (dayCount == 0) {// 今天
            if (date.getHours() < 7) {
                chatTime = "凌晨 " + datestr.substring(11, 16);
            } else if (date.getHours() < 13) {
                chatTime = "上午 " + datestr.substring(11, 16);
            } else if (date.getHours() < 18) {
                chatTime = "下午 " + datestr.substring(11, 16);
            } else {
                chatTime = "晚上 " + datestr.substring(11, 16);
            }
        } else {
            chatTime = datestr.substring(5, 10) + " " + DateUtil.getWeek(datestr) + " " + datestr.substring(11, 16);
        }
        return chatTime;
    }

    /**
     * 说说时间
     *
     * @param strDate
     * @return
     */
    public static String getTimeToToday(String strDate) {
        SimpleDateFormat dfs = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
        long between = 0;
        try {
            Date end = new Date();
            Date begin = dfs.parse(strDate);
            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (ParseException e) {
            return "";
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        if (day > 0) {
            return changeTime(strDate);
        }
        if (hour > 0) {
            return hour + "小时前";
        }
        if (min > 0) {
            return min + "分钟前";
        } else {
            return "1分钟前";
        }
    }

    public static String changeTime(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd);
        Date now = new Date();
        String today = sdf.format(now);
        if (strDate.startsWith(today)) {
            return strDate.substring(11, 16);
        }
        String yearday = sdf.format(addDay(now, -1));
        if (strDate.startsWith(yearday)) {
            return "昨天 " + strDate.substring(11, 16);//
        }
        String dqday = sdf.format(addDay(now, -2));
        if (strDate.startsWith(dqday)) {
            return "前天  " + strDate.substring(11, 16);//
        }
        return strDate.substring(5, 16);
    }

    public static Date addDay(Date date, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        return date;
    }

    public static long getLongFromString(String str) {
        return Long.valueOf(str.replaceAll("[-\\s:]", ""));
    }

    private static String[][] constellations = {{"摩羯座", "水瓶座"}, {"水瓶座", "双鱼座"}, {"双鱼座", "白羊座"}, {"白羊座", "金牛座"}, {"金牛座", "双子座"}, {"双子座", "巨蟹座"}, {"巨蟹座", "狮子座"},
            {"狮子座", "处女座"}, {"处女座", "天秤座"}, {"天秤座", "天蝎座"}, {"天蝎座", "射手座"}, {"射手座", "摩羯座"}};
    //星座分割时间
    private static int[] date = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};

    /**
     * 星座生成 传进是日期格式为: yyyy-mm-dd
     *
     * @param time
     */
    public static String getConstellations(String time) {
        if (time.isEmpty()) {
            return "";
        }
        String[] data = time.split("-");
        int day = date[Integer.parseInt(data[1]) - 1];
        String[] cl1 = constellations[Integer.parseInt(data[1]) - 1];
        if (Integer.parseInt(data[2]) >= day) {
            return cl1[1];
        } else {
            return cl1[0];
        }
    }

    public static int getAge(String birthday, int age) {
        if (birthday == null || birthday.isEmpty())
            return age;
        String now = getNow(yyyy_MM_dd);
        int nowYear = Integer.parseInt(now.substring(0, 4));
        int birthYear = Integer.parseInt(birthday.substring(0, 4));
        return nowYear - birthYear + 1;
    }
}
