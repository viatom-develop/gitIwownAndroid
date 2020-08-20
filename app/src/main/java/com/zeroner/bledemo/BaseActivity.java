package com.zeroner.bledemo;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.zeroner.bledemo.view.TitleBar;
import butterknife.ButterKnife;


/**
 * create by hzy 20160511
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * Load content main view
     */
//    @InjectView(R.id.common_base_content_layout)
    protected LinearLayout contentLayout;
    private RelativeLayout mRootView;
    /***
     * title bar
     **/
//    @InjectView(R.id.base_title_bar)
    public TitleBar titleBar;
    private long beginTime;
    public static String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Vertical screen lock
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.setContentView(R.layout.activity_base);
        contentLayout = (LinearLayout)findViewById(R.id.common_base_content_layout);
        titleBar = (TitleBar)findViewById(R.id.base_title_bar);
        mRootView= (RelativeLayout) findViewById(R.id.rl_root);
//        ButterKnife.inject(this);

        beginTime = System.currentTimeMillis();
        TAG = getClass().getSimpleName();


        if (hasKitKat() && !hasLollipop()) {
            //Transparent status bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Transparent navigation bar
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initBaseView();

    }

    public void initBaseView() {
        titleBar.setImmersive(true);
        //titleBar.setBackground(ContextCompat.getDrawable(this, R.drawable.title_background));
        titleBar.setBackgroundResource(R.color.title_bar_color);
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setSubTitleColor(Color.WHITE);
        titleBar.setActionTextColor(Color.WHITE);
    }

    public RelativeLayout getRootView() {
        return mRootView;
    }

    public void setTitleBarBackgroundColor(int id){
        titleBar.setBackgroundColor(getResources().getColor(id));
        titleBar.setTitleColor(getResources().getColor(R.color.white));
        titleBar.setLeftTextColor(getResources().getColor(R.color.white));
    }

    public void setCenterLayoutClickListener(View.OnClickListener l){
        titleBar.setCenterClickListener(l);
    }

    /**
     * titlebar right click event
     */
    public interface ActionOnclickListener {
        void onclick();
    }

    public void setLeftBtn(final ActionOnclickListener actionOnclickListener) {
        //titleBar.setLeftText(getString(R.string.common_back));
        titleBar.setLeftImageResource(R.mipmap.back3x);
        titleBar.setLeftTextColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionOnclickListener.onclick();
            }
        });
    }

    public void setRightText(String text, final ActionOnclickListener actionOnclickListener) {
        titleBar.setRightLayoutVisible(true);
        titleBar.addAction(new TitleBar.TextAction(text) {
            @Override
            public void performAction(View view) {
                actionOnclickListener.onclick();
            }
        });
    }

    public void setRightImag(int drawable, final ActionOnclickListener actionOnclickListener){
        titleBar.setRightLayoutVisible(true);
        titleBar.addAction(new TitleBar.ImageAction(drawable) {
            @Override
            public void performAction(View view) {
                actionOnclickListener.onclick();
            }
        });
    }

    public void removeAllActions() {
        titleBar.removeAllActions();
    }

    public void setRightVisible(boolean isShow) {
        titleBar.setRightLayoutVisible(isShow);
    }

    /**
     * back
     */
    public void setLeftBackTo() {
        //titleBar.setLeftText(R.string.common_back);
        titleBar.setLeftImageResource(R.mipmap.back3x);
        titleBar.setLeftTextColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        titleBar.setActionTextColor(Color.WHITE);
    }


    /**
     * title
     *
     * @param id
     */
    public void setTitleText(int id) {
        titleBar.setTitle(id);
    }

    /**
     * title
     */
    public void setTitleText(String text) {
        titleBar.setTitle(text);
    }

    /**
     * 设置左边标题
     *
     * @param leftText
     */
    public void setLeftTitle(String leftText) {
        titleBar.setLeftText(leftText);
    }

    /**
     * Set the text color on the left
     *
     * @param color
     */
    public void setLeftTextColor(int color) {
        titleBar.setLeftTextColor(color);
    }


    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        ButterKnife.bind(this, view);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        contentLayout.addView(view);
    }

    @Override
    public void setContentView(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentLayout.addView(view, params);
        ButterKnife.bind(this, view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        contentLayout.addView(view, params);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // KLog.e(TAG + "耗时 " + (System.currentTimeMillis() - beginTime));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     *
     */
    public void back() {
        //默认finish
        finish();
    }

    public void setTitleBarRightDrawable(int id){
        titleBar.setCenterTitleRightLayout(id);
    }

    /**
     * Set whether to hide the return key
     **/
    public void setLeftVisible(boolean isVisible) {
        titleBar.setLeftVisible(isVisible);
    }

    public TitleBar getTitleBar(){
        return titleBar;
    }

    public void changeViewVisible(View view){
        if (view.getVisibility()== View.VISIBLE) {
            view.setVisibility(View.GONE);
        }else {
            view.setVisibility(View.VISIBLE);
        }
    }

    public String[] getArray(int id){
        return getResources().getStringArray(id);
    }

    protected void setTitleBackground(int color){
        titleBar.setBackgroundResource(color);
    }

    /**
     * Measurement control aspect method 1
     * @param view
     * @return int[] 0.widht 1.height
     */
    private int[] getViewWidthAndHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();

        return new int[]{width, height};
    }

}
