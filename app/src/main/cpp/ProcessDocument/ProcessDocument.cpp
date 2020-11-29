//
// Created by kyle- on 2020-10-19.
//

#include "ProcessDocument.h"
#include <iostream>
#include <string>
#include <regex>
#include <android/log.h>
#include <map>

#define APPNAME "MyApp"



using std::string;
using namespace std;
using std::map;

string rawDocument;
vector<string> sentences;
int sentencesVectorSize;
map<string, int> wordFrequency;
map<string, float> sentenceRanking;
string stopwords[127];
float scoreCutOff;
int numberOfSentencesToDisplay;

ProcessDocument::ProcessDocument(string x){
    resetVariables();

    rawDocument = x;
    string stopwordsTemp[] = {"ourselves", "hers", "between", "yourself", "but", "again", "there", "about", "once", "during", "out", "very", "having", "with", "they", "own", "an", "be", "some", "for", "do", "its", "yours", "such", "into", "of", "most", "itself", "other", "off", "is", "s", "am", "or", "who", "as", "from", "him", "each", "the", "themselves", "until", "below", "are", "we", "these", "your", "his", "through", "don", "nor", "me", "were", "her", "more", "himself", "this", "down", "should", "our", "their", "while", "above", "both", "up", "to", "ours", "had", "she", "all", "no", "when", "at", "any", "before", "them", "same", "and", "been", "have", "in", "will", "on", "does", "yourselves", "then", "that", "because", "what", "over", "why", "so", "can", "did", "not", "now", "under", "he", "you", "herself", "has", "just", "where", "too", "only", "myself", "which", "those", "i", "after", "few", "whom", "t", "being", "if", "theirs", "my", "against", "a", "by", "doing", "it", "how", "further", "was", "here", "than"};
    for(int i=0; i<sizeof(stopwordsTemp)/sizeof(stopwordsTemp[0]);i++)
    {
        stopwords[i] = stopwordsTemp[i];
    }

    numberOfSentencesToDisplay = 6;
}

void ProcessDocument::resetVariables()
{
//    sentences.clear();
//    sentences.resize(100);
//    rawDocument = "";
//    wordFrequency.clear();
//    sentenceRanking.clear();
//    scoreCutOff = 0;
    sentencesVectorSize = 0;
    rawDocument = "";

}

void ProcessDocument::deallocateVariables()
{
    wordFrequency.clear();
    sentenceRanking.clear();
    sentences = vector<string>();
}

string ProcessDocument::mainLoop(){

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "0.5 trace");
    removeSquareBrackets();

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "0.25 trace");
    removeNewLines();
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "0.5 trace");
    replace("&nbsp;"," ");
    replace("&amp;","&");
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "0.6 trace");
    removeWhiteSpace();
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "0.7 trace");
    breakDownSentences();
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "0.8 trace");
    countWordFrequency();
    rankSentences();
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "6 trace");
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
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "61 trace");
    int sentencesCount = 2;
    int i = 0;
    while(rawDocument[i] != '\0' && rawDocument[i+1] != '\0')
    {
        if(rawDocument[i] == '.' && isspace(rawDocument[i+1])) sentencesCount++;
        i++;
    }

    string currentSentence = "";

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "62 trace");

    int sentenceVectorSize = 0;
    for(int i=0;i<rawDocument.length() - 1;i++)
    {
        if(rawDocument[i] == '.' && isspace(rawDocument[i+1]) )
        {
            sentenceVectorSize ++;
        }

    }


    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "63 trace");
    sentences.clear();
    sentences.resize(sentenceVectorSize+1);
//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Sentencevectorsize: %s",to_string(sentenceVectorSize).c_str());

    int j = 0;


    for(int i=0;i<rawDocument.length() - 1;i++)
    {
        if(rawDocument[i] == '.' && isspace(rawDocument[i+1]) )
        {
            currentSentence += '.';
            sentences[j] = currentSentence;
//            __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "sentence: %s, j: %s",sentences[j].c_str(), to_string(j).c_str());
            currentSentence = "";
            j++;
        }
        else
        {
            currentSentence += rawDocument[i];
        }

    }
    sentences[j] = currentSentence;

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "64 trace");

//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Sentencevectorsize (second trace): %s",to_string(sentenceVectorSize).c_str());


//    for(int i=0;i<sentencesCount;i++)
//    {
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s\n",sentences[i].c_str());
//    }


//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s",rawDocument.c_str());
//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "The number of sentences is: %d", sentencesCount);
}

void ProcessDocument::countWordFrequency()
{
    // WORK ON THIS NEXT
    string wordTemp = "";
    for(int i=0;i<rawDocument.length();i++)
    {
        if(isspace(rawDocument[i]) || rawDocument[i] == '.' || rawDocument[i] == ',' || rawDocument[i] == '(' || rawDocument[i] == ')' || rawDocument[i] == '"')
        {
            if(wordTemp != "")
            {
                if(wordFrequency[wordTemp])
                {
                    wordFrequency[wordTemp] += 1;
                } else{
                    wordFrequency[wordTemp] = 1;
                }
            }
            wordTemp = "";
        }
        else wordTemp += tolower(rawDocument[i]);

    }

//    for (auto const& pair: wordFrequency) {
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "{%s : %d}",pair.first.c_str(), pair.second);
//    }
}

void ProcessDocument::rankSentences()
{
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "1 trace");
    for(int i=0;i<sentences.size();i++)
    {
        for (auto const& pair: wordFrequency) {
            if(sentences[i].find(pair.first) != string::npos)
            {
                if(sentenceRanking[sentences[i]] )
                {

                    sentenceRanking[sentences[i]] += 1.0f;
                } else{
                    sentenceRanking[sentences[i]] = 1.0f;
                }
            }
        }
    }
//    for (auto const& pair: sentenceRanking) {
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "{%s : %d}",pair.first.c_str(), pair.second);
//    }

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "2 trace");
    int longerSentenceBias;
    for(int i=0;i<sentences.size();i++)
    {
        longerSentenceBias = 1;
        for(char& c:sentences[i])
        {
            if(c == ',') longerSentenceBias = 2;
        }
        if(longerSentenceBias > 1) sentenceRanking[sentences[i]] /= (0.75 * longerSentenceBias);
    }

    int sentenceVectorSize = 0;
    for(int i=0;i<rawDocument.length() - 1;i++)
    {
        if(rawDocument[i] == '.' && isspace(rawDocument[i+1]) )
        {
            sentenceVectorSize ++;
        }

    }

//    int tempScoreArray[sentenceVectorSize] = {0};
    vector<int> tempScoreArray;
    tempScoreArray.resize(sentences.size());
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "3 trace");
    int k = 0;
    for (auto const& pair: sentenceRanking) {
        tempScoreArray[k] = pair.second;
        k++;
    }
    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "4 trace");
    sort(tempScoreArray.begin(), tempScoreArray.end(), greater<int>()); // FIX THIS

//    for(int i=0;i<tempScoreArray.size();i++)
//    {
//        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "number (sorted): %s", to_string(tempScoreArray[i]).c_str());
//    }

    scoreCutOff = tempScoreArray[numberOfSentencesToDisplay];

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "5 trace");

//    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "{%f}", scoreCutOff);
}

string ProcessDocument::summarizer() {

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "7 trace");
    string processedDocument = sentences[0];
    for(int i=1; i<sentences.size();i++)
    {
        if(sentenceRanking[sentences[i]] > (scoreCutOff)){
            processedDocument += sentences[i];
//            __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s", sentences[i].c_str());
        }
    }

    // __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "8 trace");
    deallocateVariables();
    return processedDocument;
}