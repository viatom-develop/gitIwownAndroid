package com.zeroner.bledemo.gps;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.data.ZGBaseUtils;
import com.zeroner.bledemo.data.ZGDataParsePresenter;
import com.zeroner.blemidautumn.bluetooth.model.ZgAgpsStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ZgAGpsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_progress;
    private boolean isUpdate;
    private Button bt_click;
    private String agpsUrl = "https://search.iwown.com/cep/1513^cep_pak_3days/cep_pak.bin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agps);

        Toolbar toolbar = findViewById(R.id.toolbar_device_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUpdate) {
                    Toast.makeText(ZgAGpsActivity.this, "AGPS update,not close!", Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });
        toolbar.setTitle(R.string.zg_agps_data);


        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        tv_progress = findViewById(R.id.tv_progress);
        bt_click = findViewById(R.id.bt_click);
        bt_click.setOnClickListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventProgress(String a){
        if(a.contains("i7g-apgs-progress")){
            String[] split = a.split(":");
            if(split.length == 2){
                tv_progress.setText("progress: " + split[1] + "%");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventEnd(ZgAgpsStatus status){
        if(status.getState() == 1){
            Toast.makeText(this, "update OK", Toast.LENGTH_SHORT).show();
            tv_progress.setText("progress: update OK" );
        }else {
            tv_progress.setText("progress: update ERROR" );
            Toast.makeText(this, "update error", Toast.LENGTH_SHORT).show();
        }
        isUpdate = false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_click) {
            if(isUpdate){
                Toast.makeText(this, "update,...", Toast.LENGTH_SHORT).show();
                return;
            }
            DownloadUtil.get().download(agpsUrl, "agps_download", new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(String path) {
                    Log.e("path----","path:" + path);
                    isUpdate = true;
                    ZGDataParsePresenter.path = path;
                    ZGDataParsePresenter.status = 1;//先关闭再开启AGPS
                    ZGBaseUtils.endAgps();
                }

                @Override
                public void onDownloading(int progress) {

                }

                @Override
                public void onDownloadFailed() {
                    isUpdate = false;
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isUpdate) {
                Toast.makeText(this, "AGPS update,not close!", Toast.LENGTH_SHORT).show();
                return true;
            }
            return super.onKeyDown(keyCode,event);
        }
        return super.onKeyDown(keyCode,event);
    }

}
