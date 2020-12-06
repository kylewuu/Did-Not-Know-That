package com.example.tfk.user;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tfk.webscraping.FindMoreArticles;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


public class UserInformation {

    public Vector<String> usedWords;
    public Vector<String> usedArticles;
    public Vector<String> userWords;
    public Vector<String> userArticles;
    public JSONObject config;
    public Context context;
    private FirebaseFunctions mFunctions;

    public UserInformation(Context applicationContext, FirebaseFunctions mFunctions) throws JSONException {
        // get username
        // get data
        context = applicationContext;
        this.mFunctions = mFunctions;
        checkConfig();
        updateVectors();
    }

    private void checkConfig() throws JSONException {
        try {
            context.openFileInput("config.json");
            readConfig();
        }
        catch (FileNotFoundException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstBoot", false);
            config = jsonObject;
            writeToConfig();

            firstTimeInitTextFiles(); // add in condition for this to run
        }

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("firstBoot", false);
//        config = jsonObject;
//        writeToConfig();
//
//        firstTimeInitTextFiles(); // add in condition for this to run

    }

    private void firstTimeInitTextFiles(){
        // temp writing arguments, needs to change later
        usedWords = new Vector<>();
        usedArticles = new Vector<>();
        userWords = new Vector<>();
        userArticles = new Vector<>();

        // temp values for testing
        updateUsedWords(new String[]{"travelling", "trip"});
        updateUsedArticles(new String[]{"https://en.wikipedia.org/wiki/Norway"});
        updateUserWords(new String[]{"travel", "software", "anti-plague", "vancouver-kingsway", "military", "university", "football", "production", "announced", "unforced", "radio"});
        updateUserArticles(new String[]{"https://en.wikipedia.org/wiki/Vancouver", "https://en.wikipedia.org/wiki/Subaru"});
    }

    private void updateVectors(){
        usedWords = readUsedWordsFromFile();
        usedArticles = readUsedArticlesFromFile();
        userWords = readUserWordsFromFile();
        userArticles = readUserArticlesFromFile();
    }


