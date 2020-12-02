package com.example.tfk.UI;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.tfk.R;
import com.example.tfk.user.UserInformation;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class CardHandler {

    private ArrayList<String> cards;
    private ArrayAdapter<String> arrayAdapter;
    private int i;

    Context mContext;
    Activity activity;
    UserInformation userInfo;

    public CardHandler(Context context, UserInformation userInformation) {

        userInfo = userInformation;
        mContext = context;
        activity = (Activity) mContext;

        activity.setContentView(R.layout.activity_main);



        cards = new ArrayList<>();
        // two cards need to be in the array to begin with, or else it will not work properly
        cards.add(userInfo.getTargetWord());
        cards.add(userInfo.getTargetWord());
//        cards.add("c");
//        cards.add("python");
//        cards.add("java");
//        cards.add("html");
//        cards.add("c++");
//        cards.add("css");
//        cards.add("javascript");

        arrayAdapter = new ArrayAdapter<>(mContext, R.layout.card, R.id.helloText, cards );


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) activity.findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                Log.d("Number of cards left", String.valueOf(cards.size()));
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
                // Ask for more data here
//                cards.add("XML ".concat(String.vcardsueOf(i)));
                cards.add(userInfo.getTargetWord());
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
                if(cards.size() < 5) onAdapterAboutToEmpty(cards.size());
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
//                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setcardspha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setcardspha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

            }
        });


        // Optioncardsly add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
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