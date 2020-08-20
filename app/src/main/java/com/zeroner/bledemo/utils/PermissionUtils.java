package com.zeroner.bledemo.utils;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

/**
 * 作者：hzy on 2018/1/5 10:49
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class PermissionUtils {
    public static final int Io_Permission=111;

    //shouldShowRequestPermissionRationale主要用于给用户一个申请权限的解释，该方法只有在用户在上一次已经拒绝过你的这个权限申请。也就是说，用户已经拒绝一次了，你又弹个授权框，你需要给用户一个解释，为什么要授权，则使用该方法。
    public static void requestPermission(Activity context, String permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {

        } else {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
        }
    }
}
