package com.zeroner.bledemo.utils;
import com.socks.library.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author
 * @created 2014-4-23
 */
public class DateUtil {
    /**
     * 获取当天零点时间戳
     * 返回秒
     * 10位
     *
     * @return
     */
    public long getZeroTime() {

        DateUtil dateUtil = new DateUtil(getYear(), getMonth(), getDay());

        return dateUtil.getUnixTimestamp();
    }

    /**
     * 获取当天零点的 时间格式 字符串
     *
     * @return
     */
    public String getZeroTimeYyyyMMdd_HHmmssDate() {

        DateUtil dateUtil = new DateUtil(getYear(), getMonth(), getDay());

        return dateUtil.getYyyyMMdd_HHmmssDate();
    }

    /**
     * 13位
     *
     * @return
     */
    public long getZeroTime1() {
        DateUtil dateUtil = new DateUtil(getYear(), getMonth(), getDay());

        return dateUtil.getTimestamp();
    }


    //获取当月的天数
    public int getDayOfMonth(){
        c.set(Calendar.DATE, 1);
        c.roll(Calendar.DATE, -1);
        return c.get(Calendar.DATE);
    }


    public int getTodayMin() {
        long zeroTime = getZeroTime1();
        int i = Math.round((c.getTimeInMillis() - zeroTime) / (1000 * 60)) + 1;
        return i;
    }

    public static boolean isSameDay(Date date1, Date date2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;


    }


