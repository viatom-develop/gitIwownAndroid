package com.zeroner.bledemo.r1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zeroner.bledemo.R;

public class MyTextView extends RelativeLayout {

    private TextView tv_step_data;
    private TextView tv_touch_down_time_data;
    private TextView tv_sky_time_data;
    private TextView tv_vertical_data;
    private TextView tv_balance_data;
    private TextView tv_speed_list;
    private TextView tv_steps_list;
    private TextView tv_verticals_list;
    private TextView tv_touch_down_times_list;

    public MyTextView(Context context) {
        super(context);
        initView(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.textview_my_self,this);
        tv_step_data = this.findViewById(R.id.tv_step_data);
        tv_touch_down_time_data = this.findViewById(R.id.tv_touch_down_time_data);
        tv_sky_time_data = this.findViewById(R.id.tv_sky_time_data);
        tv_vertical_data = this.findViewById(R.id.tv_vertical_data);
        tv_balance_data = this.findViewById(R.id.tv_balance_data);

        tv_speed_list = this.findViewById(R.id.tv_speed_list);
        tv_steps_list = this.findViewById(R.id.tv_steps_list);
        tv_verticals_list = this.findViewById(R.id.tv_verticals_list);
        tv_touch_down_times_list = this.findViewById(R.id.tv_touch_down_times_list);
    }

    public void setTv_step_data(String data) {
        tv_step_data.setText(data);
    }

    public void setTv_touch_down_time_data(String data) {
        this.tv_touch_down_time_data.setText(data);
    }

    public void setTv_sky_time_data(String data) {
        this.tv_sky_time_data.setText(data);
    }

    public void setTv_vertical_data(String data) {
        this.tv_vertical_data.setText(data);
    }

    public void setTv_balance_data(String data) {
        this.tv_balance_data.setText(data);
    }

    public void setTv_speed_list(String data) {
        this.tv_speed_list.setText(data);
    }

    public void setTv_steps_list(String data) {
        this.tv_steps_list.setText(data);
    }

    public void setTv_verticals_list(String data) {
        this.tv_verticals_list.setText(data);
    }

    public void setTv_touch_down_times_list(String data) {
        this.tv_touch_down_times_list.setText(data);
    }
}
