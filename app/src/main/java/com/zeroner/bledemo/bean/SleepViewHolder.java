package com.zeroner.bledemo.bean;
import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.SportWatchSleepActivity;
import com.zeroner.bledemo.bean.data.SleepViewData;
import com.zeroner.bledemo.setting.SportWatchSettingActivity;
import com.zeroner.bledemo.sleep.SleepActivity;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;

import cn.lemon.view.adapter.BaseViewHolder;

/**
 * 作者：hzy on 2017/12/26 08:44
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class SleepViewHolder extends BaseViewHolder<SleepViewData> {
    public  TextView mTotal;
    public  TextView mLightTime;
    public  TextView mDeepTime;
    public  TextView mTitle;
    public TextView weakTime;
    private Context context;
    public SleepViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.fragment_item_sleep);
        this.context=context;
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        mTitle= (TextView) findViewById(R.id.card_title);
        mTotal = (TextView) findViewById(R.id.sleep_total_time);
        mLightTime = (TextView) findViewById(R.id.light_sleep_time);
        mDeepTime= (TextView) findViewById(R.id.deep_sleep_time);
        weakTime= (TextView) findViewById(R.id.weak_sleep_time);
    }

    @Override
    public void setData(SleepViewData data) {
        super.setData(data);
        mTitle.setText(String.valueOf(data.getTitle()));
        mTotal.setText(String.valueOf(data.getTotal()));
        mLightTime.setText(String.valueOf(data.getLight()));
        mDeepTime.setText(String.valueOf(data.getDeep()));
        weakTime.setText(String.valueOf(data.getWeak()));
    }

    @Override
    public void onItemViewClick(SleepViewData data) {
        super.onItemViewClick(data);
        if (!SuperBleSDK.isMtk(context)) {
            UI.startActivity((Activity) context,SleepActivity.class);
        }else if(SuperBleSDK.isMtk(context)){
            UI.startActivity((Activity) context, SportWatchSleepActivity.class);
        }
    }
}
