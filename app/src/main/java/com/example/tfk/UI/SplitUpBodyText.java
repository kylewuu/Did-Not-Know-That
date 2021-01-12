package com.example.tfk.UI;

import android.os.AsyncTask;
import android.text.Layout;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import org.jsoup.nodes.Document;


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
        while (finalPages[finalPages.length - 1] != "") {
            finalPages = expandArray(removeLastElementFromArray(finalPages), splitAndCheckTextIntoTwo(finalPages[finalPages.length - 1]));
        }
        finalPages = removeLastElementFromArray(finalPages);
        body.setText(finalPages[0]);
        card_item.body = new String[finalPages.length];
        card_item.setBody(finalPages);
        if (card_item.getBody().length > 1){
            pageIndicator.setText("1");
        }
        return null;
    }

    private String[] removeLastElementFromArray(String[] a) {
        int index = a.length - 1; // last element
        String[] result = new String[a.length - 1];
        for (int i = 0, k = 0; i < a.length; i++) {
            if (i == index) {
                continue;
            }
            result[k++] = a[i];
        }
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
                    } else {
                        if (countFromBack !=0 ) {
                            while ((countFromBack < (bodyText.length() - 1)) && (bodyText.charAt(bodyText.length() - 1 - countFromBack) != ' ' ) ) countFromBack ++;
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
