package com.example.tinderforknowledge.webscraping;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.tinderforknowledge.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class Article extends AsyncTask<Void, Void, String> {

    TextView tv;
    private String title;
    private String documentString;

    public Article(TextView tv) {
        this.tv = tv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Document document = Jsoup.connect("https://simple.wikipedia.org/wiki/Subaru").get();

            document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
            document.select("br").append("\\n");
            document.select("p").prepend("\\n");
            String s = document.html().replaceAll("\\\\n", "\n");

            //Get the logo source of the website
//            Element img = document.select("img").first();
//            // Locate the src attribute
//            String imgSrc = img.absUrl("src");
//            // Download image from URL
//            InputStream input = new java.net.URL(imgSrc).openStream();
//
//            //Get the title of the website
            title = document.title();
            documentString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
//
//            documentString = s;
//            tv.setText(title);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        showText();
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    public void showText() {
        tv.setText(documentString);
    }

    // C++ methods
    public native String DisplayText(String x);
}
