package com.zeroner.bledemo.heart;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.data.viewData.ViewData;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * heart detail
 */
public class HeartDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_device_setting)
    Toolbar toolbarDeviceSetting;
    @BindView(R.id.heart_line_chart)
    LineChart mChart;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_detail);
        context=this;
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {

    }

    private void initView() {
        setSupportActionBar(toolbarDeviceSetting);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDeviceSetting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initChartView();
    }

    private void initChartView() {
        mChart.setDrawGridBackground(false);
        // no description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawZeroLine(false);
        mChart.getAxisRight().setEnabled(false);

        // add data
        DateUtil date=new DateUtil();
        setData(date.getYear(),date.getMonth(),date.getDay());
//        mChart.animateX(2500);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData(int year,int month,int day) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        LineDataSet set1;
        LineDataSet set2;
        List<Integer> hearts = ViewData.getHeartDetail(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME), year, month, day);
        for (int i = 0; i < 1440; i++) {
            int size=hearts.size();
            if(i<size){
                values.add(new Entry(i, hearts.get(i)));
            }else {
//                no heart data
                values.add(new Entry(i,75));
            }
        }



        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, getString(R.string.heart_title));
            set1.setDrawIcons(false);
            set1.setColor(Color.RED);
            set1.setCircleColor(Color.RED);
            set1.setLineWidth(1f);
            set1.setCircleRadius(1f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(false);
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setFillColor(Color.RED);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            // set data
            mChart.setData(data);
        }
    }
}
