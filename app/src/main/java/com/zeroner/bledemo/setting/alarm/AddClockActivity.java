package com.zeroner.bledemo.setting.alarm;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.socks.library.KLog;
import com.zeroner.bledemo.BaseActivity;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.TB_Alarmstatue;
import com.zeroner.bledemo.data.ZGBaseUtils;
import com.zeroner.bledemo.setting.repeat.WeakDaySelectActivity;
import com.zeroner.bledemo.setting.schedule.ScheduleUtil;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.view.LSettingItem;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class AddClockActivity extends BaseActivity {
    public static int STATE_EDIT = 1;
    private static int mState = -1;
    public static int RESULT_CLOCK_DELETE = 88;

    @BindView(R.id.title_edit)
    EditText mTitleEdit;
    @BindView(R.id.start_time)
    LSettingItem mStartTime;
    @BindView(R.id.repeat)
    LSettingItem mRepeat;
    public static final String TYPE = "type";

    private byte bb = (byte) 0xff;
    private int colck_id = -1;

    private static int ADD_CLOCK_REQUEST = 838;
    private String[] daysOfWeek;

    private ClockInfo mClockInfo = new ClockInfo();
    private DateUtil startDate;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clock);
        context=this;
        ButterKnife.bind(this);

        daysOfWeek = this.getResources().getStringArray(R.array.day_of_week);
        initView();
        initData();
    }

    private void initData() {
        mRepeat.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                Intent intent = new Intent(AddClockActivity.this, WeakDaySelectActivity.class);
                intent.putExtra("what_activity", WeakDaySelectActivity.Type_Add_Clock);
                intent.putExtra("day_of_week", (int) bb);
                startActivityForResult(intent, ADD_CLOCK_REQUEST);
            }
        });

        mStartTime.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                setStartTime();
            }
        });
    }


    private void initView() {
        bb = ScheduleUtil.dataByteWeek;
        setTitleText(getString(R.string.add_clock));
        mTitleEdit.setHint(R.string.alarm_message);
        KLog.e(TAG, bb);
        setLeftBackTo();
        setRightText(getString(R.string.common_save), new ActionOnclickListener() {
            @Override
            public void onclick() {
                if (checkWithUi()) {
                    setClockInfo();
                    if (ZGBaseUtils.isZG()) {
                        ZGBaseUtils.updateAlarmAndSchedule(context);
                    }else if(ZGBaseUtils.isProtoBuf()){
                        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).addAlarm(mClockInfo.getId(),
                                true, bb & 0x7f, mClockInfo.getHour(), mClockInfo.getMin(), mClockInfo.getTitle());
                        BackgroundThreadManager.getInstance().addWriteData(context,bytes);
                    }else {
                        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).writeAlarmClock(context, mClockInfo.getId()
                                , mClockInfo.getRepeat()
                                , mClockInfo.getHour()
                                , mClockInfo.getMin()
                                , mClockInfo.getTitle());
                    }
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }


    public void setClockInfo() {
        if(startDate==null){
            startDate=new DateUtil();
        }
        mClockInfo.setId(1);
        mClockInfo.setRepeat(bb);
        mClockInfo.setHour(startDate.getHour());
        mClockInfo.setMin(startDate.getMinute());
        mClockInfo.setTitle(mTitleEdit.getText().toString().trim());
        if (mState == STATE_EDIT) {
            mClockInfo.setOpen(ScheduleUtil.dataIsOpen);
        }
        addAlarm(colck_id,bb,mClockInfo.getHour(),mClockInfo.getMin(),mClockInfo.getTitle(),mClockInfo.getTitle(),10,10);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_CLOCK_REQUEST) {
                bb = data.getByteExtra("week_repeat", (byte) 0x00);
                KLog.e(TAG, (bb & 0xff) & 0x7f);
                setRepeat();
            }
        }
    }

    private void setStartTime() {
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date,View v) {
                KLog.i(new DateUtil(date).getHHmmDate());
                startDate=new DateUtil(date);
                mStartTime.setRightText(new DateUtil(date).getHHmmDate());
            }
        })
        .setType(new boolean[]{false,false,false,true, true,false})
                .setCancelText(getString(R.string.common_cacel))
                .setSubmitText(getString(R.string.common_save))
                .setLabel("","","","","","")
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    public void setRepeat() {
        mRepeat.setRightText(getWeekReptStr(bb));
    }

    public String getWeekReptStr(byte weakRepeat) {
        StringBuilder sb = new StringBuilder();
        if (weakRepeat == (byte) 0xff || weakRepeat == (byte) 0x7f) {
            sb.append(getString(R.string.every_day));
            KLog.e(TAG, "Alarm cycle is full cycle byte-->" + weakRepeat);
            return sb.toString();
        }
        if ((weakRepeat & 0x40) != 0) {
            sb.append(daysOfWeek[0]);
            sb.append(",");
        }
        if ((weakRepeat & 0x20) != 0) {
            sb.append(daysOfWeek[1]);
            sb.append(",");
        }
        if ((weakRepeat & 0x10) != 0) {
            sb.append(daysOfWeek[2]);
            sb.append(",");
        }
        if ((weakRepeat & 0x08) != 0) {
            sb.append(daysOfWeek[3]);
            sb.append(",");
        }
        if ((weakRepeat & 0x04) != 0) {
            sb.append(daysOfWeek[4]);
            sb.append(",");
        }
        if ((weakRepeat & 0x02) != 0) {
            sb.append(daysOfWeek[5]);
            sb.append(",");
        }
        if ((weakRepeat & 0x01) != 0) {
            sb.append(daysOfWeek[6]);
            sb.append(",");
        }

        if (sb.length() == 0) {
            return "";
        }
        sb.delete(sb.length() - 1, sb.length());

        return sb.toString();
    }

    @OnClick(R.id.delete_clock_btn)
    public void onClick() {
        ScheduleUtil.packIDData(colck_id);
        setResult(RESULT_CLOCK_DELETE);
        finish();
    }





    public class ClockInfo {
        private int id;
        private String title;
        private int hour;
        private int min;
        private byte repeat;
        private int week;
        private boolean isOpen = true;

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public byte getRepeat() {
            return repeat;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public void setRepeat(byte repeat) {
            this.repeat = repeat;
        }
    }


    public  void addAlarm( int id, int weekReapt, int hour, int minute, String item, String remind,
                           int shakeMode, int shakeNum){
        TB_Alarmstatue data = new TB_Alarmstatue();

        if(0 == id){
            data.setToDefault("id");
            data.setToDefault("Ac_Idx");
        }
        else{
            data.setId(id);
            data.setAc_Idx(id);
        }

        if(0 == weekReapt){
            data.setToDefault("Ac_Conf");
        }
        else{
            data.setAc_Conf(weekReapt);
        }

        if(0 == hour){
            data.setToDefault("Ac_Hour");
        }
        else{
            data.setAc_Hour(hour);
        }

        if(0 == minute){
            data.setToDefault("Ac_Minute");
        }
        else{
            data.setAc_Minute(minute);
        }
        data.setDevice_name(PrefUtil.getString(this, BaseActionUtils.ACTION_DEVICE_NAME));
        data.setOpenState(1);
        data.setRemind(remind);
        data.setAc_String(item);
        data.setZg_mode(shakeMode);
        data.setZg_number(shakeNum);
        data.save();


        KLog.d(TAG, "addAlarm");
    }


    public boolean checkWithUi() {
        if (!BluetoothUtil.isConnected()) {
            Toast.makeText(this, R.string.disconnected, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
