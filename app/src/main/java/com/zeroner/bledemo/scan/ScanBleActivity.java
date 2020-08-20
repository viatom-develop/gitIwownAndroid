package com.zeroner.bledemo.scan;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.github.ybq.android.spinkit.SpinKitView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.ComViewHolder;
import com.zeroner.bledemo.bean.CommonRecyAdapter;
import com.zeroner.bledemo.bean.RecycleViewDivider;
import com.zeroner.bledemo.bean.WrapContentLinearLayoutManager;
import com.zeroner.bledemo.bean.data.SDKType;
import com.zeroner.bledemo.receiver.BluetoothCallbackReceiver;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.blemidautumn.bean.WristBand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanBleActivity extends AppCompatActivity {

    static {
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = "pull to scan";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "scanning...";
        ClassicsHeader.REFRESH_HEADER_LOADING = "loading";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "Release refresh  ";
        ClassicsHeader.REFRESH_HEADER_FINISH = "scan success";
        ClassicsHeader.REFRESH_HEADER_FAILED = "scan fail";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = "M-d HH:mm";
    }

    @BindView(R.id.toolbar_scan)
    Toolbar toolbarScan;
    @BindView(R.id.lv_device_type)
    RecyclerView lvDeviceType;

    MyAdapter myAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.spin_kit)
    SpinKitView spinKit;
    @BindView(R.id.connect_device_name)
    TextView connectDeviceName;

    private Context context;
    private List<WristBand> list = new ArrayList<>();
    private int sdkType = 0;
    private SDKType sdk;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= LayoutInflater.from(this).inflate(R.layout.activity_scan_ble,null);
        setContentView(view);
        ButterKnife.bind(this);
        context = this;
//        PermissionUtils.requestPermission((Activity) context,Manifest.permission.WRITE_EXTERNAL_STORAGE,PermissionUtils.Io_Permission);
//        PermissionUtils.requestPermission((Activity) context,Manifest.permission.ACCESS_FINE_LOCATION,PermissionUtils.Io_Permission);
        PermissionUtils.permission(PermissionConstants.STORAGE,PermissionConstants.LOCATION).rationale(new PermissionUtils.OnRationaleListener() {
            @Override
            public void rationale(ShouldRequest shouldRequest) {
                shouldRequest.again(true);
            }
        }).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied() {

            }
        }).request();
        handleLOCATION(this);
        initView();
    }

    /**
     * 位置 权限
     *
     * @param activity
     * @param
     */
    public  void handleLOCATION(Activity activity) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取地理位置权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
//            Toast.makeText(activity, "访问地理位置", Toast.LENGTH_LONG).show();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予地理位置权限", Toast.LENGTH_LONG).show();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了地理位置短信", Toast.LENGTH_LONG).show();
                }
            }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, false, tip);
        }
    }

    private void initView() {
        sdk = getIntent().getParcelableExtra(UI.EXTRA_OBJ);
        sdkType = sdk.getSdkType();
        LocalBroadcastManager.getInstance(this).registerReceiver(searchConnectReceiver, BaseActionUtils.getIntentFilter());
        setSupportActionBar(toolbarScan);
        toolbarScan.setTitle(R.string.menu_scan);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarScan.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvDeviceType.setLayoutManager(layoutManager);
        lvDeviceType.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.device_bgk)));

        myAdapter = new MyAdapter(context, list, R.layout.layout_device_list_item_view);
        lvDeviceType.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new ComViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                refreshLayout.setVisibility(View.INVISIBLE);
                spinKit.setVisibility(View.VISIBLE);
                connectDeviceName.setVisibility(View.VISIBLE);
                connectDeviceName.setText(String.format(getString(R.string.scan_connect_device),list.get(position).getName()));
                BleApplication.getInstance().getmService().setSDKType(context, sdkType);
                BluetoothUtil.stopScan();
                BluetoothUtil.connect(list.get(position));
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                KLog.i("onRefresh");
                if (!BluetoothUtil.isScanning()) {
                    list.clear();
                    BluetoothUtil.startScan();
                }
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                KLog.i("onLoadmore");
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
            }
        });
        BluetoothUtil.stopScan();
        BluetoothUtil.startScan();
    }


    private BroadcastReceiver searchConnectReceiver = new BluetoothCallbackReceiver() {

        @Override
        public void onPreConnect() {
            super.onPreConnect();
            //如果发现有发起连接, 更新UI
        }


        @Override
        public void onScanResult(WristBand device) {
            super.onScanResult(device);
            KLog.i("onScanResult" + device.toString());
            if (!list.contains(device) && !device.getName().contains("XXX")) {
                list.add(device);
                Collections.sort(list);
                myAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onBluetoothInit() {
            super.onBluetoothInit();
            KLog.e("=============onBluetoothInit==============");
            finish();
        }

        @Override
        public void connectStatue(boolean isConnect) {
            super.connectStatue(isConnect);
        }
    };


    class MyAdapter extends CommonRecyAdapter<WristBand> {
        private Context context;

        public MyAdapter(Context context, List<WristBand> dataList, int layoutId) {
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
        public void onBindItem(RecyclerView.ViewHolder holder, int position, WristBand device) {
            super.onBindItem(holder, position, device);
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).itemDeviceName.setText(device.getName());
                ((ViewHolder) holder).itemDeviceMac.setText(device.getAddress());
                ((ViewHolder) holder).itemDeviceRssi.setText(String.valueOf(device.getRssi()));
            }
        }

    }

    static class ViewHolder extends ComViewHolder {

        @BindView(R.id.item_device_name)
        TextView itemDeviceName;
        @BindView(R.id.item_device_mac)
        TextView itemDeviceMac;
        @BindView(R.id.item_device_rssi)
        TextView itemDeviceRssi;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(searchConnectReceiver);
    }
}
