#include <jni.h>
#include <string>
#include "test/Main.h"

using std::string;
using std::to_string;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_tinderforknowledge_MainActivity_DisplayText(
        JNIEnv* env,
        jobject /* this */,
        jstring test) {

    Main text_display{};
    string newString = text_display.HelloWorld();

    string hello = "Hello from C++";
    int x = 5;
    return env->NewStringUTF(newString.c_str());
//    return env->NewStringUTF(to_string(5).c_str());
//    return test;
}

// old template
//extern "C" JNIEXPORT jstring JNICALL
//Java_com_example_tinderforknowledge_MainActivity_stringFromJNI(
//        JNIEnv* env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_tinderforknowledge_webscraping_Article_DisplayText(JNIEnv *env, jobject thiz,
                                                                    jstring x) {

    Main text_display{};
    string newString = text_display.HelloWorld();

    return env->NewStringUTF(newString.c_str());
}