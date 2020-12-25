package com.example.tfk.UI;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.ArrayUtils;

import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.util.Arrays;

import static java.lang.Character.isSpace;

public class SplitUpBodyText extends AsyncTask<String, Void, Document> {

    private Cards card_item;
    private String bodyTextBeforeParse;
    private TextView body;
    private TextView pageIndicator;
    private ViewTreeObserver.OnGlobalLayoutListener layoutListenerClass;

    public SplitUpBodyText(TextView body, Cards card_item, String bodyText, TextView pageIndicator, ViewTreeObserver.OnGlobalLayoutListener layoutListenerClass) {
        this.body = body;
        this.card_item = card_item;
        this.bodyTextBeforeParse = bodyText;
        this.pageIndicator = pageIndicator;
        this.layoutListenerClass = layoutListenerClass;
    }

    @Override
    protected Document doInBackground(String... strings) {

        body.setText(bodyTextBeforeParse);

        String[] finalPages = new String[]{bodyTextBeforeParse};

//        System.out.println("Text before parse: " + bodyTextBeforeParse);


        while (finalPages[finalPages.length - 1] != "") {
            finalPages = expandArray(removeLastElementFromArray(finalPages), splitAndCheckTextIntoTwo(finalPages[finalPages.length - 1]));
        }

        finalPages = removeLastElementFromArray(finalPages);

        body.setText(finalPages[0]);
        card_item.body = new String[finalPages.length];
        card_item.setBody(finalPages);


//        System.out.println("Text: " + finalPages[0]);
//        System.out.println("Final pages size: " + finalPages.length);
//        System.out.println("Body pages size: " + card_item.body.length);





        return null;
    }

    private String[] removeLastElementFromArray(String[] a) {

        int index = a.length - 1; // last element
        String[] result = new String[a.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < a.length; i++) {

            // if the index is
            // the removal element index
            if (i == index) {
                continue;
            }

            // if the index is not
            // the removal element index
            result[k++] = a[i];
        }

        // return the resultant array
        return result;
    }


    private String[] expandArray(String[] a1, String[] a2) {

        int aLen = a1.length;
        int bLen = a2.length;
        String[] result = new String[aLen + bLen];

        System.arraycopy(a1, 0, result, 0, aLen);
        System.arraycopy(a2, 0, result, aLen, bLen);

        return result;
    }

    private String[] splitAndCheckTextIntoTwo(String bodyTextOnePage) {

        body.setText(bodyTextOnePage);


        Layout layout;
        String bodyText = bodyTextOnePage;
        String bodyTextSecondPage = "";
        int countFromBack = 0;


        while (true) {
            body.setText(bodyText);
            layout = body.getLayout();
            if (layout != null) {
                int lines = layout.getLineCount();
                if (lines > 0) {
                    int ellipsisCount = layout.getEllipsisCount(lines - 1);
                    if (ellipsisCount > 0) {

                        countFromBack = 1;

                        while((countFromBack < (bodyText.length() - 1)) && (bodyText.charAt(bodyText.length() - 1 - countFromBack) != ' ' ) ) countFromBack ++;

                        bodyTextSecondPage = bodyText.substring(bodyText.length() - countFromBack) + bodyTextSecondPage;
                        bodyText = bodyText.substring(0, bodyText.length() - countFromBack);



                    } else{

                        if(countFromBack !=0 ) {
                            while((countFromBack < (bodyText.length() - 1)) && (bodyText.charAt(bodyText.length() - 1 - countFromBack) != ' ' ) ) countFromBack ++;

                            bodyTextSecondPage = bodyText.substring(bodyText.length() - countFromBack) + bodyTextSecondPage;
                            bodyText = bodyText.substring(0, bodyText.length() - countFromBack);
                            bodyText = bodyText.concat(" . . .");
                            body.setText(bodyText);
                        }

                        return new String[]{bodyText, bodyTextSecondPage};

                    }
                }
            }
        }

    }

}
