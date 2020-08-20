package com.zeroner.bledemo.utils;

import android.content.Context;
import android.graphics.Color;
import com.bigkoo.pickerview.OptionsPickerView;
import com.zeroner.bledemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者：hzy on 2017/12/26 18:20
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class OptionsPickerViewUtils {

    public static OptionsPickerView getOptionsPickerView(Context context, List<String > options1Items1, OptionsPickerView.OnOptionsSelectListener listener) {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(context,listener)
                .setSubmitText(context.getString(R.string.setting_dialog_confirm))//确定按钮文字
                .setCancelText(context.getString(R.string.setting_dialog_cancel))//取消按钮文字
                .setTextColorOut(0xff666666)
                .setDividerColor(0xff444444)
                .setBgColor(Color.WHITE)
                .setBackgroundId(0x66000000)
                .setLineSpacingMultiplier(2.0f)
                .setContentTextSize(20)//滚轮文字大小
                .setSelectOptions(0)
                .setOutSideCancelable(true)
                .build();
        pvOptions.setPicker(options1Items1);//添加数据源
        return pvOptions;
    }


    public static OptionsPickerView getOptionsPickerView(Context context,List<String > hours_start,List<List<String>> hours_ends, OptionsPickerView.OnOptionsSelectListener listener) {


        OptionsPickerView pvOptions = new OptionsPickerView.Builder(context,listener)
                .setSubmitText(context.getString(R.string.setting_dialog_confirm))//确定按钮文字
                .setCancelText(context.getString(R.string.setting_dialog_cancel))//取消按钮文字
                .setTextColorOut(0xff666666)
                .setDividerColor(0xff444444)
                .setBgColor(Color.WHITE)
                .setBackgroundId(0x66000000)
                .setLineSpacingMultiplier(2.0f)
                .setContentTextSize(20)//滚轮文字大小
                .setSelectOptions(0)
                .setOutSideCancelable(true)
                .build();
        pvOptions.setPicker(hours_start,hours_ends);//添加数据源
        return pvOptions;
    }

    public static OptionsPickerView getNOptionsPickerView(Context context,List<String > item1,List<String> item2, OptionsPickerView.OnOptionsSelectListener listener) {

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(context,listener)
                .setSubmitText(context.getString(R.string.setting_dialog_confirm))//确定按钮文字
                .setCancelText(context.getString(R.string.setting_dialog_cancel))//取消按钮文字
                .setTextColorOut(0xff666666)
                .setDividerColor(0xff444444)
                .setBgColor(Color.WHITE)
                .setBackgroundId(0x66000000)
                .setLineSpacingMultiplier(2.0f)
                .setContentTextSize(20)//滚轮文字大小
                .setSelectOptions(0)
                .setOutSideCancelable(true)
                .build();
        pvOptions.setNPicker(item1,item2,null);//添加数据源
        return pvOptions;
    }

    public static List<String> getTimeItemOptions(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.setting_time_1);
        return  Arrays.asList(timeItems);
    }

    public static List<String> getDateItemOptions(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.setting_date_1);
        return  Arrays.asList(timeItems);
    }

    public static List<String> getUnitItemOptions(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.setting_unit_1);
        return  Arrays.asList(timeItems);
    }

    public static List<String> getWeatherItemOptions(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.setting_weather_1);
        return  Arrays.asList(timeItems);
    }

    public static List<String> getHandItemOptions(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.setting_hand_1);
        return  Arrays.asList(timeItems);
    }

    public static List<String> getShakeName(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.setting_shake_name);
        return  Arrays.asList(timeItems);
    }

    public static List<String> getSitDown(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.sit_down_minute);
        return  Arrays.asList(timeItems);
    }

    public static List<String> getLanguage(Context context){
        String[] timeItems = context.getResources().getStringArray(R.array.setting_language_1);
        return  Arrays.asList(timeItems);
    }

    public static int[] getShakeModel(Context context){
        int[] timeItems = context.getResources().getIntArray(R.array.shake_mode);
        return  timeItems;
    }

    public static int[] getZGShakeModel(Context context){
        int[] timeItems = context.getResources().getIntArray(R.array.shake_mode_zg);
        return  timeItems;
    }

    public static List[] getHourOptions(){
        List[] obj=new List[2];
        List<String> hours_start = new ArrayList<>();
        List<List<String>> hours_ends = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours_start.add("0" + i + ":00");
            } else {
                hours_start.add(i + ":00");
            }
            List<String> hours_end = new ArrayList<>();
            for (int j = i + 1; j <= 24; j++) {
                if (j < 10) {
                    hours_end.add("0" + j + ":00");
                } else {
                    hours_end.add(j + ":00");
                }
            }
            hours_ends.add(hours_end);
        }
        obj[0]=hours_start;
        obj[1]=hours_ends;

        return obj;
    }

}
