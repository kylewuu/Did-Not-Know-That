package com.example.tfk.webscraping;

import android.os.AsyncTask;

import com.example.tfk.user.ArticleWords;
import com.example.tfk.user.UserInformation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;


public class FindMoreArticles extends AsyncTask<Void, Void, String> {


    UserInformation userInfo;
    String url;
    String word;


    public FindMoreArticles(UserInformation userInfo, String url, String word) {
        this.userInfo = userInfo;
        this.url = url;
        this.word = word;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        findAndAddNewUrl();
        return null;

    }

    public void findAndAddNewUrl() {

        Document document;
        try {
            document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").get();

            for(Element tempElement : document.select(".mw-search-result-heading")){
                if(tempElement != null){
                    String tempUrl = "https://wikipedia.org/" + tempElement.select("a").attr("href");
                    if(userInfo.checkIfArticlesIsNotUsed(tempUrl)) {
                        this.url = tempUrl;

                        break;
                    }
                }
            }

            if(userInfo.checkIfArticlesIsNotUsed(url)) {
                userInfo.updateArticleWords(new ArticleWords[]{new ArticleWords(url, word)});
                System.out.println("Added url: " + url);
            }

            if(userInfo.articleWords.size() < 5 && userInfo.userWords.size() > 5) userInfo.findMoreArticles();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}