package com.example.tfk.UI;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tfk.R;
import com.example.tfk.user.UserInformation;
import com.example.tfk.webscraping.Articles;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CardHandler {

//    private ArrayList<String> cards;
    private Cards cardsData[];
    public ArrayAdapterCustom arrayAdapter;
    private int i;

    Context mContext;
    Activity activity;
    UserInformation userInfo;
    Articles articles;

    public List<Cards> cards;

    int maxSizeOfCardsDeck;

    public CardHandler(Context context, UserInformation userInformation) {

        maxSizeOfCardsDeck = 8;

        userInfo = userInformation;
        mContext = context;
        activity = (Activity) mContext;

        activity.setContentView(R.layout.activity_main);


        cards = new ArrayList<Cards>();
        arrayAdapter = new ArrayAdapterCustom(mContext, R.layout.card, cards );
        articles = new Articles(userInfo);


        if(userInfo.articleWords.size() > 0) {
            cards.add(new Cards(articles.getAllArticlesElements(userInfo)));
            if(userInfo.articleWords.size() > 0) cards.add(new Cards(articles.getAllArticlesElements(userInfo)));
            userInfo.replenishWordsUsingUserWord();
        }
        else cards.add(new Cards(new String[]{"Sorry!", "You are way too quick for me to keep up. Please wait a few seconds and swipe again.", ""}));


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) activity.findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                Log.d("Number of cards left", String.valueOf(cards.size()));
                if(cards.size() <= 1) cards.add(new Cards(new String[]{"Sorry!", "You are way too quick for me to keep up. Please wait a few seconds and swipe again.", ""}));
                cards.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You cardsso have access to the origincards object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(mContext, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(mContext, "Right!", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {


                addNewCard();
                arrayAdapter.notifyDataSetChanged();
                i++;
                if(cards.size() < maxSizeOfCardsDeck && userInfo.articleWords.size() >=1 ) addNewCard();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
//                Toast.makeText(mContext, "Scrolling!", Toast.LENGTH_SHORT).show();
//                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setcardspha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setcardspha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

            }
        });


        // Optioncardsly add an OnItemClickListener
//        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClicked(int itemPosition, Object dataObject) {
//                Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
//            }
//        });


    }

    private void addNewCard(){
        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(new Runnable() {
            public void run() {
                Log.d("LIST", "Adding new card");
                if(userInfo.articleWords.size() >= 1) cards.add(new Cards(articles.getAllArticlesElements(userInfo)));
                if(cards.size() < maxSizeOfCardsDeck && userInfo.articleWords.size() >=1 ) addNewCard();
            }
        });
    }




//    static void makeToast(Context ctx, String s){
//        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
//    }
//
//
//    @OnClick(R.id.right)
//    public void right() {
//        /**
//         * Trigger the right event manucardsly.
//         */
//        flingContainer.getTopCardListener().selectRight();
//    }
//
//    @OnClick(R.id.left)
//    public void left() {
//        flingContainer.getTopCardListener().selectLeft();
//    }
//



}