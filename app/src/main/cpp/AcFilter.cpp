#include <jni.h>
#include "AcFilter.h"

int m_acTemp[5];
extern "C" JNIEXPORT jint JNICALL
Java_com_zeroner_bledemo_data_WriteEcgUtil_AcFilter(
        JNIEnv *original_val, jclass clazz, jint data)
{
        int i;
        int sum;

        for (i = 0; i < 4; i++)
        {
                m_acTemp[i] = m_acTemp[i+1];
        }
        m_acTemp[4] = data;

        sum =  m_acTemp[0]+ m_acTemp[1];
        sum = sum/(2);

        return sum;
}