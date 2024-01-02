package com.zeroner.bledemo.data.sync;


import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.data.ProtobufSyncSeq;
import com.zeroner.bledemo.bean.sql.PbSupportInfo;
import com.zeroner.bledemo.bean.sql.ProtoBuf_index_80;
import com.zeroner.bledemo.bean.sql.TB_64_index_table;
import com.zeroner.bledemo.bean.sql.TB_mtk_statue;
import com.zeroner.bledemo.data.ProtoBufSleepSqlUtils;
import com.zeroner.bledemo.data.WriteEcgUtil;
import com.zeroner.bledemo.data.viewData.ViewData;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * 同步数据
 */
public class ProtoBufSync {

    /**
     * HEALTH_DATA(0),
     * /**
     * <code>GNSS_DATA = 1;</code>
     * GNSS_DATA(1),
     * <code>ECG_DATA = 2;</code>
     * ECG_DATA(2),
     * <code>PPG_DATA = 3;</code>
     * PPG_DATA(3),
     * <code>RRI_DATA = 4;</code>
     * RRI_DATA(4),
     */
    private volatile static ProtoBufSync instance;
    public static final int HEALTH_DATA = 0;
    public static final int GNSS_DATA = 1;
    public static final int ECG_DATA = 2;
    public static final int PPG_DATA = 3;
    public static final int RRI_DATA = 4;
    public static final int SWIM_DATA = 7;
    private List<Integer> typeArray = new ArrayList<>();
    private SparseArray<List<ProtobufSyncSeq>> totalSeqList = new SparseArray<>();
    private SparseArray<List<ProtoBuf_index_80>> array = new SparseArray<>();
    //    private int totalSeq = 0;//总条数
    private boolean isSync = false;//是否同步
    private int lastPosition = -1;
    private int currentType;//当前同步的类型
    private int timeDelay = 40 * 1000;
    private boolean hasData = false;//有数据
    public static boolean isFirstSync = false;//在收到90指令之后发送同步指令
    private boolean oneDayHasFinish = false;


    private List<ProtoBuf_index_80> index_80s = new ArrayList<>();

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static ProtoBufSync getInstance() {
        if (instance == null) {
            synchronized (ProtoBufSync.class) {
                if (instance == null) {
                    instance = new ProtoBufSync();
                }
            }
        }
        return instance;
    }


    /**
     * 同步数据
     */
    public void syncData() {

        if(ProtoBufUpdate.getInstance().isUpdate()){
            return;
        }

        String data_from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        byte[] realHealthData = ProtoBufSendBluetoothCmdImpl.getInstance().getRealHealthData();
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), realHealthData);

        if (isSync) {
            KLog.d("正在同步..");
            return;
        }
        //查询表数据
        PbSupportInfo protoBufSupportInfo = DataSupport.where("data_from=?", data_from).findFirst(PbSupportInfo.class);
        if(protoBufSupportInfo == null){
            return;
        }
        clearData();
        typeArray = getTypeArray(protoBufSupportInfo);
        EventBus.getDefault().post(new SyncDataEvent(-1,false));
        isSync = true;
        initData();

    }

    private void initData() {
        //一个一个写，设备上来的数据才不会错乱
        if(currentType < typeArray.size()){
            byte[] indexTab = ProtoBufSendBluetoothCmdImpl.getInstance().itHisData(typeArray.get(currentType));
            BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), indexTab);
        }
    }

