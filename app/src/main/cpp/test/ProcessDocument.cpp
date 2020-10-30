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
map<string, int> wordFrequency;
map<string, float> sentenceRanking;
string stopwords[127];
float scoreCutOff;
int numberOfSentencesToDisplay;

ProcessDocument::ProcessDocument(string x){
    rawDocument = x;
    string stopwordsTemp[] = {"ourselves", "hers", "between", "yourself", "but", "again", "there", "about", "once", "during", "out", "very", "having", "with", "they", "own", "an", "be", "some", "for", "do", "its", "yours", "such", "into", "of", "most", "itself", "other", "off", "is", "s", "am", "or", "who", "as", "from", "him", "each", "the", "themselves", "until", "below", "are", "we", "these", "your", "his", "through", "don", "nor", "me", "were", "her", "more", "himself", "this", "down", "should", "our", "their", "while", "above", "both", "up", "to", "ours", "had", "she", "all", "no", "when", "at", "any", "before", "them", "same", "and", "been", "have", "in", "will", "on", "does", "yourselves", "then", "that", "because", "what", "over", "why", "so", "can", "did", "not", "now", "under", "he", "you", "herself", "has", "just", "where", "too", "only", "myself", "which", "those", "i", "after", "few", "whom", "t", "being", "if", "theirs", "my", "against", "a", "by", "doing", "it", "how", "further", "was", "here", "than"};
    for(int i=0; i<sizeof(stopwordsTemp)/sizeof(stopwordsTemp[0]);i++)
    {
        stopwords[i] = stopwordsTemp[i];
    }

    numberOfSentencesToDisplay = 6;
}

string ProcessDocument::mainLoop(){
    removeSquareBrackets();
    removeNewLines();
    replace("&nbsp;"," ");
    replace("&amp;","&");
    removeWhiteSpace();
    breakDownSentences();
    countWordFrequency();
    rankSentences();
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

    int longerSentenceBias;
    for(int i=0;i<sentences.size();i++)
    {
        longerSentenceBias = 1;
        for(char& c:sentences[i])
        {
            if(c == ',') longerSentenceBias ++;
        }
        sentenceRanking[sentences[i]] /= (longerSentenceBias);
    }

    int tempScoreArray[sentences.size()];
    int k = 0;
    for (auto const& pair: sentenceRanking) {
        tempScoreArray[k] = pair.second;
        k++;
    }
    sort(tempScoreArray, tempScoreArray + sentences.size(), greater<int>());

    scoreCutOff = tempScoreArray[numberOfSentencesToDisplay];

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "{%f}", scoreCutOff);
}

string ProcessDocument::summarizer() {

    string processedDocument = sentences[0];
    for(int i=1; i<sentences.size();i++)
    {
        if(sentenceRanking[sentences[i]] > (scoreCutOff)){
            processedDocument += sentences[i];
//            __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s", sentences[i].c_str());
        }
    }

    return processedDocument;
}