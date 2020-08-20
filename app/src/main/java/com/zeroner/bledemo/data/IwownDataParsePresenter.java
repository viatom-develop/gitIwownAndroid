package com.zeroner.bledemo.data;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.zeroner.bledemo.bean.sql.DailyData;
import com.zeroner.bledemo.bean.sql.HeartRateHour;
import com.zeroner.bledemo.bean.sql.SleepData;
import com.zeroner.bledemo.bean.sql.SportData;
import com.zeroner.bledemo.data.sync.SyncData;
import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.model.FMdeviceInfo;
import com.zeroner.blemidautumn.bluetooth.model.IWBleParams;
import com.zeroner.blemidautumn.bluetooth.model.IWDevSetting;
import com.zeroner.blemidautumn.bluetooth.model.KeyModel;
import com.zeroner.blemidautumn.bluetooth.model.Power;
import com.zeroner.blemidautumn.bluetooth.model.StoredDataInfoDetail;
import com.zeroner.blemidautumn.bluetooth.model.StoredDataInfoTotal;
import com.zeroner.blemidautumn.heart.model.zeroner.DataDetailHeart;
import com.zeroner.blemidautumn.heart.model.zeroner.DataHourHeart;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.output.detail_sport.impl.ZeronerDetailSportParse;
import com.zeroner.blemidautumn.output.detail_sport.model.ZeronerDetailSportData;
import com.zeroner.blemidautumn.output.sleep.impl.ZeronerSleepParse;
import com.zeroner.blemidautumn.output.total_sport.model.TotalSportData;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.task.BleWriteDataTask;
import org.greenrobot.eventbus.EventBus;
import java.util.HashMap;
import java.util.List;

/**
 * Iwown Protocol Data parse
 * Created by Daemon on 2017/10/30 14:28.
 */

public class IwownDataParsePresenter {
    public static final int Type = com.zeroner.blemidautumn.Constants.Bluetooth.Zeroner_Ble_Sdk;
    private static final String TAG = IwownDataParsePresenter.class.getName();
    private static String model = "";
    private static boolean sleepOver;

    /**
     * @param context
     * @param dataType
     * @param data
     */
    public static void parseProtocolData(Context context, int dataType, String data) {
        KLog.d(TAG, "receiver data：0x" + Integer.toHexString(dataType));
        switch (dataType) {
            case 0x00:
                FMdeviceInfo info = JsonUtils.fromJson(data, FMdeviceInfo.class);
                byte[] data12 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getDeviceStateDate();
                BleWriteDataTask task12 = new BleWriteDataTask(context, data12);
                BackgroundThreadManager.getInstance().addTask(task12);
                model =info.getModel();
                PrefUtil.save(context,BaseActionUtils.Action_device_Model,model);
                PrefUtil.save(context,BaseActionUtils.Action_device_version,info.getSwversion());
                break;
            case 0x01:
                Power mPower=JsonUtils.fromJson(data,Power.class);
                String power=String.valueOf(mPower.getPower());
                PrefUtil.save(context, BaseActionUtils.Action_device_Battery,power);
                HashMap<String,Object> dataMap=new HashMap<>();
                dataMap.put(Event.Ble_Connect_Statue,true);
                EventBus.getDefault().post(new Event(Event.Ble_Connect_Statue,dataMap));
                break;
            case 0x40:
                KeyModel keyModel = JsonUtils.fromJson(data, KeyModel.class);
                int code = keyModel.getKeyCode();
                break;
            case 0x1A:

                break;
            case 0x19: {
                Log.e("BLEPareseReceiver", "receiver 0x19");
                parse19Data(data);
                break;
            }

            case 0x08:
                parse08Data(JsonUtils.fromJson(data, StoredDataInfoTotal.class));
                break;

            case 0x13:
//                23 FF 13 06 91 00 00 00 00 00
                IWBleParams params=JsonUtils.fromJson(data,IWBleParams.class);
                boolean isNewProtocol =params.isNewProtocol();
                KLog.i("data,0x13"+data+"isNewProtocol"+isNewProtocol);
                break;

            case 0x51: {
                parse51(context,dataType,data);
                break;
            }
            case 0x53: {
                parse53(context,dataType,data);
                break;
            }

            case 0x29: {
                parse29(context, dataType, data);
                break;
            }

            case 0x28: {
                process28Data(context, dataType, data);
                break;
            }
        }
    }

