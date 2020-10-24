//
// Created by kyle- on 2020-10-19.
//

#include "ProcessDocument.h"
#include <iostream>
#include <string>
#include <regex>
#include <android/log.h>

#define APPNAME "MyApp"



using std::string;
using namespace std;


string rawDocument;
vector<string> sentences;

ProcessDocument::ProcessDocument(string x){
    rawDocument = x;
}

string ProcessDocument::mainLoop(){
    removeSquareBrackets();
    removeNewLines();
    replace("&nbsp;"," ");
    replace("&amp;","&");
    removeWhiteSpace();
    breakDownSentences();
    createSentencesWithoutStopWords();
    return summarizer();
}

void ProcessDocument::removeSquareBrackets()
{
    int i = 0;
    while(i < rawDocument.length()) {
        if(rawDocument[i] == '[')
        {
            int j = i + 1;
            while(rawDocument[j] != ']') j++;
            j++;
            rawDocument.erase(i, j-i);
        }
        else i++;
    }
}

void ProcessDocument::replace(string original, string replacement)
{
    const string s = original;
    const string t = replacement;

    string::size_type n = 0;
    while ( ( n = rawDocument.find( s, n ) ) != string::npos )
    {
        rawDocument.replace( n, s.size(), t );
        n += t.size();
    }
}

void ProcessDocument::removeWhiteSpace()
{
    int i = 0;
    while(i < rawDocument.length()) {
        if(isspace(rawDocument[i])) {
            if(isspace(rawDocument[i+1]))
            {
                int j = i + 1;
                while (isspace(rawDocument[j])) j++;
                rawDocument.erase(i , j-1-i );
            }
        }
        i++;
    }
}

// work on these functions next, needs to be broken down
void ProcessDocument::removeNewLines(){
    rawDocument.erase(std::remove(rawDocument.begin(), rawDocument.end(), '\n'), rawDocument.end());
}

void ProcessDocument::breakDownSentences()
{
    int sentencesCount = 2;
    int i = 0;
    while(rawDocument[i] != '\0' && rawDocument[i+1] != '\0')
    {
        if(rawDocument[i] == '.' && isspace(rawDocument[i+1])) sentencesCount++;
        i++;
    }

    string currentSentence = "";
    int j = 0;

    for(int i=0;i<rawDocument.length() - 1;i++)
    {
        if(rawDocument[i] == '.' && isspace(rawDocument[i+1]) )
        {
            currentSentence += '.';
            sentences.push_back(currentSentence);
            currentSentence = "";
            j++;
        }
        else
        {
            currentSentence += rawDocument[i];
        }

    }
    sentences.push_back(currentSentence);

    for(int i=0;i<sentencesCount;i++)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s\n",sentences[i].c_str());
    }


//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s",rawDocument.c_str());
//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "The number of sentences is: %d", sentencesCount);
}

void createSentencesWithoutStopWords()
{
    // WORK ON THIS NEXT
}
string ProcessDocument::summarizer() {

    return rawDocument;
}