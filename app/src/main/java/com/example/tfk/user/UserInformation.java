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
    public Vector<ParentWords> parentWords;
    public Vector<ArticleWords> articleWords;
    public JSONObject config;
    public Context context;
    private FirebaseFunctions mFunctions;
    private int semaphore;
    private int semaphoreLockout;

    public UserInformation(Context applicationContext, FirebaseFunctions mFunctions) throws JSONException {
        // get username
        // get data
        context = applicationContext;
        this.mFunctions = mFunctions;
        checkConfig();
        updateVectors();
        semaphore = 0;
        semaphoreLockout = 2;
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


    }

    private void firstTimeInitTextFiles(){
        // temp writing arguments, needs to change later
        usedWords = new Vector<>();
        usedArticles = new Vector<>();
        userWords = new Vector<>();
        parentWords = new Vector<>();
        articleWords = new Vector<>();

        String[] userStartWords = new String[]{"travel", "software", "anti-plague", "vancouver-kingsway", "military", "university", "football", "production", "announced", "unforced", "radio"};
        String[] userStartArticles = new String[]{"https://en.wikipedia.org/wiki/Vancouver", "https://en.wikipedia.org/wiki/Subaru"};
        // temp values for testing but ALWAYS MAKE SURE TO START WITH SOME VALUES
        updateUsedWords(new String[]{"travelling", "trip"});
        updateUsedArticles(new String[]{"https://en.wikipedia.org/wiki/Norway"});
//        updateUserWords(userStartWords);
        updateUserWords(new String[]{"travel", "travelling", "vancouver", "calgary"});
        updateArticleWords(noWordArticlesToArticleWords(userStartArticles));
//        updateParentWords(noParentWordsToParentWordsArray(userStartWords));
        updateParentWords(new ParentWords[]{new ParentWords("travel", "travelling"), new ParentWords("vancouver", "calgary")});
    }


    private void updateVectors(){
        usedWords = readUsedWordsFromFile();
        usedArticles = readUsedArticlesFromFile();
        userWords = readUserWordsFromFile();
        parentWords = readParentWordsFromFile();
        articleWords = readArticleWordsFromFile();
    }


    private ParentWords[] noParentWordsToParentWordsArray(String[] words){
        ParentWords[] ret = new ParentWords[words.length];
        for(int i=0;i<words.length;i++){
            ret[i] = new ParentWords(words[i], "");
        }
        return ret;
    }

    private ArticleWords[] noWordArticlesToArticleWords(String[] articles){
        ArticleWords[] ret = new ArticleWords[articles.length];
        for(int i=0;i<articles.length;i++){
            ret[i] = new ArticleWords(articles[i], articles[i].split("wiki/", 2)[1].toLowerCase());
        }
        return ret;
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


    private Vector<ParentWords> readParentWordsFromFile(){
        Vector<ParentWords> ret = new Vector<>();

        try {
            InputStream inputStream = context.openFileInput("parentWords.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                    String[] parentWords = receiveString.split("&",2);
                    ret.add(new ParentWords(parentWords[0], parentWords[1]));
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

    private Vector<ArticleWords> readArticleWordsFromFile(){
        Vector<ArticleWords> ret = new Vector<>();

        try {
            InputStream inputStream = context.openFileInput("articleWords.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                    String[] articleWords = receiveString.split("&&&",2);
                    ret.add(new ArticleWords(articleWords[0], articleWords[1]));
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


    private void writeToParentWords(){

        String str = arrayToParentWords(parentWords.toArray(new ParentWords[parentWords.size()]));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("parentWords.txt", context.MODE_PRIVATE));
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeToArticleWords(){

        String str = arrayToArticleWords(articleWords.toArray(new ArticleWords[articleWords.size()]));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("articleWords.txt", context.MODE_PRIVATE));
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


    public void updateParentWords(ParentWords[] data){
        for(int i = 0; i < data.length; i++){
            parentWords.add(new ParentWords(data[i].getWord(), data[i].getParent()));
        }
        writeToParentWords();

    }

    public void updateArticleWords(ArticleWords[] data){
        for(int i = 0; i < data.length; i++){
            articleWords.add(new ArticleWords(data[i].getUrl(), data[i].getWord()));
        }
        writeToArticleWords();

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

    public synchronized String getTargetWord(){
        // add chosen word to usedWords and remove from userWords
        String[] array = userWords.toArray(new String[userWords.size()]);
        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        updateUsedWords(new String[]{array[rnd]});
        userWords.remove(array[rnd]);
        writeToUserWords();
        replenishWordsUsingUserWord();
        return ret;
    }

    public String getRandomUserWord(){
        String[] array = userWords.toArray(new String[userWords.size()]);
        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        updateUsedWords(new String[]{array[rnd]});
        userWords.remove(array[rnd]);
        writeToUserWords();
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

    private String arrayToParentWords(ParentWords[] array){
        StringBuilder ret = new StringBuilder();
        for(int i = 0;i < array.length; i++){
            ret.append(array[i].getWord());
            ret.append("&");
            ret.append(array[i].getParent());
            ret.append("\n");
        }
        return ret.toString();
    }

    private String arrayToArticleWords(ArticleWords[] array){
        StringBuilder ret = new StringBuilder();
        for(int i = 0;i < array.length; i++){
            ret.append(array[i].getUrl());
            ret.append("&&&");
            ret.append(array[i].getWord());
            ret.append("\n");
        }
        return ret.toString();
    }

    public synchronized void replenishWordsUsingUserWord(){
        if(userWords.size() < 10 && userWords.size() > 0 && semaphore < semaphoreLockout) {
            semaphore ++;
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
                                replenishArticles();
                                semaphore --;

                            }
                            else if(task.isComplete())
                            {
                                System.out.println(task.getException());
                            }
                        }
                    });;
        }
        else if(userWords.size() < 1){
            // call firebase function to get a random word, then call replenishWords();

        }
    }

    public void replenishWordsRandomly(){
        Map<String, Object> data = new HashMap<>();

//            System.out.println("Replenshing words ... using: " + targetWord);
        String[] bannedWords = this.getUsedWords().toArray(new String[this.getUsedWords().size()]);
//            String[] userWords = this.getUserWords().toArray(new String[this.getUserWords().size()]);
        data.put("bannedWords", new JSONArray(Arrays.asList(bannedWords)));

        mFunctions.getHttpsCallable("getRandomWords")
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
                    System.out.println("New found words: "+ Arrays.toString(topics));


                }
                else if(task.isComplete())
                {
                    System.out.println(task.getException());
                }
            }
        });;
    }



    public synchronized void replenishArticles() {
        if(articleWords.size() < 5 && userWords.size() > 0) {
            String topic = this.getTargetWord();
            String url = "https://en.wikipedia.org/w/index.php?search="+topic+"&title=Special%3ASearch&go=Go&ns0=1";

            (new FindMoreArticles(this, url, topic)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        else if(userWords.size() < 1){
            replenishWordsUsingUserWord();
        }

    }

    // ----------------------------------------------------------
    // --                    new functions                     --
    // ----------------------------------------------------------


    public synchronized void findMoreArticles(){
        if(articleWords.size() < 5 && userWords.size() > 0) {
            String chosenWord = this.chooseRandomUserWord();
            removeWordFromUserWords(chosenWord);
            moveUserWordToUsedWords(chosenWord);
            removeUserWordFromParentWords(chosenWord);
            callFindMoreArticles(chosenWord);
        }
        else{
//            findMoreWords(); // NEEDS IMPLEMENTATION
        }
    }

    public synchronized String chooseRandomUserWord(){
        // add chosen word to usedWords and remove from userWords
        String[] array = userWords.toArray(new String[userWords.size()]);
        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        return ret;
    }

    public synchronized void removeWordFromUserWords(String word){
        userWords.remove(word);
    }

    public synchronized void moveUserWordToUsedWords(String word){
        updateUsedWords(new String[]{word});
    }

    public synchronized void removeUserWordFromParentWords(String word){
        for(int i=0; i<parentWords.size() ;i++){
            if(parentWords.get(i).getWord() == word) {
                parentWords.remove(parentWords.get(i));
                i++;
            }
        }
    }

    public synchronized void callFindMoreArticles(String word){
        String url = "https://en.wikipedia.org/w/index.php?search="+word+"&title=Special%3ASearch&go=Go&ns0=1";

        (new FindMoreArticles(this, url, word)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized ArticleWords getArticleAndWord(){
        // add chosen word to usedWords and remove from userWords
        ArticleWords[] array = articleWords.toArray(new ArticleWords[articleWords.size()]);
        int rnd = new Random().nextInt(array.length);
        ArticleWords ret = array[rnd];
        updateUsedArticles(new String[]{array[rnd].getUrl()});
        articleWords.remove(array[rnd]);
        writeToArticleWords();
        findMoreArticles();
        return ret;
    }

    public synchronized void removeUserWordOnDislike(String word){
        removeAllChildrenWordsFromParentWordAndUserWords(word);
    }

    private synchronized void removeAllChildrenWordsFromParentWordAndUserWords(String word){
//        System.out.println("Word to remove: " + word);
        Vector<String> wordsToRemove = new Vector<>();
        wordsToRemove.add(word.toLowerCase());
        for(int i=0;i<parentWords.size();i++){
            String tempWord = parentWords.get(i).getParent();
            String tempChildWord = parentWords.get(i).getWord();

            if(wordsToRemove.contains(tempWord) || wordsToRemove.contains(tempChildWord)){
                wordsToRemove.add(tempWord);
                if(!wordsToRemove.contains(tempChildWord)) wordsToRemove.add(tempChildWord);
                if(!wordsToRemove.contains(tempWord)) wordsToRemove.add(tempWord);

                if(userWords.contains(tempChildWord)) userWords.remove(tempChildWord);
                if(userWords.contains(tempWord)) userWords.remove(tempWord);

                if(!usedWords.contains(tempChildWord)) usedWords.add(tempChildWord);
                if(!usedWords.contains(tempWord)) usedWords.add(tempWord);

                parentWords.remove(i);
                i--;
            }
        }

        for(int i=0;i<articleWords.size();i++){
            if(wordsToRemove.contains(articleWords.get(i))) {
                usedArticles.add(articleWords.get(i).getUrl());
                articleWords.remove(i);
                i--;
            }
        }

        writeToUsedArticles();
        writeToUsedWords();
        writeToUserWords();
        writeToParentWords();

        // maybe call replenish words and/or replenish articles here?
    }
    // ----------------------------------------------------------
    // --               end of new functions                   --
    // ----------------------------------------------------------


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
