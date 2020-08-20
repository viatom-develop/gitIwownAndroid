package com.zeroner.bledemo.notification;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.data.ZGBaseUtils;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.task.MessageTask;

import java.util.ArrayList;
import java.util.List;

/**
 * author：hzy on 2016/4/19 10:59
 * <p/>
 * email：hezhiyuan@iwown.com
 */
public class NotificationBiz {
    public static final int TYPE_QQ = 1;
    public static final int TYPE_WECHAT = 2;
    public static final int TYPE_OTHER_SOFTWARE = 3;
    public static final int TYPE_SMS = 4;
    public static final int TYPE_FACEBOOK = 11;
    public static final int TYPE_TWITTER = 12;
    public static final int TYPE_WHATSAPP = 13;
    public static final int TYPE_SKYPE = 14;
    public static final int TYPE_LINE = 15;
    public static final int TYPE_KAKAOTALK = 16;
    public static final int TYPE_GMAIL = 17;
    public static final int SEND_TYPE_1 = 10;
    public static final int SEND_TYPE_2 = 20;

    public static  String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    public static  String FACEBOOK  = "com.facebook.orca";
    public static  String TWITTER  = "com.twitter.android";
    public static  String WHATSAPP  = "com.whatsapp";
    public static  String SKYPE1  = "com.skype.rover";
    public static  String SKYPE2  = "com.skype.raider";
    public static  String LINE  = "jp.naver.line.android";
    public static  String KAKAOTALK  = "com.kakao.talk";
    public static  String GMAIL  = "com.google.android.gm";
    public static  String QQ  = "com.tencent.mobileqq";
    public static  String WECHAT  = "com.tencent.mm";


    private static NotificationBiz instance;
    SendListener listener;
    Context mContext;

    public static List<StatusBarNotification[]> mCurrentNotifications = new ArrayList<>();
    public static int mCurrentNotificationsCounts = 0;

    //Temporary information for whatsapp
    private String mTempText;

