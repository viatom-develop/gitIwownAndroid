package com.zeroner.bledemo.gps;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.TB_blue_gps;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.PrefUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * I7G gps数据展示
 */
public class GpsActivity extends AppCompatActivity {

    private List<TB_blue_gps> zgGpsDetailLists;
    private GpsDetailAdapter adapter;

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
        toolbar.setTitle(R.string.zg_gps_detail_data_title);

        initView();

        initData();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rc_r1);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        zgGpsDetailLists = new ArrayList<>();
        adapter = new GpsDetailAdapter(this, zgGpsDetailLists);
        recyclerView.setAdapter(adapter);
    }

    private void initData(){
        String deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
        List<TB_blue_gps> tb_blue_gps = DataSupport.where("data_from=? ", deviceName).limit(50).find(TB_blue_gps.class);
        refreshData(tb_blue_gps);
    }

    private void refreshData(List<TB_blue_gps> tb_blue_gps){
        if(tb_blue_gps != null) {
            zgGpsDetailLists.addAll(tb_blue_gps);
            adapter.notifyDataSetChanged();
        }
    }


}
