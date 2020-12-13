package com.example.tfk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfk.UI.CardHandler;
import com.example.tfk.UI.Cards;
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

        cardHandler = new CardHandler(MainActivity.this, userInfo); // should pass in the cards themselves instead of just the titles and such

    }


    public void bodyTextOnClickNext(View v){
        // testing stuff, can be removed
//        cardHandler.cards.set(0, new Cards(new String[]{"test", "test", "test"}));
//        cardHandler.cards.remove(0);
//        cardHandler.arrayAdapter.notifyDataSetChanged();


        cardHandler.arrayAdapter.bodyArray[0].setText(cardHandler.cards.get(0).getNextCard());
    }

    public void bodyTextOnClickPrevious(View v){
        // testing stuff, can be removed
//        cardHandler.cards.set(0, new Cards(new String[]{"test", "test", "test"}));
//        cardHandler.cards.remove(0);
//        cardHandler.arrayAdapter.notifyDataSetChanged();


        cardHandler.arrayAdapter.bodyArray[0].setText(cardHandler.cards.get(0).getPreviousCard());
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String DisplayText(String x);
}