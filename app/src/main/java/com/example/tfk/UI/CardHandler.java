package com.example.tfk.UI;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tfk.R;
import com.example.tfk.user.ArticleWords;
import com.example.tfk.user.ParentWords;
import com.example.tfk.user.UserInformation;
import com.example.tfk.webscraping.Articles;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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

        maxSizeOfCardsDeck = 3;

        userInfo = userInformation;
        mContext = context;
        activity = (Activity) mContext;

        activity.setContentView(R.layout.activity_main);


        cards = new ArrayList<Cards>();
        arrayAdapter = new ArrayAdapterCustom(mContext, R.layout.card, cards );
        articles = new Articles(userInfo);


        if(userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) > 0) {
            cards.add(new Cards(articles.getAllArticlesElements(userInfo, getArticlesWordsFromCard(cards))));
            if(userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) > 0) cards.add(new Cards(articles.getAllArticlesElements(userInfo, getArticlesWordsFromCard(cards))));
            userInfo.findMoreWords();
        }
        else noCardsLeft();


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) activity.findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                Log.d("Number of cards left", String.valueOf(cards.size()));
                if(cards.size() <= 1) noCardsLeft();
                userInfo.removeArticleAndWord(new ArticleWords(cards.get(0).getLink(), cards.get(0).getWord()));
                cards.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You cardsso have access to the origincards object.
                //If you want to use it just cast it (String) dataObject

                userInfo.checkSupplyOfWordsAndArticles();
                Cards card = (Cards) dataObject;
                userInfo.removeUserWordOnDislike(card.getWord());
//                Toast.makeText(mContext, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                Cards card = (Cards) dataObject;
                String word = card.getWord();
                if(word != "CardDeckEmpty") userInfo.updateUserLikedWords(new String[]{word});
                userInfo.checkSupplyOfWordsAndArticles();
//                Toast.makeText(mContext, "Right!", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                if(userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >= 1) addNewCard();
                arrayAdapter.notifyDataSetChanged();
                i++;
                if(cards.size() < maxSizeOfCardsDeck && userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >=1 ) addNewCard();
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
        ExecutorService service = Executors.newFixedThreadPool(7);
        service.submit(new Runnable() {
            public void run() {
                Log.d("LIST", "Adding new card");

                if(userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >= 1) cards.add(new Cards(articles.getAllArticlesElements(userInfo, getArticlesWordsFromCard(cards))));
                if(cards.size() < maxSizeOfCardsDeck && userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >=1 ) addNewCard();
            }
        });
    }


    private void noCardsLeft(){
//        cards.add(new Cards(new String[]{"Sorry!", "You are way too quick for me to keep up. Please wait a few seconds and swipe again.", "", "CardDeckEmpty"}));

        PageIndicatorView loadingView = activity.findViewById(R.id.loadingView);
        loadingView.setSelection(2);
        LoadingAnimation loadingAnimation = new LoadingAnimation(loadingView, activity);

        userInfo.getArticleCardAsQuickAsPossible(true);
        Timer t = new Timer( );
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {


                if(userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >= 2) {

//                    cards.add(new Cards(new String[]{"Sorry!", "You are way too quick for me to keep up. Please wait a few seconds and swipe again.", "", "CardDeckEmpty"}));

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter.notifyDataSetChanged();
                            cards.add(new Cards(articles.getAllArticlesElements(userInfo, getArticlesWordsFromCard(cards))));
                            cards.add(new Cards(articles.getAllArticlesElements(userInfo, getArticlesWordsFromCard(cards))));
                            t.cancel();
                            loadingAnimation.stop();
                        }
                    });



                }

            }
        }, 1000,5000);
    }

    private ArticleWords[] getArticlesWordsFromCard(List<Cards> cardDeck){
        ArticleWords[] articleWords = new ArticleWords[cardDeck.size()];

        for(int i=0;i<cardDeck.size();i++){
            articleWords[i] = new ArticleWords(cardDeck.get(i).getLink(), cardDeck.get(i).getWord());
        }

        return articleWords;

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