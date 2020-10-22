package com.example.tinderforknowledge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tinderforknowledge.webscraping.Article;

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

        // my functions

        this.renderCard(tv);
    }

    private void renderCard(TextView tv) {
        article = new Article(tv);
        article.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // this executes the asynctask
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String DisplayText(String x);
}