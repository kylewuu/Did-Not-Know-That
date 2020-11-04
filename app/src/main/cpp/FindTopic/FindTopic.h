//
// Created by kyle- on 2020-11-02.
//
#include <iostream>
#include <string>
#include <jni.h>

#ifndef TINDER_FOR_KNOWLEDGE_FINDDOCUMENT_H
#define TINDER_FOR_KNOWLEDGE_FINDDOCUMENT_H

using std::string;

class FindTopic {
public:
    FindTopic();
    string returnTopic();
    void seekThroughWeights();
};


#endif //TINDER_FOR_KNOWLEDGE_FINDDOCUMENT_H
