package com.mevikram.fireapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private TextView email,password,goRegPage,forget;
    protected ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    protected FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Connecting...");
        goRegPage=findViewById(R.id.goRegister);
        login=findViewById(R.id.btnLogin);
        forget=findViewById(R.id.tvForgetPassword);
        email=findViewById(R.id.etEmailLogin);
        password=findViewById(R.id.etPasswordLogin);
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user!=null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            finish();
            startActivity(new Intent(LoginActivity.this,WorkActivity.class));
        }
        setupUi();
    }
    private void setupUi(){
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                        Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                        return;
                                    }
                                    finish();
                                    startActivity(new Intent(LoginActivity.this,WorkActivity.class));
                                }
                                else{
                                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        goRegPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }
}