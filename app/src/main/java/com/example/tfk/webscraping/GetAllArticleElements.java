package com.example.tfk.webscraping;

import android.os.AsyncTask;

import com.example.tfk.user.UserInformation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Hashtable;


public class GetAllArticleElements extends AsyncTask<String, Void, Document> {


    private String url;
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


            for(Element tempElement : document.select("p")){
                if(tempElement != null){
                    if(tempElement.text().indexOf(" may refer to:") != -1){
                        return parseReferToPage(document); // if refer to page
                    }

                }
            }

            return document;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Document parseReferToPage(Document document){
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
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;


    }

}