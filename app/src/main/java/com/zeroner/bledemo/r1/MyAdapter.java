package com.zeroner.bledemo.r1;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.R1_effective_data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<R1_effective_data> list;
    private Context context;
    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormat2;
    private OnItemClickListener listener;


    public MyAdapter(Context context, List<R1_effective_data> list) {
        this.list = list;
        this.context = context;
        decimalFormat = new DecimalFormat("0.0");
        decimalFormat2 = new DecimalFormat("00");
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener i){
        this.listener = i;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.r1_recyclerview_item, viewGroup, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.MyViewHolder viewHolder, final int i) {
        viewHolder.tv_calorie.setText(decimalFormat.format(list.get(i).getCalorie()) + "千卡");
        int time = list.get(i).getTime();
        int hour = time / 3600;
        int min = time / 60 % 60;
        int sec = time % 60;
        viewHolder.tv_run_time.setText(decimalFormat2.format(hour) + ":" + decimalFormat2.format(min) + ":" + decimalFormat2.format(sec));
        viewHolder.tv_start_time.setText(getTimeFormat(list.get(i).getTime_id()));
        viewHolder.cl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onItemClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_start_time;
        TextView tv_run_time;
        TextView tv_calorie;
        ConstraintLayout cl_main;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_start_time = itemView.findViewById(R.id.tv_start_time);
            tv_run_time = itemView.findViewById(R.id.tv_run_time);
            tv_calorie = itemView.findViewById(R.id.tv_calorie);
            cl_main = itemView.findViewById(R.id.cl_main);
        }
    }

    private String getTimeFormat(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date(time * 1000));
    }

}
