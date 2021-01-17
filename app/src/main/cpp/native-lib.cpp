#include <jni.h>
#include <string>
#include "ProcessDocument/ProcessDocument.h"
#include <android/log.h>
#include <map>

#define APPNAME "MyApp"

string jstringToString(JNIEnv *pEnv, jstring pJstring);

using std::string;
using std::to_string;

extern "C" JNIEXPORT jstring JNICALL

Java_com_app_tfk_MainActivity_DisplayText(
        JNIEnv* env,
        jobject /* this */,
        jstring test)
{
    string hello = "Hello from C++";
    return test;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_app_tfk_webscraping_Articles_ReturnProcessedDocument(JNIEnv *env, jobject thiz, jstring x)
{
    string cppString = jstringToString(env, x);
    ProcessDocument processedDocument{cppString};
    string newString = processedDocument.mainLoop();
    return env->NewStringUTF(newString.c_str());
}

// house keeping functions
string jstringToString(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    string str(rtn);
    return rtn;
}
