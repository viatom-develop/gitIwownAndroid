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

import com.blankj.utilcode.util.ToastUtils;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.data.viewData.C100AGPSPresenter;
import com.zeroner.blemidautumn.bluetooth.model.C100AgpsData;
import com.zeroner.blemidautumn.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class C100GpsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_progress;
    private boolean isUpdate;
    private Button bt_click;
    private Button bt_online;
    private String agpsUrl = "https://offline-live1.services.u-blox.com/GetOfflineData.ashx?token=ALKccrhbDE6DMfGLzob8dQ;gnss=gps;period=1;resolution=1";
    private String agpsOnLineUrl = "http://online-live1.services.u-blox.com/GetOnlineData.ashx?token=ALKccrhbDE6DMfGLzob8dQ;gnss=gps;datatype=alm";

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
                    Toast.makeText(C100GpsActivity.this, "AGPS update,not close!", Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });
        toolbar.setTitle(R.string.zg_agps_data);


        initView();

    }

    private void initView() {
        tv_progress = findViewById(R.id.tv_progress);
        bt_click = findViewById(R.id.bt_click);
        bt_online = findViewById(R.id.bt_online);
        bt_online.setText("Online升级");
        bt_click.setOnClickListener(this);
        bt_online.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAGPS(C100AgpsData c100AgpsData){
        if(c100AgpsData.getIsStatus() == 0){
            if(isUpdate) {
                KLog.e("yyyyy开始AGPS指令");
                C100AGPSPresenter.getInstance().startAgps();
            }else {
                KLog.e("yyyyy检测是否结束");
//                C100AGPSPresenter.getInstance().checkUpdate();
            }
        }else if(c100AgpsData.getIsStatus() == 1){
            KLog.e("yyyyy发送AGPS指令");
            C100AGPSPresenter.getInstance().startAgps();
        }else if(c100AgpsData.getIsStatus() == 2){
            KLog.e("yyyyy检验");
            isUpdate = false;
            C100AGPSPresenter.getInstance().checkUpdate();
        }else if(c100AgpsData.getIsStatus() == 3){
            KLog.e("yyyyy结束");
            C100AGPSPresenter.getInstance().closeApgs();
            isUpdate = false;
//            if(c100AgpsData.getCode() == 1) {
                tv_progress.setText("progress: update OK");
//            }else {
//                tv_progress.setText("progress: update ERROR");
//            }

        }
    }




    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_click) {
            if(isUpdate){
                Toast.makeText(this, "update,...", Toast.LENGTH_SHORT).show();
                return;
            }
            ToastUtils.showLong("下载中,...");
            DownloadUtil.get().download(agpsUrl, "agps_download", new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(String path) {
                    Log.e("path----","path:" + path);
                    isUpdate = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong("下载完成,...开始写指令");
                        }
                    });

                    C100AGPSPresenter.getInstance().init(path);
                    C100AGPSPresenter.getInstance().openApgs();
                }

                @Override
                public void onDownloading(int progress) {

                }

                @Override
                public void onDownloadFailed() {
                    isUpdate = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong("下载失败,点击重试");
                        }
                    });

                }
            });
        }
        if (view.getId() == R.id.bt_online) {
            if(isUpdate){
                Toast.makeText(this, "update,...", Toast.LENGTH_SHORT).show();
                return;
            }
            ToastUtils.showLong("下载中,...");
            DownloadUtil.get().download(agpsOnLineUrl, "agps_download", new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(String path) {
                    Log.e("path----","path:" + path);
                    isUpdate = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong("下载完成,...开始写指令");
                        }
                    });

                    C100AGPSPresenter.getInstance().init(path,C100AGPSPresenter.ONLINE);
                    C100AGPSPresenter.getInstance().openOnlineApgs();
                }

                @Override
                public void onDownloading(int progress) {

                }

                @Override
                public void onDownloadFailed() {
                    isUpdate = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong("下载失败,点击重试");
                        }
                    });

                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventProgress(String a){
        if(a.contains("c100-apgs-progress")){
            String[] split = a.split(":");
            if(split.length == 2){
                tv_progress.setText("progress: " + split[1] + "%");
            }
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
