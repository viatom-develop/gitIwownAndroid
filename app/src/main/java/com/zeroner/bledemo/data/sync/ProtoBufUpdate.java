package com.zeroner.bledemo.data.sync;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.bledemo.gps.DownloadUtil;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.FileIOUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.Util;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufFileUpdateInfo;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * protoBuf epo升级
 */
public class ProtoBufUpdate {

    private static final int INIT = 1;
    private static final int DATA = 2;
    private static final int EXIT = 3;
    private static final int DESC = 4;

    public static class Type {
        public static final int TYPE_GPS = 0;
        public static final int TYPE_FONT = 1;
        public static final int TYPE_MGAONLINE = 2;
    }

    private static final String url1 = "https://offline-live1.services.u-blox.com/GetOfflineData.ashx?token=ALKccrhbDE6DMfGLzob8dQ;gnss=gps;period=1;resolution=1";
    private static final String url2 = "http://online-live1.services.u-blox.com/GetOnlineData.ashx?token=ALKccrhbDE6DMfGLzob8dQ;gnss=gps;datatype=alm";


    private volatile static ProtoBufUpdate instance;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 当前的position
     */
    private int position = 0;
    /**
     * 整个包的最大mtu default 244
     */
    private int maxMtu = 244;
    /**
     * 总文件大小
     */
    private byte[] allBytes;
    /**
     * 总文件的包集合
     */
    private List<byte[]> packageList;
    /**
     * 总文件cyc32
     */
    private long allCyc32;
    /**
     * 已经同步的文件
     */
    private byte[] completeBytes = new byte[0];
    /**
     * 同步文件的偏移量
     */
    private int completeOffset = 0;

    /**
     * 是否更新
     */
    private boolean isUpdate = false;

    /**
     * 更新类别
     */
    private int fuType;



