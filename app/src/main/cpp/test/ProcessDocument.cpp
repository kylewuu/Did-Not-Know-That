//
// Created by kyle- on 2020-10-19.
//

#include "ProcessDocument.h"
#include <iostream>
#include <string>

using std::string;

string rawDocument;

ProcessDocument::ProcessDocument(string x){
    rawDocument = x;
}

string ProcessDocument::mainLoop(){
    breakDownSentences();
    return summarizer();
}

// work on these functions next, needs to be broken down
void ProcessDocument::breakDownSentences(){
    rawDocument.erase(std::remove(rawDocument.begin(), rawDocument.end(), '\n'), rawDocument.end());
}

string ProcessDocument::summarizer() {

    return rawDocument;
}