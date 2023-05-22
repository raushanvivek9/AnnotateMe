package com.example.anno_tool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anno_tool.Login.User_Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splah_screen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah_screen);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mAuth=FirebaseAuth.getInstance();
        //check user
        new Handler().postDelayed(new Runnable() {//delay to open next page
            @Override
            public void run() {
                checksetup();
            }
        },1500);
    }
    private void checksetup() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null)
        {
            Intent i = new Intent(Splah_screen.this, User_Login.class);
            startActivity(i);
            finish();
        }else{
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}