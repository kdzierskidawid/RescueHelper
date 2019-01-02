package com.example.bright.RescueHelper;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Profile_Info extends AppCompatActivity {
    private static final String TAG = "Profile_Info";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private Button editProfilebtn;
    private ListView mListView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__info);

        mListView = (ListView) findViewById(R.id.listview);
        editProfilebtn = (Button) findViewById(R.id.edit_profile_button);
        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editProfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Edit_Profile_Info.class));

            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            UserInformation uInfo = new UserInformation();

            //uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail()); //set the email
            /*uInfo.setName(ds.child(userID).getValue(UserInformation.class).getName()); //set the name
            uInfo.setPhone_num(ds.child(userID).getValue(UserInformation.class).getPhone_num()); //set the phone_num
            uInfo.setDescription(ds.child(userID).getValue(UserInformation.class).getDescription());
            uInfo.setLeader(ds.child(userID).getValue(UserInformation.class).getLeader());*/




            //display all the information
            /*Log.d(TAG, "showData: name: " + uInfo.getName());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());

            Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());
            Log.d(TAG, "showData: description: " + uInfo.getDescription());*/


            ArrayList<String> array  = new ArrayList<>();
            array.add(userID);
            int i = 1;
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            if(ds.hasChild(userID)) {
                uInfo.setName(ds.child(userID).getValue(UserInformation.class).getName());
                array.add(uInfo.getName());
                uInfo.setLeader(ds.child(userID).getValue(UserInformation.class).getLeader());
                array.add(uInfo.getLeader());
                uInfo.setPhone_num(ds.child(userID).getValue(UserInformation.class).getPhone_num());
                array.add(uInfo.getPhone_num());
                uInfo.setDescription(ds.child(userID).getValue(UserInformation.class).getDescription());
                array.add(uInfo.getDescription());
                /*if(uInfo.getLeader().equals("yes"))
                    array.add("Jestes zadeklarowany jako lider");
                else array.add("Nie jestes zadeklarowany jako lider");*/ }
            else {
               // toastMessage("Brak informacji");
            }

            //array.add(uInfo.getName());
            //array.add(uInfo.getPhone_num());
           /* if(ds.child(userID).getValue(UserInformation.class)==null){
                toastMessage("Brak informacji");
                myRef.child("users").child(userID).child("name").setValue("Imie");
                myRef.child("users").child(userID).child("phone_num").setValue("0");
                myRef.child("users").child(userID).child("email").setValue(user.getEmail());
                myRef.child("users").child(userID).child("description").setValue("Opis");
                myRef.child("users").child(userID).child("leader").setValue("no");
                uInfo.setName(ds.child(userID).getValue(UserInformation.class).getName());
                array.add(uInfo.getName());
                uInfo.setLeader(ds.child(userID).getValue(UserInformation.class).getLeader());
                array.add(uInfo.getLeader());
                uInfo.setPhone_num(ds.child(userID).getValue(UserInformation.class).getPhone_num());
                array.add(uInfo.getPhone_num());
                uInfo.setDescription(ds.child(userID).getValue(UserInformation.class).getDescription());
                array.add(uInfo.getDescription());
                if(uInfo.getLeader().equals("yes"))
                    array.add("Jestes zadeklarowany jako lider");
                else array.add("Nie jestes zadeklarowany jako lider");
            }*/


            //array.add(uInfo.getDescription());


            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}