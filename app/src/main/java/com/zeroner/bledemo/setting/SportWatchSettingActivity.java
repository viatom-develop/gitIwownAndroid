package com.zeroner.bledemo.setting;

import android.content.Context;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.SportWatchFirmwareUpgradeActivity;
import com.zeroner.bledemo.receiver.BluetoothCallbackReceiver;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.OptionsPickerViewUtils;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.bledemo.view.LSettingItem;
import com.zeroner.bledemo.view.LoadingDialog;
import com.zeroner.blemidautumn.Constants;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.BaseSendBluetoothCmdImpl;
import com.zeroner.blemidautumn.bluetooth.model.FMdeviceInfo;
import com.zeroner.blemidautumn.bluetooth.model.IWDevSetting;
import com.zeroner.blemidautumn.bluetooth.model.ScheduleResult;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zeroner.bledemo.utils.BluetoothUtil.context;

public class SportWatchSettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_device_setting)
    Toolbar mToolbarDeviceSetting;
    //    @BindView(R.id.title_layout_setting)
//    AppBarLayout mTitleLayoutSetting;

    @BindView(R.id.item_time)
    LSettingItem mItemTime;
    @BindView(R.id.item_date)
    LSettingItem mItemDate;
    @BindView(R.id.item_unit)
    LSettingItem mItemUnit;
    @BindView(R.id.item_weather)
    LSettingItem mItemWeather;
    @BindView(R.id.item_gesture)
    LSettingItem mItemGesture;
    @BindView(R.id.item_gesture_time)
    LSettingItem mItemGestureTime;
    @BindView(R.id.item_hand)
    LSettingItem mItemHand;
    @BindView(R.id.item_language)
    LSettingItem mItemLanguage;
    @BindView(R.id.item_firmware_update)
    LSettingItem mItemFirmwareUpdate;
    @BindView(R.id.container)
    LinearLayout mContainer;

    LoadingDialog mDialog = null;
    @BindView(R.id.fimeware_info)
    TextView mFimewareInfo;
    @BindView(R.id.auto_hr)
    LSettingItem mAutoHr;
    @BindView(R.id.smart_track)
    LSettingItem mSmartTrack;
    @BindView(R.id.auto_sleep)
    LSettingItem mAutoSleep;
    @BindView(R.id.base_setting_title)
    TextView mBaseSettingTitle;
    @BindView(R.id.more_func)
    TextView mMoreFunc;
    @BindView(R.id.item_push_message)
    LSettingItem mItemPushMessage;
    @BindView(R.id.item_push_call_message)
    LSettingItem mItemPushCallMessage;
    @BindView(R.id.item_shake)
    LSettingItem mItemShake;
    @BindView(R.id.set_item_shake)
    LSettingItem mSetItemShake;
    @BindView(R.id.no_disturb_all_day)
    LSettingItem mNoDisturbAllDay;
    @BindView(R.id.no_disturb_when_sleep)
    LSettingItem mNoDisturbWhenSleep;
    @BindView(R.id.no_disturb_as_time_segment)
    LSettingItem mNoDisturbAsTimeSegment;
    @BindView(R.id.get_disturb_setting_info)
    LSettingItem mGetDisturbSettingInfo;
    @BindView(R.id.no_disturb_info)
    TextView mNoDisturbInfo;
    @BindView(R.id.clear_no_disturb_settings)
    LSettingItem mClearNoDisturbSettings;
    @BindView(R.id.write_alarm)
    LSettingItem mWriteAlarm;
    @BindView(R.id.close_alarm)
    LSettingItem mCloseAlarm;
    @BindView(R.id.get_alarm_info)
    LSettingItem mGetAlarmInfo;
    @BindView(R.id.alarm_info)
    TextView mAlarmInfo;
    @BindView(R.id.write_schedule)
    LSettingItem mWriteSchedule;
    @BindView(R.id.close_schedule)
    LSettingItem mCloseSchedule;
    @BindView(R.id.clear_all_schedule)
    LSettingItem mClearAllSchedule;
    @BindView(R.id.schedule_info)
    TextView mScheduleInfo;
    @BindView(R.id.get_schedule_support_info)
    LSettingItem mGetScheduleSupportInfo;
    @BindView(R.id.set_sedentary)
    LSettingItem mSetSedentary;
    @BindView(R.id.get_sedentary_info)
    LSettingItem mGetSedentaryInfo;
    @BindView(R.id.sedentary_info)
    TextView mSedentaryInfo;
    @BindView(R.id.write_user_info)
    LSettingItem mWriteUserInfo;
    @BindView(R.id.selfie)
    LSettingItem mSelfie;
    @BindView(R.id.key_model_text)
    TextView mKeyModelText;
    private IWDevSetting mDevSetting;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_watch_setting);
        ButterKnife.bind(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, BaseActionUtils.getIntentFilter());
        mDialog = new LoadingDialog(this, true);
        mDialog.show();

        byte[] information = getCmdSendImpl().getFirmwareInformation();
        sendCmd(information);
        byte[] settings = getCmdSendImpl().getDeviceStateDate();
        sendCmd(settings);

        initEvent();
    }

    private void initEvent() {

        mWriteUserInfo.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                //wirte user info 2 dev (according your own users' datas)
                /**
                 * param: steps is useless now
                 */
                sendCmd(getCmdSendImpl().setUserProfile(170, 60, true, 25, 10000));
            }
        });


        mItemTime.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mDevSetting.setIs24Hour(mDevSetting.getIs24Hour() == 0 ? 1 : 0);
                refreshSettingsUi();
                sendBaseSettingCmd();
            }
        });

        mItemDate.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mDevSetting.setIs_dd_mm_format(mDevSetting.getIs_dd_mm_format() == 0 ? 1 : 0);
                sendBaseSettingCmd();
                refreshSettingsUi();
            }
        });

        mItemUnit.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mDevSetting.setUnit(mDevSetting.getUnit() == 0 ? 1 : 0);
                sendBaseSettingCmd();
                refreshSettingsUi();
            }
        });

        mItemGesture.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                KLog.e("licl", "mItemGesture: " + isChecked);
                mDevSetting.setGesture(isChecked ? 1 : 0);
                sendBaseSettingCmd();
            }
        });

        mAutoHr.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mDevSetting.setAutoHr(isChecked ? 1 : 0);
                sendBaseSettingCmd();
            }
        });

        mAutoSleep.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mDevSetting.setAutoSleep(isChecked ? 1 : 0);
                sendBaseSettingCmd();
            }
        });

        mSmartTrack.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mDevSetting.setIsSmartTrackOpen(isChecked ? 1 : 0);
                sendBaseSettingCmd();
            }
        });

        mItemGestureTime.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                OptionsPickerView option = OptionsPickerViewUtils.getOptionsPickerView(SportWatchSettingActivity.this, OptionsPickerViewUtils.getHourOptions()[0], OptionsPickerViewUtils.getHourOptions()[1], new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        List<String> start = (List<String>) OptionsPickerViewUtils.getHourOptions()[0];
                        List<List<String>> end = (List<List<String>>) OptionsPickerViewUtils.getHourOptions()[1];
                        mDevSetting.setReverse_light_St(getHour(start.get(i)));
                        mDevSetting.setReverse_light_Et(getHour(end.get(i).get(i1)) - 1);
                        refreshSettingsUi();
                        sendBaseSettingCmd();
                    }
                });
                option.show();
            }
        });


        mItemHand.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mDevSetting.setWearingManner(mDevSetting.getWearingManner() == 0 ? 1 : 0);
                sendBaseSettingCmd();
                refreshSettingsUi();
            }
        });

        mItemLanguage.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {

                OptionsPickerView option2 = OptionsPickerViewUtils.getOptionsPickerView(SportWatchSettingActivity.this, OptionsPickerViewUtils.getLanguage(context), new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int i, int i1, int i2, View view) {
                        mDevSetting.setLanguage(i);
                        refreshSettingsUi();
                        sendBaseSettingCmd();
                    }
                });
                option2.show();
            }
        });

        mItemWeather.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                Log.e("licl", "dianjile");
                byte[] bytes = getCmdSendImpl().setWeather(0, 25, 0, 13);
                sendCmd(bytes);
            }
        });

        mItemPushMessage.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                getCmdSendImpl().writeAlertFontLibrary(SportWatchSettingActivity.this, 2, "hello world");
            }
        });

        mItemPushCallMessage.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                getCmdSendImpl().writeAlertFontLibrary(SportWatchSettingActivity.this, 1, "18200717289");
            }
        });


        mItemShake.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                //this method only can be used for experience shake
                /**
                 * 1.type 2
                 * 2.shakeModeIndex 0~16
                 * 3.num >=0
                 * 4.model null
                 */
                getCmdSendImpl().setShakeMode2(2, 4, 3, null);
            }
        });


        mSetItemShake.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                //this method can real set remind mode and remind times to device
                ArrayList<Map<String, Integer>> model = new ArrayList<Map<String, Integer>>();
                Map<String, Integer> messageMap = new HashMap<String, Integer>();
                Map<String, Integer> phoneMap = new HashMap<String, Integer>();
                //index: mode index 0-17
                messageMap.put("index", 4);
                //number: notification counts
                messageMap.put("number", 3);
                //type 0.alarm 1.phone call 2.message 3.seat for long time 4.charging 5.schedule 6.usual
                messageMap.put("type", 2);
                phoneMap.put("index", 3);
                phoneMap.put("number", 4);
                phoneMap.put("type", 1);
                model.add(messageMap);
                model.add(phoneMap);
                /**
                 * 1.type 3
                 * 2.shakeModeIndex 0~16
                 * 3.num >=0
                 * 4.model
                 */
                getCmdSendImpl().setShakeMode2(3, 0, 0, model);
            }
        });

        mNoDisturbAllDay.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                sendCmd(getCmdSendImpl().setQuietMode(1));
            }
        });

        mNoDisturbWhenSleep.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                sendCmd(getCmdSendImpl().setQuietMode(0));
            }
        });

        mNoDisturbAsTimeSegment.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                Calendar calendar = Calendar.getInstance();
                sendCmd(getCmdSendImpl().setQuietMode(calendar.get(Calendar.HOUR), 0, calendar.get(Calendar.HOUR) + 2, 0));
            }
        });

        mGetDisturbSettingInfo.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                sendCmd(getCmdSendImpl().getQuietModeInfo());
            }
        });

        mClearNoDisturbSettings.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                sendCmd(getCmdSendImpl().clearQuietMode());
            }
        });

        mWriteAlarm.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                /**
                 * id :0-7
                 * weekreapt byte
                 * "bit7: if 1, repeat
                 *  bit0: if 1,enable on sunday
                 *  bit1: if 1, enable on saturday
                 *  bit2: if 1,enable on friday
                 *  bit3: if 1, enable on thirsday
                 *  bit4: if 1, enable on wenseday
                 *  bit5: if 1, enable on tuesday
                 *  bit6: if 1, enable on monday"
                 *   hour hour
                 *   min  min
                 */
                Calendar calendar = Calendar.getInstance();
                getCmdSendImpl().writeAlarmClock(context, 0, 0xff, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE) + 2, "I am Alarm");
            }
        });

        mCloseAlarm.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                getCmdSendImpl().closeAlarm(0, context);
            }
        });

        mGetAlarmInfo.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                getCmdSendImpl().getAlarmClock(context, 0);
            }
        });

        mWriteSchedule.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                mCalendar = Calendar.getInstance();
                getCmdSendImpl().setSchedule(context, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1,
                        mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE) + 2, "I am Schedule");
            }
        });

        mCloseSchedule.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                //close acorrding your time
                getCmdSendImpl().closeSchedule(context, 2018, 4, 18, 11, 25);
            }
        });

        mClearAllSchedule.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                getCmdSendImpl().clearAllSchedule(context);
            }
        });

        mGetScheduleSupportInfo.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                getCmdSendImpl().readScheduleInfo(context);
            }
        });

        mSetSedentary.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                /**
                 * id 0-2
                 * * week byte
                 * "bit7: if 1, repeat
                 *  bit0: if 1,enable on sunday
                 *  bit1: if 1, enable on saturday
                 *  bit2: if 1,enable on friday
                 *  bit3: if 1, enable on thirsday
                 *  bit4: if 1, enable on wenseday
                 *  bit5: if 1, enable on tuesday
                 *  bit6: if 1, enable on monday"
                 *   starthour hour
                 *   endHour hour
                 *
                 *   alertDuration && workCount:  in alertDuration time (5 min below) if step less than workCount(below 200)
                 *   watch will remind you
                 */
                sendCmd(getCmdSendImpl().setSedentary(0, 0xff, 8, 20, 5, 200));
            }
        });

        mGetSedentaryInfo.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                sendCmd(getCmdSendImpl().getSedentary());
            }
        });

        mSelfie.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                /**
                 * flag true:open false:cancel
                 * when true: A icon will show on device screen, you can click it for gesture callback
                 */
                sendCmd(getCmdSendImpl().setWristBandSelfie(isChecked));
            }
        });

        mItemFirmwareUpdate.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                if (BluetoothUtil.isConnected()) {
                    UI.startActivity(SportWatchSettingActivity.this, SportWatchFirmwareUpgradeActivity.class);
                }else {
                    Toast.makeText(SportWatchSettingActivity.this, "Please connect device first", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void sendCmd(byte[] bytes) {
        BackgroundThreadManager.getInstance().addWriteData(context, bytes);
    }

    private BaseSendBluetoothCmdImpl getCmdSendImpl() {
        return SuperBleSDK.getSDKSendBluetoothCmdImpl(SportWatchSettingActivity.this);
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

    private BluetoothCallbackReceiver myReceiver = new BluetoothCallbackReceiver() {
        @Override
        public void onDataArrived(Context context, int ble_sdk_type, int dataType, String data) {
            super.onDataArrived(context, ble_sdk_type, dataType, data);
            //sport_watch mode
            if (ble_sdk_type == Constants.Bluetooth.Zeroner_Mtk_Sdk) {
                //get settings from device
                if (dataType == 0x19) {
                    mDialog.dismiss();
                    mDevSetting = new Gson().fromJson(data, IWDevSetting.class);
                    refreshSettingsUi();

                } else if (dataType == 0x00) {
                    //get firmwareinfo
                    mFimewareInfo.setText(getString(R.string.firmwareinfo) + " " + data);
                    FMdeviceInfo fMdeviceInfo = new Gson().fromJson(data, FMdeviceInfo.class);
                    mItemFirmwareUpdate.setRightText(fMdeviceInfo.getSwversion());
                } else if (dataType == 0x06) {
                    //no disturb info
                    mNoDisturbInfo.setText(data);
                } else if (dataType == 0x15) {
                    //alarm info
                    mAlarmInfo.setText("Alarm: " + data);
                } else if (dataType == 0x1e) {
                    //schedule info
                    mScheduleInfo.setText("Schedule: " + data);
                } else if (dataType == 0x1d) {
                    //judge the schedule is written successfully
                    ScheduleResult scheduleResult = new Gson().fromJson(data, ScheduleResult.class);

                    if (scheduleResult.getResult() == 1) {
                        //success
                        mWriteSchedule.setRightText("Success");
                    } else {
                        //fail
                        mWriteSchedule.setRightText("Fail");
                    }

                    mWriteSchedule.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWriteSchedule.setRightText("");
                        }
                    }, 5000);
                } else if (dataType == 0x17) {
                    //sedentary info
                    mSedentaryInfo.setText("SedentaryInfo: " + data);
                } else if(dataType==0x40){
                    mKeyModelText.setText("KeyModel: "+data);
                }
            }
        }


    };

    private void refreshSettingsUi() {
        mItemTime.setRightText(mDevSetting.getIs24Hour() == 0 ? getString(R.string.setting_dialog_time_24)
                : getString(R.string.setting_dialog_time_12));
        mItemDate.setRightText(mDevSetting.getIs_dd_mm_format() == 0 ? getString(R.string.setting_dialog_date_1)
                : getString(R.string.setting_dialog_date_2));
        mItemUnit.setRightText(mDevSetting.getUnit() == 0 ? getString(R.string.setting_dialog_unit_1)
                : getString(R.string.setting_dialog_unit_2));
        mItemGesture.setChecked(mDevSetting.getGesture() == 0 ? false : true);

        //-1 means this device not support this function
        if (mDevSetting.getReverse_light_Et() != -1) {
            mItemGestureTime.setRightText(mDevSetting.getReverse_light_St() + ":00-" + mDevSetting.getReverse_light_Et() + ":00");
        } else {
            mItemGestureTime.setVisibility(View.GONE);
        }


        if (mDevSetting.getWearingManner() != -1) {
            mItemHand.setRightText(mDevSetting.getWearingManner() == 0 ? getString(R.string.setting_dialog_hand_1) :
                    getString(R.string.setting_dialog_hand_2));
        } else {
            mItemHand.setVisibility(View.GONE);
        }

        if (mDevSetting.getAutoHr() != -1) {
            mAutoHr.setChecked(mDevSetting.getAutoHr() == 0 ? false : true);
        } else {
            mAutoHr.setVisibility(View.GONE);
        }


        if (mDevSetting.getIsSmartTrackOpen() != -1) {
            mSmartTrack.setChecked(mDevSetting.getIsSmartTrackOpen() == 0 ? false : true);
        } else {
            mSmartTrack.setVisibility(View.GONE);
        }

        if (mDevSetting.getAutoSleep() != -1) {
            mAutoSleep.setChecked(mDevSetting.getAutoSleep() == 0 ? false : true);
        } else {
            mAutoSleep.setVisibility(View.GONE);
        }

        mItemLanguage.setRightText(getLanguageString(mDevSetting.getLanguage()));
    }


    private String getLanguageString(int ble_language_code) {
        switch (ble_language_code) {
            case 0x00:
                return getString(R.string.language_english);
            case 0x01:
                return getString(R.string.language_chinese);
            case 0x02:
                return getString(R.string.language_italian);
            case 0x03:
                return getString(R.string.language_japan);
            case 0x04:
                return getString(R.string.language_french);
            case 0x05:
                return getString(R.string.language_german);
            case 0x06:
                return getString(R.string.language_portuguese);
            case 0x07:
                return getString(R.string.language_spanish);
            case 0x08:
                return getString(R.string.language_russian);
            case 0x09:
                return getString(R.string.language_korean);
            case 0x10:
                return getString(R.string.language_arabic);
            case 0x11:
                return getString(R.string.language_vietnamese);
            case 0x12:
                return getString(R.string.language_polish);
            case 0xff:
                return getString(R.string.language_simple);
        }
        return getString(R.string.language_english);
    }

    public void sendBaseSettingCmd() {
        SparseIntArray array = new SparseIntArray();
        //gestureSwitch
        array.put(1, mDevSetting.getGesture());
        //unitType
        array.put(2, mDevSetting.getUnit());
        //timeFlag
        array.put(3, mDevSetting.getIs24Hour());
        //sleepFlag
        array.put(4, mDevSetting.getAutoSleep());

        //language
        array.put(8, mDevSetting.getLanguage());

        //dateFormat
        array.put(10, mDevSetting.getIs_dd_mm_format());
        //gb_bl_st
        array.put(11, mDevSetting.getReverse_light_St());
        //gb_bl_et
        array.put(12, mDevSetting.getReverse_light_Et());
        //auto_hr
        array.put(13, mDevSetting.getAutoHr());
        //auto_spt
        array.put(14, mDevSetting.getIsSmartTrackOpen());
        //wearing_manner
        array.put(15, mDevSetting.getWearingManner());
        getCmdSendImpl().setWristBandGestureAndLight2(array);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
