package com.zeroner.bledemo.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Gavin
 * @date 2020-01-02
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHloder>{



    @NonNull
    @Override
    public MainViewHloder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHloder mainViewHloder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MainViewHloder extends RecyclerView.ViewHolder{
        public TextView contentView;
        public TextView titleView;

        public MainViewHloder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
