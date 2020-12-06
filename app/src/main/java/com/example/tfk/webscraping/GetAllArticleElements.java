package com.example.tfk.webscraping;

import android.os.AsyncTask;

import com.example.tfk.user.UserInformation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class GetAllArticleElements extends AsyncTask<String, Void, Document> {


    @Override
    protected Document doInBackground(String... params) {
        String url = params[0];
        Document document;
        try {
            document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").get();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}