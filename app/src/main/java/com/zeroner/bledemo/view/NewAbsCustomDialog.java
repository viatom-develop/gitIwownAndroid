package com.zeroner.bledemo.view;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

/**
 * 自定义无边框对话框抽象类
 * @author hzw
 */
public abstract class NewAbsCustomDialog extends Dialog {

    public Window mWindow;
    boolean isCancelable=true;
    boolean isOutCancel=false;

    public NewAbsCustomDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(getCancelable());
        setCanceledOnTouchOutside(getCanceledOnTouchOutside());
        setContentView(getLayoutResID());
        mWindow = getWindow();
        mWindow.setBackgroundDrawableResource(getBackgroundDrawableResourceId());
//        if (getType() != -1) {
//            mWindow.setType(getType());
//        }
        if (getWindowAnimationsResId() != -1) {
            mWindow.setWindowAnimations(getWindowAnimationsResId());
        }
        if (getDimEnabled()) {
            mWindow.addFlags(LayoutParams.FLAG_DIM_BEHIND);
        } else {
            mWindow.clearFlags(LayoutParams.FLAG_DIM_BEHIND);
        }
        LayoutParams lp = mWindow.getAttributes();
        lp.width = getWidth();
        lp.height = getHeight();
        lp.gravity = getGravity();
        onWindowAttributesChanged(lp);
        initView();
        initData();
        initListener();
    }

    /**
     * 是否背景模糊
     * 
     * @return
     */
    public boolean getDimEnabled() {
        return false;
    }

    /**
     * 是否可取消
     * 
     * @return
     */
    public boolean getCancelable() {
        return isCancelable;
    }

    /**
     * 触摸外部是否可取消
     * 
     * @return
     */
    public boolean getCanceledOnTouchOutside() {
        return isOutCancel;
    }

    /**
     * 背景资源ID
     * 
     * @return
     */
    public int getBackgroundDrawableResourceId() {
        return android.R.color.transparent;
    }

//    /**
//     * Dialog类型
//     * 
//     * @return
//     */
//    public int getType() {
//        return -1;
//    };

    /**
     * 动画资源ID
     * 
     * @return
     */
    public int getWindowAnimationsResId() {
        return -1;
    };

    /**
     * Dialog宽
     * 
     * @return
     */
    public int getWidth() {
        return android.view.ViewGroup.LayoutParams.MATCH_PARENT;
    }

    /**
     * Dialog高
     * 
     * @return
     */
    public int getHeight() {
        return android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    /**
     * 显示位置
     * 
     * @return
     */
    public int getGravity() {
        return Gravity.CENTER;
    }

    /**
     * 自定义布局资源ID
     * 
     * @return
     */
    public abstract int getLayoutResID();

    /**
     * 初始化View
     */
    public abstract void initView();

    /**
     * 显示数据
     */
    public abstract void initData();

    /**
     * 初始化监听器
     */
    public abstract void initListener();
}
