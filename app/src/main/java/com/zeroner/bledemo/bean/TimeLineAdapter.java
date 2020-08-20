package com.zeroner.bledemo.bean;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.vipulasri.timelineview.TimelineView;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.OrderStatus;
import com.zeroner.bledemo.bean.data.Orientation;
import com.zeroner.bledemo.bean.data.SleepStatusFlag;
import com.zeroner.bledemo.bean.data.TimeLineModel;
import com.zeroner.bledemo.utils.VectorDrawableUtils;
import com.zeroner.blemidautumn.library.KLog;

import java.util.List;

/**
 * 作者：hzy on 2018/1/9 14:42
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<TimeLineModel> feedList,Orientation orientation, boolean withLinePadding) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.item_timeline_line_padding, parent, false);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        TimeLineModel timeLineModel = mFeedList.get(position);

        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
        if(timeLineModel.getType()==100){
            holder.sleepType.setTextColor(mContext.getResources().getColor(R.color.color_1));
            holder.start.setTextColor(mContext.getResources().getColor(R.color.color_1));
            holder.end.setTextColor(mContext.getResources().getColor(R.color.color_1));
        }else if(timeLineModel.getType()== SleepStatusFlag.Deep){
            holder.sleepType.setTextColor(mContext.getResources().getColor(R.color.color_2));
            holder.start.setTextColor(mContext.getResources().getColor(R.color.color_2));
            holder.end.setTextColor(mContext.getResources().getColor(R.color.color_2));
        }else if(timeLineModel.getType()== SleepStatusFlag.Light){
            holder.sleepType.setTextColor(mContext.getResources().getColor(R.color.color_3));
            holder.start.setTextColor(mContext.getResources().getColor(R.color.color_3));
            holder.end.setTextColor(mContext.getResources().getColor(R.color.color_3));
        }

        if(!timeLineModel.getSleepType().isEmpty()) {
            holder.sleepType.setVisibility(View.VISIBLE);
            holder.sleepType.setText(timeLineModel.getSleepType());
        }
        else
            holder.sleepType.setVisibility(View.GONE);

        holder.start.setText(timeLineModel.getStart());
        holder.end.setText(timeLineModel.getEnd());

    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}
