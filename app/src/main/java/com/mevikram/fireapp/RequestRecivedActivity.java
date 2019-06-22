package com.mevikram.fireapp;

import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestRecivedActivity extends AppCompatActivity {

    ArrayList<String> friendRequest=new ArrayList<>();
    ArrayList<Truple<String,String,String>> truplesList=new ArrayList<>();
    ArrayAdapter<String> adapter;
    private ListView requestList;
    FirebaseDatabase reqDatabase= FirebaseDatabase.getInstance();
    DatabaseReference reqRef=reqDatabase.getReference(FirebaseAuth.getInstance().getUid()).child("friend request");
    DatabaseReference requestUser;
    DatabaseReference friRef,myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recived);
        setTitle("Friend Request");
        requestList=findViewById(R.id.lvRequestRecived);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,friendRequest);
        reqRef.addValueEventListener(new ValueEventListener() {
            int pos=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot all:dataSnapshot.getChildren()){
                    requestUser=reqDatabase.getReference(all.getKey()).child("name");
                    requestUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                truplesList.add(new Truple<String, String, String>(all.getKey(), dataSnapshot.getValue().toString(), pos++));
                                friendRequest.add(dataSnapshot.getValue().toString());
                                adapter.notifyDataSetChanged();
                                requestList.setAdapter(adapter);
                            }catch (NullPointerException e){}
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
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String uid;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder request=new AlertDialog.Builder(RequestRecivedActivity.this);
                request.setTitle("Request");
                request.setMessage(parent.getItemAtPosition(position).toString());
                for(Truple<String, String, String> allUsr: truplesList){
                    if(allUsr.name==parent.getItemAtPosition(position) && allUsr.position==position){
                        friRef=reqDatabase.getReference(FirebaseAuth.getInstance().getUid());
                        myref=reqDatabase.getReference(allUsr.uid);
                        uid=allUsr.uid;
                        break;
                    }
                }
                request.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        friRef.child("friends").child(uid).setValue(true);
                        myref.child("friends").child(FirebaseAuth.getInstance().getUid()).setValue(true);
                        reqRef.child(uid).removeValue();
                        friendRequest.clear();
                    }
                });
                request.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                request.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reqRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                reqRef.child(uid).removeValue();
                                friendRequest.clear();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                request.show();
            }
        });
    }
}