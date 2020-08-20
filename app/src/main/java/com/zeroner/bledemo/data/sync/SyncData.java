package com.zeroner.bledemo.data.sync;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.data.ZGBaseUtils;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zm on 2016/10/25.
 * Model classes that process synchronous data
 */
public class SyncData {
    public static final Context context= BleApplication.getContext();

    private static final String SYNC_DATA = "sync_data";
    private static final String START_ADD_28 = "start_add_28";
    private static final String END_ADD_28 = "end_add_28";
    private static final String START_ADD_29 = "start_add_29";
    private static final String END_ADD_29 = "end_add_29";
    private static final String START_ADD_51 = "start_add_51";
    private static final String END_ADD_51 = "end_add_51";
    private static final String START_ADD_53 = "start_add_53";
    private static final String END_ADD_53 = "end_add_53";

    public static final int TYPE_28 = 0x1;//0001
    public static final int TYPE_29 = 0x2;//0010
    public static final int TYPE_51 = 0x4;//0100
    public static final int TYPE_53 = 0x8;//1000


    private int starAdd28;
    private int endAdd28;
    private int starAdd29;
    private int endAdd29;
    private int starAdd51;
    private int endAdd51;
    private int starAdd53;
    private int endAdd53;
    //Current address
    private int nowAdd28;
    private int nowAdd29;
    private int nowAdd51;
    private int nowAdd53;
    //Address the largest serial number, more than this serial number, address restart calculation
    private int range28;
    private int range51;
    private int range53;

    private boolean hasData28 = true;
    private boolean hasData29 = true;
    private boolean hasData51 = true;
    private boolean hasData53 = true;

    //Corresponding to whether the instruction has been sent, to prevent repeated instructions
    private boolean posted28Start = false;
    private boolean posted29Start = false;
    private boolean posted51Start = false;
    private boolean posted53Start = false;
    private boolean posted28Stop = false;
    private boolean posted29Stop = false;
    private boolean posted51Stop = false;
    private boolean posted53Stop = false;

    //The sum of all addresses
    private int mAllAdd = 0;
    //The number of addresses that have been synchronized
    private int mDAll;

    //Synchronization status
    private boolean mIsSyncDataInfo;

    //Stores the address of synchronization
    private SparseIntArray map28 = new SparseIntArray();
    private SparseIntArray map53 = new SparseIntArray();
    private SparseIntArray map51 = new SparseIntArray();

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public int getRange53() {
        return range53;
    }

    public void setRange53(int range53) {
        this.range53 = range53;
    }

    public int getRange28() {
        return range28;
    }

    public void setRange28(int range28) {
        this.range28 = range28;
    }

    public int getRange51() {
        return range51;
    }

    public void setRange51(int range51) {
        this.range51 = range51;
    }


    public boolean isSyncDataInfo() {
        return mIsSyncDataInfo;
    }

    public void setIsSyncDataInfo(boolean isSyncDataInfo) {
        mIsSyncDataInfo = isSyncDataInfo;
    }

    public int getNowAdd53() {
        return nowAdd53;
    }

    public void setNowAdd53(int nowAdd53, boolean isOver) {
        if (!mIsSyncDataInfo) {
            return;
        }
        this.nowAdd53 = nowAdd53;
        if (map53.indexOfKey(nowAdd53) > -1) {
            remove(map53, nowAdd53);
            if (map53.size() == 0 || isOver) {
                hasData53 = false;
            }
        } else {
            if (isOver) {
                hasData53 = false;
            }
        }
        EventBus.getDefault().post(new SyncDataEvent(getProgress(),false));
        checkProgress(hasData53, TYPE_53);
    }

    private void remove(SparseIntArray map, int now) {
        map.delete(now);
    }


    public int getNowAdd28() {
        return nowAdd28;
    }

