package com.zeroner.bledemo.data;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.HeartEvent;
import com.zeroner.bledemo.bean.sql.PbSupportInfo;
import com.zeroner.bledemo.bean.sql.ProtoBuf_80_data;
import com.zeroner.bledemo.bean.sql.ProtoBuf_index_80;
import com.zeroner.bledemo.bean.sql.SwimBean;
import com.zeroner.bledemo.bean.sql.TB_62_data;
import com.zeroner.bledemo.bean.sql.TB_64_data;
import com.zeroner.bledemo.bean.sql.TB_BP_data;
import com.zeroner.bledemo.bean.sql.TB_blue_gps;
import com.zeroner.bledemo.bean.sql.TB_rri_data;
import com.zeroner.bledemo.bean.sql.TB_swim_data;
import com.zeroner.bledemo.data.sync.LongitudeAndLatitude;
import com.zeroner.bledemo.data.sync.ProtoBufIndex;
import com.zeroner.bledemo.data.sync.ProtoBufSync;
import com.zeroner.bledemo.data.sync.ProtoBufUpdate;
import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.handler_data.SportHandler;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.Constants;
import com.zeroner.blemidautumn.bluetooth.model.ConnectParmas;
import com.zeroner.blemidautumn.bluetooth.model.KeyModel;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufFileUpdateInfo;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHardwareInfo;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHisEPGData;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHisGnssData;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHisHealthData;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHisIndexTable;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHisRriData;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufSupportInfo;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufSwimData;
import com.zeroner.blemidautumn.bluetooth.model.RealTimeData;
import com.zeroner.blemidautumn.bluetooth.model.SwimDataBean;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.utils.JsonTool;
import com.zeroner.blemidautumn.utils.SingleThreadUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.zeroner.blemidautumn.utils.Util.write2SDFromString;

public class ProtoBufDataParsePersenter {

    public static final int Type = Constants.Bluetooth.Zeroner_protobuf_Sdk;
    public static final int Type2 = Constants.Bluetooth.ZERONER_PROTOBUF_VOICE_SDK;

    private static final int CASE_INDEX_TABLE = 3;
    private static final int CASE_HIS_DATA = 4;
    //    STATUS(2),
//    HEALTH(3),
//    GNSS(4),
//    ECG(5),
//    PPG(6),
//    RRI(7),
//    DATA_NOT_SET(0);
    private static final int TYPE_HIS_HEALTH = 3;
    private static final int TYPE_HIS_GNSS = 4;
    private static final int TYPE_HIS_ECG = 5;
    private static final int TYPE_HIS_PPG = 6;
    private static final int TYPE_HIS_RRI = 7;
    private static final int TYPE_HIS_SWIM = 10;
    private static int[] date = {0, 0, 0};
    private static int[] gpsDate = {0, 0, 0};

    private static ThreadPoolExecutor fixedThreadPool = new ThreadPoolExecutor(2, 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    private static android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());
    private static HeartEvent event;
    private static int index = 0;
    private static long testUid=1001;

    public static void parseProtocolData(Context context, int dataType, String data) {
        switch (dataType) {
            case 0x00f4:
            case 0x00f6:
                //标准心率协议
                if (event == null) {
                    event = new HeartEvent();
                }
                event.setHeart(Integer.parseInt(data));
                EventBus.getDefault().post(event);
                break;
            case 0x0000:
                ProtoBufHardwareInfo info = JsonTool.fromJson(data, ProtoBufHardwareInfo.class);
                PrefUtil.save(context, BaseActionUtils.Action_device_version, info.getVersion());
                PrefUtil.save(context, BaseActionUtils.Action_device_Model, info.getModel());
                PrefUtil.save(context, BaseActionUtils.Action_device_Sn, info.getSn());
                LogUtils.d("SN: " + info.getSn());
                break;
            case 0x0001:
                break;
            case 0x0002:
                parse02Data(context, data);
                break;
            case 0x0003:
                break;
            case 0x0004:
                break;
            case 0x0005:
                break;
            case 0x0006:
                break;
            case 0x0007:
                break;
            case 0x0008:
                break;
            case 0x0009:
                /**
                 * 更新支持的历史同步数据类型
                 */
                parse09Data(context, data);
                break;
            case 0x0070:
                //解析
                RealTimeData realTimeData = JsonTool.fromJson(data, RealTimeData.class);
                if (realTimeData.isBattery()) {
                    int level = realTimeData.getLevel();
                    PrefUtil.save(context, BaseActionUtils.Action_device_Battery, level + "");
                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put(Event.Ble_Connect_Statue, true);
                    EventBus.getDefault().post(new Event(Event.Ble_Connect_Statue, dataMap));
                } else if (realTimeData.isHearth()) {
                    int steps = realTimeData.getSteps();
                    int calorie = realTimeData.getCalorie();
                    int distance = realTimeData.getDistance();
                } else if (realTimeData.isTime()) {

                }
                break;
            case 0x0080:
                parse80Data(context, data);
                break;
            case 0x0090:
                parse90data(data);
                break;
            case 0xFFFF:
                parseFFData(context, data);
                break;
            default:
                break;
        }
    }

