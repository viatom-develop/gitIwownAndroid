package com.zeroner.bledemo.data.sync;


import android.os.Handler;
import android.os.Looper;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.DataIndex_68;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MTKHeadSetSync {
    private static MTKHeadSetSync instance;

    private boolean mIsSyncDataInfo; //是否在同步

    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private int total = 0;
    private List<DataIndex_68> dbIndexList;
    private int seqTotal = 0;
    private int avgPerDay = 0;
    private int counter;

    private MTKHeadSetSync() {

    }

    public static MTKHeadSetSync getInstance(){
        if(instance==null){
            instance=new MTKHeadSetSync();
        }
        return instance;
    }

    public void syncDataInfo() {
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).setHeartBeat(0));
        EventBus.getDefault().post(new SyncDataEvent());
        KLog.e("mIsSyncDataInfo:"+ String.valueOf(mIsSyncDataInfo));
        if (mIsSyncDataInfo) {
            //L.file("正在同步...", L.Type_Operate);
            return;
        }
        mIsSyncDataInfo = true;

        EventBus.getDefault().post(new SyncDataEvent(0,false));
        KLog.e("start sync r1 data");
        startSyncData68();
    }

    private void startSyncData68(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-60);


        DataSupport.deleteAll(DataIndex_68.class, "year = ? and month = ? and day=?",
                String.valueOf(cal.get(Calendar.YEAR)),
                String.valueOf(cal.get(Calendar.MONTH)+1),
                String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

        BackgroundThreadManager.getInstance().wakeUp();

        mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
            SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeR1Data();
        }
    },1000);
}

    public void syncDetailData(List<DataIndex_68> indexList){
        if(indexList != null && indexList.size()>0){
            total = indexList.size();
            Collections.sort(indexList);
            dbIndexList = indexList;

            for(DataIndex_68 dbIndex : indexList){
                seqTotal += (dbIndex.getEnd_idx() - dbIndex.getStart_idx());
            }
            avgPerDay = seqTotal/total;

            sendCmdByIndex(dbIndexList,0);
        }
        else {
            EventBus.getDefault().post(new SyncDataEvent(100,true,1,1));
            mIsSyncDataInfo = false;
            counter = 0;
        }
    }

    private void sendCmdByIndex(final List<DataIndex_68> indexList, int position){
        if(position<indexList.size()){
            DataIndex_68 index = indexList.get(position);
            SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).
                    writeR1Data(index.getYear(),
                            index.getMonth(),
                            index.getDay(),
                            index.getStart_idx(),
                            index.getEnd_idx());

            final int next_position = position + 1;
            if(next_position<indexList.size()){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCmdByIndex(indexList,next_position);
                    }
                },3000);
            }
        }
    }

    public void reportProgress(){
        EventBus.getDefault().post(new SyncDataEvent(100, true, dbIndexList.size(), dbIndexList.size()));
        mIsSyncDataInfo = false;
        counter = 0;
        jobAfterSyncFinish();
    }

    public void reportProgress(int count){
        if(count>=seqTotal){
            EventBus.getDefault().post(new SyncDataEvent(100, true, dbIndexList.size(), dbIndexList.size()));
            mIsSyncDataInfo = false;
            counter = 0;
            jobAfterSyncFinish();
        }

        int dayseq = count/avgPerDay;
        int mod = count%avgPerDay;
        if(mod==0){
            EventBus.getDefault().post(new SyncDataEvent(100, false, total, dayseq));
        }
        else{
            dayseq += 1;
            int middle = avgPerDay/2;
            if(mod==middle){
                EventBus.getDefault().post(new SyncDataEvent(50, false, total, dayseq));
            }
        }
    }

    public boolean isSyncDataInfo() {
        return mIsSyncDataInfo;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    private void jobAfterSyncFinish(){
        /*
        whole sync process finish, something to do
         */
        KLog.e("---sync job finish");
//        //1. send signal for next step calculation
        R1Tag r1Tag = new R1Tag();
        r1Tag.setTag("R1TableConvert");
        List<Integer> years = new ArrayList<>();
        List<Integer> months = new ArrayList<>();
        List<Integer> days = new ArrayList<>();
        for (int i =0 ; i< dbIndexList.size();i++){
            years.add(dbIndexList.get(i).getYear());
            months.add(dbIndexList.get(i).getMonth());
            days.add(dbIndexList.get(i).getDay());
        }
        r1Tag.setYear(years);
        r1Tag.setMonth(months);
        r1Tag.setDay(days);

        R1ConvertHandler.tb68ToConvertHistory(r1Tag);

    }
}
