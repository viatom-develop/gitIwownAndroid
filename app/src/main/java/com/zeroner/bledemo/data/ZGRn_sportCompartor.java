package com.zeroner.bledemo.data;
import com.zeroner.bledemo.bean.sql.SportData;
import java.util.Comparator;

/**
 * Created by Daemon on 2017/11/15 11:10.
 */

public class ZGRn_sportCompartor implements Comparator<SportData> {
    @Override
    public int compare(SportData o1, SportData o2) {

        long start_time_str1 = o1.getStart_time();
        long start_time_str2 = o2.getEnd_time();
        return start_time_str1 - start_time_str2 > 0 ? 1 : -1;
    }
}
