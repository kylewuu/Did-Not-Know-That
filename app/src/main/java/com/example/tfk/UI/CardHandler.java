package com.example.tfk.UI;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.example.tfk.R;
import com.example.tfk.user.ArticleWords;
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


public class CardHandler {

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

        if (!isConnection()) showNoConnection();
        else {
            if (userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) > 0) {
                addSynchronized();
                if (userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) > 0) addSynchronized();
                userInfo.findMoreWords();
            } else noCardsLeft();
        }

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) activity.findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                Log.d("Number of cards left", String.valueOf(cards.size()));
                if (isConnection()) if (cards.size() <= 1) noCardsLeft();

                userInfo.removeArticleAndWord(new ArticleWords(cards.get(0).getLink(), cards.get(0).getWord()));
                cards.remove(0);
                if (!isConnection()) showNoConnection();
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                if (isConnection()) userInfo.checkSupplyOfWordsAndArticles();
                Cards card = (Cards) dataObject;
                userInfo.removeUserWordOnDislike(card.getWord());
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards card = (Cards) dataObject;
                String word = card.getWord();
                if (word != "doNotAdd") userInfo.updateUserLikedWords(new String[]{word});
                if (isConnection()) userInfo.checkSupplyOfWordsAndArticles();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >= 1) addNewCard();
                arrayAdapter.notifyDataSetChanged();
                i++;
                if (cards.size() < maxSizeOfCardsDeck && userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >=1 ) addNewCard();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });
    }

    private void addNewCard() {
        ExecutorService service = Executors.newFixedThreadPool(7);
        service.submit(new Runnable() {
            public void run() {
                Log.d("LIST", "Adding new card");
                if (userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >= 1) addSynchronized();
                if (cards.size() < maxSizeOfCardsDeck && userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >=1 && isConnection()) addNewCard();
            }
        });
    }

    private void noCardsLeft() {
        PageIndicatorView loadingView = activity.findViewById(R.id.loadingView);
        loadingView.setSelection(2);
        LoadingAnimation loadingAnimation = new LoadingAnimation(loadingView, activity);
        userInfo.getArticleCardAsQuickAsPossible(true);
        Timer t = new Timer( );
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >= 2) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter.notifyDataSetChanged();
                            addSynchronized();
                            addSynchronized();
                            t.cancel();
                            loadingAnimation.stop();
                        }
                    });
                }

            }
        }, 1000,5000);
    }

    private synchronized ArticleWords[] getArticlesWordsFromCard(List<Cards> cardDeck) {
        ArticleWords[] articleWords = new ArticleWords[cardDeck.size()];
        for (int i=0;i<cardDeck.size();i++){
            articleWords[i] = new ArticleWords(cardDeck.get(i).getLink(), cardDeck.get(i).getWord());
        }
        return articleWords;
    }

    private synchronized void addSynchronized() {
        if (isConnection() && userInfo.getNumberOfUnusedArticles(getArticlesWordsFromCard(cards)) >= 2) cards.add(new Cards(articles.getAllArticlesElements(userInfo, getArticlesWordsFromCard(cards))));
    }

    private synchronized boolean isConnection() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    private synchronized void showNoConnection() {
        cards.clear();
        cards.add(new Cards(new String[]{"No connection", "Please check your internet connection and try again.", "", "doNotAdd"}));
    }









}