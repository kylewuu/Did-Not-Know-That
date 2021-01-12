//
// Created by kyle- on 2020-10-19.
//
#include <iostream>
#include <string>
#include <jni.h>

#ifndef TFK_MAIN_H
#define TFK_MAIN_H

using std::string;

class ProcessDocument {
public:
    ProcessDocument(string x);
    string mainLoop();
    void resetVariables();
    string summarizer();
    void removeSquareBrackets();
    void replace(string original, string replacement);
    void removeWhiteSpace();
    void removeNewLines();
    void breakDownSentences();
    void editSentences();
    void countWordFrequency();
    void rankSentences();
    void deallocateVariables();
};

#endif //TFK_MAIN_H
