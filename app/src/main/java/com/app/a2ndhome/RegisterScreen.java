package com.app.a2ndhome;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;

public class RegisterScreen extends AppCompatActivity {
    TextView registerText;
    MaterialRadioButton radioButton;
    TextInputLayout regName,regPassword,regEmail,regNum;
    MaterialButton registerbtn;
    LinearLayout linearLayout2;
    ProgressBar progressBar;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private String passvalue;
    private String numvalue;
    private String nameval;
    private String emailval;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        registerText=findViewById(R.id.registerText);
        radioButton=findViewById(R.id.agreeTerms);
        regEmail=findViewById(R.id.editTextTextEmail);
        regNum=findViewById(R.id.editTextTextPhoneNum);
        registerbtn=findViewById(R.id.button_Register);
        regName=findViewById(R.id.editTextTextFullname);
        regPassword=findViewById(R.id.editTextTextPassword);
        linearLayout2=findViewById(R.id.linearLayout2);
        reference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();
    }


    //this method opens the login activity with transition animations
    public void openLogin(View view) {
        Intent i=new Intent(RegisterScreen.this,LoginActivity.class);
        Pair[] pairs = new Pair[6];
        pairs[0] = new Pair<View, String>(registerText, "register_txt_trans");
        pairs[1] = new Pair<View, String>(radioButton, "radio_trans");
        pairs[2] = new Pair<View, String>(registerbtn, "login_trans");
        pairs[3] = new Pair<View, String>(regName, "name_trans");
        pairs[4] = new Pair<View, String>(regPassword, "password_trans");
        pairs[5] = new Pair<View, String>(linearLayout2, "openlogin_trans");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterScreen.this, pairs);
        startActivity(i, options.toBundle());
    }

    //method used in the register button. It registers a new user
    public void registerUser(View view) {
        validateName();
        validateEmail();
        validatePhoneNo();
        validatePassword();
        checkTerms();
        if(!regName.isErrorEnabled() && !regNum.isErrorEnabled() && !regEmail.isErrorEnabled() && !regPassword.isErrorEnabled()&&radioButton.isChecked()) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
            intent.putExtra("userName", nameval);
            intent.putExtra("phoneNum", numvalue);
            intent.putExtra("userEmail", emailval);
            intent.putExtra("userPass", passvalue);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    //method that validates name
    public boolean validateName(){
        nameval= Objects.requireNonNull(regName.getEditText()).getText().toString();
        if (nameval.isEmpty()) {
            regName.setError("Field cannot be empty");
            return false;
        }
        else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    //method that validates email
    private Boolean validateEmail() {
        emailval = Objects.requireNonNull(regEmail.getEditText()).getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (emailval.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            return false;
        } else if (!emailval.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    //method that validates number
    private Boolean validatePhoneNo() {
        numvalue = Objects.requireNonNull(regNum.getEditText()).getText().toString();

        if (numvalue.isEmpty()) {
            regNum.setError("Field cannot be empty");
            return false;
        }
        else if(numvalue.length() != 10){
            regNum.setError("Number must be 10 digits");
            return false;
        }

            else {
            regNum.setError(null);
            regNum.setErrorEnabled(false);
            return true;
        }
    }

    //method that validates number
    private void checkTerms() {
        if(!radioButton.isChecked()){
            Toast.makeText(getApplicationContext(),"Please agree to the Terms and Conditions",Toast.LENGTH_SHORT).show();
        }
    }

    //method that validates password
    private Boolean validatePassword() {
        passvalue = Objects.requireNonNull(regPassword.getEditText()).getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        if (passvalue.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        } else if (!passvalue.matches(passwordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }


}