    public static NotificationBiz getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationBiz(context);
        }
        return instance;
    }

    public NotificationBiz(Context context) {
        this.mContext = context;
    }

    /**
     * One way to handle the message extraction content
     *
     * @param sbn
     */
    @SuppressLint({"NewApi"})
    public void storeNotification(StatusBarNotification sbn) {
        String packageName;
        int msgid = -10000;
        String message = null;
        String appName;
        String summaryText;
        try {
            packageName = sbn.getPackageName();
            msgid = sbn.getId();
//            LogUtil.msgPushLog("NotificationBiz","notification.tickerText:>" +sbn.getNotification().tickerText.toString());
            if(!packageName.equalsIgnoreCase("com.skype.raider") && !packageName.equalsIgnoreCase("com.skype.rover")) {
                 appName = packageName;
                try {
                    ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName, 0);
                    appName = mContext.getPackageManager().getApplicationLabel(info).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    KLog.e("Version"+Build.VERSION.SDK_INT);
                    if (sbn.getNotification().extras != null) {
                        message = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
                        if(TextUtils.isEmpty(message)){
                            message = sbn.getNotification().extras.getString(Notification.EXTRA_TEXT);
                        } else if (message.equals(appName)) {
                            String otherMsg = sbn.getNotification().extras.getString(Notification.EXTRA_TEXT);
                            if (!TextUtils.isEmpty(otherMsg)) {
                                message = otherMsg;
                            }
                        }
                    }
                }

                if (TextUtils.isEmpty(message)) {
                    if (sbn.getNotification() == null) return;
                    if (sbn.getNotification().tickerText == null) return;
                    message = sbn.getNotification().tickerText.toString();
                } else if (message.equals(appName)) {
                    String otherMsg = sbn.getNotification().tickerText.toString();
                    if (!TextUtils.isEmpty(otherMsg)) {
                        message = otherMsg;
                    }
                }

                String text = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)+"";
                if (packageName.equalsIgnoreCase(WHATSAPP_PACKAGE_NAME) || packageName.equalsIgnoreCase("com.google.android.gm")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (sbn.getNotification().extras != null) {
                            if (null!=text) {
                                if (packageName.equalsIgnoreCase("com.google.android.gm")) {
                                    message = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE) +
                                            ":" + text;
                                }
                            }
                            //whatsapp go here if the array is removed due to TEXT_LINES null led to an exception (this situation represents the message for the first unread message)
                            if (packageName.equals(WHATSAPP_PACKAGE_NAME)) {
                                CharSequence[] charSequences = sbn.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
                                if (charSequences!=null && charSequences.length>0) {
                                    message = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE) +
                                            ":" + charSequences[charSequences.length-1];
                                } else {
                                    if (!text.equals(mTempText)) {
                                        message = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE) +
                                                ":" + text;
                                        mTempText = text;
                                    }else {
                                        return;
                                    }
                                }
                                if(message.length()>9 && message.substring(0,9).indexOf("WhatsApp:")>-1){
                                    message = message.substring(9);
                                }
                            }
                        }
                    } else {
                        if (sbn.getNotification().tickerText != null) {
                            message = sbn.getNotification().tickerText.toString();
                        }
                    }
                } else {
                    if (sbn.getNotification().tickerText != null) {
                        message = sbn.getNotification().tickerText.toString();
                    }
                }

            }
            else {
                //When tickertext is null skype push to go here
                if(sbn.getNotification().tickerText != null){
                    message = sbn.getNotification().tickerText.toString();
                }else {
                    String title = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
                    StringBuffer skpStr = new StringBuffer();
                    skpStr.append(title).append(" ").append(sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
                    message = skpStr.toString();
                }
            }

            if (sbn.getNotification().extras!=null && packageName.equals(WHATSAPP_PACKAGE_NAME)) {
                summaryText = sbn.getNotification().extras.getString(Notification.EXTRA_SUMMARY_TEXT);
                if (!TextUtils.isEmpty(summaryText) && message.contains(summaryText)) {
                    return;
                }
            }
            if(message!=null)
                message=message.replace("\n"," ").trim();
            switchMessageType(message,packageName,msgid,SEND_TYPE_2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Message processing two
     */
    public void updateCurrentNotifications(StatusBarNotification[] activeNos) {
        try {
            if (mCurrentNotifications.size() == 0) {
                mCurrentNotifications.add(null);
            }
            mCurrentNotifications.set(0, activeNos);
            mCurrentNotificationsCounts = activeNos.length;
            StatusBarNotification[] notifications = mCurrentNotifications.get(0);
            if (notifications.length == 0) return;
            for (int i = 0; i < notifications.length; i++) {
                if (notifications[i].getNotification().tickerText == null) {
                    if (listener != null) {
                        listener.send2DeviceListener(SEND_TYPE_2);
                    }
                    return;
                }
                String message = notifications[i].getNotification().tickerText.toString().replace("\n"," ").trim();
                String packageName = notifications[i].getPackageName();
                int msgid = notifications[i].getId();
                switchMessageType(message, packageName, msgid,SEND_TYPE_1);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.send2DeviceListener(SEND_TYPE_2);
            }
            e.printStackTrace();
        }
    }


    /**
     * Determine the type of message push
     *
     * @param message
     * @param packageName
     */
    private synchronized void switchMessageType(String message, String packageName,int msgId, int sendType) {
            if (packageName.equalsIgnoreCase(QQ) ) {
                //QQ
                sendMsgToDevice(TYPE_QQ, checkDataLength(message), sendType);
                KLog.i("NotificationBiz","【QQ message push success】");
            } else if (packageName.equalsIgnoreCase(WECHAT)) {
                //微信
                sendMsgToDevice(TYPE_WECHAT, checkDataLength(message), sendType);
                KLog.i("【wechat message push success】");
            } else if (packageName.equalsIgnoreCase(FACEBOOK)) {
                sendMsgToDevice(TYPE_FACEBOOK, checkDataLength(message), sendType);
                KLog.i("【Facebook message push success】");
            } else if (packageName.equalsIgnoreCase(TWITTER)) {
                sendMsgToDevice(TYPE_TWITTER, checkDataLength(message), sendType);
                KLog.i("【Twitter  message push success】");
            } else if (packageName.equalsIgnoreCase(WHATSAPP)) {
                sendMsgToDevice(TYPE_WHATSAPP, checkDataLength(message), sendType);
                KLog.i("【whatsapp  message push success】");
            } else if (packageName.equalsIgnoreCase(SKYPE1) || packageName.equalsIgnoreCase("com.skype.raider")) {
                sendMsgToDevice(TYPE_SKYPE, checkDataLength(message), sendType);
                KLog.i("【skype  message push success】");
            } else if (packageName.equalsIgnoreCase(LINE)) {
                sendMsgToDevice(TYPE_LINE, checkDataLength(message), sendType);
                KLog.i("【Line  message push success】");
            } else if (packageName.equalsIgnoreCase(KAKAOTALK)) {
                sendMsgToDevice(TYPE_KAKAOTALK, checkDataLength(message), sendType);
                KLog.i("【KakaoTalk  message push success】");
            } else if (packageName.equalsIgnoreCase(GMAIL)) {
                sendMsgToDevice(TYPE_GMAIL, checkDataLength(message), sendType);
                KLog.i("【Gmail  message push success】");
            }
    }


    /**
     * check length
     *
     * @param msg
     * @return
     */
    private String checkDataLength(String msg) {
        if(ZGBaseUtils.isZG()){
            return msg;
        }
        int index = 0;
        for (int j = 0; j < msg.length(); j++) {
            if (msg.charAt(j) < 0x40 || (msg.charAt(j) < 0x80 && msg.charAt(j) > 0x60)) {
                index += 1;
            } else {
                index += 3;
            }
            if (index > 192) {
                msg = msg.substring(0, j);
                break;
            }
        }
        return msg;
    }


    /**
     * Send to the bracelet  message
     *
     * @param type
     * @param msg
     */
    public void sendMsgToDevice(int type, String msg, int sendType) {
        String str = "";
        switch (type) {
            case TYPE_QQ:
                str = "QQ|";
                break;
            case TYPE_WECHAT:
                str = "wechat|";
                break;
            case TYPE_OTHER_SOFTWARE:
                break;
            case TYPE_SMS:
                str = "SMS|";
                break;
            case TYPE_FACEBOOK: {
                str = "Facebook|";
                break;
            }
            case TYPE_TWITTER: {
                str = "Twitter|";
                break;
            }
            case TYPE_SKYPE: {
                str = "Skype|";
                break;
            }
            case TYPE_WHATSAPP: {
                str = "WhatsApp|";
                break;
            }
            case TYPE_LINE: {
                str = "Line|";
                break;
            }
            case TYPE_KAKAOTALK: {
                str = "KakaoTalk|";
                break;
            }
            case TYPE_GMAIL: {
                str = "G-mail|";
                break;
            }
        }
        if (listener != null) {
            if (sendType == SEND_TYPE_1) {
                listener.send2DeviceListener(SEND_TYPE_1);
            }
        }
        addMsg(0x02,str + checkDataLength(msg));
    }

    public static void addMsg(final int type, final String msg) {
        addMsg(type, msg, System.currentTimeMillis() + MessageTask.TIME_OUT_SHORT, true);
    }

    public static void addMsg(final int type, final String msg,long time,boolean isMessage) {
        postMessage(type, msg, time, isMessage);
    }

    private static void postMessage(int type, String msg, long time, boolean isMessage) {
        KLog.e("postMessage  "+BluetoothUtil.isConnected());
        if (BluetoothUtil.isConnected()) {
            MessageTask.getInstance(BleApplication.getInstance()).addMessage(type, msg, time);
        } else {
            if (TextUtils.isEmpty(PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_NAME)) || TextUtils.isEmpty(PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_ADDRESS))) {
                return;
            }
            if (BluetoothUtil.isEnabledBluetooth() && !BluetoothUtil.isConnected()) {
                BluetoothUtil.connect();
                BackgroundThreadManager.getInstance().needWait();
            }
            MessageTask.getInstance(BleApplication.getInstance()).addMessage(type, msg, time);
        }
    }


    public interface SendListener {
        void send2DeviceListener(int type);
    }


    public SendListener getListener() {
        return listener;
    }

    public void setListener(SendListener listener) {
        this.listener = listener;
    }
}
