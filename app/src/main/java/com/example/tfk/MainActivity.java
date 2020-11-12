package com.example.tfk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tfk.webscraping.Article;

public class MainActivity extends AppCompatActivity {

    private Article article;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        TextView titleTV = findViewById(R.id.title);

        // my functions

        this.renderCard(tv, titleTV);
    }

    private void renderCard(TextView tv, TextView titleTV) {

        String topic = returnTopic();
        article = new Article(tv, titleTV, topic);
        article.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // this executes the asynctask
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String DisplayText(String x);
    public native String returnTopic();
}