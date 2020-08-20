package com.zeroner.bledemo.bean;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.zeroner.bledemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：hzy on 2018/1/9 14:41
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_timeline_sleepType)
    TextView sleepType;
    @BindView(R.id.text_timeline_start)
    TextView start;
    @BindView(R.id.text_timeline_end)
    TextView end;

    @BindView(R.id.time_marker)
    TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        mTimelineView.initLine(viewType);
    }
}
