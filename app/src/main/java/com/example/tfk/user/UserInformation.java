package com.example.tfk.user;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.tfk.webscraping.FindMoreArticles;
import com.google.android.gms.common.util.ArrayUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class UserInformation {

    public ArrayList<String> usedWords;
    public ArrayList<String> usedArticles;
    public ArrayList<String> userWords;
    public ArrayList<ParentWords> parentWords;
    public ArrayList<ArticleWords> articleWords;
    public ArrayList<String> userLikedWords;
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
//        checkConfig();
//        updateArrayLists();
        semaphore = 0;
        semaphoreLockout = 2;
    }


    public void createConfig() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstBoot", false);
        config = jsonObject;
        writeToConfig();
    }

    public boolean checkForFirstBoot(){
        try {
            context.openFileInput("config.json");
            readConfig();
            return false;
        }
        catch (FileNotFoundException | JSONException e) {

            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void firstTimeInitTextFiles(){
        // temp writing arguments, needs to change later
        usedWords = new ArrayList<>();
        usedArticles = new ArrayList<>();
        userWords = new ArrayList<>();
        parentWords = new ArrayList<>();
        articleWords = new ArrayList<>();
        userLikedWords = new ArrayList<>();

        String[] userStartWords = new String[]{"travel", "software", "anti-plague", "military", "university", "football", "production", "announced", "unforced", "radio"};
        String[] userStartArticles = new String[]{"https://en.wikipedia.org/wiki/tuplets", "https://en.wikipedia.org/wiki/Slamdunk", "https://en.wikipedia.org/wiki/Allentown"};
        // temp values for testing but ALWAYS MAKE SURE TO START WITH SOME VALUES
        updateUsedWords(new String[]{"travelling", "trip", "slamdunk", "tuplets"});
        updateUsedArticles(new String[]{"https://en.wikipedia.org/wiki/Norway"});
//        updateUserWords(userStartWords);
        updateUserWords(new String[]{"football", "vancouver-kingsway"});
        updateArticleWords(noWordArticlesToArticleWords(userStartArticles));
//        updateParentWords(noParentWordsToParentWordsArray(userStartWords));
        updateParentWords(new ParentWords[]{new ParentWords("football", ""), new ParentWords("vancouver-kingsway", "")});
        updateUserLikedWords(new String[] {});
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void firstTimeInitTextFilesBasedOnPreference(String[] chosenWords){
        usedWords = new ArrayList<>();
        usedArticles = new ArrayList<>();
        userWords = new ArrayList<>();
        parentWords = new ArrayList<>();
        articleWords = new ArrayList<>();
        userLikedWords = new ArrayList<>();

        // temp values for testing but ALWAYS MAKE SURE TO START WITH SOME VALUES
        updateUsedWords(new String[]{});
        updateUsedArticles(new String[]{});
        updateUserWords(chosenWords);
        updateArticleWords(new ArticleWords[]{});
        updateParentWords(noParentWordsToParentWordsArray(chosenWords));
        updateUserLikedWords(chosenWords);

    }


    public void updateArrayLists(){
        usedWords = readUsedWordsFromFile();
        usedArticles = readUsedArticlesFromFile();
        userWords = readUserWordsFromFile();
        parentWords = readParentWordsFromFile();
        articleWords = readArticleWordsFromFile();
        userLikedWords = readUserLikedWordsFromFile();
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

    private ArrayList<String> readUsedArticlesFromFile(){

        ArrayList<String> ret = new ArrayList<String>();

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

    private ArrayList<String> readUsedWordsFromFile(){

        ArrayList<String> ret = new ArrayList<String>();

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

    private ArrayList<String> readUserWordsFromFile(){

        ArrayList<String> ret = new ArrayList<String>();

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


    private ArrayList<ParentWords> readParentWordsFromFile(){
        ArrayList<ParentWords> ret = new ArrayList<>();

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

    private ArrayList<ArticleWords> readArticleWordsFromFile(){
        ArrayList<ArticleWords> ret = new ArrayList<>();

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

    private ArrayList<String> readUserLikedWordsFromFile(){

        ArrayList<String> ret = new ArrayList<String>();

        try {
            InputStream inputStream = context.openFileInput("userLikedWords.txt");

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

    private void writeToUserLikedWords(){

        String str = arrayToString(userLikedWords.toArray(new String[userLikedWords.size()]));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("userLikedWords.txt", context.MODE_PRIVATE));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateUserWords(String[] data){
        for(int i = 0; i < data.length; i++){
            if(!userWords.contains(data[i])) userWords.add(data[i]);
        }

        userWords = userWords.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        writeToUserWords();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateParentWords(ParentWords[] data){
        for(int i = 0; i < data.length; i++){
            if(!parentWords.contains(new ParentWords(data[i].getWord(), data[i].getParent()))) parentWords.add(new ParentWords(data[i].getWord(), data[i].getParent()));
        }
        parentWords = parentWords.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        writeToParentWords();

    }

    public void updateArticleWords(ArticleWords[] data){
        for(int i = 0; i < data.length; i++){
            articleWords.add(new ArticleWords(data[i].getUrl(), data[i].getWord()));
        }
        writeToArticleWords();

    }

    public void updateUserLikedWords(String[] data){
        for(int i = 0; i < data.length; i++){
            userLikedWords.add(data[i]);
        }
        writeToUserLikedWords();

    }

    private void updateConfig(String key, String value) throws JSONException {
        config.put(key, value);
        writeToConfig();
    }


    public ArrayList<String> getUsedWords(){
        return usedWords;
    }

    public ArrayList<String> getUserWords(){
        return userWords;
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


    public synchronized void checkSupplyOfWordsAndArticles(){
        if(userWords.size() < 10){
            findMoreWords();
        }
        else if(articleWords.size() < 7){
            findMoreArticles();
        }
    }

    public synchronized void findMoreWordsUsingTargetWord(String word){
        String chosenWord = word;
        callFirebaseFunctionFindWordsUsingTargetWord(chosenWord);
    }

    public synchronized void findMoreWords(){
        if((userWords.size() < 10 && userWords.size() > 0) || userLikedWords.size() > 0) {
            String chosenWord = chooseRandomUserWordForFindMoreWords();
            callFirebaseFunctionFindWordsUsingTargetWord(chosenWord);
        }
        else if(userWords.size() <= 0){
            replenishWordsRandomly();
        }
    }

    private synchronized String chooseRandomUserWordForFindMoreWords(){
        String[] array;
        if(userLikedWords.size() > 0) array = userLikedWords.toArray(new String[userLikedWords.size()]);
        else array = userWords.toArray(new String[userWords.size()]);

        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        return ret;
    }

    private synchronized void callFirebaseFunctionFindWordsUsingTargetWord(String word){
        if(semaphore < semaphoreLockout) {
            semaphore ++;
            Map<String, Object> data = new HashMap<>();
            String targetWord = word;
            String[] bannedWords = concatArray(this.getUsedWords().toArray(new String[this.getUsedWords().size()]), userWords.toArray(new String[userWords.size()]));
            data.put("targetWord", targetWord);
            data.put("bannedWords", new JSONArray(Arrays.asList(bannedWords)));

            mFunctions.getHttpsCallable("findTopic")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, String[]>() {
                        @Override
                        public String[] then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            String[] result = task.getResult().getData().toString().substring(1, task.getResult().getData().toString().length() -1).split("\\s*,\\s*");
                            return result;
                        }
                    }).addOnCompleteListener(new OnCompleteListener<String[]>() {
                @Override
                public void onComplete(@NonNull Task<String[]> task) {
                    if(task.isSuccessful()){
                        String[] wordsFound = task.getResult();
                        if(wordsFound.length > 0){
                            System.out.println("New added words using " + targetWord + ": "+ Arrays.toString(wordsFound));
                            ParentWords[] parentWordsToAdd = new ParentWords[wordsFound.length];
                            for(int i=0;i<wordsFound.length;i++){
                                parentWordsToAdd[i] = new ParentWords(wordsFound[i], targetWord);
                            }
                            updateParentWords(parentWordsToAdd);
                            updateUserWords(wordsFound);
                            if(!userLikedWords.contains(targetWord)) findMoreArticlesFromTargetWord(targetWord);
                            semaphore --;

                        }

                    }
                    else if(task.isComplete())
                    {
                        System.out.println(task.getException());
                    }
                }
            });
        }
    }

    private void replenishWordsRandomly(){
        Map<String, Object> data = new HashMap<>();
        String[] bannedWords = this.getUsedWords().toArray(new String[this.getUsedWords().size()]);
        data.put("bannedWords", new JSONArray(Arrays.asList(bannedWords)));

        mFunctions.getHttpsCallable("getRandomWords")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String[]>() {
                    @Override
                    public String[] then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                        String[] result = task.getResult().getData().toString().substring(1, task.getResult().getData().toString().length() -1).split("\\s*,\\s*");
                        return result;

                    }
                }).addOnCompleteListener(new OnCompleteListener<String[]>() {
            @Override
            public void onComplete(@NonNull Task<String[]> task) {
                if(task.isSuccessful()){
                    String[] wordsFound = task.getResult();
                    if(wordsFound.length > 0) {
                        System.out.println("Randomly added new words: " + Arrays.toString(wordsFound));
                        ParentWords[] parentWordsToAdd = new ParentWords[wordsFound.length];
                        for (int i = 0; i < wordsFound.length; i++) {
                            parentWordsToAdd[i] = new ParentWords(wordsFound[i], "");
                        }
                        updateParentWords(parentWordsToAdd);
                        updateUserWords(wordsFound);
                        findMoreArticles();
                    }

                }
                else if(task.isComplete())
                {
                    System.out.println(task.getException());
                }
            }
        });
    }

    private synchronized void findMoreArticlesFromTargetWord(String chosenWord){
        removeWordFromUserWords(chosenWord);
        moveUserWordToUsedWords(chosenWord);
        removeUserWordFromParentWords(chosenWord);
        callFindMoreArticles(chosenWord, true);
    }

    public synchronized void findMoreArticles(){
        if(articleWords.size() < 7 && userWords.size() > 0) {
            String chosenWord = this.chooseRandomUserWord();
            removeWordFromUserWords(chosenWord);
            moveUserWordToUsedWords(chosenWord);
            removeUserWordFromParentWords(chosenWord);
            callFindMoreArticles(chosenWord, true);

            writeToUsedArticles();
            writeToUserWords();
            writeToUsedWords();
            writeToParentWords();
            writeToArticleWords();
        }
        else if (userWords.size() > 0){
            findMoreWords();
        }
    }

    public synchronized String chooseRandomUserWord(){

        String[] array = userWords.toArray(new String[userWords.size()]);
        int rnd = new Random().nextInt(array.length);
        String ret = array[rnd];
        return ret;
    }

    public synchronized void removeWordFromUserWords(String word){
        userWords.remove(word);
    }

    public synchronized void moveUserWordToUsedWords(String word){
        if(!usedWords.contains(word)) updateUsedWords(new String[]{word});
    }

    public synchronized void removeUserWordFromParentWords(String word){
        for(int i=0; i<parentWords.size() ;i++){
            if(parentWords.get(i).getWord().equals(word)) {
                parentWords.remove(i);
                i++;
            }
        }
    }

    public synchronized void callFindMoreArticles(String word, boolean loopFlag){
        String url = "https://en.wikipedia.org/w/index.php?search="+word+"&title=Special%3ASearch&go=Go&ns0=1";

        (new FindMoreArticles(this, url, word, loopFlag)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized ArticleWords getArticleAndWord(){
        // add chosen word to usedWords and remove from userWords
        ArticleWords[] array = articleWords.toArray(new ArticleWords[articleWords.size()]);
        int rnd = new Random().nextInt(array.length);
        ArticleWords ret = array[rnd];
        updateUsedArticles(new String[]{array[rnd].getUrl()});
//        articleWords.remove(array[rnd]);
//        writeToArticleWords();
        findMoreArticles();
        return ret;
    }

    public synchronized ArticleWords getArticleAndWordMinusDeckArticleWords(ArticleWords[] deckArticleWords){
        // add chosen word to usedWords and remove from userWords
        ArticleWords[] allArticleWordsArray = articleWords.toArray(new ArticleWords[articleWords.size()]);
        ArticleWords[] array = subtractArray(allArticleWordsArray, deckArticleWords);
        int rnd = new Random().nextInt(array.length);
        ArticleWords ret = array[rnd];
        updateUsedArticles(new String[]{ret.getUrl()});
//        articleWords.remove(array[rnd]);
//        writeToArticleWords();
        findMoreArticles();
        return ret;
    }

    public synchronized int getNumberOfUnusedArticles(ArticleWords[] deckArticleWords){

        return articleWords.size() - deckArticleWords.length;
    }

    public synchronized ArticleWords[] subtractArray(ArticleWords[] a, ArticleWords[] b){

        ArrayList<ArticleWords> aArrayList = new ArrayList<ArticleWords>(Arrays.asList(a));
        ArrayList<ArticleWords> bArrayList = new ArrayList<ArticleWords>(Arrays.asList(b));

        ArticleWords[] ret = new ArticleWords[a.length - b.length];
        int j = 0;
        for(int i=0;i<aArrayList.size();i++){
            if(!containsArticleWord(b, a[i])){

                ret[j] = a[i];
                j++;
            }
        }
//        printWordOfArticleWords(a, "a");
//        printWordOfArticleWords(b, "b");
//        printWordOfArticleWords(ret, "ret");

        return ret;
    }

    // TODO COMMENT THIS OUT
//    private void printWordOfArticleWords(ArticleWords[] a, String name){
//        System.out.println("printing: " + name);
//        for(int i=0;i<a.length;i++){
//            System.out.println(a[i].getWord());
//        }
//    }

    private synchronized boolean containsArticleWord(ArticleWords[] a, ArticleWords b){
        for(int i=0;i<a.length;i++){
            if(a[i].getWord().equals(b.getWord())) return true;
        }

        return false;
    }

    public synchronized void setArticleWordUrlFromWord(String url, String word) {
        for(int i=0;i<articleWords.size();i++){
            if(articleWords.get(i).getWord() == word) articleWords.set(i, new ArticleWords(url, word));

        }
    }


    public synchronized void removeArticleAndWord(ArticleWords articleWord){
        removeArticleWordElement(articleWord);
    }

    public synchronized void removeUserWordOnDislike(String word){
        removeAllChildrenWordsFromParentWordAndUserWords(word);
    }

    private synchronized void removeAllChildrenWordsFromParentWordAndUserWords(String word){
        ArrayList<String> wordsToRemove = new ArrayList<>();
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

    }

    public void getArticleCardAsQuickAsPossible(boolean rerun){

        // choose article if there are user words
        if(userWords.size() > 0) {
            String chosenWord = this.chooseRandomUserWord();
            removeWordFromUserWords(chosenWord);
            moveUserWordToUsedWords(chosenWord);
            removeUserWordFromParentWords(chosenWord);
            callFindMoreArticles(chosenWord, false);

            writeToUsedArticles();
            writeToUserWords();
            writeToUsedWords();
            writeToParentWords();
            writeToArticleWords();

            if(rerun) getArticleCardAsQuickAsPossible(false);
        }

        // get one more word then get user article
        else {

            String chosenWord = chooseRandomUserWordForFindMoreWords();

            Map<String, Object> data = new HashMap<>();
            String targetWord = chosenWord;
            String[] bannedWords = concatArray(this.getUsedWords().toArray(new String[this.getUsedWords().size()]), userWords.toArray(new String[userWords.size()]));
            data.put("targetWord", targetWord);
            data.put("bannedWords", new JSONArray(Arrays.asList(bannedWords)));

            mFunctions.getHttpsCallable("findTopic")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, String[]>() {
                        @Override
                        public String[] then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            String[] result = task.getResult().getData().toString().substring(1, task.getResult().getData().toString().length() -1).split("\\s*,\\s*");
                            return result;
                        }
                    }).addOnCompleteListener(new OnCompleteListener<String[]>() {
                @Override
                public void onComplete(@NonNull Task<String[]> task) {
                    if(task.isSuccessful()){
                        String[] wordsFound = task.getResult();
                        if(wordsFound.length > 0){
                            System.out.println("New (quick as possible) added words using  " + targetWord + ": "+ Arrays.toString(wordsFound));
                            ParentWords[] parentWordsToAdd = new ParentWords[wordsFound.length];
                            for(int i=0;i<wordsFound.length;i++){
                                parentWordsToAdd[i] = new ParentWords(wordsFound[i], targetWord);
                            }
                            updateParentWords(parentWordsToAdd);
                            updateUserWords(wordsFound);

                            getArticleCardAsQuickAsPossible(true);

                        }

                    }
                    else if(task.isComplete())
                    {
                        System.out.println(task.getException());
                    }
                }
            });
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

    public synchronized void removeArticleWordElement(ArticleWords e){
        for(int i=0;i<articleWords.size();i++){
            if(articleWords.get(i).getUrl() == e.getUrl()) articleWords.remove(i--);

        }
    }
}
