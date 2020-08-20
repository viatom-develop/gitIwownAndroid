package com.zeroner.bledemo.bean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.DummyItem;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ZGSendBluetoothCmdImpl;

import cn.lemon.view.adapter.BaseViewHolder;

/**
 * 作者：hzy on 2017/12/26 08:44
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class CameraViewHolder extends BaseViewHolder<DummyItem> {
    public  TextView mTitle;
    private Context context;
    private Button openBtn;
    private Button closeBtn;

    public CameraViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.fragment_item_camera);
        this.context=context;
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        mTitle= (TextView) findViewById(R.id.card_title_heart);
        openBtn = findViewById(R.id.btn_open_camera);
        closeBtn = findViewById(R.id.btn_close_camera);

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BluetoothUtil.isConnected()){

                    ZGSendBluetoothCmdImpl.getInstance().openCamera(context);


                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ZGSendBluetoothCmdImpl.getInstance().closeCamera(context);


            }
        });
    }

    @Override
    public void setData(DummyItem data) {
        super.setData(data);
//        mTitle.setText(String.valueOf(data.getTitle()));
    }

    @Override
    public void onItemViewClick(DummyItem data) {
        super.onItemViewClick(data);

    }
}
