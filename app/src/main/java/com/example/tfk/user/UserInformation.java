package com.example.tfk.user;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;


public class UserInformation {

    public Vector<String> usedWords;
    public Vector<String> usedArticles;
    public Vector<String> userWords;
    public JSONObject config;
    public Context context;

    public UserInformation(Context applicationContext) throws JSONException {
        // get username
        // get data
        context = applicationContext;
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

    }

    private void firstTimeInitTextFiles(){
        // temp writing arguments, needs to change later
        usedWords = new Vector<>();
        usedArticles = new Vector<>();
        userWords = new Vector<>();

        // temp values for testing
        updateUsedWords(new String[]{"travelling", "trip", "travels", "trips", "Time-zones"});
        updateUsedArticles(new String[]{"https://en.wikipedia.org/wiki/Norway"});
        updateUserWords(new String[]{"travel", "software"});
    }

    private void updateVectors(){
        usedWords = readUsedWordsFromFile();
        usedArticles = readUsedArticlesFromFile();
        userWords = readUserWordsFromFile();
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

    private void updateConfig(String key, String value) throws JSONException {
        config.put(key, value);
        writeToConfig();
    }


    public Vector<String> getUsedWords(){
        return usedWords;
    }

    public String getTargetWord(){
        // add chosen word to usedWords and remove from userWords
        String[] array = userWords.toArray(new String[userWords.size()]);
        int rnd = new Random().nextInt(array.length);
        updateUsedWords(new String[]{array[rnd]});
        userWords.remove(array[rnd]);
        writeToUserWords();
        return array[rnd];
    }


    private String arrayToString(String[] array){
        StringBuilder ret = new StringBuilder();
        for(int i = 0;i < array.length; i++){
            ret.append(array[i]);
            ret.append("\n");
        }
        return ret.toString();
    }


    public boolean checkIfArticlesIsNotUsed(String url){
        if(usedArticles.contains(url)) return false;
        return true;
    }

    public boolean checkIfKeywordIsInUserWords(String word){
        if(userWords.contains(word)) return true;
        return false;
    }
}
