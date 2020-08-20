package com.zeroner.bledemo.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.BraceletSetting;
import com.zeroner.bledemo.setting.alarm.AddClockActivity;
import com.zeroner.bledemo.setting.schedule.ScheduleActivity;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.OptionsPickerViewUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.bledemo.view.LSettingItem;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.task.BleWriteDataTask;
import com.zeroner.blemidautumn.task.ITask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZgSettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_device_setting)
    Toolbar toolbarDevice;
    @BindView(R.id.item_shake)
    LSettingItem itemShake;
    @BindView(R.id.item_time)
    LSettingItem itemTime;
    @BindView(R.id.blood)
    LSettingItem bloodSetting;
    @BindView(R.id.item_date)
    LSettingItem itemDate;
    @BindView(R.id.item_unit)
    LSettingItem itemUnit;
    @BindView(R.id.item_weather)
    LSettingItem itemWeather;
    @BindView(R.id.item_gesture)
    LSettingItem itemGesture;
    @BindView(R.id.item_hand)
    LSettingItem itemHand;
    @BindView(R.id.item_gesture_time)
    LSettingItem itemGestureTime;
    @BindView(R.id.item_language)
    LSettingItem itemLanguage;
    @BindView(R.id.item_firmware_update)
    LSettingItem itemFirmwareUpdate;
    @BindView(R.id.item_alarm)
    LSettingItem itemAlarm;
    @BindView(R.id.item_schedule)
    LSettingItem itemSchedule;
    @BindView(R.id.item_shake_setting_mode)
    LSettingItem itemShakeSettingMode;
    @BindView(R.id.container)
    AppBarLayout container;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        context = this;
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbarDevice);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDevice.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemShake.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Shake));
        itemTime.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Time_Format));
        itemDate.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Date_Format));
        itemUnit.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Unit));
        itemWeather.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Weather_Unit));
        itemGesture.setChecked(PrefUtil.getInt(context, BaseActionUtils.Action_Setting_Roll) == 1 ? true : false);
        itemGestureTime.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Roll_Time));
        itemHand.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Hand));
        itemLanguage.setRightText(PrefUtil.getString(context, BaseActionUtils.Action_Setting_Language));


        itemShake.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getShakeName(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        itemShake.setRightText(OptionsPickerViewUtils.getShakeName(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Shake, OptionsPickerViewUtils.getShakeName(context).get(i));
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(getApplicationContext()).setShakeMode(2, OptionsPickerViewUtils.getShakeModel(context)[i], 5, null);
                        BleWriteDataTask task = new BleWriteDataTask(getApplicationContext(), bytes);
                        BackgroundThreadManager.getInstance().addTask(task);
                    }

                });
                option.show();
            }
        });
        itemTime.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getTimeItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        itemTime.setRightText(OptionsPickerViewUtils.getTimeItemOptions(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Time_Format, OptionsPickerViewUtils.getTimeItemOptions(context).get(i));
                        BraceletSetting bs1 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Time_Format);
                        bs1.setKey(BaseActionUtils.Action_Setting_Time_Format);
                        bs1.setValue(i);
                        SqlBizUtils.saveBraceletSetting(bs1);
                        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTimeDisplay(context,i);
                    }
                });
                option.show();
            }
        });

        bloodSetting.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                UI.startActivity(ZgSettingActivity.this,BloodSettingActivity.class);
            }
        });

        itemDate.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getDateItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        itemDate.setRightText(OptionsPickerViewUtils.getDateItemOptions(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Date_Format, OptionsPickerViewUtils.getDateItemOptions(context).get(i));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Date_Format);
                        setting.setKey(BaseActionUtils.Action_Setting_Date_Format);
                        setting.setValue(i);
                        SqlBizUtils.saveBraceletSetting(setting);

                    }
                });
                option.show();
            }
        });
        itemUnit.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getUnitItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        itemUnit.setRightText(OptionsPickerViewUtils.getUnitItemOptions(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Unit, OptionsPickerViewUtils.getUnitItemOptions(context).get(i));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Unit);
                        setting.setKey(BaseActionUtils.Action_Setting_Unit);
                        setting.setValue(i);
                        SqlBizUtils.saveBraceletSetting(setting);
                        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setUnitSwitch(context,i);
                    }
                });
                option.show();
            }
        });
        itemWeather.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getWeatherItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        itemWeather.setRightText(OptionsPickerViewUtils.getWeatherItemOptions(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Weather_Unit, OptionsPickerViewUtils.getWeatherItemOptions(context).get(i));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Weather_Unit);
                        setting.setKey(BaseActionUtils.Action_Setting_Weather_Unit);
                        setting.setValue(i);
                        SqlBizUtils.saveBraceletSetting(setting);
                        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTemperatureUnitSwitch(context,i);
                    }
                });
                option.show();
            }
        });

        itemGesture.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                if (b) {
                    PrefUtil.save(context, BaseActionUtils.Action_Setting_Roll, 1);
                } else {
                    PrefUtil.save(context, BaseActionUtils.Action_Setting_Roll, 0);
                }
                BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Roll_Time);
                setting.setKey(BaseActionUtils.Action_Setting_Roll_Time);
                setting.setValue(b ? 1 : 0);
                SqlBizUtils.saveBraceletSetting(setting);

            }
        });

        itemGestureTime.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getHourOptions()[0], OptionsPickerViewUtils.getHourOptions()[1], new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        List<String> start = (List<String>) OptionsPickerViewUtils.getHourOptions()[0];
                        List<List<String>> end = (List<List<String>>) OptionsPickerViewUtils.getHourOptions()[1];
                        itemGestureTime.setRightText(start.get(i) + "-" + end.get(i).get(i1));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Roll_Time, start.get(i) + "-" + end.get(i).get(i1));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Roll_Time);
                        setting.setKey(BaseActionUtils.Action_Setting_Roll_Time);
                        setting.setStartTime(getHour(start.get(i)));
                        setting.setEndTime(getHour(end.get(i).get(i1)));
                        SqlBizUtils.saveBraceletSetting(setting);
                    }
                });
                option.show();
            }
        });


        itemHand.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean b) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getHandItemOptions(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        itemHand.setRightText(OptionsPickerViewUtils.getHandItemOptions(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Hand, OptionsPickerViewUtils.getHandItemOptions(context).get(i));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Hand);
                        setting.setKey(BaseActionUtils.Action_Setting_Hand);
                        setting.setValue(i);
                        SqlBizUtils.saveBraceletSetting(setting);
                    }
                });
                option.show();
            }
        });

        itemLanguage.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(context, OptionsPickerViewUtils.getLanguage(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        itemLanguage.setRightText(OptionsPickerViewUtils.getLanguage(context).get(i));
                        PrefUtil.save(context, BaseActionUtils.Action_Setting_Language, OptionsPickerViewUtils.getLanguage(context).get(i));
                        BraceletSetting setting = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Language);
                        setting.setKey(BaseActionUtils.Action_Setting_Language);
                        setting.setValue(i);
                        SqlBizUtils.saveBraceletSetting(setting);
                        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setLanguage(context,i);
                    }
                });
                option.show();
            }
        });

        itemFirmwareUpdate.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                UI.startActivity((Activity) context, FirmwareUpdateActivity.class);
            }
        });

        itemAlarm.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                UI.startActivity((Activity) context,AddClockActivity.class);
            }
        });

        itemSchedule.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                UI.startActivity((Activity) context,ScheduleActivity.class);
            }
        });

        itemShakeSettingMode.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {

                /**
                 * This is the reference code
                 * shake model:
                 4:Staccato
                 5:Wave
                 7:Heartbeat
                 8:Radiation
                 11:Lighthouse
                 12:Symphony
                 15:Fast

                 shake number:[1-20]
                 */
                ArrayList<Map<String, Integer>> map = new ArrayList<>();
                Map<String, Integer> phoneMap = createMap(4, 10, 1);
                Map<String, Integer> clockMap = createMap(4, 10, 0);
                Map<String, Integer> scheduleMap = createMap(7,10, 5);
                Map<String, Integer> smsMap = createMap(8,10, 2);
                Map<String, Integer> sedentaryMap = createMap(12, 10, 3);
                Map<String, Integer> heartGuideMap = createMap(15,10,7);
                map.add(phoneMap);
                map.add(clockMap);
                map.add(scheduleMap);
                map.add(smsMap);
                map.add(sedentaryMap);
                map.add(heartGuideMap);
                byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setShakeMode(3, 0, 0, map);

                List<ITask> tasks = new ArrayList<>();
                for (int i = 0; i < bytes.length; i += 20) {
                    byte[] writeData = null;
                    BleWriteDataTask task = null;
                    if (i + 20 > bytes.length) {
                        writeData = Arrays.copyOfRange(bytes, i, bytes.length);
                        task = new BleWriteDataTask(context, writeData);
                        task.setFlag(false);
                        tasks.add(task);
                    } else {
                        writeData = Arrays.copyOfRange(bytes, i, i + 20);
                        task = new BleWriteDataTask(context, writeData);
                        task.setFlag(false);
                        tasks.add(task);
                    }
                }
                BackgroundThreadManager.getInstance().addAllTask(tasks);
            }
        });

    }

    @NonNull
    private static Map<String, Integer> createMap(int mode, int num, int type) {
        Map<String, Integer> phoneMap = new HashMap<>();
        phoneMap.put("index", mode);
        phoneMap.put("number", num);
        phoneMap.put("type", type);
        return phoneMap;
    }


    private void sendCmd() {
        BraceletSetting bs1 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Roll_Time);
        BraceletSetting bs2 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Unit);
        BraceletSetting bs3 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Time_Format);
        BraceletSetting bs4 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Language);
        BraceletSetting bs5 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Date_Format);
        BraceletSetting bs6 = SqlBizUtils.querySetting(BaseActionUtils.Action_Setting_Hand);
        int gesture = bs1.getValue();
        int start = bs1.getStartTime();
        int end = bs1.getEndTime();
        int unit = bs2.getValue();
        int timeFormat = bs3.getValue();
        int language = bs4.getValue();
        int dateFormat = bs5.getValue();
        int hand = bs6.getValue();
        KLog.i("gesture" + gesture + "start" + start + "end" + end + "unit" + unit + "timeFormat" + timeFormat + "language" + language + "dateFormat" + dateFormat + "hand" + hand);
        SparseBooleanArray array = new SparseBooleanArray();
        array.put(0, false);
        array.put(1, gesture == 1 ? true : false);
        array.put(2, unit == 1 ? true : false);
        array.put(3, timeFormat == 1 ? false : true);
        array.put(4, true);
        array.put(5, false);
        array.put(6, language == 1 ? true : false);
        array.put(7, false);
        array.put(8, dateFormat == 1 ? true : false);
        array.put(9, true);
        array.put(10, false);
        array.put(11, hand == 1 ? true : false);

        byte[] data = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setWristBandGestureAndLight(array,
                0,
                23,
                language, dateFormat,
                start, end);

        int cmd_nums = data.length % 20 == 0 ? data.length / 20 : data.length / 20 + 1;
        for (int j = 0; j < cmd_nums; j++) {
            byte[] data1 = Arrays.copyOfRange(data, 20 * j, 20 * (j + 1) > data.length ? data.length : 20 * (j + 1));
            BleWriteDataTask task1 = new BleWriteDataTask(context, data1);
            BackgroundThreadManager.getInstance().addTask(task1);
        }
    }

    private int getHour(String hour) {
        int iHour = 0;
        try {
            String hours[] = hour.split(":");
            iHour = Integer.parseInt(hours[0]);
            return iHour;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iHour;
    }

}
