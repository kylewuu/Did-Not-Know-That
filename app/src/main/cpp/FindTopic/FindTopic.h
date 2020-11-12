//
// Created by kyle- on 2020-11-02.
//
#include <iostream>
#include <string>
#include <jni.h>

#ifndef TFK_FINDDOCUMENT_H
#define TFK_FINDDOCUMENT_H

using std::string;

class FindTopic {
public:
    FindTopic();
    string returnTopic();
    void seekThroughWeights();
};


#endif //TFK_FINDDOCUMENT_H
