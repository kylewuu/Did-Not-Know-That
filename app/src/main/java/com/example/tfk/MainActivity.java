package com.example.tfk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.example.tfk.UI.CardHandler;
import com.example.tfk.user.UserInformation;
import com.example.tfk.webscraping.Articles;
import com.google.firebase.functions.FirebaseFunctions;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {
    private Articles article;
    private FirebaseFunctions mFunctions;
    private UserInformation userInfo;
    private CardHandler cardHandler;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFunctions = FirebaseFunctions.getInstance();
        try {
            userInfo = new UserInformation(getApplicationContext(), mFunctions);
            if (userInfo.checkForFirstBoot()) {
                Intent intent = new Intent(this, FirstBootActivity.class);
                startActivityForResult(intent,0);
            } else {
                userInfo.updateArrayLists();
                cardHandler = new CardHandler(MainActivity.this, userInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        userInfo.updateArrayLists();
        cardHandler = new CardHandler(MainActivity.this, userInfo);
        userInfo.findMoreWords();
    }

    public void bodyTextOnClickNext(View v) {
        cardHandler.arrayAdapter.bodyArray[0].setText(cardHandler.cards.get(0).getNextCard());
        if (cardHandler.cards.get(0).body.length > 1) cardHandler.arrayAdapter.pageIndicatorArray[0].setText(String.valueOf(cardHandler.cards.get(0).currentCard + 1));
    }

    public void bodyTextOnClickPrevious(View v) {
        cardHandler.arrayAdapter.bodyArray[0].setText(cardHandler.cards.get(0).getPreviousCard());
        if (cardHandler.cards.get(0).body.length > 1) cardHandler.arrayAdapter.pageIndicatorArray[0].setText(String.valueOf(cardHandler.cards.get(0).currentCard + 1));
    }

    public void linkTextOnClick(View v) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        WebView webView = (WebView) inflater.inflate(R.layout.customdialog, null, false);
        webView.loadUrl(cardHandler.cards.get(0).getLink());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        alertBuilder.setView(webView);
        alertBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); //create a new one
        layoutParams.weight = (float) 1.0;
        layoutParams.gravity = Gravity.CENTER;
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String DisplayText(String x);
}