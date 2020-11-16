package com.example.tfk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tfk.webscraping.Article;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Article article;
    private FirebaseFunctions mFunctions;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFunctions = FirebaseFunctions.getInstance();

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        TextView titleTV = findViewById(R.id.title);

        // my functions

        this.renderCard(tv, titleTV);
    }

    private void renderCard(TextView tv, TextView titleTV) {

        findTopicThroughHTTP().addOnCompleteListener(new OnCompleteListener<String[]>() {
            @Override
            public void onComplete(@NonNull Task<String[]> task) {
                if(task.isSuccessful()){
                    String[] topic = task.getResult();
                    System.out.println("the array is: " + Arrays.toString(topic));

                    // calls the article class and starts the processing for the articles
                    article = new Article(tv, titleTV, topic[0], mFunctions); // picks the first element for now, will be changed later
                    article.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // this executes the asynctask

                }
                else if(task.isComplete())
                {
                    System.out.println(task.getException());
                }
            }
        });

    }

    // change this function so that it can take in array of strings
    private Task<String[]> findTopicThroughHTTP() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", "text");
        data.put("push", true);

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
    public native String[] returnTopic();
}