package com.mevikram.fireapp;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import java.util.ArrayList;

public class WorkActivity extends AppCompatActivity {

    private MenuItem logout,profile,searchFriend,recivedRequest;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ListView friendList;
    private String friUid;

    FirebaseDatabase databaseForFriend = FirebaseDatabase.getInstance();
    DatabaseReference friendRef= databaseForFriend.getReference(FirebaseAuth.getInstance().getUid()).child("friends");
    DatabaseReference ref,refStatus=friendRef.getParent();
    ArrayList<String> listOfFriends = new ArrayList<String>();
    ArrayAdapter<String> adapterOfFriends;
    ArrayList <Truple<String,String,Integer>> nameUIDPair = new ArrayList <> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("NotificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
        setTitle("Friend List");
        setContentView(R.layout.activity_work);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        profile=findViewById(R.id.menuProfile);
        recivedRequest=findViewById(R.id.menuRecivedRequest);
        logout=findViewById(R.id.menuLogout);
        searchFriend=findViewById(R.id.menuSearchFriends);
        friendList=findViewById(R.id.lvFriendList);
        adapterOfFriends = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listOfFriends);
        messageWorkButton();
    }

    private void messageWorkButton() {
        friendRef.addValueEventListener(new ValueEventListener() {
            int count=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfFriends.clear();
               for(final DataSnapshot fri: dataSnapshot.getChildren()){
                   ref=databaseForFriend.getReference(fri.getKey()).child("name");
                   ref.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           try{
                               listOfFriends.add(dataSnapshot.getValue().toString());
                               nameUIDPair.add(new Truple<String, String, Integer>(fri.getKey(),dataSnapshot.getValue().toString(),count++));
                           }catch (Exception NullPointerException){}
                           adapterOfFriends.notifyDataSetChanged();
                           friendList.setAdapter(adapterOfFriends);
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

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friname=parent.getItemAtPosition(position).toString();
                friUid=parent.getItemAtPosition(position).toString();
                for(Truple<String,String,Integer> p: nameUIDPair){
                    if(p.name==friUid && p.position==position){
                        friUid=p.uid;
                        break;
                    }
                }
                Intent intent = new Intent(WorkActivity.this,ChatActivity.class);
                intent.putExtra("friUid",friUid);
                intent.putExtra("friname",friname);
                startActivity(intent);
            }
        });
        friendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                friUid=parent.getItemAtPosition(position).toString();
                for(Truple<String,String,Integer> p: nameUIDPair){
                    if(p.name==friUid && p.position==position){
                        friUid=p.uid;
                        break;
                    }
                }
                final AlertDialog.Builder unfriend=new AlertDialog.Builder(WorkActivity.this);
                unfriend.setTitle("Remove from friend list");
                unfriend.setMessage("Do you want to remove/block "+parent.getItemAtPosition(position)+" from your your friend list?");
                unfriend.setPositiveButton("Unfriend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        friendRef.child(friUid).removeValue();
                        databaseForFriend.getReference(friUid).child("friends").child(FirebaseAuth.getInstance().getUid()).removeValue();
                        databaseForFriend.getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child(friUid).removeValue();
                        databaseForFriend.getReference().child("user").child(friUid).child(FirebaseAuth.getInstance().getUid()).removeValue();
                    }
                });
                unfriend.setNegativeButton("Block", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(WorkActivity.this,"Available soon",Toast.LENGTH_SHORT).show();
                    }
                });
                unfriend.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                unfriend.show();
                return true;
            }
        });
        //online or offline
        refStatus.child("online").onDisconnect().setValue(false);
        refStatus.child("online").setValue(true);
        //online or offline
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuLogout:{
                OneSignal.setSubscription(false);
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(WorkActivity.this,LoginActivity.class));
                break;
            }
            case R.id.menuProfile:{
                startActivity(new Intent(WorkActivity.this,ProfileActivity.class));
                break;
            }
            case R.id.menuSearchFriends:{
                startActivity(new Intent(WorkActivity.this,SearchFriendActivity.class));
                break;
            }
            case R.id.menuRecivedRequest:{
                startActivity(new Intent(WorkActivity.this,RequestRecivedActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
