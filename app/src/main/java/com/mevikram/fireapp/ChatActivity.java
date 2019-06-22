package com.mevikram.fireapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private Button send;
    private EditText message;
    private ListView messageContent;
    private String notificationKey;

    FirebaseDatabase databaseForMessage = FirebaseDatabase.getInstance();
    DatabaseReference myRef = databaseForMessage.getReference(FirebaseAuth.getInstance().getUid());
    DatabaseReference getRef;
    DatabaseReference notifyRef,refFriStatus;
    private boolean status=false;
    private String name;

    String friDetails;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friDetails=getIntent().getStringExtra("friUid");
        setTitle(getIntent().getStringExtra("friname"));
        name=getTitle().toString();
        refFriStatus=databaseForMessage.getReference(friDetails).child("online");
        setContentView(R.layout.activity_chat);
        send =findViewById(R.id.btnSend);
        message=findViewById(R.id.etMessage);
        messageContent=findViewById(R.id.lvMessageContent);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        sendMessage();
    }

    private void sendMessage() {
        refFriStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString()=="true") {
                    status = true;
                    setTitle(name+"(online)");
                }
                else {
                    status = false;
                    setTitle(name+"(offline)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        notifyRef=databaseForMessage.getReference(friDetails).child("NotificationKey");
        notifyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationKey=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString().isEmpty())
                    return;
                //sent message to database
                String key = myRef.getParent().child("chat").push().getKey();
                myRef.getParent().child("user").child(FirebaseAuth.getInstance().getUid()).child(friDetails).child("chat").child(key).setValue(message.getText().toString()+"+");
                myRef.getParent().child("user").child(friDetails).child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(message.getText().toString()+"-");
                //send message to database

                if(message.getText().toString().charAt(message.getText().toString().length()-1)=='+') {
                    list.add("Me: " + message.getText().toString().substring(0, message.getText().toString().length() - 1));
                }
                else {
                    list.add(getIntent().getStringExtra("friname") + ": " + message.getText().toString().substring(0, message.getText().toString().length() - 1));
                    if(!status)
                        new sendNotification(message.getText().toString(),"message form: "+getIntent().getStringExtra("friname"),notificationKey);
                }
                message.setText("");
                adapter.notifyDataSetChanged();
                messageContent.setAdapter(adapter);
            }
        });

        //read
        getRef = databaseForMessage.getReference().child("user").child(friDetails).child(FirebaseAuth.getInstance().getUid()).child("chat");
        getRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot dt: dataSnapshot.getChildren()){
                    if(dt.getValue().toString().charAt(dt.getValue().toString().length()-1)=='+')
                        list.add(getIntent().getStringExtra("friname")+": "+dt.getValue().toString().substring(0,dt.getValue().toString().length()-1));
                    else
                        list.add("Me: "+dt.getValue().toString().substring(0,dt.getValue().toString().length()-1));
                    adapter.notifyDataSetChanged();
                    messageContent.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //read

    }
}
