package com.zeroner.bledemo.utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
public class UI {
    public static String EXTRA_OBJ = "extra_obj";
    /**
     * @param classOfT
     * @param obj
     */
    public static <T> void startActivity(Activity activity, Class<T> classOfT, Parcelable obj) {
        Intent intent = new Intent(activity, classOfT);
        if (obj != null) {
            intent.putExtra(EXTRA_OBJ, obj);
        }
        activity.startActivity(intent);
    }

    public static <T> void startActivity(Activity activity, Class<T> classOfT) {
        startActivity(activity, classOfT, null);
    }

}
