package com.zeroner.bledemo.notification;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.socks.library.KLog;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.PrefUtil;


public class NotificationMonitor extends NotificationListenerService implements NotificationBiz.SendListener{
	private Context mContext;
	StatusBarNotification sbn;
	private String TAG = this.getClass().getSimpleName();
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = NotificationMonitor.this;
		NotificationBiz.getInstance(mContext).setListener(this);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return super.onBind(intent);
	}
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		KLog.e(TAG,"：ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName()+" thread:"+Thread.currentThread().getName());
		this.sbn=sbn;
		if (sbn.getId() == HeartService.ZERONER_HEALTH_NOTIFICATION_ID) {
			if (!BluetoothUtil.isConnected()
					&& !TextUtils.isEmpty(PrefUtil.getString(this, BaseActionUtils.ACTION_DEVICE_ADDRESS))
					&& !TextUtils.isEmpty(PrefUtil.getString(this, BaseActionUtils.ACTION_DEVICE_NAME))) {
				BluetoothUtil.connect();
			}
		}

		try {
			NotificationBiz.getInstance(mContext).updateCurrentNotifications(getActiveNotifications());
		} catch (Exception e) {
			NotificationBiz.getInstance(mContext).storeNotification(sbn);
		}
	}
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
	}

	@Override
	public void send2DeviceListener(int type) {
		if(type!=NotificationBiz.SEND_TYPE_1){
			if (!sbn.isOngoing()) {
				NotificationBiz.getInstance(mContext).storeNotification(sbn);
			}
		}
	}
}
