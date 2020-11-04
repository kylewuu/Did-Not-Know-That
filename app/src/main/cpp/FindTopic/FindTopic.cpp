//
// Created by kyle- on 2020-11-02.
//

#include "FindTopic.h"
#include <iostream>
#include <string>
#include <regex>
#include <android/log.h>
#include <map>
#include <fstream>

#define APPNAME "MyApp"

using std::string;
using namespace std;
using std::map;

FindTopic::FindTopic()
{
    // fetch db of words or something

}

string FindTopic::returnTopic() {
    seekThroughWeights();
    return "Subaru";
}

void FindTopic::seekThroughWeights()
{
//    ifstream ifile("../../../../../../../TFK_Other_Files/word_embeddings.txt");
//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "asdfasdfasdf");
//    if(ifile)
//    {
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "asdfasdfasdf");
//        ifstream infile;
//        string read_file_name("C:/englishDesktop/Coding/TFK_Other_Files/word_embeddings.txt");
//        infile.open(read_file_name);
//        string line;
//        infile >> line;
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s", line.c_str());
//    }

    /* try to open file to read */
//    ifstream ifile;
//    ifile.open("/../../../../../../../TFK_Other_Files/word_embeddings.txt");
//    ifile.open("C:/englishDesktop/Coding/TFK_Other_Files/test.txt", ios::in);
//    if(ifile.is_open()) {
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "asdfasdfasdf");
//    } else {
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "jhkljkljkljkl");
//    }

    ifstream file("FindTopic/testText.txt");
    if (file) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "asdfasdfasdf");
    }
    else __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "werwerwerwerwer");
    file.close();

}