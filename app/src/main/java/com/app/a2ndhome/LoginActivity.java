package com.app.a2ndhome;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextView signintext, forgotPass;
    TextInputLayout number, password;
    MaterialButton loginbtn;
    LinearLayout linearLayout1;
    private String phoneNo;
    private String pass;
    ProgressBar progressBar;
    private int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signintext = findViewById(R.id.signinText);
        forgotPass = findViewById(R.id.agreeTerms);
        loginbtn = findViewById(R.id.button_Register);
        number = findViewById(R.id.editTextTextFullname);
        password = findViewById(R.id.editTextTextPassword);
        linearLayout1 = findViewById(R.id.linearLayout1);
        progressBar = findViewById(R.id.progressBar);
    }

    public void openRegister(View view) {
        Intent i = new Intent(this, RegisterScreen.class);
        Pair[] pairs = new Pair[6];
        pairs[0] = new Pair<View, String>(signintext, "register_txt_trans");
        pairs[1] = new Pair<View, String>(forgotPass, "radio_trans");
        pairs[2] = new Pair<View, String>(loginbtn, "login_trans");
        pairs[3] = new Pair<View, String>(number, "name_trans");
        pairs[4] = new Pair<View, String>(password, "password_trans");
        pairs[5] = new Pair<View, String>(linearLayout1, "openlogin_trans");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
        startActivity(i, options.toBundle());
    }

    //login button code
    public void loginUser(View view) {
        isConnected();
        if (flag == 0){
            showCustomDialog();
        }else {
            validatePhoneNo();
            validatePassword();
            if (!number.isErrorEnabled() || !password.isErrorEnabled()) {
                progressBar.setVisibility(View.VISIBLE);
                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").child(phoneNo);
                checkUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            String userPass = (String) snapshot.child("Password").getValue();
                            if (pass.equals(userPass)) {
                                Intent intent = new Intent(getApplicationContext(), DashboardScreen.class);
                                intent.putExtra("number",phoneNo);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Your password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //method that validates number
    private Boolean validatePhoneNo() {
        phoneNo = Objects.requireNonNull(number.getEditText()).getText().toString();

        if (phoneNo.isEmpty()) {
            number.setError("Field cannot be empty");
            return false;
        } else if (phoneNo.length() != 10) {
            number.setError("Number must be 10 digits");
            return false;
        } else {
            number.setError(null);
            number.setErrorEnabled(false);
            return true;
        }
    }



    //create custom dialog
    private void showCustomDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Please turn on the internet to proceed further")
                .setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    //check network connection

    public void isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiConn != null && wifiConn.isConnected()) || mobileConn != null && mobileConn.isConnected()) {
            flag=1;
        } else {
            flag=0;
        }
    }

    private Boolean validatePassword() {
        pass = Objects.requireNonNull(password.getEditText()).getText().toString();
        if (pass.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        }
         else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}