    public static ProtoBufUpdate getInstance() {
        if (instance == null) {
            synchronized (ProtoBufUpdate.class) {
                if (instance == null) {
                    instance = new ProtoBufUpdate();
                }
            }
        }
        return instance;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    /**
     * @param type 传递 Type gps font and mgaonline
     */
    public void startUpdate(int type) {
        if(!BluetoothUtil.isConnected()){
            KLog.e("yanxi....没有连接");
            return;
        }
        /**
         * 如果正在同步则不AGPS升级
         */
        if(ProtoBufSync.getInstance().isSync()){
            return;
        }
        if (isUpdate) {
            KLog.e("yanxi....正在更新");
            return;
        }

        /**
         * 判断是否需要更新有效
         */

        updateInfo(type);

    }

    private void updateInfo(int type){
        if (type == Type.TYPE_FONT) {
            this.fuType = Type.TYPE_FONT;
        } else if(type == Type.TYPE_GPS){
            this.fuType = Type.TYPE_GPS;
            //升级
            downloadFile(url1);
        } else {
            this.fuType = Type.TYPE_MGAONLINE;
            downloadFile(url2);
        }
    }

    /**
     * 初始化数据
     *
     * @param fileUpdateInfo 获取mtu
     */
    private void initData(ProtoBufFileUpdateInfo fileUpdateInfo) {
        /**
         * 暂时先用本地的数据..
         */
//        String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.ubx";
        KLog.e(fileUpdateInfo.toString());
        if (allBytes == null || allBytes.length == 0) {
                return;
        }
        allCyc32 = Util.CRC_32(allBytes);
        packageList = multipePackage(allBytes, fileUpdateInfo.getMtu());
        maxMtu = PrefUtil.getInt(BleApplication.getInstance(), BaseActionUtils.PROTOBUF_MTU_INFO);

        //判断是否有断点下载记录
        int fileOffset = fileUpdateInfo.getFileOffset();
        int crc32AtOffset = fileUpdateInfo.getCrc32AtOffset();
        if (fileOffset == 0) {
            clearInfo();
            return;
        }
        //通过偏移量计算position
        int tempOffset = 0;
        byte[] tempBytes = new byte[0];
        int tempPostion = -1;
        for (int i = 0; i < packageList.size(); i++) {
            tempOffset += packageList.get(i).length;
            tempBytes = com.zeroner.blemidautumn.utils.Util.concat(tempBytes, packageList.get(i));
            if (fileOffset == tempOffset) {
                if (crc32AtOffset == Util.CRC_32(tempBytes)) {
                    tempPostion = i;
                    position = i;
                    completeBytes = tempBytes;
                    completeOffset = tempOffset;
                    break;
                }
            }
        }
        if (tempPostion == -1) {
            clearInfo();
        }

    }

    public void updateDetail(int type, ProtoBufFileUpdateInfo fileUpdateInfo) {
        if (type == DESC) {
            Log.e("update", "desc" + fileUpdateInfo.getStatus());
            if(fileUpdateInfo.isValid()){
                isUpdate = false;
                return;
            }
            initData(fileUpdateInfo);
            if (position != 0) {
                exeData();
                return;
            }
            exeInit();
        }
        if (type == INIT) {
            if (fileUpdateInfo.getStatus() == 0) {
                exeData();
            } else {
                KLog.e("同步失败");
                isUpdate = false;
            }
        }
        if (type == DATA) {
            if (fileUpdateInfo.getStatus() == 0) {
                exeData();
            } else if (fileUpdateInfo.getStatus() == 1) {
                KLog.e("参数失败");
                initData(fileUpdateInfo);
            } else {
                isUpdate = false;
            }
        }
        if (type == EXIT) {
            if (fileUpdateInfo.getStatus() == 0) {
                if (fileUpdateInfo.isValid()) {
                    //同步完成
                    KLog.e("同步完成");
                    isUpdate = false;
                    EventBus.getDefault().post(new SyncDataEvent(100, true));
                }
            }

        }

    }


    /**
     * 按mtu分包发送
     *
     * @param bytes 需要分包的问题
     * @param mtu   分包的条件
     * @return 返回集合
     */
    private List<byte[]> multipePackage(byte[] bytes, int mtu) {
        List<byte[]> packageList = new LinkedList<>();
        if (mtu <= 0) {
            return packageList;
        }
        if (bytes.length > mtu) {
            //分包处理
            for (int i = 0; i < bytes.length; i += mtu) {
                int to = i + mtu;
                if (to > bytes.length) {
                    to = bytes.length;
                }
                packageList.add(Arrays.copyOfRange(bytes, i, to));
            }
        } else {
            packageList.add(Arrays.copyOfRange(bytes, 0, bytes.length));
        }
        return packageList;
    }


    /**
     * 执行init
     */
    private void exeInit() {

        byte[] bytes1 = ProtoBufSendBluetoothCmdImpl.getInstance().setFileInitUpdate(fuType, allBytes.length, (int) allCyc32, "cs",
                completeOffset, (int) Util.CRC_32(completeBytes));
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), bytes1);
    }

    /**
     * 执行data
     */
    private void exeData() {
        if (position < packageList.size()) {
            byte[] bytes = packageList.get(position);

            ByteString bytes1 = ByteString.copyFrom(bytes);
            completeBytes = com.zeroner.blemidautumn.utils.Util.concat(completeBytes, bytes);
            long cyc32Office = Util.CRC_32(completeBytes);
            byte[] allBytes = ProtoBufSendBluetoothCmdImpl.getInstance().setFileDataUpdate(fuType, completeOffset, (int) cyc32Office, bytes1);
            List<byte[]> bytesList = multipePackage(allBytes, maxMtu);
            for (int i = 0; i < bytesList.size(); i++) {
                BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), bytesList.get(i));
            }
            completeOffset += packageList.get(position).length;
            position++;
        } else {
            byte[] bytes = ProtoBufSendBluetoothCmdImpl.getInstance().setFileDataExit(fuType);
            BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), bytes);
            isUpdate = false;

            EventBus.getDefault().post(new SyncDataEvent(100, true));
            //AGPS升级完成.保存AGPS crc32校验码
            long checkCode = Util.CRC_32(allBytes);

            String data = new DateUtil().getY_M_D();
            if(this.fuType == Type.TYPE_GPS){
                //写入GPS
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KLog.e("写入MGAONLINE");
                        updateInfo(Type.TYPE_MGAONLINE);
                    }
                },1000);
            }else{
                isUpdate = false;
                return;
            }
        }
        int progress = position * 100 / packageList.size();
        KLog.e("progress:" + progress);
        String typeDesc = "AGPS";
        if (this.fuType == Type.TYPE_FONT) {
            typeDesc = "FONT";
        }else if(this.fuType == Type.TYPE_MGAONLINE){
            typeDesc = "MGAONLINE";
        }
        EventBus.getDefault().post(new SyncDataEvent(progress, false,typeDesc));


    }

    public int getFuType() {
        return fuType;
    }

    /**
     * 清除信息从头开始
     */
    private void clearInfo() {
        position = 0;
        completeBytes = new byte[0];
        completeOffset = 0;
    }

    private void downloadFile(String url) {

        DownloadUtil.get().download(url, "protobuf", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String path) {
                KLog.d("yanxi...PROTOBUF数据下载完成..."+fuType);
                //升级
                Log.e("update", "start");
                isUpdate = true;
                allBytes = FileIOUtils.readFile2BytesByMap(path);

                byte[] bytes = ProtoBufSendBluetoothCmdImpl.getInstance().setFileDescUpdate(true);
                BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(), bytes);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                KLog.d(" PROTOBUF数据下载失败");
                isUpdate = false;
            }
        });
    }




}
