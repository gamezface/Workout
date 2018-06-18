package com.alberoneramos.workout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alberoneramos.workout.R;
import com.hbb20.CountryCodePicker;

public class LogInActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    TextInputLayout phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        phoneNumber = (TextInputLayout) findViewById(R.id.phoneInput);
    }

    public void sendMessage(View view){
        if(TextUtils.isEmpty(this.editTextCarrierNumber.getText().toString())){
            phoneNumber.setError(getResources().getString(R.string.empty_phone_input));
        } else if(!ccp.isValidFullNumber()){
            phoneNumber.setError(getResources().getString(R.string.invalid_phone_number));
        } else{
            Intent intent = new Intent(LogInActivity.this, PINActivity.class);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            intent.putExtra("PHONE_NUMBER",this.ccp.getFormattedFullNumber());
            intent.putExtra("PHONE_NUMBER_UNFORMATTTED",this.ccp.getFullNumberWithPlus());
            startActivity(intent);
        }
    }

}
