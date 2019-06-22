package com.mevikram.fireapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {

    private Button forgetPassword;
    private EditText email;
    FirebaseAuth user=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        forgetPassword=findViewById(R.id.btnForgetPassword);
        email=findViewById(R.id.etPasswordEmail);
        forget();
    }

    private void forget() {
            forgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(email.getText().toString().isEmpty()){
                        Toast.makeText(ForgetActivity.this,"Email field can't be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!email.getText().toString().contains("gmail.com")){
                        Toast.makeText(ForgetActivity.this,"Enter correct Email address",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    user.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ForgetActivity.this,"Please check your email",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(ForgetActivity.this,"Please try again!!!",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
    }
}
