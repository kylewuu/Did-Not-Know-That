package com.example.tfk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tfk.UI.CardHandler;
import com.example.tfk.user.UserInformation;
import com.example.tfk.webscraping.Articles;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Articles article;
    private FirebaseFunctions mFunctions;
    private UserInformation userInfo;
    private CardHandler cardHandler;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFunctions = FirebaseFunctions.getInstance();


        // get user information
        try {
            userInfo = new UserInformation(getApplicationContext(), mFunctions);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new CardHandler(MainActivity.this, userInfo); // should pass in the cards themselves instead of just the titles and such

    }

    private void renderCard(TextView tv, TextView titleTV) {

        System.out.println("Calling firebase ... ");
        findTopicThroughHTTP().addOnCompleteListener(new OnCompleteListener<String[]>() {
            @Override
            public void onComplete(@NonNull Task<String[]> task) {
                if(task.isSuccessful()){
                    String[] topics = task.getResult();
                    System.out.println("the array is: " + Arrays.toString(topics));
                    userInfo.updateUserWords(topics);


                    // calls the article class and starts the processing for the article
                    int rnd = new Random().nextInt(topics.length);
                    System.out.println("random number and chosen topic: " + rnd + topics[rnd]);
//                    article = new Articles(tv, titleTV, topics[rnd], mFunctions, userInfo); // picks the first element for now, will be changed later
//                    article.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // this executes the asynctask

                }
                else if(task.isComplete())
                {
                    System.out.println(task.getException());
                }
            }
        });



    }

    private Task<String[]> findTopicThroughHTTP() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        String targetWord = userInfo.getTargetWord();
        String[] bannedWords = userInfo.getUsedWords().toArray(new String[userInfo.getUsedWords().size()]);
        data.put("targetWord", targetWord);
        data.put("bannedWords", new JSONArray(Arrays.asList(bannedWords)));

        return mFunctions
                .getHttpsCallable("findTopic")
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
                });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String DisplayText(String x);
}