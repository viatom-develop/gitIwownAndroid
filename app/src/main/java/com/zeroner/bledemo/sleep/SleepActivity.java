package com.zeroner.bledemo.sleep;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.TimeLineAdapter;
import com.zeroner.bledemo.bean.data.OrderStatus;
import com.zeroner.bledemo.bean.data.Orientation;
import com.zeroner.bledemo.bean.data.SleepStatusFlag;
import com.zeroner.bledemo.bean.data.SleepTime;
import com.zeroner.bledemo.bean.data.TimeLineModel;
import com.zeroner.bledemo.bean.sql.TB_SLEEP_Final_DATA;
import com.zeroner.bledemo.data.viewData.ViewData;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.bledemo.utils.Util;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.utils.JsonTool;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SleepActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_device_sleep)
    Toolbar toolbarDeviceSleep;
    @BindView(R.id.sleep_recyclerView)
    RecyclerView sleepRecyclerView;
    @BindView(R.id.protu_sleep)
    LinearLayout protuSleep;

    private Context context;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        context=this;
        ButterKnife.bind(this);
        if(SuperBleSDK.isProtoBuf(this)){
            initProView();
        }else{
            initView();
        }

    }

    private void initView() {
        protuSleep.setVisibility(View.GONE);
        sleepRecyclerView.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbarDeviceSleep);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDeviceSleep.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, Orientation.VERTICAL, true);
        sleepRecyclerView.setAdapter(mTimeLineAdapter);
        sleepRecyclerView.setLayoutManager(getLinearLayoutManager());
        sleepRecyclerView.setHasFixedSize(true);
    }

    private void initProView(){
        protuSleep.setVisibility(View.VISIBLE);
        sleepRecyclerView.setVisibility(View.GONE);
        DateUtil dateUtil = new DateUtil();
        dateUtil.addDay(-1);
        String dataFrom = PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME);
        TB_SLEEP_Final_DATA sleep_final_data = DataSupport.where("date=? and data_from=?",dateUtil.getSyyyyMMddDate(),dataFrom).findFirst(TB_SLEEP_Final_DATA.class);
        if(sleep_final_data!=null && !TextUtils.isEmpty(sleep_final_data.getSleep_segment())){
            TextView textViewSt = new TextView(this);
            textViewSt.setTextSize(25);
            TextView textViewEd = new TextView(this);
            textViewEd.setTextSize(25);
            DateUtil stDa = new DateUtil(sleep_final_data.getStart_time(),true);
            DateUtil enDa = new DateUtil(sleep_final_data.getEnd_time(),true);
            textViewSt.setText(getString(R.string.sleep_detail_start)+": "+stDa.getY_M_D_H_M());
            textViewEd.setText(getString(R.string.sleep_detail_end)+": "+enDa.getY_M_D_H_M());
            protuSleep.addView(textViewSt);
            protuSleep.addView(textViewEd);
            List<SleepSegment> segList = JsonTool.getListJson(sleep_final_data.getSleep_segment(),SleepSegment.class);
            if(segList!=null){
                for (SleepSegment sleepSegment : segList) {
                    int times = sleepSegment.getEt() - sleepSegment.getSt();
                    String timeStr = ": "+ times + getString(R.string.sleep_minute);
                    TextView textView = new TextView(this);
                    if(sleepSegment.getType()==3){
                        String msg = getString(R.string.sleep_detail_deep_1) + timeStr;
                        textView.setText(msg);
                    }else if(sleepSegment.getType()==4){
                        String msg = getString(R.string.sleep_detail_light_1) + timeStr;
                        textView.setText(msg);
                    }else{
                        String msg = getString(R.string.sleep_detail_weak_1) + timeStr;
                        textView.setText(msg);
                    }
                    protuSleep.addView(textView);
                }
            }
        }


    }


    private void setDataListItems(){
        DateUtil date=new DateUtil();
        SleepTime sleep=ViewData.sleepDetail(ViewData.deleteSoberSleepData(SqlBizUtils.querySleepData(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME),date.getYear(),date.getMonth(),date.getDay())));
        mDataList.add(new TimeLineModel(100,getString(R.string.sleep_detail_start), Util.minToTime(sleep.getStartMin()),"", OrderStatus.ACTIVE));
        ArrayList<SleepStatusFlag> sleeps = sleep.getSleepStatus();
        for (int i = 0; i <sleeps.size() ; i++) {
            if(sleeps.get(i).isDeepFlag()==SleepStatusFlag.Deep){
                mDataList.add(new TimeLineModel(SleepStatusFlag.Deep,getString(R.string.sleep_detail_deep_1),Util.minToTime(sleeps.get(i).getStartTime()),Util.minToTime(sleeps.get(i).getStartTime()+sleeps.get(i).getTime()), OrderStatus.COMPLETED));
            }else if(sleeps.get(i).isDeepFlag()==SleepStatusFlag.Light){
                mDataList.add(new TimeLineModel(SleepStatusFlag.Light,getString(R.string.sleep_detail_light_1),Util.minToTime(sleeps.get(i).getStartTime()),Util.minToTime(sleeps.get(i).getStartTime()+sleeps.get(i).getTime()), OrderStatus.COMPLETED));
            }else {
                mDataList.add(new TimeLineModel(SleepStatusFlag.Placement,getString(R.string.sleep_detail_placement),Util.minToTime(sleeps.get(i).getStartTime()),Util.minToTime(sleeps.get(i).getStartTime()+sleeps.get(i).getTime()), OrderStatus.COMPLETED));
            }
        }
        mDataList.add(new TimeLineModel(100,getString(R.string.sleep_detail_end), Util.minToTime(sleep.getEndMin()),"", OrderStatus.ACTIVE));
    }


    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }
}
