package com.example.henrique.workout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.goodiebag.pinview.Pinview;

public class PINActivity extends AppCompatActivity {

    TextView instruction;
    Pinview pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        instruction = (TextView) findViewById(R.id.textView3);
        pin = (Pinview) findViewById(R.id.pinview);
        instruction.setText("Please type the code sent to " + getIntent().getStringExtra("PHONE_NUMBER") + ".");
    }

    public void confirmLogin(View view){
        if(this.pin.getValue().length() == 4) {
            Intent intent = new Intent(PINActivity.this, Homescreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            startActivity(intent);
        }
    }

    public void goBack(View v){
        finish();
    }
}
