package com.zeroner.bledemo.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.DailyData;
import com.zeroner.bledemo.bean.sql.DataIndex_68;
import com.zeroner.bledemo.bean.sql.RawData68;
import com.zeroner.bledemo.bean.sql.TB_60_data;
import com.zeroner.bledemo.bean.sql.TB_61_data;
import com.zeroner.bledemo.bean.sql.TB_62_data;
import com.zeroner.bledemo.bean.sql.TB_64_data;
import com.zeroner.bledemo.bean.sql.TB_68_data;
import com.zeroner.bledemo.bean.sql.TB_f1_index;
import com.zeroner.bledemo.data.sync.Ble61DataParse;
import com.zeroner.bledemo.data.sync.Ble62DataParse;
import com.zeroner.bledemo.data.sync.Ble64DataParse;
import com.zeroner.bledemo.data.sync.Ble68DataParse;
import com.zeroner.bledemo.data.sync.LongitudeAndLatitude;
import com.zeroner.bledemo.data.sync.MTKHeadSetSync;
import com.zeroner.bledemo.data.sync.MtkDataToServer;
import com.zeroner.bledemo.data.sync.MtkSync;
import com.zeroner.bledemo.data.sync.MtkToIvHandler;
import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.model.ECG_Data;
import com.zeroner.blemidautumn.bluetooth.model.FMdeviceInfo;
import com.zeroner.blemidautumn.bluetooth.model.GnssMinData;
import com.zeroner.blemidautumn.bluetooth.model.HealthDailyData;
import com.zeroner.blemidautumn.bluetooth.model.HealthMinData;
import com.zeroner.blemidautumn.bluetooth.model.IWBleParams;
import com.zeroner.blemidautumn.bluetooth.model.IWDevSetting;
import com.zeroner.blemidautumn.bluetooth.model.KeyModel;
import com.zeroner.blemidautumn.bluetooth.model.Power;
import com.zeroner.blemidautumn.bluetooth.model.R1HealthMinuteData;
import com.zeroner.blemidautumn.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * Created by admin on 2017/11/25.
 */

public class MtkDataParsePresenter {

