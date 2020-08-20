package com.zeroner.bledemo.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.blankj.utilcode.util.ToastUtils;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.BraceletSetting;
import com.zeroner.bledemo.data.sync.ProtoBufSync;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.bledemo.firmware.ProtoBufFirmwareUpdateActivity;
import com.zeroner.bledemo.setting.alarm.AddClockActivity;
import com.zeroner.bledemo.setting.schedule.ScheduleActivity;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.OptionsPickerViewUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.bledemo.view.LSettingItem;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.task.BleWriteDataTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class I7BSettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.item_alarm)
    LSettingItem item_alarm;
    @BindView(R.id.item_calendar)
    LSettingItem item_calendar;
    @BindView(R.id.item_time)
    LSettingItem item_time;
    @BindView(R.id.item_motor)
    LSettingItem item_motor;
    @BindView(R.id.item_time_unit)
    LSettingItem item_time_unit;
    @BindView(R.id.item_date_unit)
    LSettingItem item_date_unit;
    @BindView(R.id.item_temp_unit)
    LSettingItem item_temp_unit;
    @BindView(R.id.item_auto_sport)
    LSettingItem item_auto_sport;
    @BindView(R.id.item_habit_hand)
    LSettingItem item_habit_hand;
    @BindView(R.id.item_language)
    LSettingItem item_language;
    @BindView(R.id.item_firmware_update)
    LSettingItem item_firmware_update;
    @BindView(R.id.item_swim)
    LSettingItem item_swim;
    @BindView(R.id.item_sitDown)
    LSettingItem item_sitDown;

    private int alarmId = 0;

    private Context context;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i7b_setting);
        ButterKnife.bind(this);

        context = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        initListener();


    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initListener() {

        item_alarm.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                UI.startActivity(I7BSettingActivity.this,AddClockActivity.class);
            }
        });
        item_calendar.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                UI.startActivity((Activity) context,ScheduleActivity.class);
            }
        });

        item_time.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(I7BSettingActivity.this).setTime();
                BackgroundThreadManager.getInstance().addWriteData(I7BSettingActivity.this,bytes);
            }
        });

        item_motor.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(I7BSettingActivity.this, OptionsPickerViewUtils.getShakeName(I7BSettingActivity.this), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        item_motor.setRightText(OptionsPickerViewUtils.getShakeName(I7BSettingActivity.this).get(i));
                        PrefUtil.save(I7BSettingActivity.this, BaseActionUtils.Action_Setting_Shake, OptionsPickerViewUtils.getShakeName(I7BSettingActivity.this).get(i));
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setMotorVibrate(OptionsPickerViewUtils.getZGShakeModel(I7BSettingActivity.this)[i],2);
                        BleWriteDataTask task = new BleWriteDataTask(getApplicationContext(), bytes);
                        BackgroundThreadManager.getInstance().addTask(task);
                    }

                });
                option.show();
            }
        });
        item_sitDown.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(I7BSettingActivity.this, OptionsPickerViewUtils.getSitDown(I7BSettingActivity.this), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        String s = OptionsPickerViewUtils.getSitDown(I7BSettingActivity.this).get(i);
                        int duration  = Integer.parseInt(s);
                        item_sitDown.setRightText(duration  *  5 + "");
                        DateUtil dateUtil = new DateUtil();
                        byte[] clear = ProtoBufSendBluetoothCmdImpl.getInstance().clearSedentariness();
                        BackgroundThreadManager.getInstance().addWriteData(getApplicationContext(),clear);
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setSedentariness(true,0,0xff,0,23,duration,50);
                        BleWriteDataTask task = new BleWriteDataTask(getApplicationContext(), bytes);
                        BackgroundThreadManager.getInstance().addTask(task);
                    }

                });
                option.show();
            }
        });

        item_time_unit.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getTimeItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        item_time_unit.setRightText(OptionsPickerViewUtils.getTimeItemOptions(context).get(i));
                        BraceletSetting bs1 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Time_Format);
                        bs1.setKey(BaseActionUtils.Action_Setting_Time_Format);
                        bs1.setValue(i);
                        SqlBizUtils.saveBraceletSetting(bs1);
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setHourFormat(i == 1);
                        BackgroundThreadManager.getInstance().addWriteData(context,bytes);
                    }
                });
                option.show();
            }
        });

        item_date_unit.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getDateItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        item_date_unit.setRightText(OptionsPickerViewUtils.getDateItemOptions(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Date_Format, OptionsPickerViewUtils.getDateItemOptions(context).get(i));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Date_Format);
                        setting.setKey(BaseActionUtils.Action_Setting_Date_Format);
                        setting.setValue(i);
                        SqlBizUtils.saveBraceletSetting(setting);
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setDateFormat(i == 1);
                        BackgroundThreadManager.getInstance().addWriteData(context,bytes);
                    }
                });
                option.show();
            }
        });

        item_temp_unit.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getWeatherItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        item_temp_unit.setRightText(OptionsPickerViewUtils.getWeatherItemOptions(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Weather_Unit, OptionsPickerViewUtils.getWeatherItemOptions(context).get(i));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Weather_Unit);
                        setting.setKey(BaseActionUtils.Action_Setting_Weather_Unit);
                        setting.setValue(i);
                        SqlBizUtils.saveBraceletSetting(setting);
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTemperatureUnit(i == 1);
                        BackgroundThreadManager.getInstance().addWriteData(context,bytes);
                    }
                });
                option.show();
            }
        });
        item_habit_hand.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getHandItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setHabitualHand(i == 1);
                        BackgroundThreadManager.getInstance().addWriteData(context,bytes);
                    }
                });
                option.show();
            }
        });

        item_language.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getLanguage(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setLanguage(context,i);
                    }
                });
                option.show();
            }
        });

        item_auto_sport.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {

            }
        });

        item_firmware_update.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                UI.startActivity((Activity) context, ProtoBufFirmwareUpdateActivity.class);
            }
        });

        item_swim.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                ToastUtils.showShort("开始同步");
                ProtoBufSync.getInstance().syncData();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SyncDataEvent events){
        if (events.getProgress() > 0 && !events.isStop()){
            ToastUtils.showShort("同步:" + events.getProgress());
        }
        if(events.isStop()){
            ToastUtils.showShort("同步完成");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && ProtoBufSync.getInstance().isSync()){
            ToastUtils.showShort("正在同步中.");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
