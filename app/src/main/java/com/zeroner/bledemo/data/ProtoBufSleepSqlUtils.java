package com.zeroner.bledemo.data;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.SystemClock;

import com.blankj.utilcode.util.LogUtils;
import com.iwown.app.nativeinvoke.NativeInvoker;
import com.iwown.app.nativeinvoke.SA_SleepBufInfo;
import com.iwown.app.nativeinvoke.SA_SleepDataInfo;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.SleepBean;
import com.zeroner.bledemo.bean.SleepProtoData;
import com.zeroner.bledemo.bean.sql.File_protobuf_80data;
import com.zeroner.bledemo.bean.sql.ProtoBuf_80_data;
import com.zeroner.bledemo.bean.sql.ProtoBuf_index_80;
import com.zeroner.bledemo.bean.sql.TB_SLEEP_Final_DATA;
import com.zeroner.bledemo.bean.sql.TB_rri_data;
import com.zeroner.bledemo.data.sync.ComplexPropertyPreFilter;
import com.zeroner.bledemo.sleep.SleepScoreHandler;
import com.zeroner.bledemo.sleep.SleepSegment;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.FileUtils;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.utils.JsonTool;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.zeroner.blemidautumn.utils.Util.write2SDFromString;


/**
 * @author 睡眠数据入表
 */
public class ProtoBufSleepSqlUtils {

    private static long testUid=12345;

    private static String dataFrom = "";
    private static String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final int DELAY = 500;


    public static void dispSleepData() {
        dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        List<ProtoBuf_index_80> protoBuf_index_80s = DataSupport.where("data_from=? and indexType=0", dataFrom).find(ProtoBuf_index_80.class);
        Set<SleepBean> treeSet = new TreeSet<>();
        for (ProtoBuf_index_80 index_80 : protoBuf_index_80s) {
            SleepBean bean = new SleepBean(index_80.getYear(), index_80.getMonth(), index_80.getDay());
            treeSet.add(bean);
        }
        LinkedList<SleepBean> sleepBeanList = new LinkedList<>(treeSet);
        disAndWriteData(sleepBeanList);

    }


    @SuppressLint("CheckResult")
    private static void disAndWriteData(final LinkedList<SleepBean> sortTables) {
        if(sortTables==null || sortTables.size()==0){
            return;
        }
        SleepBean sleepBean = sortTables.getFirst();
        Observable
                .just(sleepBean)
                .map(new Function<SleepBean, List<ProtoBuf_80_data>>() {
                    @Override
                    public List<ProtoBuf_80_data> apply(SleepBean sleepBean) throws Exception {
                        DateUtil dateUtil = new DateUtil(sleepBean.getYear(), sleepBean.getMonth(), sleepBean.getDay());
                        TB_SLEEP_Final_DATA tb_sleep_final_data = DataSupport.where("data_from=? and date=?",
                                dataFrom, dateUtil.getSyyyyMMddDate())
                                .findFirst(TB_SLEEP_Final_DATA.class);

                        List<ProtoBuf_80_data> index80s = new ArrayList<>();
                        if (tb_sleep_final_data == null || dateUtil.isToday()) {
                            index80s = DataSupport.where("year=? and month=? and day=? and data_from=?",
                                    dateUtil.getYear() + "", dateUtil.getMonth() + "", dateUtil.getDay() + "", dataFrom).
                                    order("seq asc").find(ProtoBuf_80_data.class);
                            return index80s;
                        }

                        return index80s;
                    }
                })
                .doAfterNext(new Consumer<List<ProtoBuf_80_data>>() {
                    @Override
                    public void accept(List<ProtoBuf_80_data> protoBuf80Data) throws Exception {
                        if(protoBuf80Data == null || protoBuf80Data.size() == 0){
                            SystemClock.sleep(DELAY);
                            sortTables.removeFirst();
                            disAndWriteData(sortTables);
                        }
                    }
                })
                .filter(new Predicate<List<ProtoBuf_80_data>>() {
                    @Override
                    public boolean test(List<ProtoBuf_80_data> index80s) throws Exception {
                        return index80s != null && index80s.size() > 0;
                    }
                })
                .map(new Function<List<ProtoBuf_80_data>, String>() {
                    @Override
                    public String apply(List<ProtoBuf_80_data> index80s) throws Exception {
                        //解析某一天的数据成json
                        String s = filterJson(index80s);
                        DateUtil dateUtil = new DateUtil(index80s.get(0).getYear(), index80s.get(0).getMonth(), index80s.get(0).getDay());
                        //writeSleep(data, s, dataFrom);
                        String[] twoDaysPath = getTwoDaysPath(dateUtil);
                        String[] pathAndName = getPathAndName(dateUtil);

                        write2SDFromString(pathAndName[0], pathAndName[1], s, false);

                        /**
                         * 计算睡眠数据
                         */
                        SA_SleepBufInfo sleep = getSleep(twoDaysPath[0], twoDaysPath[1], dateUtil, dataFrom);
                        SystemClock.sleep(DELAY);
                        String s1 = JsonUtils.toJson(sleep);
                        LogUtils.i(s1);
                        return s1;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        SystemClock.sleep(DELAY);
                        sortTables.removeFirst();
                        disAndWriteData(sortTables);
                    }
                });

    }