    private Vector<String> readUsedArticlesFromFile(){

        Vector<String> ret = new Vector<String>();

        try {
            InputStream inputStream = context.openFileInput("usedArticles.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                    ret.add(receiveString);
                }

                inputStream.close();
//                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        return ret;

    }

    private Vector<String> readUsedWordsFromFile(){

        Vector<String> ret = new Vector<String>();

        try {
            InputStream inputStream = context.openFileInput("usedWords.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                    ret.add(receiveString);
                }

                inputStream.close();
//                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        return ret;


    }

    private Vector<String> readUserWordsFromFile(){

        Vector<String> ret = new Vector<String>();

        try {
            InputStream inputStream = context.openFileInput("userWords.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                    ret.add(receiveString);
                }

                inputStream.close();
//                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        return ret;

    }

    private Vector<String> readUserArticlesFromFile(){

        Vector<String> ret = new Vector<String>();

        try {
            InputStream inputStream = context.openFileInput("userArticles.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                    ret.add(receiveString);
                }

                inputStream.close();
//                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        return ret;

    }

    private JSONObject readConfig() throws JSONException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.json");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        JSONObject jsonObject  = new JSONObject(ret);

        return jsonObject;
    }


    private void writeToUsedArticles(){

        String str = arrayToString(usedArticles.toArray(new String[usedArticles.size()]));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("usedArticles.txt", context.MODE_PRIVATE));
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeToUsedWords(){

        String str = arrayToString(usedWords.toArray(new String[usedWords.size()]));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("usedWords.txt", context.MODE_PRIVATE));
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeToUserWords(){

        String str = arrayToString(userWords.toArray(new String[userWords.size()]));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("userWords.txt", context.MODE_PRIVATE));
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeToUserArticles(){

        String str = arrayToString(userArticles.toArray(new String[userArticles.size()]));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("userArticles.txt", context.MODE_PRIVATE));
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeToConfig() throws JSONException {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.json", context.MODE_PRIVATE));
            outputStreamWriter.write(config.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    public void updateUsedArticles(String[] data){
        for(int i = 0; i < data.length; i++){
            usedArticles.add(data[i]);
        }
        writeToUsedArticles();

    }

    public void updateUsedWords(String[] data){
        for(int i = 0; i < data.length; i++){
            usedWords.add(data[i]);
        }
        writeToUsedWords();

    }

    public void updateUserWords(String[] data){
        for(int i = 0; i < data.length; i++){
            userWords.add(data[i]);
        }
        writeToUserWords();

    }

    public void updateUserArticles(String[] data){
        for(int i = 0; i < data.length; i++){
            userArticles.add(data[i]);
        }
        writeToUserArticles();

    }

    private void updateConfig(String key, String value) throws JSONException {
        config.put(key, value);
        writeToConfig();
    }


    public Vector<String> getUsedWords(){
        return usedWords;
    }

    public Vector<String> getUserWords(){
        return userWords;
    }

    public String getTargetWord(){
        // add chosen word to usedWords and remove from userWords
        String[] array = userWords.toArray(new String[userWords.size()]);
        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        updateUsedWords(new String[]{array[rnd]});
        userWords.remove(array[rnd]); // this should be removed since we want to keep the word
        writeToUserWords();
        replenishWords();
        return ret;
    }

    public String getRandomUserWord(){
        String[] array = userWords.toArray(new String[userWords.size()]);
        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        updateUsedWords(new String[]{array[rnd]});
        userWords.remove(array[rnd]); // this should be removed since we want to keep the word
        writeToUserWords();
        return ret;
    }

    public String getTargetArticle(){
        // add chosen word to usedWords and remove from userWords

        String[] array = userArticles.toArray(new String[userArticles.size()]);
        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        updateUsedArticles(new String[]{array[rnd]});
        userArticles.remove(array[rnd]);
        writeToUserArticles();
        replenishArticles();
        return ret;
    }


    private String arrayToString(String[] array){
        StringBuilder ret = new StringBuilder();
        for(int i = 0;i < array.length; i++){
            ret.append(array[i]);
            ret.append("\n");
        }
        return ret.toString();
    }

    private void replenishWords(){
        if(userWords.size() < 10) {
            Map<String, Object> data = new HashMap<>();
            String targetWord = this.getRandomUserWord();
//            System.out.println("Replenshing words ... using: " + targetWord);
            String[] bannedWords = this.getUsedWords().toArray(new String[this.getUsedWords().size()]);
//            String[] userWords = this.getUserWords().toArray(new String[this.getUserWords().size()]);
            data.put("targetWord", targetWord);
            data.put("bannedWords", new JSONArray(Arrays.asList(bannedWords)));

            mFunctions.getHttpsCallable("findTopic")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, String[]>() {
                        @Override
                        public String[] then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            // This continuation runs on either success or failure, but if the task
                            // has failed then getResult() will throw an Exception which will be
                            // propagated down.
                            String[] result = task.getResult().getData().toString().substring(1, task.getResult().getData().toString().length() -1).split("\\s*,\\s*");
                            return result;

                        }
                    }).addOnCompleteListener(new OnCompleteListener<String[]>() {
                        @Override
                        public void onComplete(@NonNull Task<String[]> task) {
                            if(task.isSuccessful()){
                                String[] topics = task.getResult();
                                System.out.println("New added words using " + targetWord + ": "+ Arrays.toString(topics));
                                updateUserWords(topics);

                            }
                            else if(task.isComplete())
                            {
                                System.out.println(task.getException());
                            }
                        }
                    });;
        }
    }

    public void replenishArticles() {
        if(userArticles.size() < 5) {
            String topic = this.getTargetWord();
            String url = "https://en.wikipedia.org/w/index.php?search="+topic+"&title=Special%3ASearch&go=Go&ns0=1";

            (new FindMoreArticles(this, url)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


    }


    public boolean checkIfArticlesIsNotUsed(String url){
        if(usedArticles.contains(url)) return false;
        return true;
    }

    public boolean checkIfKeywordIsInUserWords(String word){
        if(userWords.contains(word)) return true;
        return false;
    }

    // helper functions
    public String[] concatArray(String[] a1, String[] a2) {
        String[] array1 = a1;
        String[] array2 = a2;

        int aLen = array1.length;
        int bLen = array2.length;
        String[] result = new String[aLen + bLen];

        System.arraycopy(array1, 0, result, 0, aLen);
        System.arraycopy(array2, 0, result, aLen, bLen);

        return result;
    }
}
