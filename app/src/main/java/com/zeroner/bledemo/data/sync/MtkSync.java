package com.zeroner.bledemo.data.sync;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.socks.library.KLog;
import com.zeroner.bledemo.bean.sql.TB_sum_61_62_64;
import com.zeroner.bledemo.eventbus.EpoEvent;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.utils.ByteUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coms.mediatek.ctrl.epo.EpoDownloadChangeListener;
import coms.mediatek.ctrl.epo.EpoDownloadController;

import static com.zeroner.bledemo.utils.BluetoothUtil.context;

/**
 * 作者：hzy on 2018/1/24 11:35
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class MtkSync implements EpoDownloadChangeListener {
    private static MtkSync instance;
    private Context mContext;

    private MtkSync(Context context) {
        EpoDownloadController.addListener(this);
        mContext = context;
    }

    public static MtkSync getInstance(Context context){
        if(instance==null){
            synchronized (MtkSync.class) {
                if (instance==null) {
                    instance=new MtkSync(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
    private final String TAG = this.getClass().getSimpleName();


    //同步状态
    private boolean mIsSyncDataInfo;

    public boolean isSyncDataInfo() {
        return mIsSyncDataInfo;
    }

    //所有地址总和
    private int mAllAdd = 0;
    //已经同步的地址数量
    private int mDAll;



    private Runnable stopSyncRunnable = new Runnable() {
        @Override
        public void run() {
            KLog.e("stopSyncRunnable: Time Over 30s");
//            stopSyncAllF1Data();
        }
    };

    /**
     * 超过30s没调用此方法则停止同步
     */
    public void judgeStopSyncData() {
        mHandler.removeCallbacks(stopSyncRunnable);
        mHandler.postDelayed(stopSyncRunnable, 30000);
    }



    public void stopSyncDataAll() {
        stopSyncAllF1Data();
        mHandler.removeCallbacks(stopSyncRunnable);
        mIsSyncDataInfo = false;
    }

    private void stopSyncAllF1Data() {
       SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext).stopSyncDetailData(0x61, 0x62, 0x64);
        SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext).dailyHealthDataSwitch(false);
    }




    private long lastTime=0;

    private static Handler mHandler = new Handler(Looper.getMainLooper());


    /*********************************************61 62 数据同步*****************************************************************/
    DateUtil date = new DateUtil();
    Map<String, SeqModel> map = new HashMap<>();
    private int upIndex;
    List<P1SendBleData> bleP1=new ArrayList<>();
    List<Integer> listNum = new ArrayList<Integer>();


    public Map<String, SeqModel> getMap() {
        return map;
    }

    public void setMap(Map<String, SeqModel> map) {
        this.map = map;
    }

    public synchronized void  validate_61_62_1_data(int type) {
//        if(type==sendType) {
        upIndex++;
        KLog.i("手环上报的数据总数量:" + upIndex);
//        }
    }

    public boolean isOver(){
//        return f1IsOver;
        return System.currentTimeMillis()/1000-lastTime>=30;
    }

    public void setF1IsOver(){
        f1Over();
        f1IsOver=true;
    }

    private boolean f1IsOver=true;

    private int sendNum=0;



    private void clearF1(){
        sendNum=0;
        listNum.clear();
        upIndex=0;
        syncP1Num = 0;
        bleP1.clear();
    }

    public void getDatasIndexTables(){
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context.getApplicationContext()).getIndexTableAccordingType(0x61, 0x62, 0x64,0x68);
    }

    public void syncP1AllData(){
        clearF1();
        SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext).dailyHealthDataSwitch(true);
        List<TB_sum_61_62_64> tbSum = DataSupport.order("date_time desc").find(TB_sum_61_62_64.class);
        KLog.e("licl","开始获取全部数据 -:"+tbSum.size());
        if(tbSum.size()>0) {
            if(tbSum.size()>1) {
                String lastDate=tbSum.get(0).getDate();
                int sums=0;
                for (int i = 0; i < tbSum.size()-1; i++) {
                    int stIndex = ByteUtil.bytesToInt(ByteUtil.hexToBytes(tbSum.get(i).getSend_cmd().substring(0,4)));
                    int etIndex= ByteUtil.bytesToInt(ByteUtil.hexToBytes(tbSum.get(i).getSend_cmd().substring(4,8)));
                    P1SendBleData bleData = new P1SendBleData(tbSum.get(i).getYear(),tbSum.get(i).getMonth(),tbSum.get(i).getDay(),stIndex,etIndex,tbSum.get(i).getType());
                    bleP1.add(bleData);
                    if(tbSum.get(i).getDate().equals(tbSum.get(i+1).getDate())){
                        sums+=tbSum.get(i).getSum();
                    }else{
                        sums+=tbSum.get(i).getSum();
                        listNum.add(sums);
                        sums=0;
                    }
                }
                sums+=tbSum.get(tbSum.size()-1).getSum();
                listNum.add(sums);
                int j=tbSum.size()-1;
                int stIndex = ByteUtil.bytesToInt(ByteUtil.hexToBytes(tbSum.get(j).getSend_cmd().substring(0,4)));
                int etIndex= ByteUtil.bytesToInt(ByteUtil.hexToBytes(tbSum.get(j).getSend_cmd().substring(4,8)));
                P1SendBleData bleData = new P1SendBleData(tbSum.get(j).getYear(),tbSum.get(j).getMonth(),tbSum.get(j).getDay(),stIndex,etIndex,tbSum.get(j).getType());
                bleP1.add(bleData);
            }else{
                int stIndex = ByteUtil.bytesToInt(ByteUtil.hexToBytes(tbSum.get(0).getSend_cmd().substring(0,4)));
                int etIndex= ByteUtil.bytesToInt(ByteUtil.hexToBytes(tbSum.get(0).getSend_cmd().substring(4,8)));
                P1SendBleData bleData = new P1SendBleData(tbSum.get(0).getYear(),tbSum.get(0).getMonth(),tbSum.get(0).getDay(),stIndex,etIndex,tbSum.get(0).getType());
                bleP1.add(bleData);
            }
            for (int i = 0; i < bleP1.size(); i++) {
                KLog.e("licl","开始获取11全部数据 -:"+new Gson().toJson(bleP1.get(i)));
            }
            KLog.e("licl","开始获取总天数为:"+listNum.size());
            sync_P1_data();
        }else{
            setEpo();
            KLog.e("testf1shuju","全部同步结束------------");
            f1Over();
            bleP1.clear();
        }
    }

    private int sendType=0;
    private int nowSync;
    private int syncP1Num=0;
    public boolean sync_P1_data(){
        lastTime= System.currentTimeMillis()/1000;
        BackgroundThreadManager.getInstance().wakeUp();
        if(syncP1Num<bleP1.size()){
            if(isOneDayOver()){
                sendNum++;
                upIndex=0;
            }

            nowSync = bleP1.get(syncP1Num).getEndIndex();
            if(bleP1.get(syncP1Num).getDataType()==0x61) {
                if (nowSync > 4096)
                    nowSync -= 4096;
            }else{
                if (nowSync > 1024)
                    nowSync -= 1024;
            }
            SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext).
                    getDetailDataAsIndex(bleP1.get(syncP1Num).getYear(),bleP1.get(syncP1Num).getMonth(),bleP1.get(syncP1Num).getDay(),bleP1.get(syncP1Num).getStartIndex(),bleP1.get(syncP1Num).getEndIndex(),bleP1.get(syncP1Num).getDataType());
            KLog.e("licltestf1shuju","发送P1的数据: "+bleP1.get(syncP1Num).getStartIndex()+" -  "+bleP1.get(syncP1Num).getEndIndex()+" -- "+ nowSync+" - "+bleP1.get(syncP1Num).getDataType());
            syncP1Num++;
            return true;
        }else{
            KLog.e("licltestf1shuju","全部同步结束------------");
            EventBus.getDefault().post(new SyncDataEvent(100, true, 0, 0));
            bleP1.clear();
            f1Over();
            setEpo();
            return false;
        }
    }

    public boolean isOneDayOver(){

        Log.e("licl", "syncP1num"+syncP1Num+"/bleP1.size"+bleP1.size());

        if (bleP1.size()<=0) {
            return true;
        }

        if((syncP1Num-1)==bleP1.size()-1) {
            return true;
        }else{
            if(syncP1Num==0)
                return false;
            if(bleP1.get(syncP1Num-1).getDate().equals(bleP1.get(syncP1Num).getDate())){
                return false;
            }else{
                return true;
            }
        }
    }



    private void f1Over(){
        upIndex = 0;
        f1IsOver=true;
        lastTime=0;
        listNum.clear();
    }

    public void setEpo(){
        MtkSync.getInstance(mContext).setUpEpo(true);
        EventBus.getDefault().post(new EpoEvent(EpoEvent.STATE_INIT, 0));
        KLog.e(TAG, "准备写入EPO------>");
        SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext).writeEpo();
    }

    private int lastIndex=0;

    public void  progressUP(boolean isStop,String data){
        judgeStopSyncData();

        lastTime= System.currentTimeMillis()/1000;
        if(listNum.size()==0)
            mAllAdd=0;
        else
            mAllAdd = listNum.get(sendNum>=listNum.size()?(listNum.size()-1):sendNum);
        if(mAllAdd<=0){
            EventBus.getDefault().post(new SyncDataEvent(0, isStop,listNum.size(),sendNum+1, data));
        }else {
            double indexd=upIndex*100.0/(mAllAdd*1.0);
            int index = (int)indexd;
            if(index>=100 || indexd>99.9)
                index=100;
//            KLog.e("  testf1shuju百分比===========" + index +"  --  "+upIndex+" --- "+mAllAdd +" -- " +sendNum+" time: "+data);
            if(lastIndex!=index) {
                lastIndex=index;
                EventBus.getDefault().post(new SyncDataEvent(index, false,listNum.size(), sendNum + 1, data));
            }
//            }
        }
    }

    public void clearIndex(){
        upIndex=0;
        mAllAdd=0;
        lastIndex=0;
        mDAll=0;
    }

    public int getNowSync() {
        return nowSync;
    }

    private boolean isUpEpo=false;
    public void setUpEpo(boolean isUpEpo){
        this.isUpEpo=isUpEpo;
    }

    @Override
    public void notifyProgressChanged(float v) {
        if(v<0.01){
            KLog.e("epo开始写入");
            MtkSync.getInstance(mContext).setUpEpo(true);
            EventBus.getDefault().post(new EpoEvent(EpoEvent.STATE_INIT,0));
        }else if(v>=1){
            MtkSync.getInstance(mContext).setUpEpo(false);
            EventBus.getDefault().post(new EpoEvent(EpoEvent.STATE_END,100));
            KLog.e("epo写入完成");
        }else{
            MtkSync.getInstance(mContext).setUpEpo(true);
            EventBus.getDefault().post(new EpoEvent(EpoEvent.STATE_SENDING, (int) (v*100)));
        }
    }

    @Override
    public void notifyDownloadResult(int i) {
        if (i==0) {
            KLog.e("下载epo文件失败");
            EventBus.getDefault().post(new EpoEvent(EpoEvent.STATE_DOWNLOAD_FILE_FAIL,100));
        }
    }

    @Override
    public void notifyConnectionChanged(int i) {

    }
}
