package com.zeroner.bledemo.setting.schedule;
import com.zeroner.bledemo.bean.sql.TB_Alarmstatue;
import com.zeroner.bledemo.bean.sql.TB_schedulestatue;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2015/12/18.
 */
public class ScheduleUtil {
    //0x40 = 0100 0000
    public static final byte WEEK_1 = 0x40;
    //0x20 = 0010 0000
    public static final byte WEEK_2 = 0x20;
    //0x10 = 0001 0000
    public static final byte WEEK_3 = 0x10;
    //0x8 = 0000 1000
    public static final byte WEEK_4 = 0x8;
    //0x4 = 0000 0100
    public static final byte WEEK_5 = 0x4;
    //0x2 = 0000 0010
    public static final byte WEEK_6 = 0x2;
    //0x1 = 0000 0001
    public static final byte WEEK_7 = 0x1;
    //
    public static final byte NO_REPEAT = 0;
    //repeat
    public static final byte REPEAT = (byte) 0x80;
    //weekend
    public static final byte WEEKEND = WEEK_6 | WEEK_7;
    //weekdays
    public static final byte WORK_DAY = WEEK_1 | WEEK_2 | WEEK_3 | WEEK_4 | WEEK_5;
    //every day
    public static final byte EVERY_DAY = WEEK_1 | WEEK_2 | WEEK_3 | WEEK_4 | WEEK_5 | WEEK_6 | WEEK_7;


    public static String getScheduleDate(int year, int month, int day){
        String text = year + "-";

        if(month > 9)
            text = text + month + "-";
        else
            text = text + "0" + month + "-";

        if(day > 9)
            text = text + day;
        else
            text = text + "0" + day;

        return text;
    }

    public static String getCalendarTitle(int year, int month){
        String text = year + "-";
        if(month > 9)
            text = text + month;
        else
            text = text + "0" + month;

        return text;
    }

    public static String getStringDate(int year, int month, int day){
        String text = year + "-";

        if(month > 9){
            text += month + "-";
        }
        else{
            text += "0" + month + "-";
        }

        if(day > 9){
            text += day;
        }
        else{
            text += "0" + day;
        }

        return  text;
    }

    public static String getStringTime(int hour, int minute){
        String strHour;
        if(hour < 10)
            strHour = "0" + hour;
        else
            strHour = "" + hour;

        String strMinute;
        if(minute < 10)
            strMinute = ":0" + minute;
        else
            strMinute = ":" + minute;

        return (strHour + strMinute);
    }

    public static boolean isBeforeToday(int year, int month, int day, Calendar curCalendar){
        int cYear = curCalendar.get(Calendar.YEAR);
        int cMonth = curCalendar.get(Calendar.MONTH) + 1;
        int cDay = curCalendar.get(Calendar.DAY_OF_MONTH);

        if(year < cYear){
            return true;
        }
        else if(year > cYear){
            return false;
        }
        else if(year == cYear){
            if(month > cMonth){
                return false;
            }
            else if(month < cMonth){
                return true;
            }
            else{
                if(day < cDay){
                    return true;
                }
                else{
                    return false;
                }
            }
        }

        return false;
    }

    public static boolean isBeforeCurrentTime(int hour, int minute, Calendar curCalendar){
        int curHour = curCalendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = curCalendar.get(Calendar.MINUTE);

        if(hour < curHour) {
            return true;
        }
        else if(hour == curHour){
            if(minute <= curMinute)
                return true;
        }

        return false;
    }

    public static boolean isToday(int year, int month, int day, Calendar curCalendar){
        if(day == curCalendar.get(Calendar.DAY_OF_MONTH)
                && month == (curCalendar.get(Calendar.MONTH) + 1)
                && year == curCalendar.get(Calendar.YEAR)
                )
            return true;

        return false;
    }

    public static int getTBTimesInt(int hour, int minute){
        return (hour * 100 + minute);
    }

    public static int getTBDatesInt(int year, int month, int day){
        return ((year - 2000) * 10000 + month * 100 + day);
    }

    public static String getTBTimesString(int hour, int minute){
        return String.valueOf(getTBTimesInt(hour, minute));
    }

    public static String getTBDatesString(int year, int month, int day){
        return String.valueOf(getTBDatesInt(year, month, day));
    }

    //weekRepeat Whether included week,week = [0, 6],sun,mon..sat
    private static byte[] byteWeek = {0x01, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02};
    public static boolean isAtAlarmWeek(int week, int weekRepeat){
        if((byteWeek[week] & (byte)weekRepeat) > 0)
            return true;
        return false;
    }

