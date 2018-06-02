package com.example.henrique.workout.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.henrique.workout.R;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class PINActivity extends AppCompatActivity {

    TextView instruction;
    Pinview pin;
    Button resendCodeButton;
    TextView pinErrorText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private final String TAG = "phoneAuth";
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        resendCodeButton = findViewById(R.id.resend_sms);
        pinErrorText = findViewById(R.id.pin_error_text);
        instruction = findViewById(R.id.textView3);
        pin = findViewById(R.id.pinview);
        mAuth = FirebaseAuth.getInstance();
        resendCodeButton.setPaintFlags(resendCodeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        pin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                verifyPhoneNumberWithCode(mVerificationId,pinview.getValue());
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("PhoneAuthActivity", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
        resendCodeButton.setOnClickListener( l -> { resendVerificationCode(phoneNumber,mResendToken); });
        PhoneAuthProvider.getInstance().verifyPhoneNumber(getIntent().getStringExtra("PHONE_NUMBER_UNFORMATTTED"),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        instruction.append(" " + phoneNumber);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        pin.setValue(credential.getSmsCode());
                        Intent intent = new Intent(PINActivity.this, HomescreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        Handler handler = new Handler();
                        handler.postDelayed(() -> startActivity(intent), 500);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            pin.setValue("");
                            YoYo.with(Techniques.Shake).playOn(findViewById(R.id.pinview));
                            pinErrorText.setText(getResources().getString(R.string.invalid_pin));
                        }
                    }
                }
            });
    }


    public void goBack(View v){
        finish();
    }
}
