package com.zeroner.bledemo.gps;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.TB_blue_gps;
import com.zeroner.bledemo.utils.DateUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GpsDetailAdapter extends RecyclerView.Adapter<GpsDetailAdapter.MyViewHolder> {

    private List<TB_blue_gps> list;
    private Context context;
    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormat2;
    private OnItemClickListener listener;


    public GpsDetailAdapter(Context context, List<TB_blue_gps> list) {
        this.list = list;
        this.context = context;
        decimalFormat = new DecimalFormat("0.00");
        decimalFormat2 = new DecimalFormat("00");
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener i){
        this.listener = i;
    }

    @Override
    public GpsDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.zg_gps_recyclerview_detail_item, viewGroup, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GpsDetailAdapter.MyViewHolder viewHolder, int i) {
        double lat = list.get(viewHolder.getAdapterPosition()).getLat();
        double lang = list.get(viewHolder.getAdapterPosition()).getLon();
//        float speed = list.get(viewHolder.getAdapterPosition()).getGPS_speed();
        DateUtil dateUtil = new DateUtil(list.get(viewHolder.getAdapterPosition()).getTime(),true);
        String y_m_d_h_m_s = dateUtil.getY_M_D_H_M_S();
        viewHolder.tv_time.setText(dateUtil.getY_M_D_H_M_S());
        viewHolder.tv_lat.setText("lat:  " + decimalFormat.format(lat));
        viewHolder.tv_long.setText("long:  " + decimalFormat.format(lang));
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
        TextView tv_lat;
        TextView tv_long;
        TextView tv_speed;
        ConstraintLayout cl_main;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_lat = itemView.findViewById(R.id.tv_lat);
            tv_long = itemView.findViewById(R.id.tv_long);
            tv_speed = itemView.findViewById(R.id.tv_speed);
            cl_main = itemView.findViewById(R.id.cl_main);
        }
    }

    private String getTimeFormat(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date(time * 1000));
    }

}
