package com.zeroner.bledemo.ecg;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.TB_64_data;
import com.zeroner.bledemo.bean.sql.TB_64_index_table;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.Filtering;
import com.zeroner.blemidautumn.library.KLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EcgActivity extends AppCompatActivity {

    private EcgAdapter adapter;
    private List<TB_64_data> list;
    private List<TB_64_index_table> indexTableList;
    private List<String> ecgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);

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

        toolbar.setTitle(R.string.pb_ecg_data);

        initView();
        initdata();
    }

    private void initdata() {
        //查询ECG数据库
        String deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
        DateUtil dateUtil = new DateUtil();
        LogUtils.d("deviceName:"+deviceName+"  "+dateUtil.getMonth()+"  "+dateUtil.getDay());
        list = DataSupport.where("data_from=? and month=? and day=?", deviceName,dateUtil.getMonth()+"",dateUtil.getDay()+"").find(TB_64_data.class);
        LogUtils.d("list:"+ JsonUtils.toJson(list));
        List<TB_64_index_table> index_tables = DataSupport.where("data_from=? and data_ymd=?", deviceName,dateUtil.getSyyyyMMddDate()).limit(50).find(TB_64_index_table.class);
        refreshData(index_tables);
    }

    private void initView(){
        RecyclerView recyclerView = findViewById(R.id.rc_r1);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();
        indexTableList = new ArrayList<>();
        ecgs = new ArrayList<>();
        adapter = new EcgAdapter(this,indexTableList,ecgs);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new EcgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                KLog.d(position);
                List<Integer> tb_64_data = merge(indexTableList.get(position));
                lvbo2(tb_64_data);
            }
        });
    }

    private void refreshData(List<TB_64_index_table> index_tables){
        if(index_tables != null) {
            indexTableList.addAll(index_tables);
            ecgs.addAll(getIndex());
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 通过
     * @return
     */
    private List<String> getIndex(){
        List<String> strings = new ArrayList<>();
        Collections.sort(list);

        for(int j = 0 ; j < indexTableList.size();j++) {
            int seq_start = indexTableList.get(j).getSeq_start();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getSeq() == seq_start) {
                    strings.add(list.get(i).getEcg());
                    break;
                }
            }
        }
        return strings;
    }

    private List<Integer> merge(TB_64_index_table tb_64_index_table){
        List<Integer> listAll = new ArrayList<>();
        int seq_start = tb_64_index_table.getSeq_start();
        int seq_end = tb_64_index_table.getSeq_end();
        Collections.sort(list);
        KLog.e(list);
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0 ; i < list.size();i++){
            if(list.get(i).getSeq() == seq_start){
                startIndex = i;
            }
            if(list.get(i).getSeq() == seq_end - 1){
                endIndex = i;
                break;
            }
        }
        if(startIndex != -1){
            for (int j = startIndex; j < endIndex; j++){
                List<Integer> listJson = JsonUtils.getListJson(list.get(j).getEcg(), Integer.class);
                listAll.addAll(listJson);
            }
         }

        return listAll;
    }

    /**
     * 示例DEMO.
     * 具体情况要根据index_table表判断seq滤波
     * 先init
     * 后filteringMain
     */
    private void lvbo(TB_64_data tb_blue_gps){
        List<Integer> before = new ArrayList<>();
        List<Integer> after = new ArrayList<>();
            //滤波类实例化对象（Filter class instantiation object）
            Filtering filtering = new Filtering();
            filtering.init();
            List<Integer> listJson = JsonUtils.getListJson(tb_blue_gps.getEcg(), Integer.class);
            for (int i = 0 ; i < listJson.size();i++) {
                before.add(listJson.get(i));
                //开始滤波（Start filtering）
                int i1 = filtering.filteringMain(listJson.get(i), true);
                after.add(i1);
            }
            KLog.d("before data: " + before);
            KLog.d("after data" + after);
    }

    /**
     * 示例DEMO.
     * 具体情况要根据index_table表判断seq滤波
     * 先init
     * 后filteringMain
     */
    private void lvbo2(List<Integer> lvbo){
        List<Integer> before = new ArrayList<>();
        List<Integer> after = new ArrayList<>();
        //滤波类实例化对象
        Filtering filtering = new Filtering();
        //初始化
        filtering.init();
        for (int i = 0 ; i < lvbo.size();i++) {
            before.add(lvbo.get(i));
            //开始滤波
            int i1 = filtering.filteringMain(lvbo.get(i), true);
            after.add(i1);
        }
        KLog.d("解析前" + before);
        KLog.write2Sd(before.toString(),"before");
        KLog.write2Sd(after.toString(),"after");
        KLog.d("解析后" + after);
    }
}
