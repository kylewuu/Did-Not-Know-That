package com.example.tfk.webscraping;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class Article extends AsyncTask<Void, Void, String> {

    TextView tv;
    TextView titleTV;
    private String title;
    private String documentString;
    private String processedDocument;
    private String[] topics;
    private String topic;
    private FirebaseFunctions mFunctions;

    public Article(TextView tv, TextView title, String topic, FirebaseFunctions mFunctions) {
        this.tv = tv;
        this.titleTV = title;
//        this.topics = topics; // not sure how to get the used topics yet
        this.mFunctions = mFunctions;
        this.topic = topic;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        getDocumentAndParse();
        return null;

    }

    @Override
    protected void onPostExecute(String result) {
        // sets the texts
        summarizeDocument();
        tv.setText(processedDocument);
        titleTV.setText(title);

    }

    private void summarizeDocument() {
        processedDocument = ReturnProcessedDocument(documentString);
    }

    public void getDocumentAndParse() {
        try {

            Document document = Jsoup.connect("https://wikipedia.org/wiki/"+this.topic).get();
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

            documentString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // change this function so that it can take in array of strings
    private Task<String> findTopicThroughHTTP() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", "text");
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("findTopic")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    // C++ methods
    public native String ReturnProcessedDocument(String x);
}
