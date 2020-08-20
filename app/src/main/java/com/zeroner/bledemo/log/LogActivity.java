package com.zeroner.bledemo.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.ComViewHolder;
import com.zeroner.bledemo.bean.CommonRecyAdapter;
import com.zeroner.bledemo.bean.RecycleViewDivider;
import com.zeroner.bledemo.bean.WrapContentLinearLayoutManager;
import com.zeroner.bledemo.bean.sql.BleLog;
import com.zeroner.bledemo.receiver.BluetoothCallbackReceiver;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThread;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.utils.ByteUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_device_log)
    Toolbar toolbarDeviceLog;
    @BindView(R.id.lv_log)
    RecyclerView lvLog;
    @BindView(R.id.cmd_edit)
    EditText cmdEdit;
    @BindView(R.id.send_cmd)
    Button sendCmd;

    private Context context;
    private List<String> data;
    private MyAdapter myAdapter;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData(2);
    }

    private void initView() {
        LocalBroadcastManager.getInstance(this).registerReceiver(bleLogReceiver, BaseActionUtils.getIntentFilter());
        setSupportActionBar(toolbarDeviceLog);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDeviceLog.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbarDeviceLog.setOnMenuItemClickListener(onMenuItemClick);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvLog.setLayoutManager(layoutManager);
        lvLog.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.device_bgk)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_ble, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.ble_log_write:
                    initData(1);
                    flag = false;
                    cmdEdit.setVisibility(View.GONE);
                    sendCmd.setVisibility(View.GONE);
                    break;
                case R.id.ble_log_notify:
                    initData(2);
                    flag = false;
                    cmdEdit.setVisibility(View.GONE);
                    sendCmd.setVisibility(View.GONE);
                    break;
                case R.id.ble_log_test_command:
                    cmdEdit.setVisibility(View.VISIBLE);
                    sendCmd.setVisibility(View.VISIBLE);
                    initData(3);
                    break;
            }
            return true;
        }
    };


    private void initData(int type) {
        data = SqlBizUtils.queryLog(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME), type);
        myAdapter = new MyAdapter(context, data, R.layout.layout_log_item);
        lvLog.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.send_cmd)
    public void onViewClicked() {
        BackgroundThreadManager.getInstance().clearQueue();
        flag = true;
        String cmd=cmdEdit.getText().toString().trim();
        if(TextUtils.isEmpty(cmd)){
            return;
        }
        //debug
        BleLog log = new BleLog();
        log.setTime(System.currentTimeMillis());
        log.setDataFrom(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME));
        log.setType(3);
        log.setCmd(cmd);
        log.save();

        byte[] command=hexStringToBytes(cmd);
        BackgroundThreadManager.getInstance().addWriteData(context,command);
        cmdEdit.setText("");
    }

    public  byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    class MyAdapter extends CommonRecyAdapter<String> {
        private Context context;

        public MyAdapter(Context context, List<String> dataList, int layoutId) {
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
        public void onBindItem(RecyclerView.ViewHolder holder, int position, String log) {
            super.onBindItem(holder, position, log);
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).log.setText(log);
            }
        }

    }

    static class ViewHolder extends ComViewHolder {
        @BindView(R.id.log_text)
        TextView log;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(bleLogReceiver);
    }

    private BroadcastReceiver bleLogReceiver = new BluetoothCallbackReceiver() {

        @Override
        public void onCommonSend(byte[] data) {
            super.onCommonSend(data);
            if(data==null){
                return;
            }
            if (flag) {
                //debug
                BleLog log = new BleLog();
                log.setTime(System.currentTimeMillis());
                log.setDataFrom(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME));
                log.setType(3);
                log.setCmd(ByteUtil.bytesToString(data));
                log.save();

                initData(3);
                myAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCmdReceiver(byte[] data) {
            super.onCmdReceiver(data);
            if(data==null){
                return;
            }
            if (flag) {
                //debug
                BleLog log = new BleLog();
                log.setTime(System.currentTimeMillis());
                log.setDataFrom(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME));
                log.setType(3);
                log.setCmd(ByteUtil.bytesToString(data));
                log.save();

                initData(3);
                myAdapter.notifyDataSetChanged();
            }
        }
    };

}
