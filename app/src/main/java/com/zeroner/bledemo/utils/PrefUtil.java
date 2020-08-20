package com.zeroner.bledemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 * 
 * @author zhoushujie 2014-7-24 上午9:28:02
 */
public class PrefUtil {

    /**
     * 默认数据记录文件名称
     */
    private static final String PREF_FILE = "Zeroner_WRISTBAND_SHAREDPREFERENCES";

    /**
     * 存储长整型数据
     * 
     * @param context
     *            上下文
     * @param prefFile
     *            文件名称
     * @param key
     *            键
     * @param value
     *            long值
     * @return
     */
    public static boolean save(String prefFile, Context context, String key,
            long value) {
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE); // 读取文件,如果没有则会创建
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * 存储长整型数据
     * 
     * @param context
     *            上下文
     * @param key
     *            存储数据特定的键
     * @param value
     *            存储的数据 （long类型）
     */
    public static boolean save(Context context, String key, long value) {
        return save(PREF_FILE, context, key, value);
    }

    /**
     * 存储字符串数据
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param value
     *            String值
     * @return
     */
    public static boolean save(String prefFile, Context context, String key,
            String value) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 存储字符串数据
     * 
     * @param context
     *            上下文
     * @param key
     *            存储数据特定的键
     * @param value
     *            存储的数据 （String类型）
     */
    public static boolean save(Context context, String key, String value) {
        return save(PREF_FILE, context, key, value);
    }

    /**
     * 保存整形数据
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param value
     *            int值
     * @return
     */
    public static boolean save(String prefFile, Context context, String key,
            int value) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * 保存整形数据
     * 
     * @param context
     * @param key
     *            键
     * @param value
     *            int值
     * @return
     */
    public static boolean save(Context context, String key, int value) {
        // 读取文件,如果没有则会创建
        return save(PREF_FILE, context, key, value);
    }

    /**
     * 保存浮点型数据
     * 
     * @param prefFile
     *            记录文件名称
     * @param context
     * @param key
     *            键
     * @param value
     *            float值
     * @return
     */
    public static boolean save(String prefFile, Context context, String key,
            float value) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * 保存浮点型数据
     * 
     * @param context
     * @param key
     *            键
     * @param value
     *            float值
     * @return
     */
    public static boolean save(Context context, String key, float value) {
        return save(PREF_FILE, context, key, value);
    }

    /**
     * 保存布尔型数据
     * 
     * @param prefFile
     *            记录文件名称
     * @param context
     * @param key
     *            键
     * @param value
     *            boolean值
     * @return
     */
    public static boolean save(String prefFile, Context context, String key,
            boolean value) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * 保存布尔型数据
     * 
     * @param context
     * @param key
     *            键
     * @param value
     *            boolean值
     * @return
     */
    public static boolean save(Context context, String key, boolean value) {
        return save(PREF_FILE, context, key, value);
    }




    /**
     * 获取长整型数据，默认值0
     * 
     * @param prefFile
     *            文件名
     * @param context
     * @param key
     *            键
     * @return long
     */
    public static long getLong(String prefFile, Context context, String key) {
        return getLong(prefFile, context, key, 0l);
    }

    /**
     * 获取长整型数据，默认值0
     * 
     * @param context
     * @param key
     *            键
     * @return long
     */
    public static long getLong(Context context, String key) {
        return getLong(PREF_FILE, context, key, 0l);
    }

