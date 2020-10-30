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
    TextView titleTV;
    private String title;
    private String documentString;
    private String processedDocument;

    public Article(TextView tv, TextView title) {
        this.tv = tv;
        this.titleTV = title;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Document document = Jsoup.connect("https://wikipedia.org/wiki/Vancouver").get();
//            document.select("div#bodyContent");
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

            //Get the logo source of the website
//            Element img = document.select("img").first();
//            // Locate the src attribute
//            String imgSrc = img.absUrl("src");
//            // Download image from URL
//            InputStream input = new java.net.URL(imgSrc).openStream();
//
//            //Get the title of the website

//            title = "adsfasdfasdf";
            documentString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
//            documentString = "There are many techniques available to generate extractive summarization to keep it simple, I will be using an unsupervised learning approach to find the sentences similarity and rank them. Summarization can be defined as a task of producing a concise and fluent summary while preserving key information and overall meaning. One benefit of this will be, you don’t need to train and build a model prior start using it for your project. It’s good to understand Cosine similarity to make the best use of the code you are going to see. Cosine similarity is a measure of similarity between two non-zero vectors of an inner product space that measures the cosine of the angle between them. Its measures cosine of the angle between vectors. The angle will be 0 if sentences are similar.";
//            documentString = bodyContent.text();

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
        titleTV.setText(title);
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
