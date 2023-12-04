#include "filters.h"
#include <stdlib.h >
#include <jni.h>
#include "string.h"
#include "math.h"
#include <android/log.h>
#define TAG "myDemo-jni" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型
#define ONE_ORDER 225
#define TWO_ORDER 269
//#define SHIFT_POINTS 166
#define SHIFT_ZERO (TWO_ORDER * 2 + 1) 
#define LENGTH (SHIFT_ZERO * 2)
#define SHIFT_POINTS ((ONE_ORDER + TWO_ORDER) / 2 -1)


void insertSortbe(float *array, int start, int Eend)
{
    int i, j;
    float temp;

    for(i = start + 1; i < Eend; i++) {
//        LOGD("########## arraysfind_temp1 = %d", i);
//        LOGD("########## arraysfind_temp10 = %d", sizeof(array));
        temp = array[i];
//        LOGD("########## arraysfind_temp2 = %f", temp);
        for(j = i - 1; j >= 0; j--) {
//            LOGD("########## arraysfind_temp3 = %d", j);
//            LOGD("########## arraysfind_temp30 = %f", array[j]);
            if(array[j] > temp) {
                array[j + 1] = array[j];
//                LOGD("########## arraysfind_temp4 = %d", j);
            } else {
                break;
            }
        }
//        LOGD("########## arraysfind_temp5 = %f", array[j + 1]);
        array[j + 1] = temp;
//        LOGD("########## arraysfind_temp50 = %f", array[j + 1]);
    }
}

int find_value(float *arr, float value)
{
    int mid = 0;
    int left = 0;
    int right = ONE_ORDER - 1;
    while(left <= right) {
        mid = (left + right) / 2;
        if(arr[mid] - value > 0.000001f) {
            right = mid - 1;
        } else if(value - arr[mid] > 0.000001f) {
            left = mid + 1;
        } else
            break;
    }
    return mid;
}



int partition(float arr[], int low, int high)
{
    float key;
    key = arr[low];
    while(low < high) {
        while(low < high && arr[high] >= key)
            high--;
        if(low < high)
            arr[low++] = arr[high];
        while(low < high && arr[low] <= key)
            low++;
        if(low < high)
            arr[high--] = arr[low];
    }
    arr[low] = key;
    return low;
}

void quick_sort(float arr[], int start, int end)
{
    int pos;
    if(start < end) {
        pos = partition(arr, start, end);
        quick_sort(arr, start, pos - 1);
        quick_sort(arr, pos + 1, end);
    }
}


static float Smooth_cache1[ONE_ORDER ] = {0.0};
static int COUNT1 = 0;
static int flag1 = 1;
static float arrays1[ONE_ORDER] = {0.0};


static float Filter_median1(float Input_data)
{
    float tmp;
    float xout = 0;
    int loc;

    if(COUNT1 <= ONE_ORDER - 2) {
        Smooth_cache1[COUNT1++] = Input_data;
        return 0;
    } else {
        LOGD("########## arrays2 = %f", Input_data);
        xout = Smooth_cache1[COUNT1 % ONE_ORDER];
        Smooth_cache1[COUNT1 % ONE_ORDER] = Input_data;
        if(flag1 == 1) {
            flag1 = 0;
            memcpy(arrays1, Smooth_cache1, sizeof(float) * ONE_ORDER);
            quick_sort(arrays1, 0, ONE_ORDER - 1);
            LOGD("########## arraysflag1 = %f", Input_data);
        } else {

            LOGD("########## arraysfind_value = %f", Input_data);
            loc = find_value(arrays1, xout);
            LOGD("########## arraysfind_value2 = %f", Input_data);
            arrays1[loc] = Input_data;
            if(loc == 0 && Input_data - arrays1[1] > 0.000001f) {
                insertSortbe(arrays1, 0, ONE_ORDER);
            }
            LOGD("########## arraysfind_value3 = %f", Input_data);
            if(loc > 0 && Input_data - arrays1[loc - 1] > 0.000001f) {
                insertSortbe(arrays1, loc, ONE_ORDER);
            }
            LOGD("########## arraysfind_value4 = %f", Input_data);
            if(loc > 0 && arrays1[loc - 1] - Input_data > 0.000001f) {
                insertSortbe(arrays1, 0, loc + 1);
            }
            LOGD("########## arraysfind_value5 = %f", Input_data);
        }
        tmp = arrays1[ONE_ORDER / 2];
        LOGD("########## arrays1 = %f", tmp);
        COUNT1++;
        if(COUNT1 == 2 * ONE_ORDER)
            COUNT1 = ONE_ORDER;
        return tmp;
    }
}



