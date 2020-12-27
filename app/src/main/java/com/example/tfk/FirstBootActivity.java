package com.example.tfk;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tfk.user.UserInformation;

import org.json.JSONException;

public class FirstBootActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_boot);

        try {
            UserInformation userInfo = new UserInformation(getApplicationContext(), null);
            userInfo.firstTimeInitTextFiles();
            userInfo.createConfig();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        configureAdvanceButton();
    }

    private void configureAdvanceButton(){
        Button advanceButton = (Button) findViewById(R.id.advanceButton);
        advanceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent );
                finish();
            }
        });
    }
}