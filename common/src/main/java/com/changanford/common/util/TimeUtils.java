package com.changanford.common.util;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {


    /**
     * 输入时间戳返回时间 1443571710-->2015-9-30 0:0:0  10位
     *
     * @param time
     * @return
     */
    public static String InputTimetampAll(String time) {
        if (TextUtils.isEmpty(time) || "null".equals(time)) {
            return "";
        }
        if (time.length() > 9) {
            time = time.substring(0, 10);
        }
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String InputTimetamp(String time) {
        if (TextUtils.isEmpty(time) || "null".equals(time)) {
            return "";
        }
        if (time.length() > 9) {
            time = time.substring(0, 10);
        }
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String InputTimetamp(String time, String timeType) {
        if (TextUtils.isEmpty(time) || "null".equals(time)) {
            return "";
        }
        if (TextUtils.isEmpty(timeType) || timeType.length() == 0) {
            timeType = "yyyy-MM-dd HH:mm:ss";
        }
        if (time.length() > 9) {
            time = time.substring(0, 10);
        }
        SimpleDateFormat sdr = new SimpleDateFormat(timeType);
        long l = Long.parseLong(time);
        String times = sdr.format(new Date(l * 1000L));
        return times;
    }


    public static String InputTimetamp1(String time) {
        if (TextUtils.isEmpty(time) || "null".equals(time)) {
            return "";
        }
        if (time.length() > 9) {
            time = time.substring(0, 10);
        }
        SimpleDateFormat sdr = new SimpleDateFormat("MM-dd HH:mm");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    private static String[] str = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"};
    /**
     * 毫秒数转String
     */
    public static String FORMATE_DATE_STR = "yyyy-MM-dd HH:mm:ss";
    public static String FORMATE_DATE_STR1 = "yyyy-MM-dd HH:mm:ss";
    public static String FORMATE_DATE_YMDHM = "yyyy-MM-dd HH:mm";
    public static String FORMATE_DATE_YMDHM2 = "yyyy/MM/dd HH:mm";
    public static String FORMATE_DATE_HM = "mm:ss";
    public static String FORMATE_DATE_HM2 = "MM月dd日";
    public static String FORMATE_DATE_STR_O = "yyyy.MM.dd HH:mm";

    public static String MillisToStr(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_STR);
        return sf.format(date);
    }
    public static String MillisToStrO(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_STR_O);
        return sf.format(date);
    }
    public static String MillisToStrHM(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_HM);
        return sf.format(date);
    }

    public static String MillisToStrHM2(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_HM2);
        return sf.format(date);
    }

    public static String MillisToStr1(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_STR1);
        return sf.format(date);
    }

    public static String FORMATE_DATE_DAY_STR = "yyyy-MM-dd";

    public static String FORMATE_DATE_M_H = "yyyy.MM.dd HH:mm";

    public static String MillisToDayStr(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }

        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_DAY_STR);
        return sf.format(date);
    }
    public static String FORMATE_ACT_TIME = "yyyy.MM.dd HH:mm";

    public static String  formateActTime(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_ACT_TIME);
        return sf.format(date);
    }

    public static String MillisTo_M_H(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_M_H);
        return sf.format(date);
    }
    public static Long MillisTo_M_H_REVERSE(String time) {
        if (time == null) {
            return 0L;
        }
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_M_H);

        try {
            return sf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String MillisTo_YMDHM(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_YMDHM);
        return sf.format(date);
    }

    public static String MillisTo_YMDHM2(Long timeMillis) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(FORMATE_DATE_YMDHM2);
        return sf.format(date);
    }

    public static String MillisToStr(Long timeMillis, int type) {
        if (timeMillis == null) {
            return "";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(str[type]);
        return sf.format(date);
    }

    /**
     * String 转毫秒数
     *
     * @param timeStr
     * @return
     */
    public static Long StrToMillis(String timeStr) {
        if ("未填写".equals(timeStr)) {
            return System.currentTimeMillis();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMATE_DATE_STR);//24小时制
        Long millis = null;
        try {
            millis = simpleDateFormat.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    /**
     * String 转毫秒数年月日
     *
     * @param timeStr
     * @return
     */
    public static long StrToMillisYMD(String timeStr) {
        if ("未填写".equals(timeStr)) {
            return System.currentTimeMillis();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//24小时制
        long millis = 0;
        try {
            millis = simpleDateFormat.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    public static String dateToStamp(String s) throws ParseException {
        try {
            String res;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @param type     类型 0：天  1：小时   2：分钟   3：秒
     * @return 返回时间差
     */

    /**
     * 计算剩余日期
     *
     * @param endTime 传入的时间
     * @param type    类型 0：相差多少天  1：相差多少小时  2：相差多少分钟  3：相差多少秒
     * @return 相差的时间
     */
    public static int calculationRemainTime(long endTime, int type) {

        Date now = new Date(System.currentTimeMillis());// 获取当前时间
        long l = now.getTime() - endTime;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        Log.i("时间差", "剩余" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        switch (type) {
            case 0:
                return (int) day;
            case 1:
                return (int) hour;
            case 2:
                return (int) min;
            case 3:
                return (int) s;
            default:
                break;
        }
        return 0;
    }


    /**
     * 计算剩余日期
     * <p>
     * 传入的时间
     * 类型 0：相差多少天  1：相差多少小时  2：相差多少分钟  3：相差多少秒
     *
     * @return 相差的时间
     */
    public static String gettime(String strtime) {
        long endTime = StrToMillis(strtime);
        Date now = new Date(System.currentTimeMillis());// 获取当前时间
        long l = now.getTime() - endTime;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = l / (60 * 60 * 1000);
        long min = l / (60 * 1000);
        long s = l / 1000;
        Log.i("时间差", "剩余" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        if (min > 0 && min < 60) {
            return "刚刚";
        }
        if (hour > 0 && hour < 24) {
            return hour + "小时前";
        }
        if (day > 0) {
            return day + "天前";
        }
        return "";
    }

    public static String getsystime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }


    /**
     * 传入日期，返回星座
     */
    public static String getConstellation(Date date) {
        String constellation = "";
        if (constellationList.isEmpty()) {
            fillData();
        }
        Calendar birthday = Calendar.getInstance();
        birthday.setTime(date);
        int month = birthday.get(Calendar.MONTH) + 1;
        int day = birthday.get(Calendar.DAY_OF_MONTH);
        switch (month) {
            case 1:
                //Capricorn 摩羯座（12月22日～1月20日）
                constellation = day <= 20 ? constellationList.get(11) : constellationList.get(0);
                break;
            case 2:
                //Aquarius 水瓶座（1月21日～2月19日）
                constellation = day <= 19 ? constellationList.get(0) : constellationList.get(1);
                break;
            case 3:
                //Pisces 双鱼座（2月20日～3月20日）
                constellation = day <= 20 ? constellationList.get(1) : constellationList.get(2);
                break;
            case 4:
                //白羊座 3月21日～4月20日
                constellation = day <= 20 ? constellationList.get(2) : constellationList.get(3);
                break;
            case 5:
                //金牛座 4月21～5月21日
                constellation = day <= 21 ? constellationList.get(3) : constellationList.get(4);
                break;
            case 6:
                //双子座 5月22日～6月21日
                constellation = day <= 21 ? constellationList.get(4) : constellationList.get(5);
                break;
            case 7:
                //Cancer 巨蟹座（6月22日～7月22日）
                constellation = day <= 22 ? constellationList.get(5) : constellationList.get(6);
                break;
            case 8:
                //Leo 狮子座（7月23日～8月23日）
                constellation = day <= 23 ? constellationList.get(6) : constellationList.get(7);
                break;
            case 9:
                //Virgo 处女座（8月24日～9月23日）
                constellation = day <= 23 ? constellationList.get(7) : constellationList.get(8);
                break;
            case 10:
                //Libra 天秤座（9月24日～10月23日）
                constellation = day <= 23 ? constellationList.get(8) : constellationList.get(9);
                break;
            case 11:
                //Scorpio 天蝎座（10月24日～11月22日）
                constellation = day <= 22 ? constellationList.get(9) : constellationList.get(10);
                break;
            case 12:
                //Sagittarius 射手座（11月23日～12月21日）
                constellation = day <= 21 ? constellationList.get(10) : constellationList.get(11);
                break;
        }
        return constellation;
    }

    private static ArrayList<String> constellationList = new ArrayList<>();//存放星座的集合

    private static void fillData() {
        constellationList.add(0, "水瓶座");
        constellationList.add(1, "双鱼座");
        constellationList.add(2, "白羊座");
        constellationList.add(3, "金牛座");
        constellationList.add(4, "双子座");
        constellationList.add(5, "巨蟹座");
        constellationList.add(6, "狮子座");
        constellationList.add(7, "处女座");
        constellationList.add(8, "天秤座");
        constellationList.add(9, "天蝎座");
        constellationList.add(10, "射手座");
        constellationList.add(11, "魔羯座");
    }


    /**
     * 倒计时时分秒
     *
     * @param l
     * @return
     */
    public String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;   //取整
            second = second % 60;   //取余
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = "剩余：" + hour + "小时" + minute + "分" + second + "秒";
        return strtime;
    }

    public static String longToRelativeTime(Long l) {
        String timeStr = DateUtils.getRelativeTimeSpanString(l).toString();
        if (!TextUtils.isEmpty(timeStr)) {
            if ("0".equals(timeStr.substring(0, 1))) {
                timeStr = "刚刚";
            }
        }
        return timeStr;
    }

    public static String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new Date());
    }

    public static String getMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return sdf.format(new Date());
    }

    public static String getDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(new Date());
    }

    public static String getNowDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static String getShowYearMonth(int index) {//相较于当前月份的年月：2020年08月，前一月 index -1,后一月+1
        try {
            int year = Integer.valueOf(getYear());
            int month = Integer.valueOf(getMonth());
            month = month + index;

            if (index < 0) {
                while (month <= 0) {
                    year -= 1;
                    month += 12;
                }
            } else {
                while (month > 12) {
                    month -= 12;
                    year += 1;
                }
            }
            if (month < 10) {
                return year + "年0" + month + "月";
            } else {
                return year + "年" + month + "月";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * yyyy-MM-dd
     *
     * @param index
     * @return
     */
    public static String getRequestYearMonth(int index) {//相较于当前月份的年月：2020年08月，前一月 index -1,后一月+1
        try {
            int year = Integer.valueOf(getYear());
            int month = Integer.valueOf(getMonth());
            month = month + index;

            if (index < 0) {
                while (month <= 0) {
                    year -= 1;
                    month += 12;
                }
            } else {
                while (month > 12) {
                    month -= 12;
                    year += 1;
                }
            }
            if (month < 10) {
                return year + "-0" + month;
            } else {
                return year + "-" + month;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param pastDay yyyy-MM-hh
     * @return
     */
    public static boolean dayBefore(String pastDay) {
        String cur = MillisToDayStr(System.currentTimeMillis());
        try {
            cur = cur.replace("-", "");
            pastDay = pastDay.replace("-", "");
            return Long.valueOf(cur) - Long.valueOf(pastDay) >= 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public  String getVideoTime(long ms) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        return strMinute + ":" + strSecond ;
    }


    public static boolean isToday(String pastDay) {
        String cur = MillisToDayStr(System.currentTimeMillis());
        try {
            cur = cur.replace("-", "");
            pastDay = pastDay.replace("-", "");
            return Long.valueOf(cur) - Long.valueOf(pastDay) == 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean dayTaskBefore(String pastDay) {
        String cur = MillisToDayStr(System.currentTimeMillis());
        try {
            cur = cur.replace("-", "");
            pastDay = pastDay.replace("-", "");
            return Long.valueOf(cur) - Long.valueOf(pastDay) > 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Long next7(long timeStamp) {
        Calendar c = Calendar.getInstance();
        //过去七天
        c.setTime(new Date(timeStamp));
        c.add(Calendar.DATE, 7);
        return c.getTimeInMillis(); // 获取毫秒值
    }


}
