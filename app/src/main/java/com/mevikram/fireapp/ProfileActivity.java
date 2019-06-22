package com.mevikram.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private Button setData;
    private EditText name;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getUid());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Profile");
        setContentView(R.layout.activity_profile);
        setData=findViewById(R.id.btnSetData);
        name=findViewById(R.id.etGetName);
        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("name").setValue(name.getText().toString());
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails value = dataSnapshot.getValue(UserDetails.class);
                try
                {
                    name.setText(value.getName());
                }catch (NullPointerException e){
                    Toast.makeText(ProfileActivity.this,"please update profile",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    private boolean valid(String uid){
        if(uid.length()==28)
            return true;
        else
            return false;
    }
}

class UserDetails
{
    private String name;
    UserDetails(){

    }
    public void setData(String name,String phone){
        this.name=name;
    }
    public String getName() {
        return name;
    }
}
