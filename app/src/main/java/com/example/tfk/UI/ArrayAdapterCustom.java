package com.example.tfk.UI;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.tfk.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArrayAdapterCustom extends ArrayAdapter<Cards> {
    Context mContext;

    public TextView[] bodyArray;
    public TextView[] pageIndicatorArray;
    public CardView[] cardViewArray;


    public ArrayAdapterCustom(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
        bodyArray = new TextView[100];
        pageIndicatorArray = new TextView[100];
        cardViewArray = new CardView[100];
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        Cards card_item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.titleText);
        TextView body = (TextView) convertView.findViewById(R.id.bodyText);
        TextView link = (TextView) convertView.findViewById(R.id.linkText);
        TextView pageIndicator = (TextView) convertView.findViewById(R.id.pageIndicator);
        CardView cardView = (CardView) convertView.findViewById(R.id.cardView);

        bodyArray[position] = body;
        pageIndicatorArray[position] = pageIndicator;
        cardViewArray[position] = cardView;

        title.setText(card_item.getTitle() + " : " + card_item.getWord()); // to be removed, this is just to visualize the words
        title.setText(card_item.getTitle()); // to be uncommented
        if(card_item.getBody()[0].indexOf(" . . .") == -1) splitUpBodyTextFunction(body, card_item, card_item.getBody()[0], pageIndicatorArray[position]);
        else {
            body.setText(card_item.getBody()[0]);
            if(card_item.getBody().length > 1){
                pageIndicator.setText("1");
            }
        }
        link.setText(card_item.getLink());

        System.out.println("Running on: " + card_item.getBody()[0]);


        return convertView;
    }


    private void splitUpBodyTextFunction(TextView body, Cards card_item, String bodyText, TextView pageIndicator) {

        body.setText(bodyText);
        final String[] bodyTextFinal = {bodyText};
        final int[] i = {0};

        ViewTreeObserver vto;
        vto = body.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                body.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if(i[0] <= 0){
                    SplitUpBodyText split = new SplitUpBodyText(body, card_item, bodyText, pageIndicator, this);
                    split.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                i[0]++;

            }

        });


    }


}