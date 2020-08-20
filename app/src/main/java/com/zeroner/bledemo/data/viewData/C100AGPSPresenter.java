package com.zeroner.bledemo.data.viewData;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.utils.FileIOUtils;
import com.zeroner.blemidautumn.Constants;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.model.C100AgpsData;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

public class C100AGPSPresenter {

    private int position;//当前的位置
    private int fileSize;//文件总大小
    private int packageNumAll;//包的总个数
    private byte[] agpsFile;
    private String path;
    private volatile static C100AGPSPresenter instance;
    private boolean isInit = false;
    private int selectLine;//选择的AGPS
    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;

    public static C100AGPSPresenter getInstance(){
        if(instance == null){
            synchronized (C100AGPSPresenter.class){
                if(instance == null){
                    instance = new C100AGPSPresenter();
                }
            }
        }
        return instance;
    }

    private C100AGPSPresenter() {
    }

    //每次发送的168字节
    private void writeAGPS168(){
        //细分成168字节
        byte[] datas = Arrays.copyOfRange(agpsFile, 168 * position, 168 * position + 168);
        int num = datas.length % 16 == 0 ? datas.length / 16:datas.length / 16 + 1;

        for (int i = 0 ;i < num;i++){
            //每168字节发送num个包.每包最多16字节
            byte[] bytes;
            if(i == num - 1){
                bytes = Arrays.copyOfRange(datas, i * 16,datas.length);
            }else {
                bytes = Arrays.copyOfRange(datas, i * 16, i * 16 + 16);
            }
            if(selectLine == OFFLINE) {
                SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeC100Agps(BleApplication.getInstance(), i + 1, 1, packageNumAll, bytes);
            }else if(selectLine == ONLINE){
                SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeC100Agps(BleApplication.getInstance(), i + 1, 1, packageNumAll, bytes);
            }
        }
        KLog.e("yyyyy---发送完第" + position +"个包总共有"+ packageNumAll + "个包 总size" + fileSize);
    }


    //每次发送的168字节
    private void writeAGPS168Online(){
        byte[] datas;  //0 31  32-163 //164 - 195
        final int fixedValue = 44 * 3;
        if(position == 0){
            datas = Arrays.copyOfRange(agpsFile, position, 32);
        }else{
            if(position == 11){
                datas = Arrays.copyOfRange(agpsFile, fixedValue * (position - 1) + 32, fixedValue * (position - 1) + 32 + fixedValue/3);
                int start = fixedValue * (position - 1) + 32;
                int end = fixedValue * (position - 1) + 32 + fixedValue / 3;
                KLog.e("yyyyy---解析第" + start +"到"+ end + "个包 总size" + fileSize);
            }else {
                datas = Arrays.copyOfRange(agpsFile, fixedValue * (position - 1) + 32, fixedValue * (position - 1) + 32 + fixedValue);
                int start = fixedValue * (position - 1) + 32;
                int end = fixedValue * (position - 1) + 32 + fixedValue;
                KLog.e("yyyyy---解析第" + start +"到"+ end + "个包 总size" + fileSize);
            }
        }
        //细分成168字节
        int num = 11;

        for (int i = 0 ;i < num;i++){
            //每168字节发送num个包.每包最多16字节
            byte[] bytes;
            if(i == num - 1){
                if(datas.length > i * 16) {
                    bytes = Arrays.copyOfRange(datas, i * 16, datas.length);
                }else {
                    bytes = new byte[8];
                }
            }else {
                if(datas.length > i * 16) {
                    bytes = Arrays.copyOfRange(datas, i * 16, i * 16 + 16);
                }else {
                    bytes = new byte[16];
                }
            }
            if(position == 0) {
                SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeC100Agps(BleApplication.getInstance(), i + 1, 2, packageNumAll, bytes);
            }else if(selectLine == ONLINE){
                SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeC100Agps(BleApplication.getInstance(), i + 1, 3, packageNumAll, bytes);
            }
        }
        KLog.e("yyyyy---发送完第" + position +"个包总共有"+ packageNumAll + "个包 总size" + fileSize);
    }

//    private void writeTest(){
//        for (int i = 0 ; i < packageNumAll;i++) {
//            position = i;
//            writeAGPS168();
//            SystemClock.sleep(500);
//
//        }
//    }

    /**
     * 需要分包的大小
     */
    private void getPackageNum(){
        packageNumAll =  fileSize % 168 == 0 ? fileSize / 168:fileSize / 168 + 1;
        if(selectLine == OFFLINE) {
            packageNumAll = packageNumAll / 8 * 3;
        }else{
            packageNumAll = 12;
        }
    }

    private byte[] getFileInfo(String path){
        agpsFile = FileIOUtils.readFile2BytesByStream(path);
        return agpsFile;
    }

    private void initData(){
        //获取文件
        byte[] fileInfo = getFileInfo(path);
        if(fileInfo == null){
            return;
        }
        //获取文件长度
        fileSize = fileInfo.length;
        //获取分包的总大小
        getPackageNum();

//        KLog.e("bytes" + Util.bytesToString(fileInfo));
//        KLog.write2Sd(Util.bytesToString(fileInfo),"testcmd");

    }

    public void init(String path) {
        this.isInit = true;
        this.selectLine = OFFLINE;
        this.path = path;
        initData();
    }

    public void init(String path,int selectLine) {
        this.isInit = true;
        this.selectLine = selectLine;
        this.path = path;
        initData();
    }

    public void startAgps(){
        KLog.e("yyyyy---开始发送完第" + position +"个包总共有"+ packageNumAll + "个包");
        if(position == packageNumAll){
            position = 0;
            C100AgpsData c100AgpsData = new C100AgpsData();
            c100AgpsData.setIsStatus(3);
            c100AgpsData.setNum(0);
            EventBus.getDefault().post(c100AgpsData);
            return;
        }
        if(selectLine == OFFLINE) {
            writeAGPS168();
        }else {
            writeAGPS168Online();
        }
        int progress = calcProgress();
        EventBus.getDefault().post("c100-apgs-progress:" + progress);
        position ++;



//        if(position >= packageNumAll){
//            //发送完成
//
//        }
    }

    private int calcProgress(){
        return (position + 1) * 100 / packageNumAll;
    }

    public void openApgs(){
        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).agpsC100Operation(Constants.AgpsMode.START);
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes);
        SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeOfflineAgpsLength(BleApplication.getInstance(),packageNumAll);
    }
    public void openOnlineApgs(){
        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).agpsC100Operation(Constants.AgpsMode.START);
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes);
        SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeOnlineAgpsLength(BleApplication.getInstance(),packageNumAll);
    }

    public void closeApgs(){
        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).agpsC100Operation(Constants.AgpsMode.END);
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes);
    }

    public void checkUpdate(){
        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).getAGPSStatus();
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes);
    }


}
