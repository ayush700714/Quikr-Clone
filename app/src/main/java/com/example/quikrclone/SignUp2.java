package com.example.quikrclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUp2 extends AppCompatActivity {


    int flag=0;
    String complete_phone_number,phone_number;
    String name,age;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mCountryCode;
    private EditText mPhoneNumber;

    private Button mGenerateBtn;
    private ProgressBar mLoginProgress;

    private TextView mLoginFeedbackText;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        age = intent.getStringExtra("age");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        mCountryCode = findViewById(R.id.country_code_text);
        mPhoneNumber = findViewById(R.id.phone_number_text);
        mGenerateBtn = findViewById(R.id.generate_btn);
        mLoginProgress = findViewById(R.id.login_progress_bar);
        mLoginFeedbackText = findViewById(R.id.login_form_feedback);

        mGenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country_code = mCountryCode.getText().toString();
                phone_number = mPhoneNumber.getText().toString();

                complete_phone_number = "+" + country_code + phone_number;

                if(country_code.isEmpty() || phone_number.isEmpty()){
                    mLoginFeedbackText.setText("Please fill in the form to continue.");
                    mLoginFeedbackText.setVisibility(View.VISIBLE);
                } else {
                    mLoginProgress.setVisibility(View.VISIBLE);
                    mGenerateBtn.setEnabled(false);
                    check_NotExist();

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }



            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("error message", e.toString());
                mLoginFeedbackText.setText("Verification Failed, please try again.");
                mLoginFeedbackText.setVisibility(View.VISIBLE);
                mLoginProgress.setVisibility(View.INVISIBLE);
                mGenerateBtn.setEnabled(true);
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent otpIntent = new Intent(SignUp2.this, Signup3.class);
                                otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                otpIntent.putExtra("name",name);
                                otpIntent.putExtra("age",age);
                                otpIntent.putExtra("phone",mPhoneNumber.getText().toString().trim());
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                            }
                        },
                        10000);
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignUp2.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, String> mp = new HashMap<>();
                            mp.put("name", name);
                            mp.put("age", age);
                            db.collection("Users").document(mPhoneNumber.getText().toString().trim()).set(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull  Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        sendUserToHome();
                                    }
                                }
                            });

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                mLoginFeedbackText.setVisibility(View.VISIBLE);
                                mLoginFeedbackText.setText("There was an error verifying OTP");
                            }
                        }
                        mLoginProgress.setVisibility(View.INVISIBLE);
                        mGenerateBtn.setEnabled(true);
                    }
                });
    }

    private void sendUserToHome() {
        Intent homeIntent = new Intent(SignUp2.this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        homeIntent.putExtra("phone",mPhoneNumber.getText().toString().trim());
        startActivity(homeIntent);
        finish();
    }

    public void check_NotExist()
    {
        Log.d("checkNot","CheckNot");
        db.collection("Users").document(phone_number).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        flag=1;
                    }
                }
                send_msg();
            }
        });

    }

    public void send_msg()
    {
        Log.d("send_msg", "SDF  " + flag+"  send_msg");
        if(flag==0)
        {



            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    complete_phone_number,
                    60,
                    TimeUnit.SECONDS,
                    SignUp2.this,
                    mCallbacks
            );
        }
        else
        {
            mLoginProgress.setVisibility(View.INVISIBLE);
            mGenerateBtn.setEnabled(true);
            mLoginFeedbackText.setVisibility(View.VISIBLE);
            mLoginFeedbackText.setText("User Already Exist");
        }
    }
}