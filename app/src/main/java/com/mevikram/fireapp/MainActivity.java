package com.mevikram.fireapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button buttonRegister;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        editTextEmail = findViewById(R.id.etEmail);
        editTextPassword = findViewById(R.id.etPassword);
        textViewSignIn = findViewById(R.id.tvSignIn);
        buttonRegister = findViewById(R.id.btnRegister);
        if(user!=null) {
            finish();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }
        setupUi();

    }

    private void setupUi(){
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if(editTextEmail.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this,"Please fill all details",Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"Registration successful",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(MainActivity.this,"Please Verify your Email",Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                }
                                else {
                                    Toast.makeText(MainActivity.this,"Registration failed. Please try again",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }

}
