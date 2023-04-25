package com.zeroner.bledemo.data.sync;


import static com.zeroner.bledemo.data.sync.ProtoBufSync.ECG_DATA;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.ProtoBuf_index_80;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHisIndexTable;
import com.zeroner.blemidautumn.utils.Util;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author yanxi
 * @data 2018/12/19
 * 记录需要同步的seq
 */
public class ProtoBufIndex {

    public static List<ProtoBuf_index_80> parseIndex(ProtoBufHisIndexTable i7BHisIndexTable) {
        if (i7BHisIndexTable == null || i7BHisIndexTable.getIndexList() == null) {
            return null;
        }

        String data_from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        List<ProtoBuf_index_80> index_80s = new ArrayList<>();
        //保存的seq信息
        for (ProtoBufHisIndexTable.Index index : i7BHisIndexTable.getIndexList()) {
            int[] ints = parseTime(index.getSecond());
            String timeStamp = ints[0] + "-" + ints[1] + "-" + ints[2] + " " + ints[3] + ":" + ints[4] + ":" + ints[5];

            if (index.getStartSeq() >= index.getEndSeq()) {
                LogUtils.e("跳过:" + timeStamp + " 脏数据 " + index.getEndSeq() + "-" + index.getStartSeq());
                continue;
            }
            //不需要过滤 start
            if (i7BHisIndexTable.getHisDataType() == ECG_DATA
                    && index.getEndSeq() - index.getStartSeq() < 125) {
                //数据不完整
                LogUtils.e("跳过:" + timeStamp + " 这条心电数据不完整 " + index.getEndSeq() + "-" + index.getStartSeq());
                continue;
            }

            //不需要过滤 end
            ProtoBuf_index_80 index_80 = new ProtoBuf_index_80();
            index_80.setYear(ints[0]);
            index_80.setMonth(ints[1]);
            index_80.setDay(ints[2]);
            index_80.setHour(ints[3]);
            index_80.setMin(ints[4]);
            index_80.setSecond(ints[5]);
            int timezone = (int) (3600 * Util.getTimeZone());
            index_80.setTime((int) (index.getSecond() - timezone));

            DateUtil dateUtil = new DateUtil();
            /**/
            if(i7BHisIndexTable.getHisDataType() != ECG_DATA){
                int time = 0;
                //数据库中最近的一条数据
                ProtoBuf_index_80 last_index_80 = DataSupport.select("time")
//                    .where("UserID=? and year=? and month=? and day=? and dataFrom=?  and indexType=?",
                        .where("year=? and month=? and day=? and  indexType=?",
                                dateUtil.getYear() + "",
                                dateUtil.getMonth() + "",
                                dateUtil.getDay() + "",
//                            data_from,
                                i7BHisIndexTable.getHisDataType() + "")
                        .order("time desc")
                        .limit(1)
                        .findFirst(ProtoBuf_index_80.class);
                if (last_index_80 != null) {
                    //最近的时间
                    time = last_index_80.getTime();
                }

                if (time > index_80.getTime()) {
                    //只同步最近时间往后的数据，因为现在数据不区分来源，所以，保证数据时间线不重复
                    LogUtils.e("跳过:" + timeStamp + " 保证数据时间线不重复");
                    continue;
                }
            }

            List<ProtoBuf_index_80> index_table_repeit =
//                    LitePal.where("UserID=? and year=? and month=? and day=? and dataFrom=? " +
                    DataSupport.where("year=? and month=? and day=? " +
                                    "and start_idx=? and end_idx=? and indexType=?",
                            ints[0] + "",
                            ints[1] + "",
                            ints[2] + "",
//                            dataFrom,
                            index.getStartSeq() + "",
                            index.getEndSeq() + "",
                            i7BHisIndexTable.getHisDataType() + "").find(ProtoBuf_index_80.class);

            if (index_table_repeit != null && index_table_repeit.size() > 0) {
                //已经同步过了，跳过
                LogUtils.e("跳过:" + timeStamp + " 已经同步过了");
                continue;
            }
            //过滤2019年4月1号之前的数据
            long second = TimeUtils.string2Millis("2019-04-01 00:00:00") / 1000;

            if (second > index_80.getTime()) {
                LogUtils.e("跳过:" + second + "-----------过滤2019年4月1号之前的数据");

                continue;
            }



            //rjz 只有运动数据才会设置endseq 等等 start
            int endSeq = 0;
            if (dateUtil.getYear() == ints[0] && dateUtil.getMonth() == ints[1] && dateUtil.getDay() == ints[2]) {
                ProtoBuf_index_80 end_idx = DataSupport.select("end_idx").
                        where("year=? and month=? and day=? and data_from=?  and indexType=?",
                                dateUtil.getYear() + "",
                                dateUtil.getMonth() + "",
                                dateUtil.getDay() + "",
                                data_from,
                                i7BHisIndexTable.getHisDataType() + "").findLast(ProtoBuf_index_80.class);
                if(end_idx != null){
                    endSeq = end_idx.getEnd_idx();
                }
                KLog.d("endSEQ"+endSeq);
            }

            KLog.e("更新时间戳"+ints[0] +"--"+ ints[1]  +"--" + ints[2] +"--" + ints[3] +"--" + ints[4] +"--" + ints[5]);
            //数据库中结束seq默认是0



            LogUtils.d("i7BHisIndexTable==="+index.getStartSeq()+"===i7BHisIndexTable==="+index.getEndSeq()
                    +"====type==="+i7BHisIndexTable.getHisDataType()+"==timeStamp=="+timeStamp);
            if (endSeq > 0 && endSeq < index.getEndSeq()&& (i7BHisIndexTable.getHisDataType() != ECG_DATA)) {
                index_80.setStart_idx(endSeq);
            } else {
                index_80.setStart_idx(index.getStartSeq());
            }
            //rjz 只有运动数据才会设置endseq 等等 end
            index_80.setEnd_idx(index.getEndSeq());
            index_80.setIndexType(i7BHisIndexTable.getHisDataType());
            index_80.setData_from(data_from);
            index_80s.add(index_80);



            //保存到数据库
            index_80.saveOrUpdate("year=? and month=? and day=? and data_from=? and start_idx=? and end_idx=? and indexType=?",
                    ints[0] + "",
                    ints[1] + "",
                    ints[2] + "",
                    data_from,
                    index_80.getStart_idx() + "",
                    index_80.getEnd_idx() + "",
                    i7BHisIndexTable.getHisDataType() + "");
            KLog.d(index_80.toString());
        }
        if (index_80s.size() > 0 && index_80s.get(0).getIndexType() == ECG_DATA) {
            //排序
            Collections.sort(index_80s, new Comparator<ProtoBuf_index_80>() {
                @Override
                public int compare(ProtoBuf_index_80 index1, ProtoBuf_index_80 index2) {
                    int i = index1.getTime();
                    int i2 = index2.getTime();
                    if (i > i2) {
                        return -1;
                    } else if (i == i2) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }else{
            //排序
            Collections.sort(index_80s, new Comparator<ProtoBuf_index_80>() {
                @Override
                public int compare(ProtoBuf_index_80 index1, ProtoBuf_index_80 index2) {
                    int i = index1.getYear() * 380 + index1.getMonth() * 31 + index1.getDay();
                    i=i*24*60*60+index1.getHour()*60*60+index1.getMin()*60+index1.getSecond();

                    int i2 = index2.getYear() * 380 + index2.getMonth() * 31 + index2.getDay();
                    i2 = i2*24*60*60+index2.getHour()*60*60+index2.getMin()*60+index2.getSecond();
                    if (i > i2) {
                        return -1;
                    } else if (i == i2) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        LogUtils.d("index_80s==="+index_80s.toString());
        return index_80s;

    }

    private static int[] parseTime(long second) {
        int[] time = new int[6];
        Calendar calendar = Calendar.getInstance();
        int timezone = (int) (3600 * Util.getTimeZone());
        calendar.setTimeInMillis(second * 1000 - timezone * 1000L);
        time[0] = calendar.get(Calendar.YEAR);
        time[1] = calendar.get(Calendar.MONTH) + 1;
        time[2] = calendar.get(Calendar.DAY_OF_MONTH);
        time[3] = calendar.get(Calendar.HOUR_OF_DAY);
        time[4] = calendar.get(Calendar.MINUTE);
        time[5] = calendar.get(Calendar.SECOND);
        return time;
    }
}
