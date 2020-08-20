package com.zeroner.bledemo.setting.schedule;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bigkoo.pickerview.TimePickerView;
import com.socks.library.KLog;
import com.zeroner.bledemo.BaseActivity;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.TB_schedulestatue;
import com.zeroner.bledemo.data.ZGBaseUtils;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.view.LSettingItem;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleActivity extends BaseActivity {

    @BindView(R.id.title_edit)
    EditText titleEdit;
    @BindView(R.id.schedule_start_time)
    LSettingItem scheduleStartTime;
    private DateUtil startDate;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        context = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleEdit.setHint(R.string.schedule_message);
        scheduleStartTime.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                TimePickerView pvTime = new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        KLog.i(new DateUtil(date).getHHmmDate());
                        startDate = new DateUtil(date);
                        scheduleStartTime.setRightText(new DateUtil(date).getHHmmDate());
                    }
                })
                        .setType(new boolean[]{true, true, true, true, true, false})
                        .setCancelText(getString(R.string.common_cacel))
                        .setSubmitText(getString(R.string.common_save))
                        .setLabel("", "", "", "", "", "")
                        .build();
                pvTime.setDate(Calendar.getInstance());
                pvTime.show();
            }
        });

        setLeftBackTo();
        setTitleText(R.string.add_schedule);
        setRightText(getString(R.string.common_save), new ActionOnclickListener() {
            @Override
            public void onclick() {
                if (startDate == null) {
                    startDate = new DateUtil();
                }
                saveSchedule(startDate.getYear(), startDate.getMonth(), startDate.getDay(), startDate.getHour(), startDate.getMinute()
                        , titleEdit.getText().toString().trim(), titleEdit.getText().toString().trim());
                if (ZGBaseUtils.isZG()) {
                    ZGBaseUtils.updateAlarmAndSchedule(context);
                } else if(ZGBaseUtils.isProtoBuf()){
                    byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).addCalendar(1,startDate.getTimestamp(), titleEdit.getText().toString().trim());
                    BackgroundThreadManager.getInstance().addWriteData(context,bytes);
                }else {
                    SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setSchedule(context, startDate.getYear()
                            , startDate.getMonth()
                            , startDate.getDay()
                            , startDate.getHour()
                            , startDate.getMinute()
                            , titleEdit.getText().toString().trim());
                }
                finish();
            }
        });
    }

    private void saveSchedule(int year, int month, int day, int hour, int minute, String item, String remind) {
        TB_schedulestatue tbData = new TB_schedulestatue();
        int times = ScheduleUtil.getTBTimesInt(hour, minute);
        int dates = ScheduleUtil.getTBDatesInt(year, month, day);
        tbData.setDevice_name(PrefUtil.getString(this, BaseActionUtils.ACTION_DEVICE_NAME));
        tbData.setYear(year);
        tbData.setMonth(month);
        tbData.setDay(day);
        tbData.setHour(hour);
        tbData.setMinute(minute);
        tbData.setText(item);
        tbData.setRemind(remind);
        tbData.setTimes(times);
        tbData.setDates(dates);
        tbData.setZg_mode(5);
        tbData.setZg_number(5);
        tbData.save();
    }

}
