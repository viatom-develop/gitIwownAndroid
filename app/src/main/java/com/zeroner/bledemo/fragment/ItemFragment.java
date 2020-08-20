package com.zeroner.bledemo.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.HeartEvent;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.CameraViewHolder;
import com.zeroner.bledemo.bean.DeviceViewHolder;
import com.zeroner.bledemo.bean.HeartViewHolder;
import com.zeroner.bledemo.bean.R1ViewHolder;
import com.zeroner.bledemo.bean.SleepViewHolder;
import com.zeroner.bledemo.bean.SportViewHolder;
import com.zeroner.bledemo.bean.ZgAgpsViewHolder;
import com.zeroner.bledemo.bean.ZgGPSViewHolder;
import com.zeroner.bledemo.bean.data.HeartData;
import com.zeroner.bledemo.data.sync.MTKHeadSetSync;
import com.zeroner.bledemo.data.sync.MtkSync;
import com.zeroner.bledemo.data.sync.ProtoBufSync;
import com.zeroner.bledemo.data.sync.SyncData;
import com.zeroner.bledemo.data.viewData.ViewData;
import com.zeroner.bledemo.eventbus.EpoEvent;
import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import cn.lemon.view.RefreshRecyclerView;
import cn.lemon.view.adapter.Action;
import cn.lemon.view.adapter.BaseViewHolder;
import cn.lemon.view.adapter.CustomMultiTypeAdapter;
import cn.lemon.view.adapter.IViewHolderFactory;