static float Smooth_cache2[TWO_ORDER ] = {0.0};
static int COUNT2 = 0;
static float SumCache2 = 0;
static int FLAG2 = 1;

static float Filter_Smooth2(float Input_data)
{
    short i;
    float tmp;
    if(COUNT2 <= TWO_ORDER - 1) {
        Smooth_cache2[COUNT2++] = Input_data;
        LOGD("########## COUNT2++ = %f", Smooth_cache2[COUNT2++]);
        return 0;
    } else {
        LOGD("########## COUNT2 = %d", COUNT2);
        tmp = Smooth_cache2[COUNT2 % TWO_ORDER];

        Smooth_cache2[COUNT2 % TWO_ORDER] = Input_data;
        if(FLAG2 == 1) {
            FLAG2 = 0;
            for(i = 0; i < TWO_ORDER; i++) {
                SumCache2 += Smooth_cache2[i];
            }
            LOGD("########## SumCache2  FLAG21 = %f", SumCache2);
        } else {
            SumCache2 += (Input_data - tmp);
            LOGD("########## SumCache2 FLAG20 = %f", SumCache2);
        }
        COUNT2++;
        if(COUNT2 == 2 * TWO_ORDER) {
            COUNT2 = TWO_ORDER;
        }
        LOGD("########## SumCache2 FLAG22 = %f", SumCache2);

        return SumCache2 / (float)(TWO_ORDER);
    }
}


static float SMOOTH_ARRAY[SHIFT_POINTS] = {0.0};
static int CN_COMPUTE = 0;

static float Smooth(float InputData)
{
    float value;
    if(CN_COMPUTE < SHIFT_ZERO) {
        SMOOTH_ARRAY[CN_COMPUTE % SHIFT_POINTS] = InputData;
        CN_COMPUTE++;
        return 0;
    } else {
        SMOOTH_ARRAY[CN_COMPUTE % SHIFT_POINTS] = InputData;
        value = SMOOTH_ARRAY[(CN_COMPUTE + 1) % SHIFT_POINTS] - Filter_Smooth2(Filter_median1(InputData));
        LOGD("########## value = %f", value);
        CN_COMPUTE++;
        if(CN_COMPUTE == 11 * SHIFT_POINTS) {
            CN_COMPUTE = 10 * SHIFT_POINTS;
        }
        LOGD("########## CN_COMPUTE = %d", CN_COMPUTE);
        return value;
    }

}

static const float FCoef_50b_ECG[3] = {0.992944821090634,	-0.613673648387196,	0.992944821090634};
static const float FCoef_50a_ECG[3] = {1,	-0.613673648387196,	0.985889642181269};
static float FBuff_50_ECG[3];
static float FoutBuff_50_ECG[3];

// 滤波输出
static float FOut_50_ECG;
// 滤波函数
static float Filter_50_ECG(float Input_data, unsigned char Reset)
{
    short i = 0;
//	缓存初始化
    if(1 == Reset) {
        for(i = 0; i < 3; i++) {
            FBuff_50_ECG[i] = 0;
            FoutBuff_50_ECG[i] = 0;
        }

        FOut_50_ECG = 0;
        return 0;
    }
//	更新缓存数据
    for(i = 2; i > 0; i--) {
        FBuff_50_ECG[i] = FBuff_50_ECG[i - 1];
    }
    FBuff_50_ECG[0] = Input_data;
    LOGD("########## FOut_50_ECG = %f", FOut_50_ECG);
//	计算滤波输出
    FOut_50_ECG = 0;
    for(i = 0; i <= 2; i++) {
        FOut_50_ECG += FCoef_50b_ECG[i] * FBuff_50_ECG[i];
    }
    LOGD("########## FOut_50_ECG1 = %f", FOut_50_ECG);
    for(i = 1; i <= 2; i++) {
        FOut_50_ECG -= FCoef_50a_ECG[i] * FoutBuff_50_ECG[i - 1];
    }
    LOGD("########## FOut_50_ECG2 = %f", FOut_50_ECG);
    for(i = 2; i > 0; i--) {
        FoutBuff_50_ECG[i] = FoutBuff_50_ECG[i - 1];
    }
    LOGD("########## FOut_50_ECG3 = %f", FOut_50_ECG);
    FoutBuff_50_ECG[0] = FOut_50_ECG;
    //FOut_Low40_ECG += FCoef_Low40_ECG[4] * FBuff_Low40_ECG[4];
//	返回输出
    return FOut_50_ECG;
}



