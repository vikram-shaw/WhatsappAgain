package com.mevikram.fireapp;

import android.content.DialogInterface;
import android.nfc.NfcAdapter;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {

    private ListView allUser;
    FirebaseDatabase instance= FirebaseDatabase.getInstance();
    DatabaseReference ref=instance.getReference();
    DatabaseReference userRef,sendRef;
    DatabaseReference remRef=instance.getReference(FirebaseAuth.getInstance().getUid()).child("friends");
    DatabaseReference remreRef=instance.getReference(FirebaseAuth.getInstance().getUid()).child("friend request");
    DatabaseReference delref=instance.getReference(FirebaseAuth.getInstance().getUid()).child("alu");
    ArrayList<String> suggestionList=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayList<Truple<String,String,Integer>> userList=new ArrayList<>();
    ArrayList<String>remthis=new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        setTitle("All users");
        allUser=findViewById(R.id.lvsuggestionList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,suggestionList);
        remRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot all: dataSnapshot.getChildren()){
                    remthis.add(all.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        remreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot all:dataSnapshot.getChildren())
                    remthis.add(all.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            int pos=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestionList.clear();
                cnt:for(final DataSnapshot list:dataSnapshot.getChildren()) {
                    if(list.getKey().equals("user") || list.getKey().equals(FirebaseAuth.getInstance().getUid()))
                        continue;
                    for(String rem:remthis){
                        if(list.getKey().equals(rem))
                            continue cnt;
                    }
                    userRef=instance.getReference(list.getKey()).child("name");
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                suggestionList.add(dataSnapshot.getValue().toString());
                                userList.add(new Truple<String, String, Integer>(list.getKey(),dataSnapshot.getValue().toString(),pos++));
                            }catch (NullPointerException e){}
                            adapter.notifyDataSetChanged();
                            allUser.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        allUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder request=new AlertDialog.Builder(SearchFriendActivity.this);
                request.setTitle("Sent Friend Request to");
                request.setMessage(parent.getItemAtPosition(position).toString());
                request.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(Truple<String,String,Integer> user: userList){
                            if(position==user.position && parent.getItemAtPosition(position).toString()==user.name){
                                sendRef=instance.getReference(user.uid);
                                sendRef.child("friend request").child(FirebaseAuth.getInstance().getUid()).setValue(true);
                            }
                        }
                        Toast.makeText(SearchFriendActivity.this,"request send",Toast.LENGTH_SHORT).show();
                    }
                });
                request.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SearchFriendActivity.this,"request canceled",Toast.LENGTH_SHORT).show();
                    }
                });
                request.show();
            }
        });
    }
}

