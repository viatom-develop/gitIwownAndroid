package com.zeroner.bledemo.scan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.ComViewHolder;
import com.zeroner.bledemo.bean.CommonRecyAdapter;
import com.zeroner.bledemo.bean.RecycleViewDivider;
import com.zeroner.bledemo.bean.WrapContentLinearLayoutManager;
import com.zeroner.bledemo.bean.data.ModeItems;
import com.zeroner.bledemo.bean.data.SDKType;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.PermissionUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.bledemo.utils.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceListActivity extends AppCompatActivity {

    @BindView(R.id.lv_device)
    RecyclerView lvDevice;
    @BindView(R.id.toolbar_device)
    Toolbar toolbarDevice;

    private Context context;
    private List<ModeItems.DataBean> data;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        context = this;
        ButterKnife.bind(this);
        PermissionUtils.requestPermission((Activity) context,Manifest.permission.WRITE_EXTERNAL_STORAGE,PermissionUtils.Io_Permission);
        initView();
        initData();
    }


    private void initView() {
        setSupportActionBar(toolbarDevice);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDevice.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvDevice.setLayoutManager(layoutManager);
        lvDevice.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.device_bgk)));

    }

    private void initData() {
        String datas = PrefUtil.getString(context, BaseActionUtils.APP_SDK_UPDATE_Content);
        try {
            if (TextUtils.isEmpty(datas)) {
                datas = Util.getFromAssets(context, "modesdklist2default.txt");
                PrefUtil.save(context, BaseActionUtils.APP_SDK_UPDATE_Content, datas);
            }
            data = new Gson().fromJson(datas, ModeItems.class).getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        myAdapter = new MyAdapter(context, data, R.layout.layout_device_list_item_type);
        lvDevice.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        myAdapter.setOnItemClickListener(new ComViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                UI.startActivity((Activity) context, ScanBleActivity.class, new SDKType(data.get(position).getSdktype()));
                finish();
            }
        });
    }


    class MyAdapter extends CommonRecyAdapter<ModeItems.DataBean> {
        private Context context;

        public MyAdapter(Context context, List<ModeItems.DataBean> dataList, int layoutId) {
            super(context, dataList, layoutId);
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        protected ComViewHolder setComViewHolder(View view, int viewType) {
            return new ViewHolder(view);
        }

        @Override
        public void onBindItem(RecyclerView.ViewHolder holder, int position, ModeItems.DataBean device) {
            super.onBindItem(holder, position, device);
            if (holder instanceof ViewHolder) {
                if (device.getClassid() == 1) {
                    ((ViewHolder) holder).image2DeviceIcon.setImageResource(R.mipmap.bracelet_2x);
                } else if (device.getClassid() == 2) {
                    ((ViewHolder) holder).image2DeviceIcon.setImageResource(R.mipmap.watch_2x);
                }
                ((ViewHolder) holder).itemDeviceType.setText(device.getCategoryname());
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    static class ViewHolder extends ComViewHolder {
        @BindView(R.id.image_2_device_icon)
        ImageView image2DeviceIcon;
        @BindView(R.id.item_device_type)
        TextView itemDeviceType;
        @BindView(R.id.item_device_mac)
        TextView itemDeviceMac;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