import static com.zeroner.bledemo.utils.BluetoothUtil.context;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ItemFragment extends Fragment implements IViewHolderFactory {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    //RefreshRecyclerView init
    RefreshRecyclerView mRecyclerView;
    CustomMultiTypeAdapter mAdapter;
    //type device
    private final int VIEW_TYPE_DEVICE = 1;
    //type sport
    private final int VIEW_TYPE_SPORT = 2;
    //type heart
    private final int VIEW_TYPE_HEART = 3;
    //type sleep
    private final int VIEW_TYPE_SLEEP = 4;

    private final int VIEW_TYPE_R1 = 5;

    private final int VIEW_TYPE_ZG_GPS = 6;

    private final int VIEW_TYPE_ZG_AGPS = 7;

    private final int VIEW_TYPE_PROTOBUF_ECG = 8;

    private final int VIEW_TYPE_CAMERA = 9;

    private TextView progress;

    private HeartViewHolder heartViewHolder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        sendTestMsg();
        KLog.e("时区"+Util.getTimeZone());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_view);
        progress = (TextView) view.findViewById(R.id.sync_progress);
        mRecyclerView.setSwipeRefreshColors(0xFF437845, 0xFFE44F98, 0xFF2FAC21);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CustomMultiTypeAdapter(getContext());
        mAdapter.setViewHolderFactory(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addRefreshAction(new Action() {
            @Override
            public void onAction() {
                getData(true);
                if (BluetoothUtil.isConnected() && (SuperBleSDK.isIown(getContext()) || SuperBleSDK.isZG(getContext()))) {
                    SyncData.getInstance().syncDataInfo();
                } else if (BluetoothUtil.isConnected() && SuperBleSDK.isMtk(getContext())) {
                    //start get data index table and then get data
                    String data_from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
                    if (data_from.toUpperCase().contains("VOICE")) {
                        //r1
                        MTKHeadSetSync.getInstance().syncDataInfo();
                    } else {
                        MtkSync.getInstance(context).getDatasIndexTables();
                    }
                } else if (BluetoothUtil.isConnected() && SuperBleSDK.isProtoBuf(getContext())) {
                    KLog.d("gavin下拉刷新了-> ");
//                    //暂时同步电量和时间
                    byte[] battery = SuperBleSDK.getSDKSendBluetoothCmdImpl(getContext()).getBattery();
                    BackgroundThreadManager.getInstance().addWriteData(getContext(), battery);
                    byte[] time = SuperBleSDK.getSDKSendBluetoothCmdImpl(getContext()).setTime();
                    BackgroundThreadManager.getInstance().addWriteData(getContext(), time);
                    ProtoBufSync.getInstance().syncData();
                }
            }
        });
        if(BluetoothUtil.isHaveAddress()){
            getData(true);
        }
    }

    /**
     * refresh data
     *
     * @param isRefresh
     */
    public void getData(final boolean isRefresh) {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRefresh) {
                    mAdapter.clear();
                    mRecyclerView.dismissSwipeRefresh();
                }
                DateUtil date = new DateUtil();
                mAdapter.addAll(ViewData.getDeviceData(getContext()), VIEW_TYPE_DEVICE);
                mAdapter.addAll(ViewData.getSportData(getContext()), VIEW_TYPE_SPORT);
                mAdapter.addAll(ViewData.getHeartData(getContext()), VIEW_TYPE_HEART);
                mAdapter.addAll(ViewData.getSleepData(getContext()
                        , PrefUtil.getString(getContext(), BaseActionUtils.ACTION_DEVICE_NAME)
                        , date.getYear()
                        , date.getMonth()
                        , date.getDay())
                        , VIEW_TYPE_SLEEP);
                if (SuperBleSDK.isMtk(context)) {
                    mAdapter.addAll(ViewData.getR1data(context), VIEW_TYPE_R1);
                }
                if (SuperBleSDK.isZG(context)) {
                    mAdapter.addAll(ViewData.getZgGpsData(context), VIEW_TYPE_ZG_GPS);
                    mAdapter.addAll(ViewData.getZgAGpsData(context), VIEW_TYPE_ZG_AGPS);
                }
                if(SuperBleSDK.isProtoBuf(context)){
                    mAdapter.addAll(ViewData.getECGData(context), VIEW_TYPE_PROTOBUF_ECG);
                }
                if(SuperBleSDK.isVoice(context)){
                    mAdapter.clear();
                    mAdapter.addAll(ViewData.getDeviceData(getContext()), VIEW_TYPE_DEVICE);
                    mAdapter.addAll(ViewData.getHeartData(context), VIEW_TYPE_HEART);
                }
                mAdapter.addAll(ViewData.getDeviceData(getContext()),VIEW_TYPE_CAMERA);
                if (isRefresh) {
                    mRecyclerView.getRecyclerView().scrollToPosition(0);
                }
            }
        }, 1000);
    }


    /**
     * eventBus 3.0 use
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataReceiver(Event event) {
        switch (event.getAction()) {
            case Event.Ble_Connect_Statue:
            case Event.Ble_Data_Total:
            case Event.Ble_Data_Unbind:
                mRecyclerView.showSwipeRefresh();
                getData(true);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SyncDataEvent events) {

        if (events.getProgress() > 0 && !events.isStop()) {
            progress.setVisibility(View.VISIBLE);
            if (SuperBleSDK.isIown(BleApplication.getInstance())) {
                progress.setText(String.format(getString(R.string.sync_progress_text), String.valueOf(events.getProgress())));
            } else if (SuperBleSDK.isZG(BleApplication.getInstance())) {
                int time = events.getProgress();
                DateUtil dateUtil = new DateUtil(time, true);
                progress.setText(String.format(getString(R.string.sync_progress_text), dateUtil.getyyyyMMddDate()));
            } else if (SuperBleSDK.isMtk(BleApplication.getInstance())) {
                int mDay = events.getmDay();
                int totalDay = events.getTotalDay();
                progress.setText(String.format(getString(R.string.sync_progress_text), String.valueOf(events.getProgress())) +
                        "% " + mDay + "/" + totalDay + "天");
            } else if (SuperBleSDK.isProtoBuf(BleApplication.getInstance())) {
                int mDay = events.getmDay();
                int totalDay = events.getTotalDay();
                String date_str = events.getDate_str();
                if(totalDay == 0){
                    progress.setText(String.format(getString(R.string.sync_progress_text1),date_str, String.valueOf(events.getProgress())) + "%");
                }else{
                    progress.setText(String.format(getString(R.string.sync_progress_text1),date_str, String.valueOf(events.getProgress())) +
                            "% " + mDay + "/" + totalDay + "天");
                }
            }
        } else if (events.isStop()) {
            KLog.e("licl", "收到同步结束信号...");
            progress.setText(getString(R.string.sync_progress_text_over));
            progress.setVisibility(View.GONE);
            getData(true);
        }else if(events.getProgress()==-1){
            progress.setVisibility(View.VISIBLE);
            progress.setText(getString(R.string.sync_progress_text_start));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EpoEvent events) {
        switch (events.getState()) {
            case EpoEvent.STATE_INIT:
                progress.setVisibility(View.VISIBLE);
                progress.setText(R.string.epo_upgrading);
                break;
            case EpoEvent.STATE_SENDING:
                progress.setVisibility(View.VISIBLE);
                progress.setText(getString(R.string.epo_progress, events.getProgress()));
                break;
            case EpoEvent.STATE_END:
                progress.setText(R.string.epo_sucess);
                progress.setVisibility(View.GONE);
                break;
            case EpoEvent.STATE_DOWNLOAD_FILE_FAIL:
                progress.setVisibility(View.VISIBLE);
                progress.setText(R.string.down_load_epo_file_fail);
                break;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartEvent(HeartEvent event){
        int heart = event.getHeart();
        if(heartViewHolder != null){
            HeartData heartData = new HeartData();
            heartData.setHeart(heart+"");
            heartData.setTitle("标准心率协议");
            heartViewHolder.setData(heartData);
        }
    }

    @Override
    public <V extends BaseViewHolder> V getViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_DEVICE:
                return (V) new DeviceViewHolder(parent, getActivity());
            case VIEW_TYPE_SPORT:
                return (V) new SportViewHolder(parent, getActivity());
            case VIEW_TYPE_HEART:
                if(heartViewHolder == null){
                    heartViewHolder = new HeartViewHolder(parent, getActivity());
                }
                return (V) heartViewHolder;
            case VIEW_TYPE_SLEEP:
                return (V) new SleepViewHolder(parent, getActivity());
            case VIEW_TYPE_R1:
                return (V) new R1ViewHolder(parent, getActivity());
            case VIEW_TYPE_ZG_GPS:
                return (V) new ZgGPSViewHolder(parent, getActivity());
            case VIEW_TYPE_ZG_AGPS:
                return (V) new ZgAgpsViewHolder(parent, getActivity());
            case VIEW_TYPE_PROTOBUF_ECG:
                return (V) new ZgAgpsViewHolder(parent, getActivity());

            case VIEW_TYPE_CAMERA:
                return (V)new CameraViewHolder(parent,getContext());
            default:
                return (V) new DeviceViewHolder(parent, getActivity());
        }
    }
}
