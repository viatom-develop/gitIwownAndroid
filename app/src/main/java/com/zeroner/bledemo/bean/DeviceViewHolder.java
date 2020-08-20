package com.zeroner.bledemo.bean;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.DummyItem;
import com.zeroner.bledemo.setting.I7BSettingActivity;
import com.zeroner.bledemo.setting.SettingActivity;
import com.zeroner.bledemo.setting.SportWatchSettingActivity;
import com.zeroner.bledemo.setting.ZgSettingActivity;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;

import cn.lemon.view.adapter.BaseViewHolder;

/**
 * 作者：hzy on 2017/12/26 08:27
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class DeviceViewHolder extends BaseViewHolder<DummyItem> {
    public  TextView mIdName;
    public  TextView mIdMac;
    public  TextView mIdStatue;
    public  TextView mIdBattery;
    public  TextView device_model;
    public  TextView device_version;
    public  TextView mTitle;
    public TextView device_sn;
    private Context context;
    //    public EditText mCtei;
//    public Button mBurnCtei;

    public DeviceViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.fragment_item);
        this.context=context;
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        mTitle= (TextView) findViewById(R.id.card_title);
        mIdName = (TextView) findViewById(R.id.index_device_name);
        mIdMac = (TextView) findViewById(R.id.index_device_address);
        mIdStatue= (TextView) findViewById(R.id.connect_statue);
        mIdBattery= (TextView) findViewById(R.id.device_battery);
        device_version= (TextView) findViewById(R.id.device_version);
        device_model= (TextView) findViewById(R.id.device_model);
        device_sn = (TextView) findViewById(R.id.device_sn);

        //        mCtei = (EditText) findViewById(R.id.ctei);
//        mBurnCtei = (Button) findViewById(R.id.btn_burn_ctei);
    }

    @Override
    public void setData(DummyItem data) {
        super.setData(data);
        mIdName.setText(data.deviceName);
        mIdMac.setText(data.deviceAddress);
        mIdStatue.setText(data.deviceStatue);
        mIdBattery.setText(String.valueOf(data.battery));
        mTitle.setText(String.valueOf(data.title));
        device_version.setText(data.version);
        device_model.setText(data.model);
        device_sn.setText(data.sn);

        //        mCtei.setText(data.ctei);
//
//        mBurnCtei.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!SuperBleSDK.isProtoBuf(context)) {
//                    ToastUtils.showShort("设备类型错误");
//                    return;
//                }
//
//                String ctei = mCtei.getText().toString();
//                if (ctei.length() != 15) {
//                    ToastUtils.showShort("CTEI码错误");
//                    return;
//                }
//                String code = item.sn + ctei;
//
//                LogUtils.d("SN: " + item.sn, "CTEI: " + ctei);
//
//                byte[] bytes1 = ProtoBufSendBluetoothCmdImpl.getInstance().writeHardwareFeatures(code);
//                BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes1);
//                ToastUtils.showShort("烧录成功");
//            }
//        });
    }

    @Override
    public void onItemViewClick(DummyItem data) {
        super.onItemViewClick(data);
        if(BluetoothUtil.isConnected() && SuperBleSDK.isIown(context)){
            UI.startActivity((Activity) context, SettingActivity.class);
        }else if(BluetoothUtil.isConnected() && SuperBleSDK.isZG(context)){
            UI.startActivity((Activity) context, ZgSettingActivity.class);
        }else if(BluetoothUtil.isConnected() && SuperBleSDK.isMtk(context)){
            UI.startActivity((Activity) context, SportWatchSettingActivity.class);
        }else if(BluetoothUtil.isConnected() && SuperBleSDK.isProtoBuf(context)){
            UI.startActivity((Activity) context, I7BSettingActivity.class);
        }
    }
}
