package com.alberoneramos.workout.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alberoneramos.workout.R;
import com.hbb20.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.editText_carrierNumber)
    EditText editTextCarrierNumber;
    @BindView(R.id.phoneInput)
    TextInputLayout phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
    }

    public void sendMessage(View view) {
        if (TextUtils.isEmpty(this.editTextCarrierNumber.getText().toString())) {
            phoneNumber.setError(getResources().getString(R.string.empty_phone_input));
        } else if (!ccp.isValidFullNumber()) {
            phoneNumber.setError(getResources().getString(R.string.invalid_phone_number));
        } else {
            Intent intent = new Intent(LogInActivity.this, PINActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            intent.putExtra("PHONE_NUMBER", this.ccp.getFormattedFullNumber());
            intent.putExtra("PHONE_NUMBER_UNFORMATTTED", this.ccp.getFullNumberWithPlus());
            startActivity(intent);
        }
    }

}