static const float FCoef_Low40_ECG[21] = {-7.764354486444e-05,-0.000514336835478,-0.0007468542760801, 0.002537420977022,
   0.009007890786248,  0.00088732264706, -0.03522808195267, -0.04399312544853,
    0.07373206777332,   0.2910827186599,   0.4066252424281,   0.2910827186599,
    0.07373206777332, -0.04399312544853, -0.03522808195267,0.0008873226470599,
   0.009007890786248, 0.002537420977022,-0.0007468542760801,-0.000514336835478,
  -7.764354486444e-05};


static float FBuff_Low40_ECG[21];

// 滤波输出
static float FOut_Low40_ECG;
// 滤波函数
static float Filter_Low40_ECG(float Input_Low40, unsigned char Reset)
{
    short i = 0;
//	缓存初始化
    if(1 == Reset) {
        for(i = 0; i < 21; i++) {
            FBuff_Low40_ECG[i] = 0;
        }
        FOut_Low40_ECG = 0;
        return 0;
    }
//	更新缓存数据
    LOGD("########## FOut_Low40_ECG0 = %f", FOut_Low40_ECG);
    for(i = 20; i > 0; i--) {
        FBuff_Low40_ECG[i] = FBuff_Low40_ECG[i - 1];
    }

    FBuff_Low40_ECG[0] = Input_Low40;
//	计算滤波输出
    FOut_Low40_ECG = 0;
    LOGD("########## FOut_Low40_ECG1 = %f", FOut_Low40_ECG);
    for(i = 0; i < 21; i++) {
        FOut_Low40_ECG += FCoef_Low40_ECG[i] * FBuff_Low40_ECG[i];
    }
    LOGD("########## FOut_Low40_ECG2 = %f", FOut_Low40_ECG);
//	返回输出
    return FOut_Low40_ECG;
}

static const float FCoef_60b_ECG[3] = {0.991545474884747,	-0.124519311009906,	0.991545474884747};
static const float FCoef_60a_ECG[3] = {1,	-0.124519311009906,	0.983090949769495};

static float FBuff_60_ECG[3];
static float FoutBuff_60_ECG[3];

// 滤波输出
static float FOut_60_ECG;
// 滤波函数
static float Filter_60_ECG(float Input_data, unsigned char Reset)
{
    short i = 0;
//	缓存初始化
    if(1 == Reset) {
        for(i = 0; i < 3; i++) {
            FBuff_60_ECG[i] = 0;
            FoutBuff_60_ECG[i] = 0;
        }
        FOut_60_ECG = 0;
        return 0;
    }
//	更新缓存数据
    for(i = 2; i > 0; i--) {
        FBuff_60_ECG[i] = FBuff_60_ECG[i - 1];

    }
    FBuff_60_ECG[0] = Input_data;
    LOGD("########## FOut_60_ECG0 = %f", FOut_60_ECG);
//	计算滤波输出
    FOut_60_ECG = 0;
    for(i = 0; i <= 2; i++) {
        FOut_60_ECG += FCoef_60b_ECG[i] * FBuff_60_ECG[i];
    }
    LOGD("########## FOut_60_ECG1 = %f", FOut_60_ECG);
    for(i = 1; i <= 2; i++) {
        FOut_60_ECG -= FCoef_60a_ECG[i] * FoutBuff_60_ECG[i - 1];
    }
    LOGD("########## FOut_60_ECG2 = %f", FOut_60_ECG);
    for(i = 2; i > 0; i--) {
        FoutBuff_60_ECG[i] = FoutBuff_60_ECG[i - 1];
    }
    LOGD("########## FOut_60_ECG3 = %f", FOut_60_ECG);
    FoutBuff_60_ECG[0] = FOut_60_ECG;
//	返回输出
    return FOut_60_ECG;
}


extern "C" void Java_com_zeroner_bledemo_data_sync_ProtoBufSleepHandler_filterTrapInit(
        JNIEnv *env, jclass clazz)
{
    Filter_Low40_ECG(0, 1);
    Filter_60_ECG(0, 1);
    Filter_50_ECG(0, 1);
}

extern "C" JNIEXPORT jfloat JNICALL
Java_com_lepu_lepucare_ble_data_sync_ProtoBufSleepHandler_filterTrapValConvert(
        JNIEnv *original_val, jclass clazz, jdouble position)
{
    float filterdata;

    filterdata = Smooth(Filter_50_ECG(Filter_60_ECG(Filter_Low40_ECG((short)(position), 0), 0), 0));

    return filterdata;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_com_lepu_lepucare_ble_data_sync_ProtoBufSleepHandler_test(
        JNIEnv *original_val, jclass clazz, jdouble position)
{   position =  position * 10;
    LOGD("########## i = %f", position);
    return position;
}


