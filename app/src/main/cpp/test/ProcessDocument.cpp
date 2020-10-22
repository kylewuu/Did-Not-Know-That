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
    return summarizer();
}

// work on this function next, needs to be broken down
string ProcessDocument::summarizer() {

    return rawDocument;
}