   private static String TAG = "MtkDataParsePresenter";
    private static android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());
    public static final int Type = com.zeroner.blemidautumn.Constants.Bluetooth.Zeroner_Mtk_Sdk;
    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    private static int[] months=new int[3];
    private  static Gson mGson = new Gson();

    private static String DATA62_FILE_PATH = Environment.getExternalStorageDirectory() +"/Zeroner/zeroner_5_0/blelog/62_data/";
    private static String DATA62_FILE_DIR_PATH = "/Zeroner/zeroner_5_0/blelog/62_data/";

    private static String model = "";
    private static String swversion = "";
    private static boolean isTwo;

    private static Context mContext;


    /**
     * @param context
     * @param dataType
     * @param data
     */
    public static void parseProtoclData(Context context, int dataType, String data) {
        mContext = context.getApplicationContext();

        KLog.d(TAG, "数据接收：0x" + Integer.toHexString(dataType));
        KLog.d(TAG, "数据接收："+ data);
//
        switch (dataType) {
            case 0x00:
                FMdeviceInfo info = mGson.fromJson(data, FMdeviceInfo.class);
                model =info.getModel();
                swversion = info.getSwversion();
                PrefUtil.save(context, BaseActionUtils.Action_device_FirmwareInfo,data);
                PrefUtil.save(context,BaseActionUtils.Action_device_Model,model);
                PrefUtil.save(context,BaseActionUtils.Action_device_version,info.getSwversion());
                break;

            case 0x01:
                Power mPower=mGson.fromJson(data,Power.class);
                String power=String.valueOf(mPower.getPower());
                PrefUtil.save(context, BaseActionUtils.Action_device_Battery,  power);


                HashMap<String,Object> dataMap=new HashMap<>();
                dataMap.put(Event.Ble_Connect_Statue,true);
                EventBus.getDefault().post(new Event(Event.Ble_Connect_Statue,dataMap));
                break;
            case 0x40:
                KeyModel keyModel = mGson.fromJson(data, KeyModel.class);
                break;
            case 0x19: {
                IWDevSetting setting = mGson.fromJson(data, IWDevSetting.class);
                PrefUtil.save(context, BaseActionUtils.Action_device_Settings,data);
                break;
            }
            case 0x13:
                //23 FF 13 06 91 00 00 00 00 00
                IWBleParams params=mGson.fromJson(data,IWBleParams.class);
                break;
            case 0x60:
                parse60Data(context.getApplicationContext(), data);
                break;
            case 0x61:
                parse61Data(context,data);
                break;
            case 0x62:
                parse62Data(context,data);
                break;
            case 0x64:
                parse64Data(context,data);
                break;
            case 0x68:
                parse68Data(context,data);
                break;

            }

            KLog.d("data : " + data);
        }


    private static void parse64Data(Context context, final String data) {
        int ctrl64= 0;
        try {
            ctrl64 = JsonUtils.getInt(data,"ctrl");
        } catch (Exception e) {
            ctrl64=-100;
            e.printStackTrace();
        }
        com.socks.library.KLog.e("licl","获取到64 -:"+ctrl64);
        final String from=PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        if (ctrl64 == 0) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Ble64DataParse.parseCtrl0(data);
                    MtkSync.getInstance(mContext).syncP1AllData();
                }
            });
        }else{
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mHandler.removeCallbacks(sync61Runnable);
                    MtkSync.getInstance(mContext).validate_61_62_1_data(4);
                    ECG_Data cmd64=new Gson().fromJson(data,ECG_Data.class);
                    TB_64_data tb_data=new TB_64_data();
                    tb_data.setData_from(from);
                    tb_data.setSeq(cmd64.getSeq());
                    tb_data.setYear(cmd64.getYear());
                    tb_data.setMonth(cmd64.getMonth());
                    tb_data.setDay(cmd64.getDay());
                    tb_data.setHour(cmd64.getHour());
                    tb_data.setSecond(cmd64.getSecond());
                    tb_data.setMin(cmd64.getMin());
                    tb_data.setEcg(new Gson().toJson(cmd64.getEcg_raw_data()));
//                            tb_data.setCmd(Utils.bytesToHexString(datas));
                    MtkSync.getInstance(mContext).progressUP(false,tb_data.getYear()+"-"+tb_data.getMonth()+"-"+tb_data.getDay());
                    tb_data.setTime(new DateUtil(cmd64.getYear(),cmd64.getMonth(),cmd64.getDay(),cmd64.getHour(),cmd64.getMin(),cmd64.getSecond()).getUnixTimestamp());
                    tb_data.saveOrUpdate("data_from=?  and year=? and month=? and day=? and hour=? and min=? and second=? and seq=?"
                            ,String.valueOf(from)
                            ,String.valueOf(tb_data.getYear())
                            ,String.valueOf(tb_data.getMonth())
                            ,String.valueOf(tb_data.getDay())
                            ,String.valueOf(tb_data.getHour())
                            ,String.valueOf(tb_data.getMin())
                            ,String.valueOf(tb_data.getSecond())
                            ,String.valueOf(tb_data.getSeq())
                    );
                    if(cmd64.getSeq()+1==MtkSync.getInstance(mContext).getNowSync()) {
                        mHandler.post(sync61Runnable);
                    }
                    mHandler.postDelayed(sync61Runnable, 10000);
                }
            });
        }
    }


    private static void parse62Data(final Context context, final String data) {
        int ctrl62= JsonUtils.getInt(data,"ctrl");
        Log.e("licl","获取到62 -:"+ctrl62);
        final String from=PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        if (ctrl62 == 0) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Ble62DataParse.parseCtrl0(data);
                    SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext).getIndexTableAccordingType(0x64);
                }
            });
        }else {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mHandler.removeCallbacks(sync61Runnable);
                    GnssMinData ble62DataParse = new Gson().fromJson(data, GnssMinData.class);
                    int year = ble62DataParse.getYear();
                    int month = ble62DataParse.getMonth();
                    int day = ble62DataParse.getDay();
                    DateUtil date = new DateUtil(year, month, day);
                    String key = 2 + "_" + date.getYyyyMMddDate();
                    MtkSync.getInstance(mContext).validate_61_62_1_data(2);
                    TB_62_data cmd62 = new TB_62_data();
                    cmd62.setData_from(from+"");
                    cmd62.setCtrl(ble62DataParse.getCtrl());
                    cmd62.setSeq(ble62DataParse.getIndex());
                    cmd62.setYear(ble62DataParse.getYear());
                    cmd62.setMonth(ble62DataParse.getMonth());
                    cmd62.setDay(ble62DataParse.getDay());
                    cmd62.setHour(ble62DataParse.getHour());
                    cmd62.setMin(ble62DataParse.getMin());
                    cmd62.setFreq(ble62DataParse.getFreq());
                    cmd62.setNum(ble62DataParse.getNum());
                    cmd62.setCmd(ble62DataParse.getCmd());
                    //存入时间戳
                    cmd62.setTime(new DateUtil(cmd62.getYear(), cmd62.getMonth(), cmd62.getDay(), cmd62.getHour(), cmd62.getMin(), 0).getTimestamp());

                    List<GnssMinData.Gnss> gnssDatas = ble62DataParse.getmGnssMinDataList();
                    List<LongitudeAndLatitude> laData = new ArrayList<>();
                    for (GnssMinData.Gnss gnssData : gnssDatas) {
                        LongitudeAndLatitude la = new LongitudeAndLatitude();
                        la.setLatitude(gnssData.getLatitude());
                        la.setLongitude(gnssData.getLongitude());
                        la.setAltitude(gnssData.getAltitude());
                        la.setGps_speed(gnssData.getGps_speed());
                        laData.add(la);
                    }
                    cmd62.setGnssData(new Gson().toJson(laData));
//                    MtkSync.getInstance(mContext).progressUP(false, cmd62.getYear()+"-"+cmd62.getMonth() + "-" + cmd62.getDay()+" -62"+(ble62DataParse.getIndex()+1)+" - "+MtkSync.getInstance(mContext).getNowSync());
                    MtkSync.getInstance(mContext).progressUP(false, cmd62.getYear()+"-"+cmd62.getMonth() + "-" + cmd62.getDay());
                    if (cmd62.getYear() - 2000 == 0xff && cmd62.getMonth() - 1 == 0xff && cmd62.getDay() - 1 == 0xff && cmd62.getHour() == 0xff && cmd62.getMin() == 0xff) {
                        return;
                    }
                    cmd62.saveOrUpdate("seq=? and year =? and month=? and day=? and hour=? and min=?"
                            , String.valueOf(cmd62.getSeq())
                            , String.valueOf(cmd62.getYear())
                            , String.valueOf(cmd62.getMonth())
                            , String.valueOf(cmd62.getDay())
                            , String.valueOf(cmd62.getHour())
                            , String.valueOf(cmd62.getMin()));
                    if (ble62DataParse.getIndex() + 1 == MtkSync.getInstance(mContext).getNowSync()) {
                        com.socks.library.KLog.e("testf1shuju111","62一天的数据结束:"+cmd62.getYear()+"-"+cmd62.getMonth()+"-"+cmd62.getDay());
                        mHandler.post(sync61Runnable);
                    }
                    mHandler.postDelayed(sync61Runnable, 10000);
                }
            });
        }
    }

    private static void parse60Data(Context context, String data) {
        String datafrom=SuperBleSDK.createInstance(context).getWristBand().getName()+"";
        HealthDailyData ble60DataParse=new Gson().fromJson(data, HealthDailyData.class);
        com.socks.library.KLog.i("ble60DataParse"+ble60DataParse.toString());
        TB_60_data cmd_60=new TB_60_data();
        cmd_60.setData_from(datafrom);
        cmd_60.setYear(ble60DataParse.getYear());
        cmd_60.setMonth(ble60DataParse.getMonth());
        cmd_60.setDay(ble60DataParse.getDay());
        cmd_60.setData_type(ble60DataParse.getData_type());
        cmd_60.setSteps(ble60DataParse.getSteps());
        cmd_60.setCalorie(ble60DataParse.getCalorie());
        cmd_60.setDistance(ble60DataParse.getDistance());
        cmd_60.setAvg_bpm(ble60DataParse.getAvg_bpm());
        cmd_60.setMax_bpm(ble60DataParse.getMax_bpm());
        cmd_60.setMin_bpm(ble60DataParse.getMin_bpm());
        cmd_60.setAvg_bpm(ble60DataParse.getAvg_bpm());
        cmd_60.setLevel(ble60DataParse.getLevel());
        cmd_60.setSdnn(ble60DataParse.getSdnn());
        cmd_60.setLf(ble60DataParse.getLf());
        cmd_60.setHf(ble60DataParse.getHf());
        cmd_60.setLf_hf(ble60DataParse.getLf_hf());
        cmd_60.setBpm_hr(ble60DataParse.getBpm_hr());
        cmd_60.setSbp(ble60DataParse.getSbp());
        cmd_60.setDbp(ble60DataParse.getDbp());
        cmd_60.setBpm(ble60DataParse.getBpm());

        cmd_60.saveOrUpdate("data_from=? and year=? and month=? and day=? "
                ,datafrom
                ,String.valueOf(cmd_60.getYear())
                ,String.valueOf(cmd_60.getMonth())
                ,String.valueOf(cmd_60.getDay()));


        DailyData dailyData=new DailyData();
        DateUtil date=new DateUtil(cmd_60.getYear(),cmd_60.getMonth(),cmd_60.getDay());
        dailyData.setTimeStamp((int) date.getUnixTimestamp());
        dailyData.setData_from(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME));
        dailyData.setDate(date.getSyyyyMMddDate());
        dailyData.setSteps(cmd_60.getSteps());
        dailyData.setCalories(cmd_60.getCalorie());
        dailyData.setDistance(cmd_60.getDistance());
        dailyData.saveOrUpdate("date=? and data_from=?",date.getSyyyyMMddDate(),PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS));
    }

    static int number = 0;
    static Runnable sync61Runnable=new Runnable() {
        @Override
        public void run() {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mHandler.removeCallbacks(sync61Runnable);
                    com.socks.library.KLog.e("licldltestf1shuju111","数据接收超时或者接收完毕发送其他命令: ");

                    if(!MtkSync.getInstance(mContext).isOver()) {
                        if(MtkSync.getInstance(mContext).isOneDayOver()){
                            number++;
                            List<TB_61_data> list= MtkToIvHandler.sort61DataBySeq(
                                     months[0]
                                    , months[1]
                                    , months[2]
                                    , PrefUtil.getString(mContext, BaseActionUtils.ACTION_DEVICE_NAME));


                            if(number==1){
                                com.socks.library.KLog.i("=====sync one day====="+(number==1));
                                MtkToIvHandler.mtk61DataToHeart(
                                          months[0]
                                        , months[1]
                                        , months[2]
                                        ,  PrefUtil.getString(mContext, BaseActionUtils.ACTION_DEVICE_NAME)+""
                                        ,list);

                                MtkToIvHandler.p161DataToIvSport(months[0], months[1], months[2]);
                            }
                            if (number==2) {
                                MtkDataToServer.saveTodayCmd();
                            }
                        }
                        if (!MtkSync.getInstance(mContext).sync_P1_data()) {
                            com.socks.library.KLog.e("upDataToServer", "upCmdToServer--2");
                            MtkDataToServer.upCmdToServer();
                            MtkDataToServer.upCmd62ToServer();
                            number=0;
                        }
                        mHandler.postDelayed(sync61Runnable, 10000);
                    }
                }
            });
        }
    };

    private static void parse61Data(final Context context, final String data) {
        try {
            final String from=PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
            JSONObject jb = new JSONObject(data);
            int ctrl = jb.getInt("ctrl");
            if (ctrl == 0) {
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Ble61DataParse.parseCtrl0(data);
                        SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext).getIndexTableAccordingType(0x62);
                    }
                });
            } else {
                mHandler.removeCallbacks(sync61Runnable);
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        HealthMinData ble61DataParse = new Gson().fromJson(data, HealthMinData.class);
                        int year=ble61DataParse.getYear();
                        int month=ble61DataParse.getMonth();
                        int day=ble61DataParse.getDay();
                        DateUtil date=new DateUtil(year,month,day);
                        String key=1+"_"+date.getYyyyMMddDate();
                        MtkSync.getInstance(mContext).validate_61_62_1_data(1);

                        TB_61_data cmd_61=new TB_61_data();
                        cmd_61.setData_from(from);
                        cmd_61.setCtrl(ble61DataParse.getCtrl());
                        cmd_61.setSeq(ble61DataParse.getSeq());
                        cmd_61.setYear(ble61DataParse.getYear());
                        cmd_61.setMonth(ble61DataParse.getMonth());
                        cmd_61.setDay(ble61DataParse.getDay());
                        cmd_61.setHour(ble61DataParse.getHour());
                        cmd_61.setMin(ble61DataParse.getMin());
//                            if(Utils.bytesToHexString(datas).equals(lastcmd)){
//                                xiangtong++;
////                                Log.e("teristicChanged-->",lastcmd +"  --  "+xiangtong);
//                            }
//                                lastcmd=Utils.bytesToHexString(datas);
//                        MtkSync.getInstance(mContext).progressUP(false,cmd_61.getYear()+"-"+cmd_61.getMonth()+"-"+cmd_61.getDay()+" -61-" +(ble61DataParse.getSeq()+1)+" - "+MtkSync.getInstance(mContext).getNowSync());
                        MtkSync.getInstance(mContext).progressUP(false,cmd_61.getYear()+"-"+cmd_61.getMonth()+"-"+cmd_61.getDay());
                        if(cmd_61.getYear()-2000==0xff&&cmd_61.getMonth()-1==0xff&&cmd_61.getDay()-1==0xff&&cmd_61.getHour()==0xff&&cmd_61.getMin()==0xff){
                            return ;
                        }
                        months[0]=ble61DataParse.getYear();
                        months[1]=ble61DataParse.getMonth();
                        months[2]=ble61DataParse.getDay();
                        //存入时间戳
                        cmd_61.setTime(new DateUtil(cmd_61.getYear(),cmd_61.getMonth(),cmd_61.getDay(),cmd_61.getHour(),cmd_61.getMin(),ble61DataParse.getSecond()).getTimestamp());
                        cmd_61.setData_type(ble61DataParse.getData_type());
                        cmd_61.setSport_type(ble61DataParse.getSport_type());
                        cmd_61.setCalorie(ble61DataParse.getCalorie());
                        cmd_61.setStep(ble61DataParse.getStep());
                        cmd_61.setDistance(ble61DataParse.getDistance());
                        cmd_61.setState_type(ble61DataParse.getState_type());
                        cmd_61.setAutomatic(ble61DataParse.getAutomaticMin());
                        cmd_61.setReserve(ble61DataParse.getSecond());
                        cmd_61.setMin_bpm(ble61DataParse.getMin_bpm());
                        cmd_61.setMax_bpm(ble61DataParse.getMax_bpm());
                        cmd_61.setAvg_bpm(ble61DataParse.getAvg_bpm());
                        cmd_61.setLevel(ble61DataParse.getLevel());
                        cmd_61.setSdnn(ble61DataParse.getSdnn());
                        cmd_61.setLf_hf(ble61DataParse.getLf());
                        cmd_61.setHf(ble61DataParse.getHf());
                        cmd_61.setLf_hf(ble61DataParse.getLf_hf());
                        cmd_61.setBpm_hr(ble61DataParse.getBpm_hr());
                        cmd_61.setSbp(ble61DataParse.getSbp());
                        cmd_61.setDbp(ble61DataParse.getDbp());
                        cmd_61.setBpm(ble61DataParse.getBpm());
                        cmd_61.setCmd(ble61DataParse.getCmd());

//                            cmd_61.save();
                        cmd_61.saveOrUpdate("cmd=?"
                                ,cmd_61.getCmd()+""
                        );
                        if(ble61DataParse.getSeq()+1==MtkSync.getInstance(mContext).getNowSync()) {
                            com.socks.library.KLog.e("testf1shuju111","61有一天的同步结束: "+year+"-"+month+"-"+day+"  已同步到的: "+MtkSync.getInstance(mContext).getNowSync());
                            ContentValues values = new ContentValues();
                            values.put("ok",1);
                            DataSupport.updateAll(TB_f1_index.class,values,"data_from=? and end_seq=?",
                                    from+"",MtkSync.getInstance(mContext).getNowSync()+"");
                            mHandler.post(sync61Runnable);
                        }
                        mHandler.postDelayed(sync61Runnable, 10000);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parse68Data(final Context context, final String data) {
        try {
            final String from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
            JSONObject jb = new JSONObject(data);
            int ctrl = jb.getInt("ctrl");
            if (ctrl == 0) {
                com.socks.library.KLog.e("receive 68 index data");
                fixedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        List<DataIndex_68> dataIndex68List = Ble68DataParse.parseCtrl0(data);
                        MTKHeadSetSync.getInstance().syncDetailData(dataIndex68List);
                    }
                });
            } else {
                mHandler.removeCallbacks(sync68Timeout);
                fixedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        com.socks.library.KLog.e("receive 68 detail data");
                        R1HealthMinuteData ble68DataParse = new Gson().fromJson(data, R1HealthMinuteData.class);

                        //save raw data, prepare for upload
                        RawData68 rawData68 = new RawData68();
                        rawData68.setData_from(from);
                        rawData68.setYear(ble68DataParse.getYear());
                        rawData68.setMonth(ble68DataParse.getMonth());
                        rawData68.setDay(ble68DataParse.getDay());
                        rawData68.setHour(ble68DataParse.getHour());
                        rawData68.setMin(ble68DataParse.getMinute());
                        rawData68.setSecond(ble68DataParse.getSecond());
                        rawData68.setSeq(ble68DataParse.getSeq());
                        rawData68.setRaw_data(data);
                        rawData68.saveOrUpdate("data_from=? and seq=? " +
                                        "and year=? and month=? and day=? and hour=? and min=? and " +
                                        "second=?",
                                from,String.valueOf(rawData68.getSeq()),
                                String.valueOf(rawData68.getYear()),
                                String.valueOf(rawData68.getMonth()),
                                String.valueOf(rawData68.getDay()),
                                String.valueOf(rawData68.getHour()),
                                String.valueOf(rawData68.getMin()),
                                String.valueOf(rawData68.getSecond())
                        );
                        com.socks.library.KLog.e("save raw data 68");
                        TB_68_data data68 = new TB_68_data();
                        data68.setData_from(from);
                        data68.setCtrl(ble68DataParse.getCtrl());
                        data68.setSeq(ble68DataParse.getSeq());
                        data68.setYear(ble68DataParse.getYear());
                        data68.setMonth(ble68DataParse.getMonth());
                        data68.setDay(ble68DataParse.getDay());
                        data68.setHour(ble68DataParse.getHour());
                        data68.setMin(ble68DataParse.getMinute());
                        data68.setSeconds(ble68DataParse.getSecond());

                        MTKHeadSetSync.getInstance().setCounter(MTKHeadSetSync.getInstance().getCounter() + 1);

                        //handle exceptional case
                        if (data68.getYear() - 2000 == 0xff && data68.getMonth() - 1 == 0xff && data68.getDay() - 1 == 0xff && data68.getHour() == 0xff
                                && data68.getMin() == 0xff) {
                            com.socks.library.KLog.e("invalid data");
                            return;
                        }

                        months[0] = ble68DataParse.getYear();
                        months[1] = ble68DataParse.getMonth();
                        months[2] = ble68DataParse.getDay();
                        //存入时间戳
                        data68.setTime(new DateUtil(data68.getYear(), data68.getMonth(), data68.getDay(), data68.getHour(),
                                data68.getMin(), data68.getSeconds()).getTimestamp());
                        data68.setData_type(ble68DataParse.getData_type());
                        if((data68.getData_type() & 240) == 32){
                            com.socks.library.KLog.e("contain sport data");
                            data68.setSport_type(ble68DataParse.getWalk().getSport_type());
                            data68.setCalorie(ble68DataParse.getWalk().getCalorie());
                            data68.setStep(ble68DataParse.getWalk().getStep());
                            data68.setDistance(ble68DataParse.getWalk().getDistance());
                            data68.setState_type(ble68DataParse.getWalk().getState_type());
                            data68.setRateOfStride_avg(ble68DataParse.getWalk().getRateOfStride_avg());
                            data68.setRateOfStride_max(ble68DataParse.getWalk().getRateOfStride_max());
                            data68.setRateOfStride_min(ble68DataParse.getWalk().getRateOfStride_min());
                            data68.setFlight_avg(ble68DataParse.getWalk().getFlight_avg());
                            data68.setFlight_max(ble68DataParse.getWalk().getFlight_max());
                            data68.setFlight_min(ble68DataParse.getWalk().getFlight_min());
                            data68.setTouchDown_avg(ble68DataParse.getWalk().getTouchDown_avg());
                            data68.setTouchDown_max(ble68DataParse.getWalk().getTouchDown_max());
                            data68.setTouchDown_min(ble68DataParse.getWalk().getTouchDown_min());
                            data68.setTouchDownPower_avg(ble68DataParse.getWalk().getTouchDownPower_avg());
                            data68.setTouchDownPower_balance(ble68DataParse.getWalk().getTouchDownPower_balance());
                            data68.setTouchDownPower_max(ble68DataParse.getWalk().getTouchDownPower_max());
                            data68.setTouchDownPower_min(ble68DataParse.getWalk().getTouchDownPower_min());
                            data68.setTouchDownPower_stop(ble68DataParse.getWalk().getTouchDownPower_stop());
                        }
                        if((data68.getData_type() & 15) == 1){
                            com.socks.library.KLog.e("contain hr data");
                            data68.setAvg_hr(ble68DataParse.getHr().getAvg_hr());
                            data68.setMax_hr(ble68DataParse.getHr().getMax_hr());
                            data68.setMin_hr(ble68DataParse.getHr().getMin_hr());
                        }

                        data68.setCmd(ble68DataParse.getCmd());
                        data68.saveOrUpdate("data_from=? and data_type=? and year=? and month=? " +
                                        "and day=? and hour=? and min=? and seconds=? and state_type=? " +
                                        "and sport_type=?"
                                , from
                                , String.valueOf(data68.getData_type())
                                , String.valueOf(data68.getYear())
                                , String.valueOf(data68.getMonth())
                                , String.valueOf(data68.getDay())
                                , String.valueOf(data68.getHour())
                                , String.valueOf(data68.getMin())
                                , String.valueOf(data68.getSeconds())
                                , String.valueOf(data68.getState_type())
                                , String.valueOf(data68.getSport_type())
                        );
                        com.socks.library.KLog.e("save 68 detail data");
                        MTKHeadSetSync.getInstance().reportProgress(MTKHeadSetSync.getInstance().getCounter());
                    }
                });
                mHandler.postDelayed(sync68Timeout, 5000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Runnable sync68Timeout = new Runnable() {
        @Override
        public void run() {
            MTKHeadSetSync.getInstance().reportProgress();
        }
    };

}


