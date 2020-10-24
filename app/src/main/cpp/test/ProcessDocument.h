//
// Created by kyle- on 2020-10-19.
//
#include <iostream>
#include <string>
#include <jni.h>

#ifndef TINDER_FOR_KNOWLEDGE_MAIN_H
#define TINDER_FOR_KNOWLEDGE_MAIN_H

using std::string;

class ProcessDocument {
public:
    ProcessDocument(string x);
    string mainLoop();
    string summarizer();
    void removeSquareBrackets();
    void replace(string original, string replacement);
    void removeWhiteSpace();
    void removeNewLines();
    void breakDownSentences();
    void createSentencesWithoutStopWords();
};


#endif //TINDER_FOR_KNOWLEDGE_MAIN_H