    public void setNowAdd28(int nowAdd28, boolean isOver) {
        if (!mIsSyncDataInfo) {
            return;
        }
        this.nowAdd28 = nowAdd28;
        if (map28.indexOfKey(nowAdd28) > -1) {
            remove(map28, nowAdd28);
            if (map28.size() == 0 || isOver) {
                hasData28 = false;
            }
        } else {
            if (isOver) {
                hasData28 = false;
            }
        }
        EventBus.getDefault().post(new SyncDataEvent(getProgress(),false));
        checkProgress(hasData28, TYPE_28);
    }

    public int getNowAdd29() {
        return nowAdd29;
    }

    public void setNowAdd29(int nowAdd29, boolean isOver) {
        this.nowAdd29 = nowAdd29;
//        if (map28.size()==0||isOver){
//            hasData29=false;
//        }
        checkProgress(hasData29, TYPE_29);
    }

    public int getNowAdd51() {
        return nowAdd51;
    }

    public void setNowAdd51(int nowAdd51, boolean isOver) {
        if (!mIsSyncDataInfo) {
            return;
        }
        this.nowAdd51 = nowAdd51;
        if (map51.indexOfKey(nowAdd51) > -1) {
            remove(map51, nowAdd51);
            if (map51.size() == 0 || isOver) {
                hasData51 = false;
            }
        } else {
            if (isOver) {
                hasData51 = false;
            }
        }
        EventBus.getDefault().post(new SyncDataEvent(getProgress(),false));
        checkProgress(hasData51, TYPE_51);
    }

    private static SyncData instance;

    public static SyncData getInstance() {
        if (instance == null) {
            synchronized (SyncData.class) {
                if (instance == null) {
                    instance = new SyncData();
                }
            }
        }
        return instance;

    }

    private SyncData() {
        initData();
    }

    private void initData() {
        SharedPreferences sp = context.getSharedPreferences(SYNC_DATA, Context.MODE_PRIVATE);
        setStarAdd28(sp.getInt(START_ADD_28, 0));
        setEndAdd28(sp.getInt(END_ADD_28, 0));
        setStarAdd29(sp.getInt(START_ADD_29, 0));
        setEndAdd29(sp.getInt(END_ADD_29, 0));
        setStarAdd51(sp.getInt(START_ADD_51, 0));
        setEndAdd51(sp.getInt(END_ADD_51, 0));
        setStarAdd53(sp.getInt(START_ADD_53, 0));
        setEndAdd53(sp.getInt(END_ADD_53, 0));
    }

    public int getStarAdd28() {
        return starAdd28;
    }

    public void setStarAdd28(int starAdd28) {
        this.starAdd28 = starAdd28;
    }

    public int getEndAdd28() {
        return endAdd28;
    }

    public void setEndAdd28(int endAdd28) {
        this.endAdd28 = endAdd28;
    }

    public int getStarAdd29() {
        return starAdd29;
    }

    public void setStarAdd29(int starAdd29) {
        this.starAdd29 = starAdd29;
    }

    public int getEndAdd29() {
        return endAdd29;
    }

    public void setEndAdd29(int endAdd29) {
        this.endAdd29 = endAdd29;
    }

    public int getStarAdd51() {
        return starAdd51;
    }

    public void setStarAdd51(int starAdd51) {
        this.starAdd51 = starAdd51;
    }

    public int getEndAdd51() {
        return endAdd51;
    }

    public void setEndAdd51(int endAdd51) {
        this.endAdd51 = endAdd51;
    }

    public int getStarAdd53() {
        return starAdd53;
    }

    public void setStarAdd53(int starAdd53) {
        this.starAdd53 = starAdd53;
    }

    public int getEndAdd53() {
        return endAdd53;
    }

    public void setEndAdd53(int endAdd53) {
        this.endAdd53 = endAdd53;

    }

