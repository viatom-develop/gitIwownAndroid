package com.zeroner.bledemo.setting;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zeroner.bledemo.R;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;

public class BloodSettingActivity extends AppCompatActivity {

    private EditText et_sbp1;
    private EditText et_dbp1;
    private EditText et_sbp2;
    private EditText et_dbp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_setting);

        et_sbp1 = findViewById(R.id.et_sbp1);
        et_dbp1 = findViewById(R.id.et_dbp1);
        et_sbp2 = findViewById(R.id.et_sbp2);
        et_dbp2 = findViewById(R.id.et_dbp2);

        Button calibration = findViewById(R.id.calibration);
        calibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sbp1 = Integer.parseInt(et_sbp1.getText().toString());
                int dbp1 = Integer.parseInt(et_dbp1.getText().toString());
                int sbp2 = Integer.parseInt(et_sbp2.getText().toString());
                int dbp2 = Integer.parseInt(et_dbp2.getText().toString());
                int[] blood = new int[8];
                blood[0] = loword(sbp1);
                blood[1] = hiword(sbp1);
                blood[2] = loword(dbp1);
                blood[3] = hiword(dbp1);
                blood[4] = loword(sbp2);
                blood[5] = hiword(sbp2);
                blood[6] = loword(dbp2);
                blood[7] = hiword(dbp2);
                SuperBleSDK.getSDKSendBluetoothCmdImpl(BloodSettingActivity.this).writeBloodAndEnable(BloodSettingActivity.this, "", 1, 176, 1, blood);
            }
        });

    }

    /**
     * 取低位
     *
     * @param i
     * @return
     */
    public static int loword(int i) {
        return i & 0xFFFF;
    }

    /**
     * 取高位
     *
     * @param i
     * @return
     */
    public static int hiword(int i) {
        return i >>> 8;
    }
}
