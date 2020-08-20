package com.zeroner.bledemo.data.sync;


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
            if (index.getStartSeq() >= index.getEndSeq()) {
                continue;
            }
            DateUtil dateUtil = new DateUtil();
            int endSeq = 0;
            List<ProtoBuf_index_80> index_table;
            if (dateUtil.getYear() == ints[0] && dateUtil.getMonth() == ints[1] && dateUtil.getDay() == ints[2]) {
                ProtoBuf_index_80 end_idx = DataSupport.select("end_idx").where("year=? and month=? and day=? and data_from=?  and indexType=? and isFinish=1",
                        dateUtil.getYear() + "",
                        dateUtil.getMonth() + "",
                        dateUtil.getDay() + "",
                        data_from,
                        i7BHisIndexTable.getHisDataType() + "").findLast(ProtoBuf_index_80.class);
                if(end_idx != null){
                    endSeq = end_idx.getEnd_idx();
                }

            }

            index_table = DataSupport.where("year=? and month=? and day=? and data_from=? and start_idx=? and end_idx=? and indexType=? and isFinish=1",
                    ints[0] + "",
                    ints[1] + "",
                    ints[2] + "",
                    data_from,
                    index.getStartSeq() + "",
                    index.getEndSeq() + "",
                    i7BHisIndexTable.getHisDataType() + "").find(ProtoBuf_index_80.class);

            if (index_table != null && index_table.size() > 0) {
                continue;
            }


            ProtoBuf_index_80 index_80 = new ProtoBuf_index_80();
            index_80.setYear(ints[0]);
            index_80.setMonth(ints[1]);
            index_80.setDay(ints[2]);
            index_80.setHour(ints[3]);
            index_80.setMin(ints[4]);
            index_80.setSecond(ints[5]);
            int timezone = (int) (3600 * Util.getTimeZone());
            index_80.setTime((int) (index.getSecond() - timezone));
            index_80.setData_from(data_from);
            if (endSeq > 0 && endSeq < index.getEndSeq()) {
                index_80.setStart_idx(endSeq);
            } else {
                index_80.setStart_idx(index.getStartSeq());
            }
            index_80.setEnd_idx(index.getEndSeq());
            index_80.setIsFinish(0);
            index_80.setIndexType(i7BHisIndexTable.getHisDataType());
            index_80s.add(index_80);

            //保存到数据库
            index_80.saveOrUpdate("year=? and month=? and day=? and hour=? and min=? and second=? and data_from=? and start_idx=? and indexType=?",
                    ints[0] + "",
                    ints[1] + "",
                    ints[2] + "",
                    ints[3] + "",
                    ints[4] + "",
                    ints[5] + "",
                    data_from,
                    index_80.getStart_idx() + "",
                    i7BHisIndexTable.getHisDataType() + "");
        }

        //排序
        Collections.sort(index_80s, new Comparator<ProtoBuf_index_80>() {
            @Override
            public int compare(ProtoBuf_index_80 index1, ProtoBuf_index_80 index2) {
                int i = index1.getYear() * 380 + index1.getMonth() * 31 + index1.getDay();
                int i2 = index2.getYear() * 380 + index2.getMonth() * 31 + index2.getDay();
                if (i > i2) {
                    return -1;
                } else if (i == i2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });


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