    /**
     * Initialize the storage number of the container
     */
    public void initMap() {
        createMap(map28, starAdd28, endAdd28, range28);
        createMap(map51, starAdd51, endAdd51, range51);
        createMap(map53, starAdd53, endAdd53, range53);
        hasData28 = starAdd28 != endAdd28;
        hasData28 = true;
        hasData51 = starAdd51 != endAdd51;
        hasData53 = starAdd53 != endAdd53;
        mAllAdd = map28.size() + map51.size() + map53.size();
    }

    private void createMap(SparseIntArray map, int start, int end, int range) {
        KLog.d("start : " + start + "  end :  " + end);
        map.clear();
        if (start > end) {
            end += range;
        }
        if (start == end || range == 0) {
            return;
        }
        for (int index = start; index <= end; index++) {
            map.put(index % range, index % range);
        }
    }

    public void save() {
            SharedPreferences.Editor edit =context.getSharedPreferences(SYNC_DATA, Context.MODE_PRIVATE).edit();
        edit.putInt(START_ADD_28, starAdd28);
        edit.putInt(END_ADD_28, starAdd28);
        edit.putInt(START_ADD_29, starAdd29);
        edit.putInt(END_ADD_29, starAdd29);
        edit.putInt(START_ADD_51, starAdd51);
        edit.putInt(END_ADD_51, starAdd51);
        edit.putInt(START_ADD_53, starAdd53);
        edit.putInt(END_ADD_53, starAdd53);
        edit.apply();
    }

