package com.zeroner.bledemo.ecg;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.sql.TB_64_index_table;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author yanxi
 * @data 2018/12/19
 */
public class EcgAdapter extends RecyclerView.Adapter<EcgAdapter.MyViewHolder>{
    private List<TB_64_index_table> list;
    private List<String> ecgs;
    private Context context;
    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormat2;
    private EcgAdapter.OnItemClickListener listener;


    public EcgAdapter(Context context, List<TB_64_index_table> list, List<String> ecgs) {
        this.list = list;
        this.ecgs = ecgs;
        this.context = context;
        decimalFormat = new DecimalFormat("0.0");
        decimalFormat2 = new DecimalFormat("00");
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(EcgAdapter.OnItemClickListener i){
        this.listener = i;
    }

    @Override
    public EcgAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.zg_gps_recyclerview_item, viewGroup, false);

        EcgAdapter.MyViewHolder myViewHolder = new EcgAdapter.MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final EcgAdapter.MyViewHolder viewHolder, int i) {
//        int year = list.get(viewHolder.getAdapterPosition()).getYear();
//        int month = list.get(viewHolder.getAdapterPosition()).getMonth();
//        int day = list.get(viewHolder.getAdapterPosition()).getDay();
//        int hour = list.get(viewHolder.getAdapterPosition()).getHour();
//        int min = list.get(viewHolder.getAdapterPosition()).getMin();
//        int sec = list.get(viewHolder.getAdapterPosition()).getSecond();
//        int seq = list.get(viewHolder.getAdapterPosition()).getSeq();
//        viewHolder.tv_time.setText(year + "-"+ month +"-" + day + " "+ hour + ":"+ min+ ":"+ sec + "  seq:" + seq);
//        viewHolder.tv_time.setText(list.get(viewHolder.getAdapterPosition()).getSeq_start() + "------" + list.get(viewHolder.getAdapterPosition()).getSeq_end());
        viewHolder.tv_time.setText(list.get(viewHolder.getAdapterPosition()).getDate() + "    " + list.get(viewHolder.getAdapterPosition()).getSeq_start() + "-" + list.get(viewHolder.getAdapterPosition()).getSeq_end());
        viewHolder.tv_name.setText("ecg:"+ecgs.get(viewHolder.getAdapterPosition()));
        viewHolder.cl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(viewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_time;
        TextView tv_name;
        ConstraintLayout cl_main;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_name = itemView.findViewById(R.id.tv_name);
            cl_main = itemView.findViewById(R.id.cl_main);
        }
    }

    private String getTimeFormat(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date(time * 1000));
    }


}