    private static void parse02Data(Context context, String data) {
//        case 0: return ACCEPT;
//        case 1: return REJECT;
//        case 2: return MUTE;
        KeyModel model = JsonTool.fromJson(data, KeyModel.class);
        int keyCode = model.getKeyCode();
        if (keyCode == 1) {
            Intent intent1 = new Intent(BaseActionUtils.Action_Phone_Statue_Out);
            intent1.setComponent(
                    new ComponentName(context.getApplicationContext().getPackageName(), "com.zeroner.bledemo.notification.CallReceiver"));
            context.sendBroadcast(intent1);
        }
    }

    private static void parse09Data(Context context, String data) {
        String data_from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        ProtoBufSupportInfo info = JsonTool.fromJson(data, ProtoBufSupportInfo.class);
        PbSupportInfo dbInfos = new PbSupportInfo();
        dbInfos.setSupport_health(true);
        dbInfos.setData_from(data_from);
        dbInfos.setSupport_gnss(info.isSupport_gnss());
        dbInfos.setSupport_ppg(info.isSupport_ppg());
        dbInfos.setSupport_ecg(info.isSupport_ecg());
        dbInfos.setSupport_ppg(info.isSupport_ppg());
        dbInfos.setSupport_rri(info.isSupport_rri());
        dbInfos.saveOrUpdate("data_from=?", data_from);
    }