    public void clear() {
        SharedPreferences.Editor edit = context.getSharedPreferences(SYNC_DATA, Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.apply();
        initData();
    }

    private void checkProgress(boolean haveData, int type) {
        mDAll = mAllAdd - (map28.size() + map51.size() + map53.size());
        KLog.d("checkProgress : " + mDAll + "  total : " + mAllAdd);
        mNowType = type;
        if (!haveData) {
            stopSyncData(type);
            syncData();
        } else {
            judgeStopSyncData();
        }
    }

    private int mNowType;

    private Runnable stopSyncRunnable = new Runnable() {
        @Override
        public void run() {
            stopSyncData(mNowType);
            syncData();
        }
    };

    /**
     * Over 15s did not call this method to stop synchronization
     */
    public void judgeStopSyncData() {
        mHandler.removeCallbacks(stopSyncRunnable);
        mHandler.postDelayed(stopSyncRunnable, 15000);
    }

    /**
     * Get synchronized data progress
     *
     * @return progress 0-100
     */
    public int getProgress() {

        //color screen bracelet  Sync progress processing
        if (SuperBleSDK.isZG(context)) {
            return ZGBaseUtils.progress_date;
        }

        if (mAllAdd <= 0) {
            return 0;
        }
        if (mDAll >= mAllAdd) {
           KLog.d("getProgress : " + mDAll + "  total : " + mAllAdd);
            mDAll = mAllAdd;
        }
        return (mDAll * 100) / mAllAdd;
    }

    /**
     * Sync data does not include 29
     */
    public void syncData() {
        if (!check28()) {
            if (!check51()) {
                check53();
            }
        }
    }

    private boolean check53() {
        if (hasData53) {
            syncData(TYPE_53);
            return true;
        }
        return false;
    }

    private boolean check51() {
        if (hasData51) {
            syncData(TYPE_51);
            return true;
        }
        return false;
    }

    private boolean check28() {
        if (hasData28) {
            syncData(TYPE_28);
            return true;
        }
        return false;
    }

    /**
     * Send different type synchronization instructions
     *
     * @param type TYPE_28 TYPE_29 TYPE_51 TYPE_53
     */
    public void syncData(int type) {
        KLog.d("Send start synchronization command : " + type);
        mHandler.removeCallbacks(mSyncDataRunnable);
        if ((type & TYPE_28) > 0 && !posted28Start) {
            posted28Start = true;
            posted28Stop = false;
            mNowType = type;
            //Begin to freeze data synchronization in segments
            byte[] data4 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setDialydata28(1, true, 0);
            BackgroundThreadManager.getInstance().addWriteData(context, data4);
        }
        if ((type & TYPE_29) > 0) {
            sync29Data();
        }
        if ((type & TYPE_51) > 0 && !posted51Start) {
            posted51Start = true;
            posted51Stop = false;
            mNowType = type;
            //Turn on sync segment heart rate
            byte[] data12 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).syncHeartRateSegmentData(1);
            BackgroundThreadManager.getInstance().addWriteData(context, data12);
        }
        if ((type & TYPE_53) > 0 && !posted53Start) {
            posted53Start = true;
            posted53Stop = false;
            mNowType = type;
            byte[] data11 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).syncHeartRateHourData(1);
            BackgroundThreadManager.getInstance().addWriteData(context, data11);
        }
        judgeStopSyncData();
    }

    public void stopSyncDataAll() {
        stopSyncData(SyncData.TYPE_28 | SyncData.TYPE_29 | SyncData.TYPE_51 | SyncData.TYPE_53);
        mHandler.removeCallbacks(stopSyncRunnable);
        mHandler.removeCallbacks(mSyncDataRunnable);
    }

    /**
     * Send different type to stop synchronous command
     *
     * @param type TYPE_28 TYPE_29 TYPE_51 TYPE_53
     */
    public void stopSyncData(int type) {
        if (type == mNowType) {
            mHandler.removeCallbacks(stopSyncRunnable);
        }
        KLog.d("Send stop synchronization command : " + type);
//        mIsSyncDataInfo = false;
        if ((type & TYPE_28) > 0) {
            //停止分段冻结数据同步
            hasData28 = false;
            if (BluetoothUtil.isConnected() && type != TYPE_28 && !posted28Stop) {
                posted28Stop = true;
                posted28Start = false;
                byte[] data4 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setDialydata28(0, true, 0);
                BackgroundThreadManager.getInstance().addWriteData(context, data4);
            }
        }
        if ((type & TYPE_29) > 0) {
            hasData29 = false;
            removeSync29DataTimeTask();
            if (BluetoothUtil.isConnected() && type != TYPE_29) {
                byte[] data5 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setDialydata29(0);
                BackgroundThreadManager.getInstance().addWriteData(context, data5);
            }
        }
        if ((type & TYPE_51) > 0) {
            hasData51 = false;
            if (BluetoothUtil.isConnected() && !posted51Stop && !isNotHaveHeart()) {
                posted51Stop = true;
                posted51Start = false;
                //Stop synchronizing segment heart rate
                byte[] data12 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).syncHeartRateSegmentData(0);
                BackgroundThreadManager.getInstance().addWriteData(context, data12);
            }
        }
        if ((type & TYPE_53) > 0) {
            hasData53 = false;
            if (BluetoothUtil.isConnected() && !posted53Stop && !isNotHaveHeart()) {
                posted53Stop = true;
                posted53Start = false;
                byte[] data11 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).syncHeartRateHourData(0);
                BackgroundThreadManager.getInstance().addWriteData(context, data11);

            }
        }
        if (!hasData28 && !hasData51 && !hasData53 && !hasData29) {
            mHandler.removeCallbacks(stopSyncRunnable);
            if (BluetoothUtil.isConnected()) {
                if (mIsSyncDataInfo) {
                    //Stores data synchronization time
                    PrefUtil.save(context, BaseActionUtils.Action_Last_Sync_Data_Time,System.currentTimeMillis());
                    EventBus.getDefault().post(new SyncDataEvent(0, true));
                }
                if (type < 15) {
                    byte[] data4 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setDialydata28(0, true, 0);
                    BackgroundThreadManager.getInstance().addWriteData(context, data4);

                    byte[] data5 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setDialydata29(0);
                    BackgroundThreadManager.getInstance().addWriteData(context, data5);

//                    byte[] data12 = WristBandDevice.getInstance().setWristBand_SleepData_Dialydata(1, true, 0);
//                    WriteOneDataTask task12 = new WriteOneDataTask(ZeronerApplication.getInstance(), data12);
//                    NewAgreementBackgroundThreadManager.getInstance().addTask(task12);
//
//                    byte[] data13 = WristBandDevice.getInstance().setWristBand_SportData_DialydataCurr(1);
//                    WriteOneDataTask task13 = new WriteOneDataTask(ZeronerApplication.getInstance(), data13);
//                    NewAgreementBackgroundThreadManager.getInstance().addTask(task13);
                }
            }
            mIsSyncDataInfo = false;
        }
    }

    private void sync29Data() {
        mHandler.removeCallbacks(mSync29DataRunnable);
        if (BluetoothUtil.isConnected()) {
            byte[] data5 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setDialydata29(1);
            BackgroundThreadManager.getInstance().addWriteData(context, data5);
            mHandler.postDelayed(mSync29DataRunnable, 10000);
        }
    }

    private int mSync29DataCount;

    private Runnable mSync29DataRunnable = new Runnable() {
        @Override
        public void run() {
            mSync29DataCount++;
            if (mSync29DataCount > 2) {
                mSync29DataCount = 0;
                stopSyncData(TYPE_29);
                return;
            }
            sync29Data();
        }
    };

    public void removeSync29DataTimeTask() {
        mHandler.removeCallbacks(mSync29DataRunnable);
        mSync29DataCount = 0;
    }

    /**
     * Send instructions to get synchronization information
     */
    public void syncDataInfo() {
        KLog.e("syncDataInfo");
        Context applicationContext = context.getApplicationContext();
        if (SuperBleSDK.readSdkType(context.getApplicationContext()) == SuperBleSDK.SDK_Zg) {
            if (applicationContext != null) {

                ZGBaseUtils.syncinitDataInfo(applicationContext);

            }
            return;
        }

        EventBus.getDefault().post(new SyncDataEvent());
        if (mIsSyncDataInfo) {
            KLog.d("Synchronizing...");
            return;
        }
        clearMap();
        mIsSyncDataInfo = true;
        mNowType = 0;
        clearData();
        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).readDataInfoStored();
        BackgroundThreadManager.getInstance().addWriteData(context, bytes);
        judgeSyncData();
        judgeStopSyncData();
    }

    private void clearData() {
        hasData28 = true;
        hasData29 = true;
        posted28Start = false;
        posted29Start = false;
        posted51Start = false;
        posted53Start = false;

        posted28Stop = false;
        posted29Stop = false;
        posted51Stop = false;
        posted53Stop = false;
        hasData51 = true;
        hasData53 = true;

        if (isNotHaveHeart()) {
            hasData51 = false;
            hasData53 = false;
        }
        mAllAdd = 0;
        mDAll = 0;
    }

    private boolean isNotHaveHeart() {
//        int type;
//        if (TextUtils.isEmpty(deviceModel)) {
//            WristBand wristBand = BluetoothUtil.getWristBand();
//            String derviceName = wristBand == null ? "" : wristBand.getName();
//            type = Util.bracelet_type(derviceName);
//        } else {
//            type = Util.bracelet_type_bymodel(deviceModel);
//        }
//        return type == Util.I5PLUS || type == Util.I5PRO || type == Util.V6;
        return true;
    }

    private void clearMap() {
        clearMapImpl();
    }

    private void clearMapImpl() {
        map28.clear();
        map53.clear();
        map51.clear();
    }


    private Runnable mSyncDataRunnable = new Runnable() {
        @Override
        public void run() {
            syncData(TYPE_29);
            syncData();
        }
    };


    /**
     * More than 10s did not get the synchronization information is synchronized
     */
    private void judgeSyncData() {
        mHandler.removeCallbacks(mSyncDataRunnable);
        mHandler.postDelayed(mSyncDataRunnable, 10000);
    }
}
