package com.zeroner.bledemo.bean;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.BraceletData;
import com.zeroner.bledemo.bean.data.HeartData;
import com.zeroner.bledemo.heart.HeartDetailActivity;
import com.zeroner.bledemo.setting.SettingActivity;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.blemidautumn.library.KLog;

import cn.lemon.view.adapter.BaseViewHolder;

/**
 * 作者：hzy on 2017/12/26 08:44
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class HeartViewHolder extends BaseViewHolder<HeartData> {
    public  TextView mTitle;
    public  TextView mHeart;
    private Context context;
    public HeartViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.fragment_item_heart);
        this.context=context;
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        mTitle= (TextView) findViewById(R.id.card_title_heart);
        mHeart = (TextView) findViewById(R.id.bracelet_heart);
    }

    @Override
    public void setData(HeartData data) {
        super.setData(data);
        mTitle.setText(String.valueOf(data.getTitle()));
        mHeart.setText(String.valueOf(data.getHeart()));
    }

    @Override
    public void onItemViewClick(HeartData data) {
        super.onItemViewClick(data);
        KLog.i(data.toString());
        UI.startActivity((Activity) context, HeartDetailActivity.class);
    }
}
