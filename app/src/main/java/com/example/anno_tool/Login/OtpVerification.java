package com.example.anno_tool.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anno_tool.Email.JavaMailAPI;
import com.example.anno_tool.MainActivity;
import com.example.anno_tool.Model.UserDetailNote;
import com.example.anno_tool.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Random;

public class OtpVerification extends AppCompatActivity {
    private int otp_no;
    private int otp;
    Button otpsubmit;
    TextInputEditText  otpnumber;
    TextView resendotp,email_text;
    private String name,email,password,phone;
    private FirebaseAuth mAuth;
   // private DatabaseReference myRef;
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otpnumber = findViewById(R.id.otpNumber);
        otpsubmit = findViewById(R.id.otpsubmit);
        resendotp = findViewById(R.id.resendotp);
        email_text = findViewById(R.id.email_text);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        phone = intent.getStringExtra("phone");
        email_text.setText(email_text.getText()+""+email);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        randomNumber();
        otpsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp=Integer.parseInt(otpnumber.getText().toString());
                final ProgressDialog progressDialog=new ProgressDialog(OtpVerification.this);
                progressDialog.setTitle("Please Wait...");
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                if(otp==otp_no){
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        String currentuser=mAuth.getCurrentUser().getUid();
                                        //generate device token
                                        String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                        //add user details in firestore
                                        UserDetailNote note=new UserDetailNote(email,name,currentuser,phone,deviceToken);
                                        db.collection("Users").document(currentuser).set(note);
                                        progressDialog.dismiss();
                                        Intent i=new Intent(OtpVerification.this, MainActivity.class);
                                        //clear all open activity
                                        i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                        Toast.makeText(getApplicationContext(),"user registered successful"+deviceToken,Toast.LENGTH_LONG).show();

                                    }
                                    else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        progressDialog.dismiss();

                                        finish();
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }else
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Resend OTP if click on resend
        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomNumber();
            }
        });
    }

    private void randomNumber() {
        Random random=new Random();
        otp_no=random.nextInt(999999);
        //send otp on email
        sendMail();
        Toast.makeText(getApplicationContext(),"OTP sent successfully on your Email Id",Toast.LENGTH_LONG).show();
    }
    //send mail
    private void sendMail() {
        String message = "Your OTP(One Time Password) Verification Code is "+otp_no;
        String subject = "Login OTP";

        //Send Mail
        try {
            JavaMailAPI javaMailAPI = new JavaMailAPI(this,email,subject,message);

            javaMailAPI.execute();
        }
        catch (Exception e){
        }

    }
}