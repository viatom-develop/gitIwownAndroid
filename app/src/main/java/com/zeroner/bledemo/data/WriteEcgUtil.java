package com.zeroner.bledemo.data;

import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.SPUtils;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.zeroner.bledemo.bean.sql.ProtoBuf_index_80;
import com.zeroner.bledemo.bean.sql.TB_64_data;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.blemidautumn.bluetooth.Filtering;
import com.zeroner.blemidautumn.utils.JsonTool;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WriteEcgUtil {
    static{

        System.loadLibrary( "filters");

    }
    public static void dispECGData(List<ProtoBuf_index_80> indexTablesEcg) {
        if (indexTablesEcg == null) {
            return;
        }
        LogUtils.d("indexTablesEcg==="+indexTablesEcg.toString());
        LogUtils.d("处理心电数据:" + indexTablesEcg.size());
        List<Integer> Y;
        List<Integer> y;


        int dirtyDataLength = /*120*/1250;
        //新w1 pro手表不需要前5秒数据
//        if (xxx.contains("le W1 Pro")) {
//            dirtyDataLength = 500;
//        }
        StringBuilder ecgContent;
        for (int i = 0; i < indexTablesEcg.size(); i++) {
            ProtoBuf_index_80 dbIndex = indexTablesEcg.get(i);
            DateUtil  d = new DateUtil(dbIndex.getYear(), dbIndex.getMonth(), dbIndex.getDay(), dbIndex.getHour(),
                    dbIndex.getMin(), dbIndex.getSecond());
            String filePath =PathUtils.getExternalStoragePath() + "/ecg/"+ "xxxxtest";
            File file = new File(PathUtils.getExternalStoragePath() + "/ecg/");
            if (!file.exists()){
                file.mkdir();
            }

            long time = d.getUnixTimestamp();
            Log.v("timestamp=====",""+time);

            //rjz 修改，心电数据不能使用seq start 和seq end判断一条数据，需要通过时间获取数据
            List<TB_64_data> seqListData = DataSupport
                    .where("time=?",
                            String.valueOf(time))
                    .find(TB_64_data.class);

            Collections.sort(seqListData);

            int seqCount = 0;
            StringBuilder sb = new StringBuilder();
            LogUtils.d("==ecgPosition=="+seqListData.toString());
            Y = new ArrayList<>();
            double allSizePostion = 0;
            int positionSize = 0;
            int testPostion = 0;
            for (int j = 0; j < seqListData.size(); j++) {
                TB_64_data tb64Data = seqListData.get(j);
                if (tb64Data != null) {
                    seqCount++;
                    sb.append(tb64Data.getSeq());

                    y = JsonTool.getListJson(tb64Data.getEcg(), Integer.class);
                    for (int ecgPosition : y) {
                        if (positionSize > dirtyDataLength) {
                            allSizePostion = allSizePostion + ecgPosition;
                            testPostion = testPostion +1;
                        }
                        positionSize = positionSize + 1;
                    }
                    Y.addAll(y);
                }

                if (j == seqListData.size() - 1) {
                    continue;
                }
                sb.append("|");

            }


            if (Y.size() > dirtyDataLength) {
                ecgContent = new StringBuilder("F-0-01," + "250,I,2000,");
                int j =0 ;

                //---------------------------------------埃微滤波算法----------------------------------
                //每一个ecg文件，都重新创建滤波对象初始化，避免两个相影响 start
                Filtering filtering = new Filtering();
                filtering.init();
                //每一个ecg文件，都重新创建滤波对象初始化，避免两个相影响 end
                int result;
//                filterTrapInit();
                for (int k = 0; k < Y.size(); k++) {
                    LogUtils.d("first 滤波前"+k+"===="+Y.get(k));
                    result = Y.get(k);
                        result = AcFilter(result);//新手表过滤的东西
                        result = filtering.filteringMain(result, true);


                    if (k < dirtyDataLength) {
                        continue;
                    }

                    ecgContent.append(result);
                    if (k == Y.size() - 1) {
                        continue;
                    }
                    ecgContent.append(",");

                }


                FileIOUtils.writeFileFromString(filePath, ecgContent.toString(), false);


            }
        }

    }
    public static native int AcFilter(int data);
}
