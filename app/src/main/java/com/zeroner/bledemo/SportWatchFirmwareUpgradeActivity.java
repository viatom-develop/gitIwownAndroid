package com.zeroner.bledemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zeroner.bledemo.service.ZeronerFotaService;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.library.KLog;

import java.io.File;

public class SportWatchFirmwareUpgradeActivity extends AppCompatActivity {

    private TextView mTip;
    private Button mStartBtn;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_watch_firmware_upgrade);
        initView();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ZeronerFotaService.BROADCAST_PROGRESS);
        mIntentFilter.addAction(ZeronerFotaService.BROADCAST_ERROR);

        LocalBroadcastManager.getInstance(this).registerReceiver(upReceiver, mIntentFilter);
    }

    private void startUpGrade() {
        if (!can_start) {
            KLog.e("licl", "can_start: "+can_start);
            return;
        }

        if (!BluetoothUtil.isConnected()) {
            Toast.makeText(this, "Please connect dev first", Toast.LENGTH_SHORT).show();
        }else {

            mTip.setText(R.string.upgrading);

            Intent updateIntent = new Intent(this, ZeronerFotaService.class);
            stopService(updateIntent);

            //according you own path and file, below just a sample
            File fir = new File(Environment.getExternalStorageDirectory()+getString(R.string.firmware_up_dir)+"F1_Firmware_1.1.0.8.bin");

            if (!fir.exists()) {
                Toast.makeText(this, "file not exist: "+fir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                return;
            }

            updateIntent.putExtra(ZeronerFotaService.EXTRA_DEVICE_ADDRESS, SuperBleSDK.createInstance(this).getWristBand().getAddress()+"");
            updateIntent.putExtra(ZeronerFotaService.EXTRA_FILE_PATH, fir.getPath());
            updateIntent.putExtra(ZeronerFotaService.EXTRA_DEVICE_NAME, SuperBleSDK.createInstance(this).getWristBand().getName());
            startService(updateIntent);
        }
    }


    private boolean can_start = true;
    BroadcastReceiver upReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action==ZeronerFotaService.BROADCAST_ERROR) {
                can_start = true;
                mTip.setText(getString(R.string.firmware_up_erro) + intent.getIntExtra(ZeronerFotaService.EXTRA_DATA, -2000));
            }else if(action==ZeronerFotaService.BROADCAST_PROGRESS){

                int progress = intent.getIntExtra(ZeronerFotaService.EXTRA_DATA, 0);
                if (progress>=0) {
                    mTip.setText(getString(R.string.firmware_upgrade_progress, progress));
                    can_start = false;
                }else if(progress==ZeronerFotaService.PROGRESS_COMPLETED){
                    mTip.setText(R.string.upgrade_complete);
                    can_start = true;
                }else {
                    can_start = true;
                }
            }
        }
    };

    private void initView() {
        mTip = (TextView) findViewById(R.id.tip);
        mStartBtn = (Button) findViewById(R.id.start_btn);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUpGrade();
            }
        });
    }

    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(upReceiver);
        super.onDestroy();

    }
}
