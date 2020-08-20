package com.zeroner.bledemo.utils;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonUtils {
    public static final String RETCODE="retCode";
    public static final String MESSAGE="message";
    private static Gson gson = new Gson();

    /**
     * @param bean
     * @return String 返回类型
     * @Title: toJson
     * @throws：
     */
    public static String toJson(Object bean) {

        return gson.toJson(bean);
    }

    public static String toJson(Object bean, Type type) {

        return gson.toJson(bean, type);
    }

    /**
     * @param json
     * @param type
     * @return T 返回类型
     * @Title: fromJson
     * @Description:
     * @throws：
     */
    public static Object fromJson(String json, Type type) {

        return gson.fromJson(json, type);
    }

    /**
     * @param <T>
     * @param json
     * @param classOfT
     * @return T 返回类型
     * @Title: fromJson
     * @Description:
     * @throws：
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /**
     * 集合解析
     *
     * @param json
     * @param t
     * @param <T>
     * @return
     */
//    public static <T> List<T> getListJson(String json, Class<T> t) {
//        return gson.fromJson(json, new TypeToken<List<T>>() {
//        }.getType());
//    }

    // 将Json数组解析成相应的映射对象列表
    public static <T> ArrayList<T> getListJson(String json, Class<T> t){
        Type type = new ListParameterizedType(t);
        return gson.fromJson(json, type);
    }

    private static class ListParameterizedType implements ParameterizedType {
        private Type type;
        private ListParameterizedType(Type type) {
            this.type = type;
        }
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {type};
        }
        @Override
        public Type getRawType() {
            return ArrayList.class;
        }
        @Override
        public Type getOwnerType() {
            return null;
        }
        // implement equals method too! (as per javadoc)
    }

    /**
     * 获取String
     * @param response
     * @return
     */
    public static String getString(String response,String key){
        String retCode=null;
        try {
            JSONObject jsonObject=new JSONObject(response);
            retCode=jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retCode;
    }

    /**
     * 获取int
     * @param response
     * @return
     */
    public static int getInt(String response,String key){
        int retCode=-1;
        try {
            JSONObject jsonObject=new JSONObject(response);
            retCode=jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retCode;
    }

    public static boolean getBoolean(String response,String key){
        boolean retCode=false;
        try {
            JSONObject jsonObject=new JSONObject(response);
            retCode=jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retCode;
    }

    /**
     * 获取int
     * @param response
     * @return
     */
    public static long getLong(String response,String key){
        long retCode=-1;
        try {
            JSONObject jsonObject=new JSONObject(response);
            retCode=jsonObject.getLong("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retCode;
    }

    /**
     * 获取retCode
     * @param response
     * @return
     */
    public static int getRetCode(String response){
        int retCode=-1;
        try {
            JSONObject jsonObject=new JSONObject(response);
            retCode=jsonObject.getInt(RETCODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retCode;
    }


    public static boolean retCodeisOk(int retCode) {
        return retCode == 0;
    }
}