    /**
     * @param dateUtil
     * @return string[] 0 昨天 1 今天
     */
    private static String[] getTwoDaysPath(DateUtil dateUtil) {
        String[] path = new String[2];
        DateUtil yesDateUtil = new DateUtil(dateUtil.getUnixTimestamp(), true);
        yesDateUtil.addDay(-1);
        String rootPath = BaseActionUtils.FilePath.ProtoBuf_Ble_80_Sleep_Dir + dateUtil.getSyyyyMMddDate() + "/";
        String fileName = "uid-" + testUid + "-date-" + dateUtil.getSyyyyMMddDate() + "-source-" + dataFrom + ".json";
        String yesRootPath = BaseActionUtils.FilePath.ProtoBuf_Ble_80_Sleep_Dir + yesDateUtil.getSyyyyMMddDate() + "/";
        String yesFileName = "uid-" + testUid + "-date-" + yesDateUtil.getSyyyyMMddDate() + "-source-" + dataFrom + ".json";
        path[0] = yesRootPath + yesFileName;
        path[1] = rootPath + fileName;
        return path;
    }

    private static String[] getPathAndName(DateUtil dateUtil) {
        String[] path = new String[2];
        String rootPath = BaseActionUtils.FilePath.ProtoBuf_Ble_80_Sleep_Dir + dateUtil.getSyyyyMMddDate() + "/";
        String fileName = "uid-" + testUid + "-date-" + dateUtil.getSyyyyMMddDate() + "-source-" + dataFrom + ".json";
        path[0] = rootPath;
        path[1] = fileName;
        return path;
    }

    //将数据过滤成json
    private static String filterJson(List<ProtoBuf_80_data> data) {
        //保存到本地
        List<File_protobuf_80data> protobufLists = new ArrayList<>();
        for (ProtoBuf_80_data index : data) {
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

            protobufLists.add(file_protobuf_80data);

        }


        //解析
        ComplexPropertyPreFilter filter = new ComplexPropertyPreFilter();
        Map<Class<?>, String[]> includes = new HashMap<>();
        Map<Class<?>, String[]> excludes = new HashMap<>();
        excludes.put(File_protobuf_80data.Pedo.class, new String[]{"t", "a", "c", "s", "d"});
        excludes.put(File_protobuf_80data.HeartRate.class, new String[]{"n", "x", "a"});
        excludes.put(File_protobuf_80data.HRV.class, new String[]{"s", "r", "p", "m", "f"});
        excludes.put(File_protobuf_80data.Sleep.class, new String[]{"a", "c", "s"});
        includes.put(File_protobuf_80data.class, new String[]{"Q", "T", "E", "H", "P", "V"});
        filter.setExcludes(excludes);
        filter.setIncludes(includes);
        return JsonTool.toJson(protobufLists, filter);
    }

