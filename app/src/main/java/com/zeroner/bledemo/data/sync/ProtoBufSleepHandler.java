package com.zeroner.bledemo.data.sync;

import android.os.Environment;
import android.util.Log;

import com.iwown.app.nativeinvoke.NativeInvoker;
import com.iwown.app.nativeinvoke.SA_SleepBufInfo;
import com.iwown.app.nativeinvoke.SA_SleepDataInfo;
import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.File_protobuf_80data;
import com.zeroner.bledemo.bean.sql.ProtoBuf_80_data;
import com.zeroner.bledemo.bean.sql.ProtoBuf_index_80;
import com.zeroner.bledemo.bean.sql.TB_SLEEP_Final_DATA;
import com.zeroner.bledemo.sleep.SleepScoreHandler;
import com.zeroner.bledemo.sleep.SleepSegment;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.FileUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.utils.JsonTool;
import com.zeroner.blemidautumn.utils.SingleThreadUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.zeroner.blemidautumn.utils.Util.write2SDFromString;

public class ProtoBufSleepHandler {


    public static void dispSleepData(List<ProtoBuf_index_80> indexTables){
        //解析本地睡眠表上传睡眠数据
        //查询数据库
        String data_from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        List<Integer> years = new ArrayList<>();
        List<Integer> months = new ArrayList<>();
        List<Integer> days = new ArrayList<>();
//        indexTables = DataSupport.where("device_name=?", data_from).find(ProtoBuf_index_80.class);
//        indexTables = DataSupport.findAll(ProtoBuf_index_80.class);
        if (indexTables == null) {
            return;
        }
        for (ProtoBuf_index_80 index_80 : indexTables) {
            if (!years.contains(index_80.getYear()) || !months.contains(index_80.getMonth()) || !days.contains(index_80.getDay())) {
                if (index_80.getIndexType() == ProtoBufSync.HEALTH_DATA) {
                    years.add(index_80.getYear());
                    months.add(index_80.getMonth());
                    days.add(index_80.getDay());
                }
            }
        }

        //
        for (int i = 0; i < years.size(); i++) {
            Log.e("yanxi---msg", "yanxi--"+ years.get(i) + "-" + months.get(i) + "-" + days.get(i));
            List<ProtoBuf_80_data> index_80s = DataSupport.where("year=? and month=? and day=? and data_from=?", years.get(i) + "", months.get(i) + "", days.get(i) + "", data_from).order("seq asc").find(ProtoBuf_80_data.class);
            if (index_80s != null && index_80s.size() > 0) {
                //保存到本地
                List<File_protobuf_80data> protobuf_Lists = new ArrayList<>();
                for (ProtoBuf_80_data index : index_80s) {
                    File_protobuf_80data file_protobuf_80data = new File_protobuf_80data();

                    File_protobuf_80data.Sleep sleep = new File_protobuf_80data.Sleep();
                    sleep.setA(JsonTool.fromJson(index.getSleepData(), int[].class));
                    if (index.isShutdown()) {
                        sleep.setS(1);
                    } else {
                        sleep.setS(0);
                    }
                    if (index.isCharge()) {
                        sleep.setC(1);
                    } else {
                        sleep.setC(0);
                    }


                    File_protobuf_80data.HeartRate heartRate = new File_protobuf_80data.HeartRate();
                    heartRate.setX(index.getMax_bpm());
                    heartRate.setN(index.getMin_bpm());
                    heartRate.setA(index.getAvg_bpm());

                    File_protobuf_80data.HRV hrv = new File_protobuf_80data.HRV();
                    hrv.setS(index.getSDNN());
                    hrv.setR(index.getRMSSD());
                    hrv.setP(index.getPNN50());
                    hrv.setM(index.getMEAN());
                    hrv.setF(index.getFatigue());

                    File_protobuf_80data.Pedo pedo = new File_protobuf_80data.Pedo();
                    pedo.setS(index.getStep());
                    pedo.setD((int) index.getDistance());
                    pedo.setC(index.getCalorie());
                    pedo.setT(index.getType());
                    pedo.setA(index.getState());

                    file_protobuf_80data.setQ(index.getSeq());
                    file_protobuf_80data.setT(file_protobuf_80data.parseTime(index.getHour(), index.getMinute()));
                    file_protobuf_80data.setE(sleep);
                    file_protobuf_80data.setP(pedo);
                    file_protobuf_80data.setH(heartRate);
                    file_protobuf_80data.setV(hrv);

                    protobuf_Lists.add(file_protobuf_80data);

                }

                String data = new DateUtil(years.get(i), months.get(i), days.get(i)).getSyyyyMMddDate();
                //解析
                ComplexPropertyPreFilter filter = new ComplexPropertyPreFilter();
                Map<Class<?>, String[]> includes = new HashMap<>();
                Map<Class<?>, String[]> excludes = new HashMap<>();
                excludes.put(File_protobuf_80data.Pedo.class, new String[]{"t", "a", "c", "s", "d"});
                excludes.put(File_protobuf_80data.HeartRate.class, new String[]{"n", "x", "a"});
                excludes.put(File_protobuf_80data.HRV.class, new String[]{"s", "r", "p", "m", "f"});
                excludes.put(File_protobuf_80data.Sleep.class, new String[]{"a","c", "s"});
                includes.put(File_protobuf_80data.class, new String[]{"Q", "T", "E", "H", "P","V"});
                filter.setExcludes(excludes);
                filter.setIncludes(includes);
                String s = JsonTool.toJson(protobuf_Lists,filter);
                disposeSleep(data, s, data_from);
            }
        }
    }