    private static void parse53(Context context, int dataType, String data) {
        DataHourHeart dataHourHeart = JsonUtils.fromJson(data, DataHourHeart.class);
        SyncData.getInstance().setNowAdd53(dataHourHeart.getNowAdd53(), dataHourHeart.isLast());
        HeartRateHour heart= new HeartRateHour();
        heart.setYear(dataHourHeart.getYear());
        heart.setMonth(dataHourHeart.getMonth());
        heart.setDay(dataHourHeart.getDay());
        heart.setData_from(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME));
        heart.setHours(dataHourHeart.getHour());
        DateUtil date=new DateUtil(dataHourHeart.getYear(),dataHourHeart.getMonth(),dataHourHeart.getDay(),dataHourHeart.getHour(),0);
        heart.setRecord_date(date.getYyyyMMdd_HHmmDate());
        heart.setTime_stamp(date.getUnixTimestamp());
        heart.setDetail_data(JsonUtils.toJson(dataHourHeart.getRates()));
        if(dataHourHeart.isLast()){

        }else {
            SqlBizUtils.saveHeartData(heart);
        }
    }

    private static void parse51(Context context, int dataType, String data) {
        DataDetailHeart dataDetailHeart = null;
        try {
            dataDetailHeart = JsonUtils.fromJson(data, DataDetailHeart.class);
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("51 heart data parse error");
            return;
        }
        SyncData.getInstance().setNowAdd51(dataDetailHeart.getNowAdd51(), dataDetailHeart.isLast());
    }

    private static void process28Data(Context context, int dataType, String data1) {
        KLog.d("Block freeze motion data");
        int type = JsonUtils.getInt(data1, "type");
        boolean isLast = JsonUtils.getBoolean(data1, "last");
        int index = JsonUtils.getInt(data1, "index");
        //0 stop ，1,sleep data  2,sport data
        KLog.e(data1);
        ZeronerSleepParse iwownSleepParse = null;
        ZeronerDetailSportParse iwownDetailSportParse = null;
        SyncData.getInstance().setNowAdd28(index, isLast);
      if (type == 1) {
            iwownSleepParse = JsonUtils.fromJson(data1, ZeronerSleepParse.class);
            sleepOver = false;
            //New protocol for sleep
                SleepData sleepEntity = SleepData.parse(iwownSleepParse.getData(), context);
            if (!SqlBizUtils.querySleepDataExists(sleepEntity.getTimeStamp(),sleepEntity.getStart_time(), sleepEntity.getEnd_time(), sleepEntity.getActivity(), sleepEntity.getSleep_type(),PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME))) {
                sleepEntity.save();
            }
        } else if (type == 2) {
            iwownDetailSportParse = JsonUtils.fromJson(data1, ZeronerDetailSportParse.class);
            ZeronerDetailSportData iwownDetailSportData = iwownDetailSportParse.getData();
            SportData entity = SportData.parse(iwownDetailSportData, context);
            if (entity.isLive()) {

            }else {
                if (SqlBizUtils.querySportDataExists(entity.getStart_unixTime(),entity.getEnd_unixTime(),
                        entity.getStart_time(), entity.getEnd_time(), entity.getSport_type(),PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME))) {
                } else {
                    entity.save();
                }
            }
        }
    }


    private static void parse29(Context context, int dataType, String data) {
        KLog.i("parse29" + data);
        TotalSportData totalSportData = JsonUtils.fromJson(data, TotalSportData.class);
        SyncData.getInstance().removeSync29DataTimeTask();
        if (SyncData.getInstance().isSyncDataInfo()) {
            SyncData.getInstance().judgeStopSyncData();
            if (totalSportData.isLast()) {
                SyncData.getInstance().stopSyncData(SyncData.TYPE_29);
            }
        }
        DailyData dailyData=new DailyData();
        DateUtil date=new DateUtil(totalSportData.getYear(),totalSportData.getMonth(),totalSportData.getDay());
        dailyData.setTimeStamp((int) date.getUnixTimestamp());
        dailyData.setData_from(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME));
        dailyData.setDate(date.getSyyyyMMddDate());
        dailyData.setSteps(totalSportData.getSteps());
        dailyData.setCalories(totalSportData.getCalories());
        dailyData.setDistance(totalSportData.getDistance());
        dailyData.saveOrUpdate("timeStamp=? and data_from=?",String.valueOf(date.getUnixTimestamp()),PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS));
    }



    private static void parse19Data(String data) {
        IWDevSetting iwDevSetting = JsonUtils.fromJson(data, IWDevSetting.class);
    }

    private static void parse08Data(StoredDataInfoTotal storedDataInfoTotal) {
        if (!SyncData.getInstance().isSyncDataInfo()) {
            KLog.d("Abandon the analysis of 08 data-->mIsSyncDataInfo:"+ SyncData.getInstance().isSyncDataInfo(), TAG);
            return;
        }
        List<StoredDataInfoDetail> details = storedDataInfoTotal.getInfoList();
        SyncData.getInstance().clear();
        for (int i = 0; i < details.size(); i++) {
            StoredDataInfoDetail detail = details.get(i);
            int range = detail.getMax_range();
            int startAdd = detail.getStart_index();
            int endAdd = detail.getEnd_index();
            switch (detail.getType()) {
                case 0x28:
                    SyncData.getInstance().setStarAdd28(startAdd);
                    SyncData.getInstance().setRange28(range);
                    SyncData.getInstance().setEndAdd28(endAdd);
                    KLog.d("0x28-->start:"+startAdd+"  end:"+endAdd+"  range:"+range);
                    break;
                case 0x29:
//                    SyncData.getInstance().setStarAdd29(startAdd);
//                    SyncData.getInstance().setEndAdd29(endAdd);
                    break;
                case 0x51:
                    SyncData.getInstance().setStarAdd51(startAdd);
                    SyncData.getInstance().setRange51(range);
                    SyncData.getInstance().setEndAdd51(endAdd);
                    KLog.d("0x51-->start:"+startAdd+"  end:"+endAdd+"  range:"+range);
                    break;
                case 0x53:
                    SyncData.getInstance().setStarAdd53(startAdd);
                    SyncData.getInstance().setRange53(range);
                    SyncData.getInstance().setEndAdd53(endAdd);
                    KLog.d("0x53-->start:"+startAdd+"  end:"+endAdd+"  range:"+range);
                    break;
            }
            KLog.d("index : " + i + "  data : " + new Gson().toJson(detail));
        }

        SyncData.getInstance().initMap();
        SyncData.getInstance().save();
        SyncData.getInstance().syncData(SyncData.TYPE_29);
//        SyncData.getInstance().syncData(SyncData.TYPE_28);
        SyncData.getInstance().syncData();
    }
}
