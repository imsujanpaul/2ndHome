package com.app.a2ndhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    private String phoneNumber;
    TextView otptext;
    private String codeBySystem;
    private String smsCode;
    PinView pinFromUser;
    private DatabaseReference reference;
    private ProgressBar progressBar;
    private String passvalue;
    private String nameval;
    private String emailval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        progressBar = findViewById(R.id.progressBar2);
        Bundle extra = getIntent().getExtras();
        phoneNumber = extra.getString("phoneNum");
        nameval = extra.getString("userName");
        passvalue = extra.getString("userPass");
        emailval = extra.getString("userEmail");
        otptext = findViewById(R.id.textView2);
        pinFromUser = findViewById(R.id.firstPinView);
        otptext.setText("Enter One Time Password Sent On " + phoneNumber);
        sendVerificationCodeToUser(phoneNumber);
        reference = FirebaseDatabase.getInstance().getReference();


    }

    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                    smsCode = s;

                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinFromUser.setText(code);
                        progressBar.setVisibility(View.VISIBLE);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Verification completed successfully here Either
                            // store the data or do whatever desire
                            reference.child("Users").child(phoneNumber).child("OTP").setValue("Verified");
                            reference.child("Users").child(phoneNumber).child("Name").setValue(nameval);
                            reference.child("Users").child(phoneNumber).child("Number").setValue(phoneNumber);
                            reference.child("Users").child(phoneNumber).child("Email").setValue(emailval);
                            reference.child("Users").child(phoneNumber).child("Password").setValue(passvalue);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(OtpActivity.this, "Verification Completed!", Toast.LENGTH_SHORT).show();
                                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(login);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(OtpActivity.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });

    }

    public void verifyOTP(View view) {
        String code = pinFromUser.getText().toString();
        if (!code.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);
        }
    }
}