//    private void

    public void syncDetailData( List<ProtoBuf_index_80> index_80s) {
        this.index_80s.clear();
        this.index_80s.addAll(index_80s);
        positionSync = 0;
        //查询表中是否有记录
        if (index_80s != null && index_80s.size() > 0) {
            hasData = true;
            int indexType = index_80s.get(0).getIndexType();
            array.put(indexType, index_80s);
            List<ProtobufSyncSeq> protobufSyncSeqs = new ArrayList<>();
            for (int i = 0; i < index_80s.size(); i++) {
                ProtoBuf_index_80 dbIndex = index_80s.get(i);
                int startIdx = dbIndex.getStart_idx();
                int endIdx = dbIndex.getEnd_idx();
                int totalSeq = dbIndex.getEnd_idx() - dbIndex.getStart_idx();
                ProtobufSyncSeq protobufSyncSeq = new ProtobufSyncSeq(totalSeq, startIdx, i + 1, endIdx, indexType);
                protobufSyncSeqs.add(protobufSyncSeq);

                saveIndexTable(indexType, dbIndex);
            }
            totalSeqList.put(indexType, protobufSyncSeqs);
            syncDetailByIndex();
        } else {
            //同步完成
            currentType++;
            if (currentType < typeArray.size()) {
                initData();
            } else {
                ProtoBufSync.getInstance().progressFinish();
            }
        }

    }

    private int positionSync = 0;

    private void syncDetailByIndex() {
        if (positionSync < index_80s.size()) {
            int hisDataType = index_80s.get(positionSync).getIndexType();
            mHandler.removeCallbacks(syncTimeOutRunnable);
            mHandler.postDelayed(syncTimeOutRunnable,15*1000);
            ProtoBuf_index_80 index = index_80s.get(positionSync);
            final int startSeq = index.getStart_idx();
            final int endSeq = index.getEnd_idx();
            oneDayHasFinish = false;
            lastPosition = -1;
//            AwLog.i(Author.GuanFengJun,"同步"+index.getMonth()+"-"+index.getDay()+" == "+startSeq+"-"+endSeq);
            detailData(hisDataType, startSeq, endSeq);
        }
    }


    private void detailData(int type, int startSeq, int endSeq) {
        byte[] hisData = ProtoBufSendBluetoothCmdImpl.getInstance().startHisData(type, startSeq, endSeq);
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), hisData);
    }

    private void syncFinish() {
        //计算所有
        //计算睡眠延迟5秒，
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ProtoBufSleepSqlUtils.dispSleepData();
            }
        },5000);
        List<ProtoBuf_index_80> indexTablesEcg = array.get(ECG_DATA);
        WriteEcgUtil.dispECGData(indexTablesEcg);

        totalSeqList.clear();
        array.clear();
        positionSync = 0;
        if(typeArray.contains(GNSS_DATA)) {
            ProtoBufUpdate.getInstance().startUpdate(ProtoBufUpdate.Type.TYPE_GPS);
        }
    }


    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
        if(!sync){
            clearData();
        }
    }


    public int currentProgress(int type, int seq) {
//        int currentIndex = -1;
        String typeDesc = "";
        if (type == HEALTH_DATA) {
            typeDesc = " health ";
        } else if (type == GNSS_DATA) {
            typeDesc = "GPS";
        } else if (type == ECG_DATA) {
            typeDesc = "ECG";
        }else if (type == RRI_DATA) {
            typeDesc = "RRI";
        }
        if(index_80s==null || positionSync>=index_80s.size()){
            return 0;
        }
        int totalNum = index_80s.get(positionSync).getEnd_idx() - index_80s.get(positionSync).getStart_idx();
        int progress = (seq+1-index_80s.get(positionSync).getStart_idx())*100/totalNum;
        if (lastPosition != progress) {
            int postProgress = progress>100 ? 100:progress;
            EventBus.getDefault().post(new SyncDataEvent(postProgress, false, index_80s.size(), positionSync+1, typeDesc));
            lastPosition = progress;
            if(progress ==100){
                updateStatus();
                lastPosition = -1;
                mHandler.removeCallbacks(syncTimeOutRunnable);
                mHandler.post(syncTimeOutRunnable);
            }else{
                mHandler.removeCallbacks(syncTimeOutRunnable);
                mHandler.postDelayed(syncTimeOutRunnable,15*1000);
            }
        }
        return progress;
    }



    public void progressFinish() {
        //如果没有执行同步结束指令.执行结束
        if(isSync){
            hasData = false;
            EventBus.getDefault().post(new SyncDataEvent(100, true));
            isSync = false;
            syncFinish();
        }
    }



    public void stopSync() {
        isSync = false;
        for (int i = 0; i < typeArray.size(); i++) {
            byte[] bytes = ProtoBufSendBluetoothCmdImpl.getInstance().stopHisData(typeArray.get(i));
            BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), bytes);
        }
    }


    private void saveIndexTable(int indexType, ProtoBuf_index_80 dbIndex) {
        if (indexType == GNSS_DATA) {
            DateUtil dateUtil = new DateUtil(dbIndex.getYear(), dbIndex.getMonth(), dbIndex.getDay());
            TB_mtk_statue mtk_statue = new TB_mtk_statue();
            mtk_statue.setData_from(dbIndex.getData_from());
            mtk_statue.setType(80);
            mtk_statue.setYear(dbIndex.getYear());
            mtk_statue.setMonth(dbIndex.getMonth());
            mtk_statue.setDay(dbIndex.getDay());
            mtk_statue.setHas_file(2);
            mtk_statue.setHas_up(2);
            mtk_statue.setHas_tb(2);
            mtk_statue.setDate(dateUtil.getUnixTimestamp());
            mtk_statue.saveOrUpdate("data_from=? and type=? and date=?",
                    dbIndex.getData_from(), "80", dateUtil.getUnixTimestamp() + "");

        } else if (indexType == ECG_DATA) {
            TB_64_index_table indexTable = new TB_64_index_table();
            DateUtil d = new DateUtil(dbIndex.getYear(), dbIndex.getMonth(), dbIndex.getDay(), dbIndex.getHour(), dbIndex.getMin(), dbIndex.getSecond());
            indexTable.setUid(dbIndex.getUid());
            indexTable.setData_from(dbIndex.getData_from());
            indexTable.setData_ymd(d.getSyyyyMMddDate());
            indexTable.setSeq_start(dbIndex.getStart_idx());
            indexTable.setSeq_end(dbIndex.getEnd_idx());
            indexTable.setSync_seq(dbIndex.getEnd_idx());
            indexTable.setDate(d.getY_M_D_H_M_S());
            indexTable.setUnixTime(d.getUnixTimestamp());
            indexTable.saveOrUpdate("uid=? and data_from =? and date=?",
                    String.valueOf(dbIndex.getUid()), dbIndex.getData_from(), d.getY_M_D_H_M_S());
        }
    }

    private List<Integer> getTypeArray(PbSupportInfo protoBufSupportInfo){
        List<Integer> integers = new ArrayList<>();
        if(protoBufSupportInfo.isSupport_health()){
            integers.add(HEALTH_DATA);
        }
        if(protoBufSupportInfo.isSupport_gnss()){
            integers.add(GNSS_DATA);
        }
        if(protoBufSupportInfo.isSupport_ecg()){
            integers.add(ECG_DATA);
        }
        if(protoBufSupportInfo.isSupport_ppg()){
            integers.add(PPG_DATA);
        }
        if(protoBufSupportInfo.isSupport_rri()){
            integers.add(RRI_DATA);
        }
        return integers;
    }


    public void clearData(){

        currentType = 0;
        positionSync = 0;
        index_80s.clear();
        totalSeqList.clear();
        array.clear();
    }

    private void updateStatus() {
        if(index_80s!=null && index_80s.size()>0 && (positionSync < index_80s.size())) {
            oneDayHasFinish = true;
            ProtoBuf_index_80 index80 = index_80s.get(positionSync);
            index80.setIsFinish(1);
            index80.update(index80.getId());
        }
    }

    Runnable syncTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (!BluetoothUtil.isConnected()) {
                isSync = false;
                clearData();
                return;
            }

            //防止最后1，2条数据设备没有发送上来
            if(lastPosition>=99 && !oneDayHasFinish){
                updateStatus();
            }
            positionSync++;
            if(positionSync >= index_80s.size()){
                //同步完同一个类型的指令了.同步下一条
                currentType++;
                if (currentType < typeArray.size()) {
                    initData();
                }else{
                    //没有数据了,同步结束
                    progressFinish();
                }
            }else {
                syncDetailByIndex();
            }
        }
    };

}

