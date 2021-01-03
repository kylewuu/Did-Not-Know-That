package com.example.tfk.webscraping;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tfk.UI.Cards;
import com.example.tfk.user.ArticleWords;
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
import java.util.concurrent.ExecutionException;

public class Articles{

    private UserInformation userInfo;

    public Articles(UserInformation userInfo) {
        this.userInfo = userInfo;

    }

//    public void findMoreArticles() {
//        String topic = userInfo.getTargetWord();
//        String url = "https://en.wikipedia.org/w/index.php?search="+topic+"&title=Special%3ASearch&go=Go&ns0=1";
//
//        (new FindMoreArticles(userInfo, url)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//    }

    public synchronized String[] getAllArticlesElements(UserInformation userInfo, ArticleWords[] articlesInDeck) {
        String[] ret = new String[4]; // title, body, link
//        String url = userInfo.getTargetArticle();
//        ArticleWords articleWord = userInfo.getArticleAndWord();
        ArticleWords articleWord = userInfo.getArticleAndWordMinusDeckArticleWords(articlesInDeck);
        String url = articleWord.getUrl();
        String word = articleWord.getWord();
        ret[2] = url;
        ret[3] = word;


        try {
//            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
//                    .referrer("http://www.google.com").get();

            System.out.println("Grabbing document from url: " + url);
            Document document = (new GetAllArticleElements(url, userInfo)).execute().get();

            ret[0] = parseTitle(document);
            ret[1] = parseBody(document).trim();
            System.out.println("Adding new card titled: " + ret[0]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }


    private String parseTitle(Document document){
        String title = document.getElementById("firstHeading").text();
        return title;
    }

    private String parseBody(Document document) {


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

        String documentString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        if(documentString.indexOf("(Redirected from ") != -1) documentString = documentString.substring(documentString.indexOf(")") + 1);
        return ReturnProcessedDocument(documentString); // summarized document

    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    // C++ methods
    public native String ReturnProcessedDocument(String x);
}
