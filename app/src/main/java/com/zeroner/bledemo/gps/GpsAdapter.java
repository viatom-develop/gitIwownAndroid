package com.zeroner.bledemo.gps;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.blemidautumn.bluetooth.model.ZgGpsTotalInfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GpsAdapter extends RecyclerView.Adapter<GpsAdapter.MyViewHolder> {

    List<ZgGpsTotalInfo.GpsTotalList> list;
    private Context context;
    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormat2;
    private OnItemClickListener listener;


    public GpsAdapter(Context context, List<ZgGpsTotalInfo.GpsTotalList> list) {
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
    public GpsAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.zg_gps_recyclerview_item, viewGroup, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GpsAdapter.MyViewHolder viewHolder, int i) {
        int year = list.get(viewHolder.getAdapterPosition()).getGpsYear();
        int month = list.get(viewHolder.getAdapterPosition()).getGpsMonth();
        int day = list.get(viewHolder.getAdapterPosition()).getGpsDay();
        viewHolder.tv_time.setText(year + "-"+ month +"-" + day);
        viewHolder.cl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onItemClick(viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_time;
        ConstraintLayout cl_main;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            cl_main = itemView.findViewById(R.id.cl_main);
        }
    }

    private String getTimeFormat(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date(time * 1000));
    }

}
