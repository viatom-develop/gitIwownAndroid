package com.zeroner.bledemo.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeroner.bledemo.R;


/**
 * 正在加载的对话框
 * 
 * 
 */
public class LoadingDialog extends NewAbsCustomDialog {

    /**
     * 内容
     */
    private TextView msgTv;

    private String msg;
    private boolean isCancel=true;
    private ImageView imageView;

    public LoadingDialog(Context context) {
        this(context, "");
    }

    public LoadingDialog(Context context, String msg) {
        super(context);
        this.msg = msg;
    }

    public LoadingDialog(Context context, boolean isCancel) {
        super(context);
        this.msg = "";
        this.isCancel = isCancel;
    }

    public LoadingDialog(Context context, int resId) {
        super(context);
        this.msg = context.getString(resId);
    }

    public LoadingDialog(Context context, int resId, Object... formatArgs) {
        super(context);
        this.msg = context.getString(resId, formatArgs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        msgTv = (TextView) findViewById(R.id.new_loading_msg_tv);
        imageView = (ImageView)findViewById(R.id.new_iv_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
//        Glide.with(getContext()).load(R.mipmap.loading_gif).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    @Override
    public void initData() {
        msgTv.setText(msg);
    }

    @Override
    public void initListener() {

    }


    @Override
    public int getWindowAnimationsResId() {
        return android.R.style.Animation_Dialog;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.new_view_loading;
    }

    @Override
    public int getWidth() {
        return android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getHeight() {
        return android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public boolean getCancelable() {
        return isCancel;
    }

    @Override
    public boolean getCanceledOnTouchOutside() {
        return false;
    }

    @Override
    public boolean getDimEnabled() {
        return false;
    }

    /**
     * 设置显示文本
     * 
     * @param msg
     */
    public void setMessage(String msg) {
        this.msg = msg;
        if (msgTv != null) {
            msgTv.setText(this.msg);
        }
    }

    /**
     * 
     * @param resId
     */
    public void setMessage(int resId) {
        setMessage(getContext().getString(resId));
    }

    /**
     * 
     * @param msg
     */
    public void show(String msg) {
        super.show();
        this.msg = msg;
        if (msgTv != null) {
            msgTv.setText(this.msg);
        }
    }

    /**
     * 
     * @param resId
     */
    public void show(int resId) {
        show(getContext().getString(resId));
    }
}
