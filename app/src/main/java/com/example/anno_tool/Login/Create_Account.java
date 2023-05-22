package com.example.anno_tool.Login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anno_tool.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Create_Account extends AppCompatActivity {
    TextInputEditText email,password,name,phoneno;
    TextView already_ac;
    Button signup;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_create_account);
        email=(TextInputEditText) findViewById(R.id.email);
        name=(TextInputEditText) findViewById(R.id.name);
        password=(TextInputEditText) findViewById(R.id.password);
        signup=(Button)findViewById(R.id.signup);
        phoneno=(TextInputEditText) findViewById(R.id.phoneno);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        already_ac=findViewById(R.id.already_acc);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username=email.getText().toString();
                final String pswd=password.getText().toString();
                final String phone=phoneno.getText().toString();
                final String uname=name.getText().toString();

                if(name.getText().toString().isEmpty()){
                    name.setError("email is required");
                    name.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                    email.setError("please enter the valid email");
                    email.requestFocus();
                    return;
                }
                if(pswd.isEmpty()){
                    password.setError("password is required");
                    password.requestFocus();
                    return;
                }
                if(pswd.length()<6){
                    password.setError("Minimum length of password should be 6");
                    password.requestFocus();
                    return;
                }else {
                    if (!isValidPassword(pswd)) {
                        password.setError("should be occur at least one letter,number,symbol and no whitespace is allowed.");
                        password.requestFocus();
                        return;
                    }
                }

                if(phone.length()<10) {
                    phoneno.setError("Invalid Phone no.");
                    phoneno.requestFocus();
                    return;
                }
                if (!Patterns.PHONE.matcher(phone).matches()) {
                    phoneno.setError("Please enter valid phone number");
                    phoneno.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                if(null!=infos){
                    Intent intent=new Intent(getApplicationContext(),OtpVerification.class);
                    intent.putExtra("name",name.getText().toString());
                    intent.putExtra("email",username);
                    intent.putExtra("password",pswd);
                    intent.putExtra("phone",phone);
                    progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                }


            }
        });
        already_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),User_Login.class);
                startActivity(intent);
            }
        });

    }
    public boolean isValidPassword(final String password) {
        /*
         ^                   # start-of-string
         (?=.*[0-9])       # a digit must occur at least once
         (?=.*[a-z])       # a lower case letter must occur at least once
         (?=.*[A-Z])       # an upper case letter must occur at least once
         (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
         (?=\\S+$)          # no whitespace allowed in the entire string
         .{4,}             # anything, at least six places though
         $                 # end-of-string

         */

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}