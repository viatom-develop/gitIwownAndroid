package com.zeroner.bledemo.r1;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.R1_effective_data;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.PrefUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class R1ListActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    private List<R1_effective_data> r1_datas;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r1_list);

        RecyclerView recyclerView = findViewById(R.id.rc_r1);

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

        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(manager);

        r1_datas = new ArrayList<>();

        adapter = new MyAdapter(this,r1_datas);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        initDB_R1();

    }

    private void initDB_R1(){
        String dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
        List<R1_effective_data> r1_datas = DataSupport.where("time_id>?  ",0+"").find(R1_effective_data.class);
        this.r1_datas.clear();
        this.r1_datas.addAll(r1_datas);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this,R1DetailActivity.class);
        intent.putExtra("r1_data",r1_datas.get(position));
        startActivity(intent);
    }
}