    public static byte getWeekByte(int week, boolean isRepeat){
        byte resultWeek = byteWeek[week];

        if(isRepeat){
            resultWeek |= 0x80;
        }
        else{
            resultWeek &= 0x7f;
        }

        return resultWeek;
    }

//    public static String getStringAlarmWeek(int week){
//        byte bWeek = (byte)week;
//
//        String text;
//        if((0x80 & bWeek) == 0){
//            text = ZeronerApplication.getInstance().getString(R.string.schedule_only_once);
//        }
//        else{
//            text = ZeronerApplication.getInstance().getString(R.string.repeat);
//        }
//
//        byte bWeek2 = (byte)(0x7f & bWeek);
//        if(bWeek2 == 0x7f)
//        {
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_every_day);
//            return text;
//        }
//
//        if((0x01 & bWeek2) > 0){
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_sun);
//        }
//
//        if((0x40 & bWeek2) > 0){
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_mon);
//        }
//
//        if((0x20 & bWeek2) > 0){
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_tue);
//        }
//
//        if((0x10 & bWeek2) > 0){
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_wes);
//        }
//
//        if((0x08 & bWeek2) > 0){
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_thur);
//        }
//
//        if((0x04 & bWeek2) > 0){
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_fir);
//        }
//
//        if((0x02 & bWeek2) > 0){
//            text += ZeronerApplication.getInstance().getString(R.string.schedule_sat);
//        }
//
////        if((0x40 & bWeek2) > 0){
////            text += "日 ";
////        }
////
////        if((0x01 & bWeek2) > 0){
////            text += "一 ";
////        }
////
////        if((0x02 & bWeek2) > 0){
////            text += "二 ";
////        }
////
////        if((0x04 & bWeek2) > 0){
////            text += "三 ";
////        }
////
////        if((0x08 & bWeek2) > 0){
////            text += "四 ";
////        }
////
////        if((0x10 & bWeek2) > 0){
////            text += "五 ";
////        }
////
////        if((0x20 & bWeek2) > 0){
////            text += "六 ";
////        }
//
//        return text;
//    }

    //操作要改变的日程内容是否合法
    public static boolean isChangSchedule(String UID, int year, int month, int day, int hour, int minute){
        String times = ScheduleUtil.getTBTimesString(hour, minute);
        String dates = ScheduleUtil.getTBDatesString(year, month, day);
        int num = DataSupport.where("UID = ? AND dates = ? AND times = ?", UID, dates, times).count(TB_schedulestatue.class);

        return (num == 0 ? true : false);
    }

    public static int dataID;
    public static int dataYear;
    public static int dataMonth;
    public static int dataDay;
    public static int dataHour;
    public static int dataMinute;
    public static String dataItem;
    public static String dataRemind;
    public static int shakeNum;
    public static int  shakeMode;
    public static void packScheduleData(int id, int year, int month, int day, int hour, int minute, String item, String remind,
                                        int shakeMode2, int shakeNum2){
        dataID = id;
        dataYear = year;
        dataMonth = month;
        dataDay = day;
        dataHour = hour;
        dataMinute = minute;
        dataItem = item;
        dataRemind = remind;
        shakeNum = shakeNum2;
        shakeMode = shakeMode2;
    }

    public static void packIDData(int id){
        dataID = id;
    }

    public static byte dataByteWeek;
    public static boolean dataIsOpen;
    public static void packAlarmData(int id, byte byteWeek, int hour, int minute, String item, String remind, boolean isOpen,
                                     int shakeMode2, int shakeNum2){
        dataID = id;
        dataByteWeek = byteWeek;
        dataHour = hour;
        dataMinute = minute;
        dataItem = item;
        dataRemind = remind;
        dataIsOpen = isOpen;
        shakeMode = shakeMode2;
        shakeNum = shakeNum2;
    }

    public static TB_schedulestatue tbScheduleStatue = new TB_schedulestatue();
    public static void packScheduleTBData(TB_schedulestatue data){
        tbScheduleStatue = data;
    }

    public static List<TB_Alarmstatue> getAllAlarmData(String UID){
        List<TB_Alarmstatue> listAll = new ArrayList<TB_Alarmstatue>();
        listAll = DataSupport.where("UID=?", UID).find(TB_Alarmstatue.class);

        return listAll;
    }
}
