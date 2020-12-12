package com.example.tfk.UI;

import android.os.AsyncTask;
import android.text.Layout;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import org.jsoup.nodes.Document;

public class SplitUpBodyText extends AsyncTask<String, Void, Document> {

    private Cards card_item;
    private String bodyText;
    private TextView body;

    public SplitUpBodyText(TextView body, Cards card_item, String bodyText) {
        this.body = body;
        this.card_item = card_item;
        this.bodyText = bodyText;
    }

    @Override
    protected Document doInBackground(String... strings) {

        body.setText(bodyText);
        final String[] bodyTextFinal = {bodyText};


        Layout layout;
        String bodyText = bodyTextFinal[0];

        while (true) {
            body.setText(bodyText);
            layout = body.getLayout();
            if (layout != null) {
                int lines = layout.getLineCount();
                if (lines > 0) {
                    int ellipsisCount = layout.getEllipsisCount(lines - 1);
                    if (ellipsisCount > 0) {

                        bodyText = bodyText.substring(0, bodyText.length() - 1);


                    } else{

                        break;
                    }

                }

            }

        }

//            }
//        });
//
        return null;
    }
}
