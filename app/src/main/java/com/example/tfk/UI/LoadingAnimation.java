package com.example.tfk.UI;

import android.app.Activity;
import android.view.View;

import com.rd.PageIndicatorView;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingAnimation {

    PageIndicatorView loadingView;
    Activity activity;
    Timer timer;
    int position;
    String direction;

    public LoadingAnimation(PageIndicatorView loadingView, Activity activity){
        this.loadingView = loadingView;
        this.activity = activity;
        position = 0;
        direction = "right";
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.VISIBLE);
                loadingView.setSelection(position);


            }
        });

        timer = new Timer( );
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextCircle();
                    }
                });
            }
        }, 750,500);





    }

    private void nextCircle(){
        if(direction.equals("right")){
            if(position < loadingView.getCount() - 1) position ++;
            else{
                direction = "left";
                position --;
            }
        }
        else{
            if(position > 0) position --;
            else{
                direction = "right";
                position ++;
            }
        }

        loadingView.setSelection(position);
    }



    public void stop(){
        timer.cancel();
        loadingView.setVisibility(View.INVISIBLE);
    }



}