    public static void localSleepDataToSleepFinal(SA_SleepBufInfo f1SleepData, String date) {
        KLog.i("==========mtkLocalSleepDataToSleepFinal==========" + JsonTool.toJson(f1SleepData));
        if (null != f1SleepData.sleepdata) {
            if (f1SleepData.datastatus != 0 && f1SleepData.datastatus != 1) {
                return;
            }
//            if (f1SleepData.completeFlag != 1) {
//                return;
//            }
            List<SleepSegment> segList = new ArrayList<>();
            SA_SleepDataInfo[] sleepData = f1SleepData.sleepdata;
            if (sleepData.length <= 0) {
                return;
            }
            int totalDeep = 0;
            int totalLight = 0;
            int totalWakeUp = 0;
            SleepSegment tampSeg = new SleepSegment();
            for (int i = 0; i < sleepData.length; i++) {
                SA_SleepDataInfo bean = sleepData[i];
                SleepSegment segment = new SleepSegment();
                int start = bean.startTime.hour * 60 + bean.startTime.minute;
                int end = bean.stopTime.hour * 60 + bean.stopTime.minute;
                int activity = 0;
                if (start <= end) {
                    activity = end - start;
                } else {
                    activity = end + 1440 - start;
                }
                KLog.e(start + "===no2855-->=start" + end + "end" + activity + "activity=====");
                if (i == 0) {
                    segment.setSt(0);
                    segment.setEt(activity);
                    segment.setType(bean.sleepMode);
                    tampSeg = segment;
                    segList.add(0, segment);
                } else if (i > 0) {
                    segment.setSt(tampSeg.getEt());
                    segment.setEt(tampSeg.getEt() + activity);
                    segment.setType(bean.sleepMode);
                    segList.add(segment);
                    tampSeg = segment;
                }

                //睡眠类型
                int sleepType = bean.sleepMode;
                if (sleepType == 3) {
                    totalDeep += activity;
                } else if (sleepType == 4) {
                    totalLight += activity;
                } else if (sleepType == 6) {
                    totalWakeUp += activity;
                }

            }
            DateUtil startDateUtil = new DateUtil(f1SleepData.inSleepTime.year + 2000, f1SleepData.inSleepTime.month, f1SleepData.inSleepTime.day, f1SleepData.inSleepTime.hour, f1SleepData.inSleepTime.minute);
            DateUtil endDateUtil = new DateUtil(f1SleepData.outSleepTime.year + 2000, f1SleepData.outSleepTime.month, f1SleepData.outSleepTime.day, f1SleepData.outSleepTime.hour, f1SleepData.outSleepTime.minute);

            TB_SLEEP_Final_DATA sleepDataByDate1 = getSleepDataByDate(date);
            if (sleepDataByDate1 == null) {
                sleepDataByDate1 = new TB_SLEEP_Final_DATA();
                sleepDataByDate1.setToDefault("_uploaded");
                sleepDataByDate1.setData_from(PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "");
                sleepDataByDate1.setDate(date);
            }

            try {
                int score = SleepScoreHandler.calSleepScore((totalDeep + totalLight + totalWakeUp), totalDeep, startDateUtil.getUnixTimestamp());
                sleepDataByDate1.setYear(startDateUtil.getYear());
                sleepDataByDate1.setMonth(startDateUtil.getMonth());
                sleepDataByDate1.setStart_time(startDateUtil.getUnixTimestamp());
                sleepDataByDate1.setEnd_time(endDateUtil.getUnixTimestamp());
                sleepDataByDate1.setDeepSleepTime(totalDeep);
                sleepDataByDate1.setLightSleepTime(totalLight);
                sleepDataByDate1.setScore(score);
                sleepDataByDate1.setSleep_segment(JsonTool.toJson(segList));
                sleepDataByDate1.saveAsync().listen(new SaveCallback() {
                    @Override
                    public void onFinish(boolean success) {
//                        EventBus.getDefault().post(new ViewRefresh(false,0x61));
//                        KLog.d("testSleep","no2855-->testSleep睡眠保存并指出来: ");
//                        HealthDataEventBus.updateHealthSleepEvent();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static TB_SLEEP_Final_DATA getSleepDataByDate(String date) {
        return DataSupport.where("date =? and data_from=?",
                date, PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "").findFirst(TB_SLEEP_Final_DATA.class);
    }

    public static SA_SleepBufInfo getSleep(String date, final String data_from) {
        try {
            SA_SleepBufInfo retData = new SA_SleepBufInfo();
            String path = BaseActionUtils.FilePath.ProtoBuf_Ble_Data_Log_Dir + date + "/uid-12345-" + "date-" + date + "-source-" + data_from + ".json";
            com.zeroner.blemidautumn.library.KLog.d("testSleep", "testSleep睡眠：" + path + "   存在？" + FileUtils.checkFileExists(path));
            if (FileUtils.checkFileExists(path)) {
                File rootDir = Environment.getExternalStorageDirectory();
                NativeInvoker jni = new NativeInvoker();
                int status = jni.calculateSleep(rootDir.getAbsolutePath() + BaseActionUtils.FilePath.ProtoBuf_Ble_Data_Log_Dir, 12345, date, data_from, 1, retData);
                retData.datastatus = status;
                com.zeroner.blemidautumn.library.KLog.d("testSleep", "testSleep: " + JsonTool.toJson(retData));
                localSleepDataToSleepFinal(retData,date);
                return retData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void disposeSleep(final String data, final String msg, final String data_from) {
        Future<String> result = SingleThreadUtil.getLogSingleThread().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    write2SDFromString(BaseActionUtils.FilePath.ProtoBuf_Ble_Data_Log_Dir + data + "/", "uid-12345-" + "date-" + data + "-source-" + data_from + ".json",
                            msg, false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }, "SUCCESS");

        try {
            if("SUCCESS".equals(result.get())){
                //转换数据
                SA_SleepBufInfo sleep = getSleep(data, data_from);
                String s1 = JsonTool.toJson(sleep);
                Log.e("testSleep", s1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
