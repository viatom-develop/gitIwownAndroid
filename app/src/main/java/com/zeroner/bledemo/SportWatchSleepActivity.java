package com.zeroner.bledemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iwown.app.nativeinvoke.SA_SleepBufInfo;
import com.iwown.app.nativeinvoke.SA_SleepDataInfo;
import com.zeroner.bledemo.bean.ComViewHolder;
import com.zeroner.bledemo.bean.CommonRecyAdapter;
import com.zeroner.bledemo.data.sync.MtkToIvHandler;
import com.zeroner.bledemo.utils.DateUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SportWatchSleepActivity extends AppCompatActivity {

    @BindView(R.id.sleep_rcy)
    RecyclerView mSleepRcy;
    @BindView(R.id.top)
    TextView mTop;
    private SA_SleepBufInfo mSleepBufInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_watch_sleep);
        ButterKnife.bind(this);
        mSleepBufInfo = MtkToIvHandler.getP1Sleep(new DateUtil().getSyyyyMMddDate());

        if (null!=mSleepBufInfo) {
            mTop.setText(mSleepBufInfo.inSleepTime.year+2000+"/"+mSleepBufInfo.inSleepTime.month+"/"+mSleepBufInfo.inSleepTime.day+" "
                +mSleepBufInfo.inSleepTime.hour+":"+mSleepBufInfo.inSleepTime.minute+"-" +(mSleepBufInfo.outSleepTime.year+2000)+"/"+mSleepBufInfo.outSleepTime.month+"/"+mSleepBufInfo.outSleepTime.day+" "
                    +mSleepBufInfo.outSleepTime.hour+":"+mSleepBufInfo.outSleepTime.minute);
        }

        if (mSleepBufInfo.sleepdata != null && mSleepBufInfo.sleepdata.length != 0) {
            mSleepRcy.setLayoutManager(new LinearLayoutManager(this));
            mSleepRcy.setAdapter(new CommonRecyAdapter<SA_SleepDataInfo>(this, Arrays.asList(mSleepBufInfo.sleepdata), R.layout.sport_watch_sleep_item_layout) {
                @Override
                protected ComViewHolder setComViewHolder(View view, int viewType) {
                    return new ViewHolder(view);
                }

                @Override
                public void onBindItem(RecyclerView.ViewHolder holder, int position, SA_SleepDataInfo item) {
                    super.onBindItem(holder, position, item);
                    if (item.sleepMode == 4) {
                        ((ViewHolder) holder).mTitle.setText(getString(R.string.sleep_detail_light_1) + ": "
                                + item.startTime.hour + ":" + item.startTime.minute + "-" + item.stopTime.hour + ":" + item.stopTime.minute);
                    } else if (item.sleepMode == 3) {
                        ((ViewHolder) holder).mTitle.setText(getString(R.string.sleep_detail_deep_1) + ": "
                                + item.startTime.hour + ":" + item.startTime.minute + "-" + item.stopTime.hour + ":" + item.stopTime.minute);
                    }
                }
            });
        }


    }

    static class ViewHolder extends ComViewHolder {
        @BindView(R.id.title)
        TextView mTitle;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