    /**
     * 获取长整型数据，自定义默认值
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return long
     */
    public static long getLong(String prefFile, Context context, String key,
            long defValue) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.getLong(key, defValue);
    }

    /**
     * 获取长整型数据，自定义默认值
     * 
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return long
     */
    public static long getLong(Context context, String key, long defValue) {
        return getLong(PREF_FILE, context, key, defValue);
    }

    /**
     * 获取浮点型数据，默认值0
     * 
     * @param prefFile
     *            文件名
     * @param context
     * @param key
     *            键
     * @return float
     */
    public static float getFloat(String prefFile, Context context, String key) {
        return getFloat(prefFile, context, key, 0f);
    }

    /**
     * 获取浮点型数据，默认值0
     * 
     * @param context
     * @param key
     *            键
     * @return float
     */
    public static float getFloat(Context context, String key) {
        return getFloat(PREF_FILE, context, key, 0f);
    }

    /**
     * 获取浮点型数据，自定义默认值
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return float
     */
    public static float getFloat(String prefFile, Context context, String key,
            float defValue) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.getFloat(key, defValue);
    }

    /**
     * 获取浮点型数据，自定义默认值
     * 
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return float
     */
    public static float getFloat(Context context, String key, float defValue) {
        return getFloat(PREF_FILE, context, key, defValue);
    }

    /**
     * 获取字符串数据，默认值""
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @return String
     */
    public static String getString(String prefFile, Context context, String key) {
        return getString(prefFile, context, key, "");
    }

    /**
     * 获取字符串数据，默认值""
     * 
     * @param context
     * @param key
     *            键
     * @return String
     */
    public static String getString(Context context, String key) {
        return getString(PREF_FILE, context, key, "");
    }

    /**
     * 获取字符串数据，自定义默认值
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return String
     */
    public static String getString(String prefFile, Context context,
            String key, String defValue) {
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.getString(key, defValue);
    }

    /**
     * 获取字符串数据，自定义默认值
     * 
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return String
     */
    public static String getString(Context context, String key, String defValue) {
        return getString(PREF_FILE, context, key, defValue);
    }

    /**
     * 获取整型数据，自定义默认值
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @return int值
     */
    public static int getInt(String prefFile, Context context, String key) {
        return getInt(prefFile, context, key, 0);
    }

    /**
     * 获取整型数据，默认值0
     * 
     * @param context
     * @param key
     *            键
     * @return int值
     */
    public static int getInt(Context context, String key) {
        return getInt(PREF_FILE, context, key, 0);
    }

    /**
     * 获取整型数据，自定义默认值
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return int值
     */
    public static int getInt(String prefFile, Context context, String key,
            int defValue) {
        // 读取文件,如果没有则会创建
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.getInt(key, defValue);
    }

    /**
     * 获取整型数据，自定义默认值
     * 
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return int值
     */
    public static int getInt(Context context, String key, int defValue) {
        return getInt(PREF_FILE, context, key, defValue);
    }

    /**
     * 获取布尔型数据，默认值false
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @return boolean值
     */
    public static boolean getBoolean(String prefFile, Context context,
            String key) {
        return getBoolean(prefFile, context, key, false);
    }

    /**
     * 获取布尔型数据，默认值false
     * 
     * @param context
     * @param key
     *            键
     * @return boolean值
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(PREF_FILE, context, key, false);
    }

    /**
     * 获取布尔型数据，自定义默认值
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return boolean值
     */
    public static boolean getBoolean(String prefFile, Context context,
            String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.getBoolean(key, defValue);
    }

    /**
     * 获取布尔型数据，自定义默认值
     * 
     * @param context
     * @param key
     *            键
     * @param defValue
     *            默认值
     * @return boolean值
     */
    public static boolean getBoolean(Context context, String key,
            boolean defValue) {
        return getBoolean(PREF_FILE, context, key, defValue);
    }


    /**
     * 判断数据是否存在
     * 
     * @param prefFile
     *            记录文件名称
     * @param context
     * @param key
     *            键
     * @return true存在，false不存在
     */
    public static boolean contains(String prefFile, Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.contains(key);
    }

    /**
     * 判断数据是否存在
     * 
     * @param context
     * @param key
     *            键
     * @return true存在，false不存在
     */
    public static boolean contains(Context context, String key) {
        return contains(PREF_FILE, context, key);
    }

    /**
     * 移除指定数据
     * 
     * @param prefFile
     *            记录文件名
     * @param context
     * @param key
     *            键
     * @return true已移除，false未移除
     */
    public static boolean remove(String prefFile, Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.edit().remove(key).commit();
    }

    /**
     * 清除配置信息
     * 
     * @param prefFile
     *            配置文件
     * @param context
     * @return 是否清除成功
     */
    public static boolean clear(String prefFile, Context context) {
        SharedPreferences settings = context.getSharedPreferences(prefFile,
                Context.MODE_PRIVATE);
        return settings.edit().clear().commit();
    }

    /**
     * 移除指定数据
     * 
     * @param context
     * @param key
     *            键
     * @return 是否移除成功
     */
    public static boolean remove(Context context, String key) {
        return remove(PREF_FILE, context, key);
    }

    /**
     * 清除配置信息
     * 
     * @param context
     * @return 是否清除成功
     */
    public static boolean clear(Context context) {
        return clear(PREF_FILE, context);
    }
}
