package com.zeroner.bledemo.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by xionghao on 15/10/30.
 */
public abstract class CommonRecyAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final boolean on;
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDataList;
    protected final int mItemLayoutId;
    protected int headerlayoutid;
    protected int footerlayoutid;
    public static final int RECYLER_ITEM_TYPE = 0;
    public static final int RECYLER_HEAD_TYPE = 1;
    public static final int RECYLER_FOOTER_TYPE = 2;

    private ComViewHolder.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(ComViewHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CommonRecyAdapter(Context context, List<T> dataList, int layoutId) {
        this(context, dataList, layoutId, 0);
    }

    public CommonRecyAdapter(Context context, List<T> dataList, int layoutId, int headerlayoutid) {
        this(context, dataList, layoutId, headerlayoutid, 0);
    }

    public CommonRecyAdapter(Context context, List<T> dataList, int layoutId, int headerlayoutid, int footerlayoutid) {
        this(context, dataList, layoutId, headerlayoutid, footerlayoutid, false);
    }

    public CommonRecyAdapter(Context context, List<T> dataList, int layoutId, int headerlayoutid, int footerlayoutid, boolean isneedM) {
        this.mContext = context;
        this.mDataList = dataList;
        this.mItemLayoutId = layoutId;
        this.headerlayoutid = headerlayoutid;
        this.footerlayoutid = footerlayoutid;
        this.mInflater = LayoutInflater.from(context);
        this.on = isneedM;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerlayoutid != 0 && position == 0) {
            return RECYLER_HEAD_TYPE;
        } else {
            if (footerlayoutid != 0 && position == getItemCount()-1) {
                return RECYLER_FOOTER_TYPE;
            }
            return RECYLER_ITEM_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ComViewHolder viewHolder = null;
        if (headerlayoutid != 0 && viewType == RECYLER_HEAD_TYPE) {
            View view = mInflater.inflate(headerlayoutid, parent, false);
            viewHolder = setComViewHolder(view, viewType);

        } else if (footerlayoutid != 0 && viewType == RECYLER_FOOTER_TYPE) {
            View view = mInflater.inflate(footerlayoutid, parent, false);
            viewHolder = setComViewHolder(view, viewType);
        } else {
            View view = mInflater.inflate(mItemLayoutId, parent, false);
            viewHolder = setComViewHolder(view, viewType);
            viewHolder.onItemClickListener(onItemClickListener);
        }

        return viewHolder;

    }

    protected abstract ComViewHolder setComViewHolder(View view, int viewType);


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (headerlayoutid != 0) {
            if (position == 0) {
                onBindHeader(holder);
            } else {
                if (footerlayoutid!=0&&position==getItemCount()-1){
                    onBindFooter(holder);
                }else {
                    onBindItem(holder, position - 1, mDataList.get(position - 1));
                }

            }
        } else {
            if (footerlayoutid!=0&&position==getItemCount()-1){
                onBindFooter(holder);
            }else {
                onBindItem(holder, position, mDataList.get(position));
            }
        }
    }

    private void onBindFooter(RecyclerView.ViewHolder holder) {

    }

    public void onBindHeader(RecyclerView.ViewHolder holder) {
    }

    public void onBindItem(RecyclerView.ViewHolder holder, int position, T item) {
    }

    @Override
    public int getItemCount() {
        int size = mDataList.size();
        if (headerlayoutid != 0) {
            size++;
        }
        if (footerlayoutid != 0) {
            size++;
        }
        return size;
    }

}
