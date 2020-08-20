package com.zeroner.bledemo;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.zeroner.bledemo.bean.data.SDKType;
import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.fragment.ItemFragment;
import com.zeroner.bledemo.scan.ScanBleActivity;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.PermissionUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.blemidautumn.bean.WristBand;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zeroner.bledemo.R.id.main_frame_layout;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    int i = 0;
    int max = 10;
    int min = 0;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ImageView mImageView;

    private Context context;
    private int sdkType = 0;
    int hour = 10;
    private ItemFragment itemFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        timeFormat();
        context = this;
        initView();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.firmware_up_dir));
        if (!file.exists()) {
            file.mkdirs();
        }
        itemFragment = ItemFragment.newInstance(0);
        int commit = getSupportFragmentManager().beginTransaction().add(main_frame_layout, itemFragment).commit();

    }

    private void initView() {
        PermissionUtils.requestPermission((Activity) context, Manifest.permission.BLUETOOTH, PermissionUtils.Io_Permission);
        PermissionUtils.requestPermission((Activity) context, Manifest.permission.BLUETOOTH_ADMIN, PermissionUtils.Io_Permission);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        mImageView.setOnClickListener(this);


        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false);

        BluetoothUtil.checkBluetooth(this, 100);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
//        MtkToIvHandler.getP1Sleep2(148248060888508232L,"watch-P1-7093","20180708");
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.menu_scan:

                    if (!PrefUtil.getBoolean(MainActivity.this, BaseActionUtils.HAS_SELECT_SDK_FIRST)) {
                        Toast.makeText(context, getString(R.string.select_sdk_tip), Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (!TextUtils.isEmpty(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME)) && !TextUtils.isEmpty(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_ADDRESS))) {
                        Toast.makeText(context, getString(R.string.pls_unbind_bracelet), Toast.LENGTH_SHORT).show();
                    } else {
                        UI.startActivity((Activity) context, ScanBleActivity.class, new SDKType(SuperBleSDK.readSdkType(MainActivity.this)));
                    }
                    break;
                case R.id.menu_select_sdk:
                    BluetoothUtil.disconnect(false);
                    PrefUtil.save(context, BaseActionUtils.ACTION_DEVICE_NAME, "");
                    PrefUtil.save(context, BaseActionUtils.ACTION_DEVICE_ADDRESS, "");
                    EventBus.getDefault().post(new Event(Event.Ble_Data_Unbind));
                    UI.startActivity((Activity) context, SwitchSdkTypeActivity.class);
                    break;
                default:
                    break;

            }

            if (!msg.equals("")) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("gavin","gavin->id1111 ");
        int id = item.getItemId();
        Log.d("gavin","gavin->id "+id);
        switch (id) {
            case R.id.nav_unbind:
                PrefUtil.save(context, BaseActionUtils.ACTION_DEVICE_NAME, "");
                PrefUtil.save(context, BaseActionUtils.ACTION_DEVICE_ADDRESS, "");
                PrefUtil.save(context, BaseActionUtils.Action_device_Model,"");
                PrefUtil.save(context, BaseActionUtils.Action_device_version,"");
                if (SuperBleSDK.isMtk(context)) {
                    BluetoothUtil.disconnect(false);
                } else {
                    BluetoothUtil.unbindDevice(false);
                }
                EventBus.getDefault().post(new Event(Event.Ble_Data_Unbind));
                break;
            case R.id.nav_suggestion:
//                SuperBleSDK.getSDKSendBluetoothCmdImpl(this).writeOfflineAgpsLength(this,100);
                byte[] bytes1 = ProtoBufSendBluetoothCmdImpl.getInstance().writeHardwareFeatures("LWW0119060600001");
                BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes1);
                break;
            case R.id.nav_about:
//                byte[] bytes = ProtoBufSendBluetoothCmdImpl.getInstance().setSedentariness(true, 1, 0x7f, 8, 20, 1, 0,false);
//                BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes);
                byte[] rtSensorData = ProtoBufSendBluetoothCmdImpl.getInstance().setAfConf(true,0);
                BackgroundThreadManager.getInstance().addWriteData(context,rtSensorData);
                break;
            case R.id.nav_connect:
                if (BluetoothUtil.isConnected()) {
                    int[] blood = new int[8];
                    for (int i = 0 ; i < blood.length;i++){
                        blood[i] = i+100;
                    }
                    //insert 8 length
                    //SrcSbp_LB(); 0
                    //SrcSbp_HB(); 1
                    //SrcDbp_LB(); 2
                    //SrcDbp_HB(); 3
                    //DstSbp_LB(); 4
                    //DstSbp_HB(); 5
                    //DstDbp_LB(); 6
                    //DstDbp_HB(); 7
                    int[] bvalues = new int[]{0,0,0,0,0,0,0,0};
                    SuperBleSDK.getSDKSendBluetoothCmdImpl(getApplicationContext()).writeWelcomePageText(context,"hello",8,170,0);
//                    SuperBleSDK.getSDKSendBluetoothCmdImpl(this).writeWelcomePageText(this,"111",8,170,0,blood,1);
                    break;
                }
                BluetoothUtil.connect(new WristBand(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME), PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_ADDRESS)));
                break;
            case R.id.nav_disconnect:
                BluetoothUtil.setNeedReconnect(false);
                BluetoothUtil.disconnect();
                break;
            case R.id.nav_notification:
                if (!isEnabled()) {
                    openNotificationAccess();
                }
                break;
            case R.id.nav_ble_log:
//                UI.startActivity(this, LogActivity.class);
//                byte[] battery = ProtoBufSendBluetoothCmdImpl.getInstance().getBattery();
//                BackgroundThreadManager.getInstance().addWriteData(context,battery);
                byte[] rtSensorData1 = ProtoBufSendBluetoothCmdImpl.getInstance().getRtSensorData(1, 3);
                BackgroundThreadManager.getInstance().addWriteData(context,rtSensorData1);
                break;

            case R.id.nav_firmware:
//                byte[] dataInfo = ProtoBufSendBluetoothCmdImpl.getInstance().getDataInfo();
//                BackgroundThreadManager.getInstance().addWriteData(context,dataInfo);
//                byte[] call = ProtoBufSendBluetoothCmdImpl.getInstance().setMsgNotificationNotifyByCall(9999, 0, true, true, true, "管封君", "");
//                BackgroundThreadManager.getInstance().addWriteData(context,call);
//                byte[] hardwareFeatures = ProtoBufSendBluetoothCmdImpl.getInstance().getHardwareFeatures();
//                BackgroundThreadManager.getInstance().addWriteData(context,hardwareFeatures);
                byte[] rtSensorData2 = ProtoBufSendBluetoothCmdImpl.getInstance().getRtSensorData(0, 3);
                BackgroundThreadManager.getInstance().addWriteData(context,rtSensorData2);
                break;
            default:
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
//        SyncData.getInstance().syncDataInfo();
        return true;

    }

    @Override
    public void onClick(View v) {
        byte[] battery = SuperBleSDK.getSDKSendBluetoothCmdImpl(this).getBattery();
        BackgroundThreadManager.getInstance().addWriteData(this, battery);
    }

    private void openNotificationAccess() {
        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void timeFormat(){
        int _t = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (3600 * 1000);
        Log.e("MainActivity",_t+"");

//        byte[] backTimes = ProtoBufSendBluetoothCmdImpl.getInstance().setBackLightTime(15);
//        BackgroundThreadManager.getInstance().addWriteData(context,backTimes);
    }



}
