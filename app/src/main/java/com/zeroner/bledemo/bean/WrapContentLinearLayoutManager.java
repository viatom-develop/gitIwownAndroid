package com.zeroner.bledemo.bean;
import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socks.library.KLog;

/**
 * 作者：hzy on 2017/11/2 19:13
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public  class WrapContentLinearLayoutManager extends LinearLayoutManager {
    //... constructor
    public WrapContentLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            KLog.e("probe", "meet a IOOBE in RecyclerView");
        }
    }
}
