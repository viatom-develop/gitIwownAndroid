package com.zeroner.bledemo.r1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.R1DataBean;
import com.zeroner.bledemo.bean.sql.R1_effective_data;
import com.zeroner.bledemo.data.R1DataPresenter;

public class R1DetailActivity extends AppCompatActivity implements R1DataPresenter.R1DataImpl {

    private MyTextView tv_view;
    private R1_effective_data r1_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r1);

        Toolbar toolbar_device_setting = findViewById(R.id.toolbar_device_setting);

        tv_view = findViewById(R.id.tv_view);

        r1_data = (R1_effective_data) getIntent().getSerializableExtra("r1_data");

        setSupportActionBar(toolbar_device_setting);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_device_setting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        R1DataPresenter dataPresenter = new R1DataPresenter(r1_data,this);
        dataPresenter.setR1Data();


    }

    @Override
    public void showR1Data(R1DataBean r1DataBean) {
        tv_view.setTv_step_data(r1DataBean.getRate_avg());
        tv_view.setTv_touch_down_time_data(r1DataBean.getEarth_time_avg());
        tv_view.setTv_sky_time_data(r1DataBean.getSky_time_avg());
        tv_view.setTv_vertical_data(r1DataBean.getVertical_avg());
        tv_view.setTv_balance_data(r1DataBean.getEarth_balance());
        tv_view.setTv_speed_list(r1DataBean.getSpeedLists().toString());
        tv_view.setTv_steps_list(r1DataBean.getStepRateLists().toString());
        tv_view.setTv_verticals_list(r1DataBean.getVerticalLists().toString());
        tv_view.setTv_touch_down_times_list(r1DataBean.getEarthTimeLists().toString());
    }
}
