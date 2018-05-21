package com.example.henrique.workout;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class LogIn extends AppCompatActivity {

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
            phoneNumber.setError("Insira seu nº de celular");
        } else if(!ccp.isValidFullNumber()){
            phoneNumber.setError("Número inválido");
        } else{
            Intent intent = new Intent(LogIn.this, PINActivity.class);
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            intent.putExtra("PHONE_NUMBER",this.ccp.getFormattedFullNumber());
            startActivity(intent);
        }
    }

}
