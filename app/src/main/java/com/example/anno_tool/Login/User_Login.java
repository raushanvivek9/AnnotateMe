package com.example.anno_tool.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anno_tool.MainActivity;
import com.example.anno_tool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.nullness.qual.NonNull;

public class User_Login extends AppCompatActivity {
    TextInputEditText email, password;
    TextView forgot,signup;
    ProgressBar progressBar;
    private ProgressDialog loadingbar;
    Button login;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usercol,colref;
    private String pswd;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
//        getSupportActionBar().hide(); // hide the title bar
        email = (TextInputEditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        forgot = findViewById(R.id.forgot);
        signup =  findViewById(R.id.signup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingbar=new ProgressDialog(User_Login.this);
        loadingbar.setTitle("Loading");
        loadingbar.setMessage("Checking your details, please wait....");
        loadingbar.setCanceledOnTouchOutside(false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check Internet Connection

                ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                if(null!=infos){
                    userlogin();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    loadingbar.dismiss();
                    Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                }

            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check Internet Connection
                ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                if(null!=infos){
                    View v= LayoutInflater.from(User_Login.this).inflate(R.layout.activity_forgot,null);
                    final EditText user=(EditText)v.findViewById(R.id.f_email);
                    AlertDialog.Builder builder=new AlertDialog.Builder(User_Login.this);
                    builder.setTitle("Forgot Password.")
                            .setCancelable(false)
                            .setView(v)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String u=user.getText().toString();
                                    if (u.equals("")) {
                                        Toast.makeText(User_Login.this, "Please enter your registered EmailID", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        mAuth.sendPasswordResetEmail(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(User_Login.this, "Reset Link send to your Email ID", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(User_Login.this, "Error to sending reset link", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }

                                }
                            })
                            .setNegativeButton("Cancel",null);
                    AlertDialog alert= builder.create();
                    alert.show();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(User_Login.this, Create_Account.class);
                startActivity(i);
            }
        });

    }

    private void userlogin() {
        username = email.getText().toString();
        pswd = password.getText().toString();
        if (username.isEmpty()) {
            email.setError("email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            email.setError("please enter the valid email");
            email.requestFocus();
            return;
        }
        if (pswd.isEmpty()) {
            password.setError("password is required");
            password.requestFocus();
            return;
        }
        if (pswd.length() < 6) {
            password.setError("Minimum length of password should be 6");
            password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        loadingbar.setCancelable(true);
        loadingbar.show();
        usercol=db.collection("Users");
        usercol.whereEqualTo("username",username)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.getResult().size()>0){
                    mAuth.signInWithEmailAndPassword(username,pswd)
                            .addOnCompleteListener(User_Login.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentuser=mAuth.getCurrentUser();
                                        String userid=currentuser.getUid();

                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<String> task) {
                                                //generate device token
                                                String deviceToken=task.getResult();
                                                db.collection("Users").document(userid).update("deviceToken",deviceToken);

                                                Intent i = new Intent(User_Login.this, MainActivity.class);
                                                //clear all open activity
                                                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);

                                                startActivity(i);
                                                progressBar.setVisibility(View.GONE);
                                                loadingbar.dismiss();
                                                finish();
                                            }
                                        });

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        loadingbar.dismiss();

                                    }
                                }
                            });
                    progressBar.setVisibility(View.GONE);

                }else {
                    Toast.makeText(getApplicationContext(),"Invalid UserID",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    loadingbar.dismiss();
                    return;

                }
            }

        });

    }
}