    private static void parse80Data(final Context context, final String data) {
        int hisDataCase = JsonTool.getIntValue(data, "hisDataCase");
        final String from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        if (hisDataCase == CASE_INDEX_TABLE) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    ProtoBufHisIndexTable i7BHisIndexTable = JsonTool.fromJson(data, ProtoBufHisIndexTable.class);
                    List<ProtoBuf_index_80> index_80s = ProtoBufIndex.parseIndex(i7BHisIndexTable);
                    for (ProtoBuf_index_80 index80 : index_80s) {
                        KLog.e(index80.toString());
                    }
                    ProtoBufSync.getInstance().syncDetailData(index_80s);
                }
            });

        } else if (hisDataCase == CASE_HIS_DATA) {
            //入表历史数据
            final int hisDataType = JsonTool.getIntValue(data, "hisDataType");
            if (hisDataType == TYPE_HIS_HEALTH) {
                //历史健康数据
                //存表

                mHandler.removeCallbacks(sync80Timeout);
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        //查询数据
                        ProtoBufHisHealthData i7BHisHealthData = JsonTool.fromJson(data, ProtoBufHisHealthData.class);
                        ProtoBuf_80_data data80 = new ProtoBuf_80_data();
                        data80.setData_from(from);
                        data80.setYear(i7BHisHealthData.getYear());
                        data80.setMonth(i7BHisHealthData.getMonth());
                        data80.setDay(i7BHisHealthData.getDay());
                        data80.setHour(i7BHisHealthData.getHour());
                        data80.setMinute(i7BHisHealthData.getMinute());
                        data80.setSeq(i7BHisHealthData.getSeq());
                        //睡眠
                        data80.setSleepData(i7BHisHealthData.getSleep().toString());
                        data80.setCharge(i7BHisHealthData.getSleep().isCharge());
                        data80.setShutdown(i7BHisHealthData.getSleep().isShutdown());
                        //健康
                        data80.setType(i7BHisHealthData.getPedo().getType());
                        data80.setState(i7BHisHealthData.getPedo().getState());
                        /**
                         * 手表返回的卡路里单位是0.1千卡
                         */
                        data80.setCalorie(i7BHisHealthData.getPedo().getCalorie() * 1.0f / 10);
                        data80.setStep(i7BHisHealthData.getPedo().getStep());
                        data80.setDistance(i7BHisHealthData.getPedo().getDistance() / 10f);
                        //心率
                        data80.setMin_bpm(i7BHisHealthData.getHeartRate().getMin_bpm());
                        data80.setMax_bpm(i7BHisHealthData.getHeartRate().getMax_bpm());
                        data80.setAvg_bpm(i7BHisHealthData.getHeartRate().getAvg_bpm());
                        //疲劳度
                        data80.setSDNN(i7BHisHealthData.getHrv().getSDNN());
                        data80.setRMSSD(i7BHisHealthData.getHrv().getRMSSD());
                        data80.setPNN50(i7BHisHealthData.getHrv().getPNN50());
                        data80.setMEAN(i7BHisHealthData.getHrv().getMEAN());
                        data80.setFatigue(i7BHisHealthData.getHrv().getFatigue());
                        //血压
                        data80.setSbp(i7BHisHealthData.getBp().getSbp());
                        data80.setDbp(i7BHisHealthData.getBp().getDbp());
                        data80.setBpTime(i7BHisHealthData.getBp().getTime());
                        data80.setTime(i7BHisHealthData.getTime());
                        data80.setSecond(i7BHisHealthData.getSecond());

                        data80.setMdt_SDNN(i7BHisHealthData.getMdt().getMdt_SDNN());
                        data80.setMdt_RMSSD(i7BHisHealthData.getMdt().getMdt_RMSSD());
                        data80.setMdt_PNN50(i7BHisHealthData.getMdt().getMdt_PNN50());
                        data80.setMdt_MEAN(i7BHisHealthData.getMdt().getMdt_MEAN());
                        data80.setMdt_status(i7BHisHealthData.getMdt().getMdt_status());
                        data80.setMdt_RELAX(i7BHisHealthData.getMdt().getMdt_RELAX());
                        data80.setMdt_RESULT(i7BHisHealthData.getMdt().getMdt_RESULT());

                        data80.setAvgSpo2(i7BHisHealthData.getSpo2().getAvgSpo2());
                        data80.setMaxSpo2(i7BHisHealthData.getSpo2().getMaxSpo2());
                        data80.setMinSpo2(i7BHisHealthData.getSpo2().getMinSpo2());

                        int eviBody = i7BHisHealthData.getTemper().getEvi_body();
                        int estiArm = i7BHisHealthData.getTemper().getEsti_arm();

                        data80.setTemperType(i7BHisHealthData.getTemper().getType());
                        //温度
                        data80.setTemperEnv((eviBody & 0xffff0000) >> 16);
                        data80.setTemperBody(eviBody & 0xffff);
                        data80.setTemperDef((estiArm & 0xffff0000) >> 16);
                        data80.setTemperArm(estiArm & 0xffff);

                        data80.saveOrUpdate("year=? and month=? and day=? and hour=? and minute=? and second=? and data_from=? and seq=?",
                                data80.getYear() + "",
                                data80.getMonth() + "",
                                data80.getDay() + "",
                                data80.getHour() + "",
                                data80.getMinute() + "",
                                data80.getSecond() + "",
                                data80.getData_from(),
                                data80.getSeq() + "");

                        int progress = ProtoBufSync.getInstance().currentProgress(ProtoBufSync.HEALTH_DATA, i7BHisHealthData.getSeq());
                        KLog.d("健康数据" + data);
                        finishHealth(progress,data80.getYear(), data80.getMonth(), data80.getDay(), i7BHisHealthData.getSeq(), from);
                        mHandler.removeCallbacks(sync80Timeout);
                        mHandler.postDelayed(sync80Timeout, 5000);
                    }
                });

            } else if (hisDataType == TYPE_HIS_GNSS) {
                mHandler.removeCallbacks(sync80Timeout);
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        ProtoBufHisGnssData gnssData = JsonTool.fromJson(data, ProtoBufHisGnssData.class);
                        KLog.d("gps数据" + data);
                        TB_62_data tb_62_data = new TB_62_data();
                        DateUtil dateUtil = new DateUtil(gnssData.getTime_stamp(), true);
                        tb_62_data.setYear(dateUtil.getYear());
                        tb_62_data.setMonth(dateUtil.getMonth());
                        tb_62_data.setDay(dateUtil.getDay());
                        tb_62_data.setData_from(from);
                        tb_62_data.setHour(dateUtil.getHour());
                        tb_62_data.setMin(dateUtil.getMinute());
                        tb_62_data.setTime(gnssData.getTime_stamp() * 1000L);
                        tb_62_data.setSeq(gnssData.getSeq());
                        if (gnssData.getFrequency() <= 0) {
                            tb_62_data.setFreq(1);
                        } else {
                            tb_62_data.setFreq(gnssData.getFrequency());
                        }
                        List<LongitudeAndLatitude> laData = new ArrayList<>();
                        for (ProtoBufHisGnssData.Gnss gnss : gnssData.getGnssList()) {
                            LongitudeAndLatitude la = new LongitudeAndLatitude();
                            la.setLatitude(gnss.getLatitude());
                            la.setLongitude(gnss.getLongitude());
                            la.setAltitude((int) gnss.getAltitude());
                            la.setGps_speed((int) gnss.getSpeed());
                            laData.add(la);
                        }
                        tb_62_data.setGnssData(JsonTool.toJson(laData));
                        int progress = ProtoBufSync.getInstance().currentProgress(ProtoBufSync.GNSS_DATA, gnssData.getSeq());
                        tb_62_data.saveOrUpdate("time=? and data_from=? and seq=?", tb_62_data.getTime() + "", gnssData.getSeq() + "", from);
                        //同步完一天的GPS数据
                        finishGps(dateUtil.getYear(), dateUtil.getMonth(), dateUtil.getDay(), progress);
                        mHandler.removeCallbacks(sync80Timeout);
                        mHandler.postDelayed(sync80Timeout, 5000);
                    }
                });

            } else if (hisDataType == TYPE_HIS_ECG || hisDataType == TYPE_HIS_PPG) {
                mHandler.removeCallbacks(sync80Timeout);
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        //ECG和PPG
                        ProtoBufHisEPGData protoBufHisEPGData = JsonTool.fromJson(data, ProtoBufHisEPGData.class);
                        //ECG
                        List<Integer> ecg_data = protoBufHisEPGData.getEcg_data();
                        List<Integer> ppg_data = protoBufHisEPGData.getPpg_data();
                        KLog.d("ecg和ppg数据");
                        if (ecg_data != null && ecg_data.size() > 0 && hisDataType == TYPE_HIS_ECG) {
                            KLog.d("ecg数据");
                            int[] ecgRealData = new int[ecg_data.size()];
                            for (int i = 0; i < ecg_data.size(); i++) {
                                ecgRealData[i] = ecg_data.get(i);
                            }
                            TB_64_data ecgData = new TB_64_data();
                            ecgData.setData_from(from);
                            ecgData.setSeq(protoBufHisEPGData.getSeq());
                            ecgData.setYear(protoBufHisEPGData.getYear());
                            ecgData.setMonth(protoBufHisEPGData.getMonth());
                            ecgData.setDay(protoBufHisEPGData.getDay());
                            ecgData.setHour(protoBufHisEPGData.getHour());
                            ecgData.setMin(protoBufHisEPGData.getMinute());
                            ecgData.setSecond(protoBufHisEPGData.getSecond());
                            DateUtil dateUtil = new DateUtil(ecgData.getYear(), ecgData.getMonth(), ecgData.getDay(), ecgData.getHour(), ecgData.getMin(), ecgData.getSecond());
                           Log.v("update========",""+dateUtil.getUnixTimestamp());
                            ecgData.setTime(dateUtil.getUnixTimestamp());
                            ecgData.setEcg(JsonTool.toJson(ecgRealData));
                            ecgData.saveOrUpdate("data_from=?  and year=? and month=? and day=? and hour=? and min=? and second=? and seq=?"
                                    , String.valueOf(from)
                                    , String.valueOf(ecgData.getYear())
                                    , String.valueOf(ecgData.getMonth())
                                    , String.valueOf(ecgData.getDay())
                                    , String.valueOf(ecgData.getHour())
                                    , String.valueOf(ecgData.getMin())
                                    , String.valueOf(ecgData.getSecond())
                                    , String.valueOf(ecgData.getSeq()));
                            int progress = ProtoBufSync.getInstance().currentProgress(ProtoBufSync.ECG_DATA, ecgData.getSeq());
                            finishECG(progress);
                        }
                        if (ppg_data != null && ppg_data.size() > 0 && hisDataType == TYPE_HIS_PPG) {
                            //PPG数据
                            KLog.d("ppg数据");
                        }
                        mHandler.removeCallbacks(sync80Timeout);
                        mHandler.postDelayed(sync80Timeout, 5000);
                    }
                });
            } else if (hisDataType == TYPE_HIS_RRI) {
                mHandler.removeCallbacks(sync80Timeout);
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        //房颤
                        //取高8位和低8位
                        ProtoBufHisRriData protoBufHisRriData = JsonTool.fromJson(data, ProtoBufHisRriData.class);
                        List<Integer> raw_data = protoBufHisRriData.getRaw_data();
                        if (raw_data != null && raw_data.size() > 0) {
                            int[] rriRealData = new int[raw_data.size() * 2];
                            for (int i = 0; i < raw_data.size(); i++) {
                                int ecg = raw_data.get(i);
                                short high = (short) ((ecg >> 16) & 0x0000ffff);
                                short low = (short) (ecg & 0x0000ffff);
                                rriRealData[i * 2] = high;
                                rriRealData[i * 2 + 1] = low;
                            }
                            DateUtil dateUtil = new DateUtil(protoBufHisRriData.getYear(), protoBufHisRriData.getMonth(),
                                    protoBufHisRriData.getDay(), protoBufHisRriData.getHour(), protoBufHisRriData.getMinute(), protoBufHisRriData.getSecond());
                            TB_rri_data rri_data = new TB_rri_data();
                            rri_data.setYear(protoBufHisRriData.getYear());
                            rri_data.setMonth(protoBufHisRriData.getMonth());
                            rri_data.setDay(protoBufHisRriData.getDay());
                            rri_data.setHour(protoBufHisRriData.getHour());
                            rri_data.setMinute(protoBufHisRriData.getMinute());
                            rri_data.setSecond(protoBufHisRriData.getSecond());
                            rri_data.setData_from(from);
                            rri_data.setRawData(JsonTool.toJson(rriRealData));
                            rri_data.setSeq(protoBufHisRriData.getSeq());
                            rri_data.setTimeStamp(protoBufHisRriData.getTimestamp());
                            rri_data.setDate(dateUtil.getSyyyyMMddDate());
                            rri_data.saveOrUpdate("data_from=?  and year=? and month=? and day=? and hour=? and minute=? and second=? and seq=?"
                                    , String.valueOf(from)
                                    , String.valueOf(rri_data.getYear())
                                    , String.valueOf(rri_data.getMonth())
                                    , String.valueOf(rri_data.getDay())
                                    , String.valueOf(rri_data.getHour())
                                    , String.valueOf(rri_data.getMinute())
                                    , String.valueOf(rri_data.getSecond())
                                    , String.valueOf(rri_data.getSeq()));
                            int progress = ProtoBufSync.getInstance().currentProgress(ProtoBufSync.RRI_DATA, protoBufHisRriData.getSeq());
                            finishRRI(progress);
                            com.socks.library.KLog.d(protoBufHisRriData.toString());

                        }
                        mHandler.removeCallbacks(sync80Timeout);
                        mHandler.postDelayed(sync80Timeout, 5000);
                    }
                });

            } else if (hisDataType == TYPE_HIS_SWIM) {
                mHandler.removeCallbacks(sync80Timeout);
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        ProtoBufSwimData protoBufSwimData = JsonTool.fromJson(data, ProtoBufSwimData.class);
                        int progress = ProtoBufSync.getInstance().currentProgress(ProtoBufSync.SWIM_DATA, protoBufSwimData.getSeq());
                        List<SwimDataBean> accGyroDataList = protoBufSwimData.getAccGyroDataList();
                        List<SwimDataBean> magDataList = protoBufSwimData.getMagDataList();
                        DateUtil dateUtil = new DateUtil(protoBufSwimData.getYear(), protoBufSwimData.getMonth(),
                                protoBufSwimData.getDay(), protoBufSwimData.getHour(), protoBufSwimData.getMinute(), protoBufSwimData.getSecond());
                        TB_swim_data tb_swim_data = new TB_swim_data();
                        tb_swim_data.setAcc(JsonUtils.toJson(accGyroDataList));
                        tb_swim_data.setMag(JsonUtils.toJson(magDataList));
                        tb_swim_data.setTime(protoBufSwimData.getTime());
                        tb_swim_data.setYear(protoBufSwimData.getYear());
                        tb_swim_data.setMonth(protoBufSwimData.getMonth());
                        tb_swim_data.setDay(protoBufSwimData.getDay());
                        tb_swim_data.setHour(protoBufSwimData.getHour());
                        tb_swim_data.setMinute(protoBufSwimData.getMinute());
                        tb_swim_data.setSecond(protoBufSwimData.getSecond());
                        tb_swim_data.setDataFrom(from);
                        tb_swim_data.setSeq(protoBufSwimData.getSeq());
                        tb_swim_data.setDate(dateUtil.getSyyyyMMddDate());
                        tb_swim_data.saveOrUpdate("dataFrom=?  and year=? and month=? and day=? and hour=? and minute=? and second=? and seq=?"
                                ,from
                                , String.valueOf(protoBufSwimData.getYear())
                                , String.valueOf(protoBufSwimData.getMonth())
                                , String.valueOf(protoBufSwimData.getDay())
                                , String.valueOf(protoBufSwimData.getHour())
                                , String.valueOf(protoBufSwimData.getMinute())
                                , String.valueOf(protoBufSwimData.getSecond())
                                , String.valueOf(protoBufSwimData.getSeq()));
                        if (progress >= 100) {
                            finalSwim(from,dateUtil);
                        }
//                        KLog.e(protoBufSwimData.toString());
                        mHandler.removeCallbacks(sync80Timeout);
                        mHandler.postDelayed(sync80Timeout, 5000);
                    }
                });

            }
        }
    }

    private static Runnable sync80Timeout = new Runnable() {
        @Override
        public void run() {
            final String dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
            writeMtkGps2TB(dataFrom, gpsDate[0], gpsDate[1], gpsDate[2], true);
            dispBp();
            clearTime();
            ProtoBufSync.getInstance().progressFinish();
        }
    };

    private static synchronized void timeChoose(ProtoBufHisHealthData i7BHisHealthData) {
        int sum = i7BHisHealthData.getYear() + i7BHisHealthData.getMonth() + i7BHisHealthData.getDay();
        int dateSum = date[0] + date[1] + date[2];
        if (dateSum == 0) {
            date[0] = i7BHisHealthData.getYear();
            date[1] = i7BHisHealthData.getMonth();
            date[2] = i7BHisHealthData.getDay();
        } else {
            if (dateSum != sum) {
                KLog.e("time_chanege", date[0] + "-" + date[1] + "-" + date[2]);
                //跨天了
                date[0] = i7BHisHealthData.getYear();
                date[1] = i7BHisHealthData.getMonth();
                date[2] = i7BHisHealthData.getDay();
            }
        }
    }


    private synchronized static void finishHealth(int progress, int year, int month, int day, int seq, String dataFrom) {
        KLog.d("sync-health:" + progress);
        if (progress >= 100) {
            //当天的同步完成
            if (year == 0 && month == 0 && day == 0) {
                return;
            }
            int mYear = year;
            int mMonth = month;
            int mDay = day;
            //最近两天的数据计算睡眠（Last two days' data calculation sleep）
            if(index == 2) {
                DateUtil dateUtil = new DateUtil();
                ProtoBufSleepSqlUtils.dispSleepData(dateUtil.getYear(), dateUtil.getMonth(), dateUtil.getDay());
            }
            pbToOther(mYear, mMonth, mDay);
        }
    }

    private static void pbToOther(int year, int month, int day) {
        KLog.i("no2855时间y:" + year + "m:" + month + "d:" + day);
        final String dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        List<ProtoBuf_80_data> data = DataSupport.where("year=? and month=? and day=? and data_from=?",
                year + "", month + "", day + "", dataFrom).order("seq asc").find(ProtoBuf_80_data.class);
        PbTolvHandler.pbDataToHeart(year, month, day, dataFrom, data);


        SportHandler.ble2SportAndWalkData(dataFrom,data);

        PbTolvHandler.pbToSpo2(data,dataFrom,testUid);
        PbTolvHandler.pbToTemper(data,dataFrom,testUid);

//        PbToIvHandler.pbFatigueDataToIv(uid, year, month, day, dataFrom, data);
//        PbToIvHandler.sportAnd51HeartDataToIv(uid, year, month, day, dataFrom, data);

//        PbToIvHandler.pbDataToBloodOxygen(uid, year, month, day, dataFrom, data);
//        PbToIvHandler.pbToMood(data,dataFrom,uid);
    }

    public static void clearTime() {
        date[0] = 0;
        date[1] = 0;
        date[2] = 0;
        gpsDate[0] = 0;
        gpsDate[1] = 0;
        gpsDate[2] = 0;
    }

    private static void parse90data(String data) {
        if (ProtoBufUpdate.getInstance().isUpdate()) {
            ArrayList<ProtoBufFileUpdateInfo> fileUpdateInfos = JsonTool.getListJson(data, ProtoBufFileUpdateInfo.class);
            if (fileUpdateInfos != null && fileUpdateInfos.size() > 0) {
                int gpsFont = ProtoBufUpdate.getInstance().getFuType();
                //DESC
                if (fileUpdateInfos.size() > 1) {
                    for (int i = 0; i < fileUpdateInfos.size(); i++) {
                        int fuType = fileUpdateInfos.get(i).getFuType();
                        if (fuType == gpsFont) {
                            ProtoBufUpdate.getInstance().updateDetail(fileUpdateInfos.get(i).getType(), fileUpdateInfos.get(i));
                        }
                    }
                } else {//INIT DATA
                    ProtoBufUpdate.getInstance().updateDetail(fileUpdateInfos.get(0).getType(), fileUpdateInfos.get(0));
                    KLog.e("yanxi------>>> aGPS ---数据" + fileUpdateInfos.get(0).getType());
                }
            } else {
                ProtoBufUpdate.getInstance().setUpdate(false);
            }
        }
    }


    private static void parseFFData(Context context, String data) {
        //保存MTU信息
        ConnectParmas connectParmas = JsonTool.fromJson(data, ConnectParmas.class);
        PrefUtil.save(context, BaseActionUtils.PROTOBUF_MTU_INFO, connectParmas.getMtu());
    }

    /**
     * 处理protobuf血压入表
     */
    private static void dispBp() {
        final String from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        List<ProtoBuf_80_data> bp = DataSupport.where("" +
                "data_from=? and dbp>0 and dbp< 1000 and sbp>0 and sbp<1000", from).find(ProtoBuf_80_data.class);
        if (bp != null) {

            for (ProtoBuf_80_data data : bp) {
                TB_BP_data tb_bp_data = new TB_BP_data();
                tb_bp_data.setBpTime(data.getTime());
                tb_bp_data.setDataFrom(from);
                tb_bp_data.setDbp(data.getDbp());
                tb_bp_data.setSbp(data.getSbp());
                tb_bp_data.saveOrUpdate("dataFrom=? and bpTime=?", from, data.getTime() + "");
            }
        }

    }


    private synchronized static void finishECG(int progress) {
        if (progress >= 100) {
//            KLog.e("ecg完成一天");
        }
    }

    /**
     * gps数据转为坐标并写入文件
     */
    public static void writeMtkGps2TB(String dataFrom, int year, int month, int day, boolean isP1MIni) {
        KLog.e("no2855--> mtk_statues。时间: " + year + "-" + month + "-" + day);
        List<TB_62_data> list = DataSupport.where("data_from=? and year=? and month=? and day=?",
                dataFrom, year + "", month + "", day + "")
                .order("time asc").find(TB_62_data.class);
        if (list != null && list.size() > 0) {
            KLog.e("no2855--> list.size: " + list.size());
            Iterator<TB_62_data> iterator = list.iterator();
            while (iterator.hasNext()) {
                TB_62_data data = iterator.next();
                List<LongitudeAndLatitude> latitudes = JsonTool.getListJson(data.getGnssData(), LongitudeAndLatitude.class);
                if (latitudes != null && latitudes.size() > 0) {
                    KLog.e("no2855--> latitudes解析成功 " + latitudes.size());
                    long startTime = data.getTime() / 1000;
                    int num = 0;
                    int value;
                    if (isP1MIni) {
                        value = data.getFreq() < 1 ? 1 : data.getFreq();
                    } else {
                        value = 60 / latitudes.size();
                    }
                    for (LongitudeAndLatitude latitude : latitudes) {
                        TB_blue_gps blue_gps = new TB_blue_gps();
                        blue_gps.setData_from(dataFrom);
                        long mTime = startTime + num * value;
                        blue_gps.setTime(mTime);
                        blue_gps.setLat(latitude.getLatitude());
                        blue_gps.setLon(latitude.getLongitude());
                        blue_gps.saveOrUpdate("data_from=? and time=?", dataFrom, mTime + "");
//                        KLog.e("no2855--> blue_gps入表成功 "+latitude.getLatitude()+" - "+latitude.getLongitude());
                        num++;
                    }
                }
            }

        }
    }

    private synchronized static void finishRRI(int progress) {
        if (progress >= 100) {
            com.socks.library.KLog.e("更新RR1信息一条");
        }
    }

    private synchronized static void finishGps(int progress, int year, int month, int day) {
        if (progress == 100) {
            if (year == 0 && month == 0 && day == 0) {
                return;
            }
            KLog.d("同步GPS结束一条");
            //同步完一天的数据
            final String from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
            writeMtkGps2TB(from, year, month, day, true);
        }
    }

    private static void writeToFile(List<SwimDataBean> swimDataBeans,String type,TB_swim_data tb_swim_data) {
        int time = tb_swim_data.getTime();
        DateUtil dateUtil = new DateUtil(time, true);
        for (SwimDataBean bean : swimDataBeans) {
            SwimBean swimBean = new SwimBean();
            swimBean.setSeq(tb_swim_data.getSeq());
            swimBean.setTime((int) dateUtil.getUnixTimestamp());
            swimBean.setX(bean.getX());
            swimBean.setY(bean.getY());
            swimBean.setZ(bean.getZ());
            swimBean.setType(type);
            writeFile(type,dateUtil.getY_M_D(),getContent(swimBean));
        }

    }

    private static void writeFile(final String type, final String date, final String content) {
        SingleThreadUtil.getLogSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                write2SDFromString("Zeroner/zeroner_3/swim/" + type + "/", "swim - " + type + "-" + date + ".txt",
                        content);
            }
        });
    }

    private static String getContent(SwimBean bean) {
        return bean.getX() + "," + bean.getY() + "," + bean.getZ() + "," + bean.getTime() + "," + bean.getSeq();
    }

    @SuppressLint("CheckResult")
    private static void finalSwim(final String from,final DateUtil dateUtil) {

        Observable.create(new ObservableOnSubscribe<List<TB_swim_data>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TB_swim_data>> emitter) throws Exception {
                List<TB_swim_data> tb_swim_data = DataSupport.where("dataFrom=? and date=?", from,dateUtil.getSyyyyMMddDate()).order("seq asc").find(TB_swim_data.class);
                emitter.onNext(tb_swim_data);
            }
        })
                .concatMap(new Function<List<TB_swim_data>, ObservableSource<TB_swim_data>>() {
                    @Override
                    public ObservableSource<TB_swim_data> apply(List<TB_swim_data> tb_swim_data) throws Exception {
                        return Observable.fromIterable(tb_swim_data);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        KLog.i("finish");
                    }
                })
                .subscribe(new Observer<TB_swim_data>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TB_swim_data s) {
                        String acc = s.getAcc();
                        String mag = s.getMag();
                        ArrayList<SwimDataBean> accList = JsonUtils.getListJson(acc, SwimDataBean.class);
                        ArrayList<SwimDataBean> magList = JsonUtils.getListJson(mag, SwimDataBean.class);
                        writeToFile(accList,"ACC",s);
                        writeToFile(magList,"MAG",s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.d(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