    public enum DateFormater {
        MMdd, MMdd_HHmm, yyyyMM, yyyyMMdd, yyyyMMdd_HHmm, yyyyMMdd_HHmmss, HHmm, HHmmss, yyyyMMddHHmm, SyyyyMMdd, dFyyyyMMdd,MMDD_1
    }
    private final static ThreadLocal<SimpleDateFormat> dFMMdd_1 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM/dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFMMdd = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFMMdd_HHmm = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd HH:mm");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFyyyyMM = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFyyyyMMdd = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFyyyyMMdd_HHmm = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFyyyyMMdd_HHmmss = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFHHmm = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFHHmmss = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dFSyyyyMMdd = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    public static DateUtil valueOf(String sdate) {
        String MMddFmt = "[0-9]{2}-[0-9]{2}"; // MM-dd
        String MMdd_HHmmFmt = "[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}"; // MM-dd
        // HH:mm
        String yyyyMMFmt = "[0-9]{4}-[0-9]{2}"; // yyyy-MM
        String yyyyMMddFmt = "[0-9]{4}-[0-9]{2}-[0-9]{2}"; // yyyy-MM-dd
        String yyyyMMdd_HHmmFmt = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}"; // yyyy-MM-dd
        // HH:mm
        String yyyyMMdd_HHmmssFmt = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"; // yyyy-MM-dd
        // HH:mm:ss
        String HHmmFmt = "[0-9]{2}:[0-9]{2}"; // HH:mm
        String HHmmssFmt = "[0-9]{2}:[0-9]{2}:[0-9]{2}"; // HH:mm:ss
        Pattern p = Pattern.compile(yyyyMMdd_HHmmssFmt);
        try {
            p = Pattern.compile(MMddFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.MMdd);
            }
            p = Pattern.compile(MMdd_HHmmFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.MMdd_HHmm);
            }
            p = Pattern.compile(yyyyMMFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMM);
            }
            p = Pattern.compile(yyyyMMddFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMMdd);
            }
            p = Pattern.compile(yyyyMMdd_HHmmFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMMdd_HHmm);
            }
            p = Pattern.compile(yyyyMMdd_HHmmssFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMMdd_HHmmss);
            }
            p = Pattern.compile(HHmmFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.HHmm);
            }
            p = Pattern.compile(HHmmssFmt);
            if (p.matcher(sdate).matches()) {
                return parse(sdate, DateFormater.HHmmss);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DateUtil parse(String sdate, DateFormater formater)
            throws ParseException {
        Date date = null;
        switch (formater) {
            case MMDD_1:
                date = dFMMdd_1.get().parse(sdate);
                break;
            case MMdd:
                date = dFMMdd.get().parse(sdate);
                break;
            case MMdd_HHmm:
                date = dFMMdd_HHmm.get().parse(sdate);
                break;
            case yyyyMM:
                date = dFyyyyMM.get().parse(sdate);
                break;
            case yyyyMMdd:
                date = dFyyyyMMdd.get().parse(sdate);
                break;
            case yyyyMMdd_HHmm:
                date = dFyyyyMMdd_HHmm.get().parse(sdate);
                break;
            case yyyyMMdd_HHmmss:
                date = dFyyyyMMdd_HHmmss.get().parse(sdate);
                break;
            case HHmm:
                date = dFHHmm.get().parse(sdate);
                break;
            case HHmmss:
                date = dFHHmmss.get().parse(sdate);
                break;
        }
        return new DateUtil(date);
    }

    private Calendar c;

    public DateUtil() {
        c = Calendar.getInstance();
    }

    public DateUtil(long timestamp, boolean isUnix) {
        c = Calendar.getInstance();
        if (isUnix) {
            c.setTimeInMillis(timestamp * 1000L);
        } else {
            c.setTimeInMillis(timestamp);
        }
    }

    public DateUtil(Date date) {
        c = Calendar.getInstance();
        c.setTime(date);
    }

    public DateUtil(int year, int month, int day) {
        this(year, month, day, 0, 0, 0);
    }

    public DateUtil(int year, int month, int day, int hour, int minute) {
        this(year, month, day, hour, minute, 0);
    }

    public DateUtil(int year, int month, int day, int hour, int minute, int second) {
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1); // 系统从0开始算
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
    }

    public DateUtil(int hour, int minute) {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
    }

    public boolean isToday() {
        DateUtil d = new DateUtil();
        return this.getYear() == d.getYear() && this.getMonth() == d.getMonth()
                && this.getDay() == d.getDay();
    }

    public boolean isSameWeek(int number) {
        DateUtil date = new DateUtil(new Date());
        int index = date.getWeekOfYear();
        return number == index;
    }

    public boolean isSameMonth(int number, int year) {
        DateUtil date = new DateUtil(new Date());
        int index = date.getMonth();
        int nYear = date.getYear();
        return number == index && nYear == year;
    }

    /**
     * 转换为java.util.Date对象
     *
     * @return
     */
    public Date toDate() {
        return c.getTime();
    }

    public String toFormatString(DateFormater formater) {
        Date date = toDate();
        String sdate = "Unknown";
        switch (formater) {
            case MMDD_1:
                sdate=dFMMdd_1.get().format(date);
                break;
            case MMdd:
                sdate = dFMMdd.get().format(date);
                break;
            case MMdd_HHmm:
                sdate = dFMMdd_HHmm.get().format(date);
                break;
            case yyyyMM:
                sdate = dFyyyyMM.get().format(date);
                break;
            case yyyyMMdd:
                sdate = dFyyyyMMdd.get().format(date);
                break;
            case yyyyMMdd_HHmm:
                sdate = dFyyyyMMdd_HHmm.get().format(date);
                break;
            case yyyyMMdd_HHmmss:
                sdate = dFyyyyMMdd_HHmmss.get().format(date);
                break;
            case HHmm:
                sdate = dFHHmm.get().format(date);
                break;
            case HHmmss:
                sdate = dFHHmmss.get().format(date);
                break;
            case SyyyyMMdd:
                sdate = dFSyyyyMMdd.get().format(date);
                break;
            case dFyyyyMMdd:
                sdate = dFyyyyMMdd.get().format(date);
                break;
        }
        return sdate;
    }


    public static final String yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMdd_HHmm = "yyyy-MM-dd HH:mm";
    public static final String dFyyyyMMdd1 = "yyyy-MM-dd";
    public static SimpleDateFormat yyyyMMdd_HHmmssF = new SimpleDateFormat(yyyyMMdd_HHmmss);
    public static SimpleDateFormat dFyyyyMMddF = new SimpleDateFormat(dFyyyyMMdd1);
    public static SimpleDateFormat dFyyyyMMddmmF = new SimpleDateFormat(yyyyMMdd_HHmm);

    public static Date String2Date(String formater, String dateString) {
        Date date = null;
        try {
            switch (formater) {
                case yyyyMMdd_HHmmss:
                    date = yyyyMMdd_HHmmssF.parse(dateString);
                    break;
                case yyyyMMdd_HHmm:
                    date = dFyyyyMMddmmF.parse(dateString);
                    break;

                case dFyyyyMMdd1:
                    date = dFyyyyMMddF.parse(dateString);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("解析 dateString 异常");
        }
        return date;

    }


    public String getMMddDate() {
        return toFormatString(DateFormater.MMdd);
    }

    public String getMMdd_HHmmDate() {
        return toFormatString(DateFormater.MMdd_HHmm);
    }


    public String getY_M_D() {
        return toFormatString(DateFormater.dFyyyyMMdd);
    }

    public String getY_M_D_H_M_S() {
        return toFormatString(DateFormater.yyyyMMdd_HHmmss);
    }


    public String getY_M_D_H_M() {
        return toFormatString(DateFormater.yyyyMMdd_HHmm);
    }

    public String getYyyyMMDate() {
        return toFormatString(DateFormater.yyyyMM);
    }

    public String getYyyyMMddDate() {
        return toFormatString(DateFormater.yyyyMMdd);
    }

    public String getYyyyMMdd_HHmmDate() {
        return toFormatString(DateFormater.yyyyMMdd_HHmm);
    }

    public String getYyyyMMdd_HHmmssDate() {
        return toFormatString(DateFormater.yyyyMMdd_HHmmss);
    }

    public String getHHmmDate() {
        return toFormatString(DateFormater.HHmm);
    }

    public String getHHmmssDate() {
        return toFormatString(DateFormater.HHmmss);
    }

    public String getSyyyyMMddDate() {
        return toFormatString(DateFormater.SyyyyMMdd);
    }

    public String getyyyyMMddDate() {
        return toFormatString(DateFormater.yyyyMMdd);
    }

    public int getYear() {
        return c.get(Calendar.YEAR);
    }

    public void setYear(int year) {
        c.set(Calendar.YEAR, year);
    }

    public int getMonth() {
        return c.get(Calendar.MONTH) + 1;
    }

    public void setMonth(int month) {
        c.set(Calendar.MONTH, month - 1);
    }

    public int getDay() {
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public void setDay(int day) {
        c.set(Calendar.DAY_OF_MONTH, day);
    }

    public void addDay(int day) {
        c.add(Calendar.DAY_OF_MONTH, day);
    }

    public void addMonth(int month) {
        c.add(Calendar.MONTH, month);
    }

    public int getHour() {
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public void setHour(int hour) {
        c.set(Calendar.HOUR_OF_DAY, hour);
    }

    public int getMinute() {
        return c.get(Calendar.MINUTE);
    }

    public void setMinute(int minute) {
        c.set(Calendar.MINUTE, minute);
    }

    public int getSecond() {
        return c.get(Calendar.SECOND);
    }

    public void setSecond(int second) {
        c.set(Calendar.SECOND, second);
    }

    public long getTimestamp() {
        return c.getTimeInMillis();
    }

    public void setTimestamp(long timestamp) {
        c.setTimeInMillis(timestamp);
    }

    public long getUnixTimestamp() {
        return c.getTimeInMillis() / 1000L;
    }

    public void setUnixTimestamp(long unix_timestamp) {
        c.setTimeInMillis(unix_timestamp * 1000);
    }

    public int getDayOfWeek() {
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public int getWeekOfYear() {
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    public int getWeekOfMonth() {
        return c.get(Calendar.WEEK_OF_MONTH);
    }

    public String getMonDate() {
//		int dayWeek = c.get(Calendar.DAY_OF_WEEK); // 获得当前日期是一个星期的第几天 
        int day = getDayOfWeek();
        c.add(Calendar.DATE, c.getFirstDayOfWeek() - day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(c.getTime());
    }

    @Override
    public String toString() {
        return this.getYyyyMMdd_HHmmssDate();
    }

    /**
     * 如果是今天就返回具体时间,否则返回日期
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        long now = System.currentTimeMillis();
        long dTime = (now - time) / (1000 * 60 * 60 * 24);
        DateUtil dateUtil = new DateUtil(time, false);
        if (dTime > 0) {
            return dateUtil.getYyyyMMddDate();
        } else {
            return dateUtil.getHHmmDate();
        }
    }

    public static long getSunDayTimeFromWeek() {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
        long l = cal.getTime().getTime() - i * (1000 * 60 * 60 * 24);
//        KLog.e("周末 -> 周六" + DataTimeUtils.getyyyyMMddHHmmss(l) + "  > " + DataTimeUtils.getyyyyMMddHHmmss(l + 6 * 1000 * 60 * 60 * 24));

        return l;
    }

    /**
     * 周日->周六
     * 0->6
     *
     * @param size
     * @return
     */
    public static Date getDateByWeekMagin(int size) {
        long sunDayTimeFromWeek = getSunDayTimeFromWeek();
        return new Date(sunDayTimeFromWeek + (size * (1000 * 60 * 60 * 24)));
    }

    public static int differentDaysByMillisecond(Date date1,Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
}
