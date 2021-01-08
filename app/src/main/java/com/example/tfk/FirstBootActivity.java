package com.example.tfk;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tfk.user.UserInformation;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Vector;

public class FirstBootActivity extends AppCompatActivity {


    UserInformation userInfo;
    ImageButton advanceButton;
    Vector<String> chosenWords;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_boot);
        chosenWords = new Vector<String>();
        createCards();


        try {
            userInfo = new UserInformation(getApplicationContext(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        configureAdvanceButton();
    }

    private void configureAdvanceButton(){
        advanceButton = (ImageButton) findViewById(R.id.advanceButton);
        advanceButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                System.out.println("Words chosen: ");
                for (Object o : chosenWords) {
                    System.out.print(o + " ");
                }

                try {
                    userInfo.createConfig();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // TODO change it back to the dynamic init text files instead of the fake data
                // userInfo.firstTimeInitTextFilesBasedOnPreference(Arrays.copyOf(chosenWords.toArray(), chosenWords.toArray().length, String[].class));
                userInfo.firstTimeInitTextFiles();

                Intent intent = new Intent();
                setResult(RESULT_OK,intent );
                finish();
            }
        });
    }

    private void createCards(){
        String[] wordsToChooseFrom = new String[]{
                "technology",
                "basketball",
                "nfl",
                "belgium",
                "physics",
                "fishing",
                "pittsburgh",
                "decriminalizing",
                "windpower",
                "linguistique",
                "birdwatcher",
                "octagons",

        };

        int numberOfCards = 12;

        TextView[] cards = new TextView[numberOfCards];
        for(int i=0;i<numberOfCards;i++){
            cards[i] = (TextView) ((LinearLayout) findViewById(getResources().getIdentifier("card" + String.valueOf(i), "id", getPackageName()))).getChildAt(0);
            cards[i].setText(wordsToChooseFrom[i]);
        }




    }

    public void firstBootCardOnClick(View v){
        TextView view = (TextView) v;
        String word = (String) view.getText();
        if(view.getCurrentTextColor() == getResources().getColor(R.color.dark_gray) ){
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.first_boot_card_background_selected) );
            view.setTextColor(getResources().getColor(R.color.white));
            chosenWords.add(word);
        }
        else {
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.first_boot_card_background) );
            view.setTextColor(getResources().getColor(R.color.dark_gray));
            if(chosenWords.contains(word)) chosenWords.remove(word);
        }

        if(chosenWords.size() < 3) advanceButton.setVisibility(View.INVISIBLE);
        else advanceButton.setVisibility(View.VISIBLE);

    }
}