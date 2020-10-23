package com.example.tinderforknowledge.webscraping;

import android.os.AsyncTask;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.net.MalformedURLException;

public class Article extends AsyncTask<Void, Void, String> {

    TextView tv;
    private String title;
    private String documentString;
    private String processedDocument;

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
            Element bodyContent = document.getElementById("bodyContent");

            document.outputSettings(new Document.OutputSettings().prettyPrint(true));//makes html() preserve linebreaks and spacing
            document.select("br").append(" ");
            document.select("p").prepend(" ");
            String s = document.html().replaceAll("\\\\n", " ");

            //Get the logo source of the website
//            Element img = document.select("img").first();
//            // Locate the src attribute
//            String imgSrc = img.absUrl("src");
//            // Download image from URL
//            InputStream input = new java.net.URL(imgSrc).openStream();
//
//            //Get the title of the website
            title = document.title();
//            documentString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
            documentString = bodyContent.text();
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
        summarizeDocument();
        tv.setText(processedDocument);
    }

    private void summarizeDocument() {
        processedDocument = ReturnProcessedDocument(documentString);
    }

    public void showTextTemp() {
        tv.setText(documentString);
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    // C++ methods
    public native String ReturnProcessedDocument(String x);
}
