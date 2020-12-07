package com.example.tfk.UI;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tfk.R;

import java.util.List;

public class ArrayAdapterCustom extends ArrayAdapter<Cards> {
    Context mContext;

    public TextView[] bodyArray;


    public ArrayAdapterCustom(Context context, int resourceId, List<Cards> items){
        super(context, resourceId, items);
        bodyArray = new TextView[100];
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.titleText);
        TextView body = (TextView) convertView.findViewById(R.id.bodyText);
        TextView link = (TextView) convertView.findViewById(R.id.linkText);

        bodyArray[position] = body;

        title.setText(card_item.getTitle());
//        body.setText(card_item.getBody()[0]);
        splitUpBodyText(body, card_item);
        link.setText(card_item.getLink());


        return convertView;
    }

    public void splitUpBodyText(TextView body, Cards card_item) {

        boolean[] bodyTextWillFitFlag = {false};
        boolean[] loopFinishedFlag = {true};
        String[] bodyText = {card_item.getBody()[0]};

//        while(!bodyTextWillFitFlag[0]){


//            if(loopFinishedFlag[0]){
                loopFinishedFlag[0] = false;
                System.out.println("Subtracting");

                body.setText(bodyText[0]);
                ViewTreeObserver vto = body.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Layout layout = body.getLayout();

                        loopFinishedFlag[0] = true;
                        if(layout != null) {
                            int lines = layout.getLineCount();
                            if(lines > 0) {
                                int ellipsisCount = layout.getEllipsisCount(lines - 1);
                                if ( ellipsisCount > 0) {
                                    bodyText[0] = bodyText[0].substring(0, bodyText[0].length() -1 );
                                    System.out.println("Subtracted");

                                }
                                else bodyTextWillFitFlag[0] = true;
                            }
                            else bodyTextWillFitFlag[0] = true;
                        }
                        else bodyTextWillFitFlag[0] = true;
                    }
                });

//            }

//        }

        System.out.println("BodyText trimmed is: " + bodyText[0]);



    }


}
