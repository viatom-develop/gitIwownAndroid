package com.zeroner.bledemo.gps;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.receiver.BluetoothCallbackReceiver;
import com.zeroner.bledemo.utils.BleReceiverHelper;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.model.ZgGpsDetailInfo;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.utils.JsonTool;

import java.util.ArrayList;
import java.util.List;

public class ZgGpsDetailActivity extends AppCompatActivity {

    private List<ZgGpsDetailInfo.ZgGpsDetailList> zgGpsDetailLists;
    private GpsDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r1_list);

        int gps_position = getIntent().getIntExtra("gps_position", 0);

        Toolbar toolbar = findViewById(R.id.toolbar_device_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle(R.string.zg_gps_detail_data_title);

        BleReceiverHelper.registerBleReceiver(this,new BluetoothCallbackReceiver(){
            @Override
            public void onDataArrived(Context context, int ble_sdk_type, int dataType, String data) {
                super.onDataArrived(context, ble_sdk_type, dataType, data);
                if(data != null){
                    //解析
                    ZgGpsDetailInfo zgGpsDetailInfo = JsonTool.fromJson(data, ZgGpsDetailInfo.class);
                    refreshData(zgGpsDetailInfo);
                }
            }
        });

        //获取GPS总信息
        byte[] gpsTotalData = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).getGpsDetailData(gps_position);
        BackgroundThreadManager.getInstance().addWriteData(this,gpsTotalData);
        initView();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rc_r1);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        zgGpsDetailLists = new ArrayList<>();
//        adapter = new GpsDetailAdapter(this, zgGpsDetailLists);
        recyclerView.setAdapter(adapter);
    }

    private void refreshData(ZgGpsDetailInfo zgGpsDetailInfo){
        if(zgGpsDetailInfo.getZgGpsDetailLists() != null) {
            zgGpsDetailLists.addAll(zgGpsDetailInfo.getZgGpsDetailLists());
            adapter.notifyDataSetChanged();
        }
    }

}
