package com.app.tfk.webscraping;

import android.os.AsyncTask;
import com.app.tfk.user.UserInformation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Hashtable;


public class GetAllArticleElements extends AsyncTask<String, Void, Document> {
    public String url;
    UserInformation userInfo;

    public GetAllArticleElements(String url, UserInformation userInfo){
        this.url = url;
        this.userInfo = userInfo;
    }

    @Override
    protected Document doInBackground(String... params) {
        Document document;
        try {
            document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").get();
            for (Element tempElement : document.select("p")) {
                if (tempElement != null) {
                    if (checkForList(document, tempElement)) {
                        return parseReferToPage(document);
                    }
                }
            }
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document parseReferToPage(Document document) {
        Elements body = document.select("#mw-content-text");
        String[] elementsToBeRemovedByClass = {
                "tocright"
        };
        for (String element: elementsToBeRemovedByClass) {
            Elements tempElements = body.select("."+element);
            for (Element tempElement : tempElements) {
                if (tempElement != null) body.select("."+element).remove();
            }
        }
        Hashtable<String, Integer> linksRankings = new Hashtable<String, Integer>();
        for (Element tempElement : body.select("li")) {
            String link = "";
            int score = 0;
            if (tempElement != null) {
                String linkText = tempElement.text().toLowerCase();
                link = tempElement.select("a").attr("href");
                String[] words = linkText.split("\\W+");
                score = 0;
                for (int i=0;i<words.length;i++) {
                    if (userInfo.checkIfKeywordIsInUserWords(words[i]) && words[i] != "and") {
                        score ++;
                    }
                }
            }
            linksRankings.put(link, score);
        }
        int tempMaxScore = 0;
        for (String name: linksRankings.keySet()){
            String key = name.toString();
            String value = linksRankings.get(name).toString();
            String tempUrl =  "https://wikipedia.org/" + key;
            if (linksRankings.get(name) >= tempMaxScore && userInfo.checkIfArticlesIsNotUsed(tempUrl)) {
                url = tempUrl;
                tempMaxScore = linksRankings.get(name);
            }
        }
        try {
            document = Jsoup.connect(url).get();
            for (Element tempElement : document.select("p")) {
                if (tempElement != null) {
                    if (checkForList(document, tempElement)) {
                        return parseReferToPage(document);
                    }
                }
            }
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkForList(Document doc, Element tempElement) {
        return isAListBasedOnKeyWord(doc, tempElement, "ay refer to:") || isAListBasedOnKeyWord(doc, tempElement, "may be:") || isAListBasedOnKeyWord(doc, tempElement, "may mean:") || isAListBasedOnKeyWord(doc, tempElement, "may refer to several");
    }

    private boolean isAListBasedOnKeyWord(Document doc, Element tempElement, String text) {
        return (tempElement.text().indexOf(text) != -1 && (tempElement.text().indexOf(doc.getElementById("firstHeading").text()) < tempElement.text().indexOf(text)) && (tempElement.text().indexOf(doc.getElementById("firstHeading").text()) > tempElement.text().indexOf(text) - 60));
    }
}