    @SuppressLint("CheckResult")
    public static void dispSleepData(final int year, final int month, final int day) {
        //解析本地睡眠表上传睡眠数据
        //查询数据库
        /**
         * 通过年月日计算睡眠数据
         */
        dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";

        Observable.create(new ObservableOnSubscribe<SleepProtoData>() {
            @Override
            public void subscribe(ObservableEmitter<SleepProtoData> emitter) throws Exception {
                List<ProtoBuf_80_data> index80s = DataSupport.where("year=? and month=? and day=? and data_from=?",
                        year + "", month + "", day + "", dataFrom + "")
                        .order("seq asc").find(ProtoBuf_80_data.class);

                DateUtil dateUtil = new DateUtil(year,month,day);
                dateUtil.addDay(-1);
                List<ProtoBuf_80_data> yes80 = DataSupport.where("year=? and month=? and day=? and data_from=?",
                        dateUtil.getYear() + "", dateUtil.getMonth() + "", dateUtil.getDay() + "", dataFrom + "")
                        .order("seq asc").find(ProtoBuf_80_data.class);

                SleepProtoData sleepProtoData = new SleepProtoData();
                sleepProtoData.setTodayList(index80s);
                sleepProtoData.setYesterdayList(yes80);
                sleepProtoData.setYesDate(dateUtil);
                emitter.onNext(sleepProtoData);
            }
        })
                .filter(new Predicate<SleepProtoData>() {
                    @Override
                    public boolean test(SleepProtoData sleepProtoData) throws Exception {
                        return sleepProtoData.hasData();
                    }
                })
                .map(new Function<SleepProtoData, String>() {
                    @Override
                    public String apply(SleepProtoData sleepProtoData) throws Exception {
                        String s = filterJson(sleepProtoData.getTodayList());
                        DateUtil dateUtil = new DateUtil(year, month, day);
                        String[] twoDaysPath = getTwoDaysPath(dateUtil);
                        String[] pathAndName = getPathAndName(dateUtil);
                        //写睡眠数据到本地
                        write2SDFromString(pathAndName[0], pathAndName[1], s, false);
                        if(sleepProtoData.getYesterdayList()!=null && sleepProtoData.getYesterdayList().size()>0){
                            String yesS = filterJson(sleepProtoData.getYesterdayList());
                            String[] yesPathAndName = getPathAndName(sleepProtoData.getYesDate());
                            write2SDFromString(yesPathAndName[0], yesPathAndName[1], yesS, false);
                        }
                        /**
                         * 计算睡眠数据
                         */
                        SA_SleepBufInfo sleep = getSleep(twoDaysPath[0],twoDaysPath[1],dateUtil, dataFrom);
                        String s1 = JsonTool.toJson(sleep);
                        return s1;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtils.i(s);
                    }
                });

    }


    /**
     * 将睡眠数据入本地表
     *
     * @param f1SleepData
     * @param date
     */
    public static void localSleepDataToSleepFinal(SA_SleepBufInfo f1SleepData, String date) {
        if (null != f1SleepData.sleepdata) {
            if (f1SleepData.datastatus != 0 && f1SleepData.datastatus != 1) {
                return;
            }
            List<SleepSegment> segList = new ArrayList<>();
            SA_SleepDataInfo[] sleepData = f1SleepData.sleepdata;
            if (sleepData.length <= 0) {
                return;
            }
            int totalDeep = 0;
            int totalLight = 0;
            int totalWakeUp = 0;
            int totalEye = 0;
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
                } else if (sleepType == 7) {
                    totalEye += activity;
                }

            }
            DateUtil startDateUtil = new DateUtil(f1SleepData.inSleepTime.year + 2000, f1SleepData.inSleepTime.month, f1SleepData.inSleepTime.day, f1SleepData.inSleepTime.hour, f1SleepData.inSleepTime.minute);
            final DateUtil endDateUtil = new DateUtil(f1SleepData.outSleepTime.year + 2000, f1SleepData.outSleepTime.month, f1SleepData.outSleepTime.day, f1SleepData.outSleepTime.hour, f1SleepData.outSleepTime.minute);

            TB_SLEEP_Final_DATA sleepDataByDate1 = getSleepDataByDate(date);
            if (sleepDataByDate1 == null) {
                sleepDataByDate1 = new TB_SLEEP_Final_DATA();
                sleepDataByDate1.setToDefault("_uploaded");
                sleepDataByDate1.setData_from(dataFrom);
                sleepDataByDate1.setDate(date);
            }

