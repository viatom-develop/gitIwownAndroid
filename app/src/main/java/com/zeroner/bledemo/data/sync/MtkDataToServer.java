package com.zeroner.bledemo.data.sync;

import android.content.ContentValues;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;


import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.TB_61_data;
import com.zeroner.bledemo.bean.sql.TB_62_data;
import com.zeroner.bledemo.bean.sql.TB_sum_61_62_64;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.FileUtils;
import com.zeroner.bledemo.utils.PrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 作者：hzy on 2018/3/26 10:05
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class MtkDataToServer {
    private static boolean isTwo;
    private static android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    public static long Fictitious_Uid = 10087;

    public static void upCmdToServer(){
        isTwo=true;
        List<TB_sum_61_62_64> httpList = DataSupport.findAll(TB_sum_61_62_64.class);
        Set<String> set =new HashSet<>();
        final String deviceName= PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        for(int i=0;i<httpList.size();i++){
            String date=httpList.get(i).getDate();
            String log_file=BaseActionUtils.FilePath.Mtk_Ble_61_Data_Log_Dir + BleApplication.getInstance().getString(R.string.file_61_name_format, Fictitious_Uid+"", date, deviceName) + ".txt";
            FileUtils.clearInfoForFile(log_file,BaseActionUtils.FilePath.Mtk_Ble_61_Data_Log_Dir);
            if(FileUtils.checkFileExists(log_file)){
                FileUtils.deleteFile(log_file);
            }


            String sd1=  BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir+date+"/";
            String fileName= BleApplication.getInstance().getString(R.string.file_61_name_format, Fictitious_Uid+"", date, deviceName);
            FileUtils.clearInfoForFile(sd1+fileName,BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir);
            if(FileUtils.checkFileExists(sd1+fileName)){
                FileUtils.deleteFile(sd1+fileName);
            }
            set.add(date);
            List<TB_61_data> list61= DataSupport.where("data_from=? and year=? and month=? and day=?",
                    String.valueOf(PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+""),httpList.get(i).getYear()+"",httpList.get(i).getMonth()+"",httpList.get(i).getDay()+"").order("time asc").find(TB_61_data.class);
            for(int j=0;j<list61.size();j++){
                isTwo=true;
                sleepCmdSaveToFile(list61.get(j));
            }
        }


    }


    private static void sleepCmdSaveToFile(TB_61_data data) {
        String date = new DateUtil(data.getYear(), data.getMonth(), data.getDay()).getSyyyyMMddDate();
            String deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
            String fileName = BleApplication.getInstance().getString(R.string.file_61_name_format, Fictitious_Uid+"", date, deviceName);
            String path = BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir + date + "/";
            FileUtils.write2SDFromString_1(path, fileName, data.getCmd());
            String fileName_1 = fileName+".txt";
            FileUtils.write2SDFromString_1(BaseActionUtils.FilePath.Mtk_Ble_61_Data_Log_Dir,fileName_1,data.getCmd());
    }




    public static void saveTodayCmd(){
        isTwo=false;
        String deviceName=PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        DateUtil dateUtil=new DateUtil();
        List<MyDay> days = new ArrayList<>();
        days.add(new MyDay(dateUtil.getYear(),dateUtil.getMonth(),dateUtil.getDay(),dateUtil.getSyyyyMMddDate()));
        dateUtil.addDay(-1);
        days.add(new MyDay(dateUtil.getYear(),dateUtil.getMonth(),dateUtil.getDay(),dateUtil.getSyyyyMMddDate()));

        for (int i = 0; i < days.size(); i++) {
            List<TB_61_data> list61= DataSupport.where("data_from=? and year=? and month=? and day=?",
                    deviceName,days.get(i).getYear()+"",days.get(i).getMonth()+"",days.get(i).getDay()+"").order("time asc").find(TB_61_data.class);
            if(list61.size()>0){
                if(isTwo)
                    break;
                String date=days.get(i).getDate();
                String sd1=  BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir+date+"/";
                String fileName=BleApplication.getInstance().getString(R.string.file_61_name_format, Fictitious_Uid+"", date, deviceName);
                String path1=sd1+fileName;
                FileUtils.clearInfoForFile(path1,BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir);
                if(FileUtils.checkFileExists(path1)){
                    FileUtils.deleteFile(path1);
                }
                for(int j=0;j<list61.size();j++){
                    if(isTwo)
                        break;
                    sleepCmdSaveToFile(list61.get(j));
                }
            }
        }
    }


    public static void upCmd62ToServer(){

        String deviceName=PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        List<TB_62_data> list= DataSupport.where("data_from =? ",
                String.valueOf(deviceName))
                .order("time asc").find(TB_62_data.class);

        Set<String> set =new HashSet<>();
        KLog.i("62dataUp"+list.size());
        if(list.size()>0){
            for (int i = 0; i <list.size() ; i++) {
                TB_62_data data=list.get(i);
                String date=  new DateUtil(data.getYear(),data.getMonth(),data.getDay()).getSyyyyMMddDate();
                String path= BaseActionUtils.FilePath.Mtk_Ble_62_Data_Log_Dir + date+"_"+deviceName+".txt";
                FileUtils.clearInfoForFile(path,BaseActionUtils.FilePath.Mtk_Ble_62_Data_Log_Dir);
                try {
                    if(FileUtils.checkFileExists(path)){
                        FileUtils.deleteFile(path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                set.add(date);
            }
            for (int i = 0; i <list.size() ; i++) {
                gnssCmdSaveToFile(list.get(i));
            }

        }

    }

    private static void gnssCmdSaveToFile(TB_62_data data){
        String deviceName=PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        String date=  new DateUtil(data.getYear(),data.getMonth(),data.getDay()).getSyyyyMMddDate();
        String fileName = date+"_"+deviceName+ ".txt";
        FileUtils.write2SDFromString_1(BaseActionUtils.FilePath.Mtk_Ble_62_Data_Log_Dir, fileName , data.getCmd());
    }
}
