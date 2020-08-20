package com.zeroner.bledemo.gps;

import android.content.Context;
import android.content.Intent;
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
import com.zeroner.blemidautumn.bluetooth.model.ZgGpsTotalInfo;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.utils.JsonTool;

import java.util.ArrayList;
import java.util.List;

public class ZgGpsListActivity extends AppCompatActivity implements GpsAdapter.OnItemClickListener {

    private GpsAdapter adapter;
    private List<ZgGpsTotalInfo.GpsTotalList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r1_list);

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
        toolbar.setTitle(R.string.zg_gps_data_title);

        BleReceiverHelper.registerBleReceiver(this,new BluetoothCallbackReceiver(){
            @Override
            public void onDataArrived(Context context, int ble_sdk_type, int dataType, String data) {
                super.onDataArrived(context, ble_sdk_type, dataType, data);
                if(data != null){
                    //解析
                    ZgGpsTotalInfo zgGpsTotalInfo = JsonTool.fromJson(data, ZgGpsTotalInfo.class);
                    refreshData(zgGpsTotalInfo);
                }
            }
        });

        //获取GPS总信息
        byte[] gpsTotalData = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).getGpsTotalData();
        BackgroundThreadManager.getInstance().addWriteData(this,gpsTotalData);

        initView();

    }

    private void initView(){
        RecyclerView recyclerView = findViewById(R.id.rc_r1);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();
        adapter = new GpsAdapter(this,list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this,ZgGpsDetailActivity.class);
        intent.putExtra("gps_position",list.get(position).getPosition());
        startActivity(intent);
    }

    private void refreshData(ZgGpsTotalInfo info){
        if(info.getGpsTotalLists() != null) {
            list.addAll(info.getGpsTotalLists());
            adapter.notifyDataSetChanged();
        }
    }
}