            try {
                if(totalEye == 0 && sleepDataByDate1.getEye_move_time()!=0){
                    return;
                }
                if(totalLight > 13*60 || totalDeep > 10*60 || totalWakeUp > 6*60){
                    return;
                }

                int score = SleepScoreHandler.calSleepScore((totalDeep + totalLight + totalWakeUp), totalDeep, startDateUtil.getUnixTimestamp());
                sleepDataByDate1.setYear(startDateUtil.getYear());
                sleepDataByDate1.setMonth(startDateUtil.getMonth());
                sleepDataByDate1.setStart_time(startDateUtil.getUnixTimestamp());
                sleepDataByDate1.setEnd_time(endDateUtil.getUnixTimestamp());
                sleepDataByDate1.setDeepSleepTime(totalDeep);
                sleepDataByDate1.setEye_move_time(totalEye);
                sleepDataByDate1.setLightSleepTime(totalLight);
                sleepDataByDate1.setScore(score);
                sleepDataByDate1.setSleep_segment(JsonUtils.toJson(segList));
                sleepDataByDate1.saveAsync().listen(new SaveCallback() {
                    @Override
                    public void onFinish(boolean success) {
                        if (endDateUtil.isToday()) {
                            //notify
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static TB_SLEEP_Final_DATA getSleepDataByDate(String date) {
        return DataSupport.where("date =? and data_from=?",
                date, dataFrom).findFirst(TB_SLEEP_Final_DATA.class);
    }

    private static SA_SleepBufInfo getSleep(String yesDatePath, String toDatePath, DateUtil dateUtil, final String dataFrom) {
        try {
            SA_SleepBufInfo retData = new SA_SleepBufInfo();
            if (FileUtils.checkFileExists(toDatePath)) {
                NativeInvoker jni = new NativeInvoker();
                DateUtil yesDateUtil = new DateUtil(dateUtil.getUnixTimestamp(), true);
                yesDateUtil.addDay(-1);
                double[] rriData = getRriData(0, dateUtil.getSyyyyMMddDate(), dataFrom);
                double[] yesRriData = getRriData(0, yesDateUtil.getSyyyyMMddDate(), dataFrom);
                LogUtils.i(JsonUtils.toJson(rriData));
                LogUtils.i(rriData.length + "---" + yesRriData.length + "-----" +  dateUtil.getY_M_D());
                int status = jni.calculateSleepFileWithAF(rootPath + yesDatePath, rootPath + toDatePath, dateUtil.getSyyyyMMddDate(), 1, yesRriData, rriData, retData);
                retData.datastatus = status;
                /**
                 * 本地入表
                 */
                localSleepDataToSleepFinal(retData, dateUtil.getSyyyyMMddDate());
                return retData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static List<ProtoBuf_index_80> sort(List<ProtoBuf_index_80> index80s) {
        //排序
        Collections.sort(index80s, new Comparator<ProtoBuf_index_80>() {
            @Override
            public int compare(ProtoBuf_index_80 index1, ProtoBuf_index_80 index2) {
                int i = index1.getYear() * 380 + index1.getMonth() * 31 + index1.getDay();
                int i2 = index2.getYear() * 380 + index2.getMonth() * 31 + index2.getDay();
                if (i > i2) {
                    return 1;
                } else if (i == i2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return index80s;
    }

    private static double[] getRriData(long uid, String date, String dataFrom) {
        //rri need uid please add uid
        List<Integer> list = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();
        //查询数据库
        String seqMsg ="data_from=? and date=?";
        List<TB_rri_data> tbRriData = DataSupport.where(seqMsg, dataFrom, date).order("seq asc").find(TB_rri_data.class);

        if(tbRriData == null || tbRriData.size() == 0){
            return new double[0];
        }
        for (TB_rri_data rriData:tbRriData) {
            List<Integer> listJson = JsonUtils.getListJson(rriData.getRawData(), Integer.class);
            list.addAll(listJson);
        }


        for (int i = 0 ; i < list.size();i++){

            if(i == 0){
                list2.add(Double.valueOf(list.get(0)));
            }else{
                int last = list.get(i - 1);
                int current = list.get(i);
                if(last == -1 && current == -1){
                }else{
                    list2.add((double) current);
                }
            }

        }

        double[] rri = new double[list2.size()];
        for (int i = 0 ;i < list2.size(); i++) {
            rri[i] = list2.get(i);
        }



        return rri;

    }



}
