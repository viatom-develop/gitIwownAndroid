package com.zeroner.bledemo.bean;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.R1Data;
import com.zeroner.bledemo.gps.GpsActivity;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.blemidautumn.library.KLog;

import cn.lemon.view.adapter.BaseViewHolder;

/**
 * 作者：hzy on 2017/12/26 08:44
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class ZgGPSViewHolder extends BaseViewHolder<R1Data> {
    public  TextView mTitle;
    private Context context;
    public ZgGPSViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.fragment_item_heart);
        this.context=context;
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        mTitle= (TextView) findViewById(R.id.card_title_heart);
    }

    @Override
    public void setData(R1Data data) {
        super.setData(data);
        mTitle.setText(String.valueOf(data.getTitle()));
    }

    @Override
    public void onItemViewClick(R1Data data) {
        super.onItemViewClick(data);
        KLog.i(data.toString());
        UI.startActivity((Activity) context, GpsActivity.class);
    }
}
