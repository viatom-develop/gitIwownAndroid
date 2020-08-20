package com.zeroner.bledemo.notification;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import com.zeroner.bledemo.BaseActivity;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.view.LSettingItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends BaseActivity {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    @BindView(R.id.item_call)
    LSettingItem itemCall;
    @BindView(R.id.item_sms)
    LSettingItem itemSms;
    @BindView(R.id.item_whatsapp)
    LSettingItem itemWhatsapp;
    @BindView(R.id.item_skype)
    LSettingItem itemSkype;
    @BindView(R.id.item_facebook)
    LSettingItem itemFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setLeftBackTo();
        setTitleText(getString(R.string.message_push_title));
        itemCall.setChecked(true);
        itemSms.setChecked(true);
        itemWhatsapp.setChecked(true);
        itemSkype.setChecked(true);
        itemFacebook.setChecked(true);

        itemCall.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {

            }
        });

        itemSms.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {

            }
        });
        itemWhatsapp.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                if(!isChecked){
                    NotificationBiz.WHATSAPP="com";
                }else {
                    NotificationBiz.WHATSAPP  = "com.whatsapp";
                }
            }
        });
        itemSkype.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                if(!isChecked){
                    NotificationBiz.WHATSAPP="com";
                }else {
                    NotificationBiz.SKYPE1  = "com.skype.rover";
                }
            }
        });
        itemFacebook.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                if(!isChecked){
                    NotificationBiz.WHATSAPP="com";
                }else {
                    NotificationBiz.SKYPE1  = "com.facebook.orca";
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isEnabled()) {
            openNotificationAccess();
        }
    }


    private void openNotificationAccess() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle(R.string.alert_title);
        localBuilder.setIcon(R.mipmap.ic_launcher);
        localBuilder.setMessage(R.string.alert_content);
        localBuilder.setPositiveButton(R.string.alert_content_comfirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });
        localBuilder.setNegativeButton(R.string.common_cacel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {

            }
        });
        localBuilder.setCancelable(false).create();
        localBuilder.show();
    }

    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
