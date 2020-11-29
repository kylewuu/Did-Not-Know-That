package com.example.tfk.webscraping;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tfk.user.UserInformation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;


import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Article extends AsyncTask<Void, Void, String> {

    TextView tv;
    TextView titleTV;
    private String title;
    private String documentString;
    private String processedDocument;
    private String[] topics;
    private String topic;
    private FirebaseFunctions mFunctions;
    private String url;
    private UserInformation userInfo;

    public Article(TextView tv, TextView title, String topic, FirebaseFunctions mFunctions, UserInformation userInfo) {
        this.tv = tv;
        this.titleTV = title;
//        this.topics = topics; // not sure how to get the used topics yet
        this.mFunctions = mFunctions;
        this.topic = topic;
        this.userInfo = userInfo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        getDocument();
        return null;

    }

    @Override
    protected void onPostExecute(String result) {
        // sets the texts
        System.out.println(url);
        userInfo.updateUsedArticles(new String[]{url}); // this needs to be moved
        summarizeDocument();
//        System.out.println(documentString);
        tv.setText(processedDocument);
        titleTV.setText(title);

    }

    private void summarizeDocument() {
        processedDocument = ReturnProcessedDocument(documentString);
//        System.out.println(processedDocument);
    }

    public void getDocument() {
        try {

            url = "https://wikipedia.org/wiki/"+this.topic;

//            System.out.println(url);

            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").get();


            boolean parseReferToPageFlag = false;
            for(Element tempElement : document.select("p")){
                if(tempElement != null){
                    if(tempElement.text().indexOf(" may refer to:") != -1){
                        parseReferToPage(document); // if refer to page
                        parseReferToPageFlag = true;
                    }

                }
            }
            if(!parseReferToPageFlag) parsePageFound(document); // if page found

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            parseSearchPage(); // if page not found aka do a search

        }
    }

    private void parsePageFound(Document document) {
        title = document.getElementById("firstHeading").text();



        String[] elementsToBeRemovedById = {
//                    "bodyContent",
                "siteSub",
                "contentSub2",
                "coordinates",
                "toc",
                "mw-panel",
                "mw-head",
                "catlinks",
                "footer",
                "mw-navigation",
                "firstHeading",

        };
        String[] elementsToBeRemovedByClass = {
                "thumbinner",
                "mw-headline",
                "mw-editsection",
                "infobox",
                "mw-jump-link",
                "hatnote",
                "shortdescription",
                "mw-disambig",
                "reflist",
                "printfooter",
                "navbox",
                "boilerplate",
                "mbox-small",
                "rt-commentedText",

        };

        String[] elementsToBeRemovedByType = {
                "ul",
                "table",
                "dl",
                "title",
        };

        for(String element: elementsToBeRemovedById){
            Element tempElement = document.getElementById(element);
            if(tempElement != null){
                tempElement.remove();
            }
        }

        for(String className: elementsToBeRemovedByClass) {
            for(Element tempElement : document.select("."+className)){
                if(tempElement != null){
                    tempElement.remove();
                }
            }
        }

        for(String typeName: elementsToBeRemovedByType) {
            for(Element tempElement : document.select(typeName)){
                if(tempElement != null){
                    tempElement.remove();
                }
            }
        }

        document.outputSettings(new Document.OutputSettings().prettyPrint(true));//makes html() preserve linebreaks and spacing
        document.select("br").append("\n");
        document.select("p").prepend("\n");
//
        String s = document.html().replaceAll("\\\\n", " ");

        documentString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        if(documentString.indexOf("(Redirected from ") != -1) documentString = documentString.substring(documentString.indexOf(")") + 1);
    }

    private void parseSearchPage(){
        url = "https://en.wikipedia.org/w/index.php?search="+this.topic+"&title=Special%3ASearch&go=Go&ns0=1";
        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").get();


            for(Element tempElement : document.select(".mw-search-result-heading")){
                if(tempElement != null){
                    String tempUrl = "https://wikipedia.org/" + tempElement.select("a").attr("href");
                    if(userInfo.checkIfArticlesIsNotUsed(tempUrl)) {
                        url = tempUrl;
                        break;
                    }
                }
            }
            document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").get();

            parsePageFound(document);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void parseReferToPage(Document document){
//        checkIfKeywordIsNotInUserWords
        // do a broad check of the different contents before
        Elements body = document.select("#mw-content-text");

        String[] elementsToBeRemovedByClass = {
                "tocright"
        };
        for(String element: elementsToBeRemovedByClass) {
            Elements tempElements = body.select("."+element);
            for(Element tempElement : tempElements){
                if(tempElement != null) body.select("."+element).remove();
            }
        }



        Hashtable<String, Integer> linksRankings = new Hashtable<String, Integer>();
        for(Element tempElement : body.select("li")){
            String link = "";
            int score = 0;
            if(tempElement != null){
                String linkText = tempElement.text().toLowerCase();
                link = tempElement.select("a").attr("href");
                String[] words = linkText.split("\\W+");
//                System.out.println("possible links: " + link);

                score = 0;
                for(int i=0;i<words.length;i++){
                    if(userInfo.checkIfKeywordIsInUserWords(words[i]) && words[i] != "and") {
                        score ++;
                    };
                }
            }
            linksRankings.put(link, score);

        }

        int tempMaxScore = 0;
        for (String name: linksRankings.keySet()){
            String key = name.toString();
            String value = linksRankings.get(name).toString();
            String tempUrl =  "https://wikipedia.org/" + key;
            if(linksRankings.get(name) >= tempMaxScore && userInfo.checkIfArticlesIsNotUsed(tempUrl)){
                url = tempUrl;
                tempMaxScore = linksRankings.get(name);
            }
        }

        // pass the url to parsePageFound
        try {
            document = Jsoup.connect(url).get();
            parsePageFound(document);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // change this function so that it can take in array of strings
    public String getChosenUrl(){
        return this.url;
    }


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    // C++ methods
    public native String ReturnProcessedDocument(String x);
}
