package com.zeroner.bledemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.zeroner.bledemo.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 作者：hzy on 2017/12/22 15:08
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class Util {
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int getSportName(int k, int sportType) {
        Map<Integer, Integer[]> sportMap = new HashMap<Integer, Integer[]>();
        int id = -1;
        sportMap.put(0x01, new Integer[]{R.string.sport_plan_walking});// 步行
        sportMap.put(0x02, new Integer[]{R.string.sport_plan_situp});// 仰卧起坐
        sportMap.put(0x03, new Integer[]{R.string.sport_plan_pushup});// 俯卧撑
        sportMap.put(0x04, new Integer[]{R.string.sport_plan_jump});// 跳绳
        sportMap.put(0x05, new Integer[]{R.string.sport_plan_mountaineering});// 登山
        sportMap.put(0x06, new Integer[]{R.string.sport_plan_pullup});// 引体向上
        sportMap.put(0x07, new Integer[]{R.string.sport_plan_running});// 跑步
        sportMap.put(0x80, new Integer[]{R.string.sport_plan_badminton});// 羽毛球
        sportMap.put(0x81, new Integer[]{R.string.sport_plan_basketball});// 篮球
        sportMap.put(0x82, new Integer[]{R.string.sport_plan_football});// 足球
        sportMap.put(0x83, new Integer[]{R.string.sport_plan_swimming});// 游泳
        sportMap.put(0x84, new Integer[]{R.string.sport_plan_volleyball});// 排球
        sportMap.put(0x85, new Integer[]{R.string.sport_plan_pingpong});// 乒乓球
        sportMap.put(0x86, new Integer[]{R.string.sport_plan_bowling});// 保龄球
        sportMap.put(0x87, new Integer[]{R.string.sport_plan_tennis});// 网球
        sportMap.put(0x88, new Integer[]{R.string.sport_plan_cycling});// 骑行
        sportMap.put(0x89, new Integer[]{R.string.sport_plan_skee});// 滑雪
        sportMap.put(0x8a, new Integer[]{R.string.sport_plan_skate});// 滑冰
        sportMap.put(0x8b, new Integer[]{R.string.sport_plan_climbing});// 攀岩
        sportMap.put(0x8c, new Integer[]{R.string.sport_plan_gymnasium});// 健身
        sportMap.put(0x8d, new Integer[]{R.string.sport_plan_dance});// 舞蹈
        sportMap.put(0x8e, new Integer[]{R.string.sport_plan_slap});// 平板撑
        sportMap.put(0x8f, new Integer[]{R.string.sport_plan_bodymechanics});// 健身操
        sportMap.put(0x90, new Integer[]{R.string.sport_plan_yoga});// 瑜伽
        sportMap.put(0x91, new Integer[]{R.string.sport_plan_shuttlecock});// 毽球

        Set<Integer> set = sportMap.keySet();
        for (Integer integer : set) {
            if (integer == sportType) {
                return sportMap.get(integer)[k];
            }
        }
        return id;
    }


    public static String minToTime(int min) {
        String sHour;
        String sMin;
        int hour = min / 60;
        int m = min % 60;
        if (hour < 10) {
            sHour = "0" + hour;
        } else {
            sHour = "" + hour;
        }
        if (m < 10) {
            sMin = "0" + m;
        } else {
            sMin = "" + m;
        }
        return sHour + ":" + sMin;
    }

    public static String minToTimeUnit(int min) {
        int hour = min / 60;
        int m = min % 60;
        return hour + "h" + m + "min";
    }


    /**
     * 根据运动类型的编码获取对应的运动图片和运动名称 k=0时用过运动类型编码获取运动名称 k=1时通过运动类型获取运动图片,k=3 获取颜色
     */
    @SuppressLint("UseSparseArrays")
    public static int getSporyImgOrName(int k, int sportType) {
        Map<Integer, Integer[]> sportMap = new HashMap<Integer, Integer[]>();
        int id = -1;
        sportMap.put(0x01, new Integer[]{R.string.sport_module_walking});// 步行
        sportMap.put(0x02, new Integer[]{R.string.sport_module_sport_plan_situp});// 仰卧起坐
        sportMap.put(0x03, new Integer[]{R.string.sport_module_sport_plan_pushup});// 俯卧撑
        sportMap.put(0x04, new Integer[]{R.string.sport_module_sport_plan_jump});// 跳绳
        sportMap.put(0x05, new Integer[]{R.string.sport_module_sport_plan_mountaineering});// 登山
        sportMap.put(0x06, new Integer[]{R.string.sport_module_sport_plan_pullup});// 引体向上
        sportMap.put(0x07, new Integer[]{R.string.sport_module_running});// 跑步
        sportMap.put(0x80, new Integer[]{R.string.sport_module_sport_plan_badminton});// 羽毛球
        sportMap.put(0x81, new Integer[]{R.string.sport_module_sport_plan_basketball});// 篮球
        sportMap.put(0x82, new Integer[]{R.string.sport_module_sport_plan_football});// 足球
        sportMap.put(0x83, new Integer[]{R.string.sport_module_sport_plan_swimming});// 游泳
        sportMap.put(0x84, new Integer[]{R.string.sport_module_sport_plan_volleyball});// 排球
        sportMap.put(0x85, new Integer[]{R.string.sport_module_sport_plan_pingpong});// 乒乓球
        sportMap.put(0x86, new Integer[]{R.string.sport_module_sport_plan_bowling});// 保龄球
        sportMap.put(0x87, new Integer[]{R.string.sport_module_sport_plan_tennis});// 网球
        sportMap.put(0x88, new Integer[]{R.string.sport_module_sport_plan_cycling});// 骑行
        sportMap.put(0x89, new Integer[]{R.string.sport_module_sport_plan_skee});// 滑雪
        sportMap.put(0x8a, new Integer[]{R.string.sport_module_sport_plan_skate});// 滑冰
        sportMap.put(0x8b, new Integer[]{R.string.sport_module_sport_plan_climbing});// 攀岩
        sportMap.put(0x8c, new Integer[]{R.string.sport_module_sport_plan_gymnasium});// 健身
        sportMap.put(0x8d, new Integer[]{R.string.sport_module_sport_plan_dance});// 舞蹈
        sportMap.put(0x8e, new Integer[]{R.string.sport_module_sport_plan_slap});// 平板撑
        sportMap.put(0x8f, new Integer[]{R.string.sport_module_sport_plan_bodymechanics});// 健身操
        sportMap.put(0x90, new Integer[]{R.string.sport_module_sport_plan_yoga});// 瑜伽
        sportMap.put(0x91, new Integer[]{R.string.sport_module_sport_plan_shuttlecock});// 毽球
        sportMap.put(0x92, new Integer[]{R.string.sport_module_sport_ball_game});// 毽球
        sportMap.put(0x93, new Integer[]{R.string.sport_module_sport_plan_speed_walking});// 健步走
        sportMap.put(0x94, new Integer[]{R.string.sport_module_sport_plan_golf});// 高尔夫
        sportMap.put(0x95, new Integer[]{R.string.sport_module_sport_plan_canoeing});// 皮划艇

        sportMap.put(0x1000, new Integer[]{R.string.sport_module_sport_plan_treadmill});// 跑步机
        sportMap.put(0x1001, new Integer[]{R.string.sport_module_sport_plan_spinning});// 室内自行车
        sportMap.put(0xff, new Integer[]{R.string.sport_module_sport_other});//其他 看做一种特殊运动，用来处理28，29的差值
        Set<Integer> set = sportMap.keySet();
        for (Integer integer : set) {
            if (integer == sportType) {
                return sportMap.get(integer)[k];
            }
        }

        return id;
    }


    /**
     * @param accuracy 小数点后保留几位
     * @param num
     * @return
     */
    public static float doubleToFloat(int accuracy, double num) {
        BigDecimal b = new BigDecimal(num);
        return b.setScale(accuracy, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    public static long CRC_32(byte[] bytes) {
        long resultCrcValue = 0x00000000ffffffffL;
        for (int i = 0; i < bytes.length; i++) {
            int index = (int) ((resultCrcValue ^ bytes[i]) & 0xff);
            resultCrcValue = crc32Table[index] ^ (resultCrcValue >> 8);
        }
        resultCrcValue = resultCrcValue ^ 0x00000000ffffffffL;
        return resultCrcValue;
    }

    private static long[] crc32Table = new long[256];

    static {
        long crcValue;
        for (int i = 0; i < 256; i++) {
            crcValue = i;
            for (int j = 0; j < 8; j++) {
                if ((crcValue & 1) == 1) {
                    crcValue = crcValue >> 1;
                    crcValue = 0x00000000edb88320L ^ crcValue;
                } else {
                    crcValue = crcValue >> 1;
                }
            }
            crc32Table[i] = crcValue;
        }
    }
}
