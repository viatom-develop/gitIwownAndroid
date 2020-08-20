package com.zeroner.bledemo.data.sync;

import com.alibaba.json.JSON;
import com.alibaba.json.serializer.JSONSerializer;
import com.alibaba.json.serializer.PropertyPreFilter;
import com.alibaba.json.serializer.SerializerFeature;
import com.zeroner.bledemo.bean.sql.File_protobuf_80data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ComplexPropertyPreFilter implements PropertyPreFilter {

    private Map<Class<?>, String[]> includes = new HashMap<>();
    private Map<Class<?>, String[]> excludes = new HashMap<>();

    static {
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.mask;
    }

    public ComplexPropertyPreFilter() {

    }

    public ComplexPropertyPreFilter(Map<Class<?>, String[]> includes) {
        super();
        this.includes = includes;
    }

    @Override
    public boolean apply(JSONSerializer serializer, Object source, String name) {

        //对象为空。直接放行
        if (source == null) {
            return true;
        }

        // 获取当前需要序列化的对象的类对象
        Class<?> clazz = source.getClass();

        // 无需序列的对象、寻找需要过滤的对象，可以提高查找层级
        // 找到不需要的序列化的类型
        for (Map.Entry<Class<?>, String[]> item : this.excludes.entrySet()) {
            // isAssignableFrom()，用来判断类型间是否有继承关系
            if (item.getKey().isAssignableFrom(clazz)) {
                String[] strs = item.getValue();

                // 该类型下 此 name 值无需序列化
                if (isHave(strs, name)) {
                    return isFilter(name, source);
                }
            }
        }

        // 需要序列的对象集合为空 表示 全部需要序列化
        if (this.includes.isEmpty()) {
            return true;
        }

        // 需要序列的对象
        // 找到不需要的序列化的类型
        for (Map.Entry<Class<?>, String[]> item : this.includes.entrySet()) {
            // isAssignableFrom()，用来判断类型间是否有继承关系
            if (item.getKey().isAssignableFrom(clazz)) {
                String[] strs = item.getValue();
                // 该类型下 此 name 值无需序列化
                if (isHave(strs, name)) {
                    return isFilter(name, source);
                }
            }
        }

        return false;
    }

    /**
     * 此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
     */
    public static boolean isHave(String[] str, String s) {

        for (String i : str) {
            // 循环查找字符串数组中的每个字符串中是否包含所有查找的内容
            if (i.equals(s)) {
                // 查找到了就返回真，不在继续查询
                return true;
            }
        }

        // 没找到返回false
        return false;
    }

    private static boolean isFilter(String name, Object value) {

        try {
            if (value instanceof File_protobuf_80data.Sleep) {
                Field[] declaredFields = Class.forName(value.getClass().getName()).getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.getName().equals(name)) {
                        if ("c".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.Sleep) value).getC() == 0) {
                                return false;
                            }
                        }
                        if ("s".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.Sleep) value).getS() == 0) {
                                return false;
                            }
                        }
                        if("a".equalsIgnoreCase(name)){
                            if(((File_protobuf_80data.Sleep) value).getA().length == 0){
                                return false;
                            }
                        }
                    }
                }
            } else if (value instanceof File_protobuf_80data.HeartRate) {
                Field[] declaredFields = Class.forName(value.getClass().getName()).getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.getName().equals(name)) {
                        if ("a".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HeartRate) value).getA() == 0) {
                                return false;
                            }
                        }
                        if ("n".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HeartRate) value).getN() == 0) {
                                return false;
                            }
                        }
                        if ("x".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HeartRate) value).getX() == 0) {
                                return false;
                            }
                        }
                    }
                }

            } else if (value instanceof File_protobuf_80data.Pedo) {

                Field[] declaredFields = Class.forName(value.getClass().getName()).getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.getName().equals(name)) {
                        if ("a".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.Pedo) value).getA() == 0) {
                                return false;
                            }
                        }
                        if ("c".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.Pedo) value).getC() == 0) {
                                return false;
                            }
                        }
                        if ("d".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.Pedo) value).getD() == 0) {
                                return false;
                            }
                        }
                        if ("s".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.Pedo) value).getS() == 0) {
                                return false;
                            }
                        }
                        if ("t".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.Pedo) value).getT() == 0) {
                                return false;
                            }
                        }
                    }

                }
            } else if (value instanceof File_protobuf_80data.HRV) {
                Field[] declaredFields = Class.forName(value.getClass().getName()).getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.getName().equals(name)) {
                        if ("f".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HRV) value).getF() == 0) {
                                return false;
                            }
                        }
                        if ("m".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HRV) value).getM() == 0) {
                                return false;
                            }
                        }
                        if ("p".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HRV) value).getP() == 0) {
                                return false;
                            }
                        }
                        if ("r".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HRV) value).getR() == 0) {
                                return false;
                            }
                        }
                        if ("s".equalsIgnoreCase(name)) {
                            if (((File_protobuf_80data.HRV) value).getS() == 0) {
                                return false;
                            }
                        }
                    }
                }
            } else if (value instanceof File_protobuf_80data) {
                Field[] declaredFields = Class.forName(value.getClass().getName()).getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.getName().equals(name)) {
                        if ("v".equalsIgnoreCase(name)) {
                            File_protobuf_80data.HRV hrv = ((File_protobuf_80data) value).getV();
                            if (hrv.getF() == 0 && hrv.getM() == 0 && hrv.getP() == 0 && hrv.getR() == 0 && hrv.getS() == 0) {
                                return false;
                            }
                        }
                        if ("p".equalsIgnoreCase(name)) {
                            File_protobuf_80data.Pedo pedo = ((File_protobuf_80data) value).getP();
                            if (pedo.getA() == 0 && pedo.getC() == 0 && pedo.getD() == 0 && pedo.getS() == 0 && pedo.getT() == 0) {
                                return false;
                            }
                        }
                        if ("h".equalsIgnoreCase(name)) {
                            File_protobuf_80data.HeartRate heartRate = ((File_protobuf_80data) value).getH();
                            if (heartRate.getA() == 0 && heartRate.getN() == 0 && heartRate.getX() == 0) {
                                return false;
                            }
                        }
                        if("e".equalsIgnoreCase(name)){
                            File_protobuf_80data.Sleep sleep = ((File_protobuf_80data) value).getE();
                            if(sleep.getA().length == 0 && sleep.getC() == 0 && sleep.getS() == 0){
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            return true;
        } catch (Exception e) {
            return true;
        }

    }

    public Map<Class<?>, String[]> getIncludes() {
        return includes;
    }

    public void setIncludes(Map<Class<?>, String[]> includes) {
        this.includes = includes;
    }

    public Map<Class<?>, String[]> getExcludes() {
        return excludes;
    }

    public void setExcludes(Map<Class<?>, String[]> excludes) {
        this.excludes = excludes;
    }
}