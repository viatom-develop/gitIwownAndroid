package com.zeroner.bledemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.Constants;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SwitchSdkTypeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.wrist_sdk)
    Button mWristSdk;
    @BindView(R.id.zg_wrist_sdk)
    Button mZgWristSdk;
    @BindView(R.id.sport_watch_sdk)
    Button mSportWatchSdk;
    @BindView(R.id.i7B_sdk)
    Button mProtobufSdk;
    @BindView(R.id.voice)
    Button mVoiceSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        mWristSdk.setOnClickListener(this);
        mZgWristSdk.setOnClickListener(this);
        mSportWatchSdk.setOnClickListener(this);
        mProtobufSdk.setOnClickListener(this);
        mVoiceSdk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        BluetoothUtil.disconnect();
        switch (view.getId()) {
            case R.id.wrist_sdk:
                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Ble_Sdk);
                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Ble_Sdk);
                break;
            case R.id.zg_wrist_sdk:
                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Zg_Sdk);
                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Zg_Sdk);
                break;
            case R.id.sport_watch_sdk:
                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Mtk_Sdk);
                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Mtk_Sdk);
                break;
            case R.id.i7B_sdk:
                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_protobuf_Sdk);
                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_protobuf_Sdk);
                break;
            case R.id.voice:
                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.ZERONER_PROTOBUF_VOICE_SDK);
                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.ZERONER_PROTOBUF_VOICE_SDK);
                break;
            default:
                break;
        }
        PrefUtil.save(this, BaseActionUtils.ACTION_DEVICE_NAME, "");
        PrefUtil.save(this, BaseActionUtils.ACTION_DEVICE_ADDRESS, "");
        PrefUtil.save(this, BaseActionUtils.Action_device_Model, "");
        PrefUtil.save(this, BaseActionUtils.Action_device_version, "");
        PrefUtil.save(this, BaseActionUtils.HAS_SELECT_SDK_FIRST, true);
        EventBus.getDefault().post(Event.Ble_Connect_Statue);
        finish();
    }

//    @OnClick({R.id.wrist_sdk, R.id.zg_wrist_sdk, R.id.sport_watch_sdk, R.id.i7B_sdk,R.id.voice})
//    public void onViewClicked(View view) {
//        BluetoothUtil.disconnect();
//        switch (view.getId()) {
//            case R.id.wrist_sdk:
//                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Ble_Sdk);
//                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Ble_Sdk);
//                break;
//            case R.id.zg_wrist_sdk:
//                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Zg_Sdk);
//                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Zg_Sdk);
//                break;
//            case R.id.sport_watch_sdk:
//                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Mtk_Sdk);
//                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_Mtk_Sdk);
//                break;
//            case R.id.i7B_sdk:
//                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.Zeroner_protobuf_Sdk);
//                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.Zeroner_protobuf_Sdk);
//                break;
//            case R.id.voice:
//                SuperBleSDK.switchSDKTYpe(this.getApplicationContext(), Constants.Bluetooth.ZERONER_PROTOBUF_VOICE_SDK);
//                BleApplication.getInstance().getmService().setSDKType(this.getApplicationContext(), Constants.Bluetooth.ZERONER_PROTOBUF_VOICE_SDK);
//                break;
//            default:
//                break;
//        }
//
//        PrefUtil.save(this, BaseActionUtils.ACTION_DEVICE_NAME, "");
//        PrefUtil.save(this, BaseActionUtils.ACTION_DEVICE_ADDRESS, "");
//        PrefUtil.save(this, BaseActionUtils.Action_device_Model, "");
//        PrefUtil.save(this, BaseActionUtils.Action_device_version, "");
//        PrefUtil.save(this, BaseActionUtils.HAS_SELECT_SDK_FIRST, true);
//        EventBus.getDefault().post(Event.Ble_Connect_Statue);
//        finish();
//    }
}
