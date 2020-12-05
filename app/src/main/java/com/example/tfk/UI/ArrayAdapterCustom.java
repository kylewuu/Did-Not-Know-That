package com.example.tfk.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tfk.R;

import java.util.List;

public class ArrayAdapterCustom extends ArrayAdapter<Cards> {
    Context mContext;

    public ArrayAdapterCustom(Context context, int resourceId, List<Cards> items){
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.titleText);
        TextView body = (TextView) convertView.findViewById(R.id.bodyText);
        TextView link = (TextView) convertView.findViewById(R.id.linkText);

        title.setText(card_item.getTitle());
        body.setText(card_item.getBody());
        link.setText(card_item.getLink());

        return convertView;
    }
}
