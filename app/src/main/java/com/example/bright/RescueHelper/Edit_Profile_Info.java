package com.example.bright.RescueHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Edit_Profile_Info extends AppCompatActivity {

    private static final String TAG = "AddToDatabase";

    private Button mAddToDB, btnUploadPhoto;

    private EditText mNewFood, mNewName, mNewPhone, mNewDescription, mLeader_of_group;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    public String newFood, newName, newPhone, newDescription, newleader_of_group;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_to_database);
        //declare variables in oncreate
        mAddToDB = (Button) findViewById(R.id.save);
        btnUploadPhoto = (Button) findViewById(R.id.upload_screen);
        mNewName = (EditText) findViewById(R.id.name_database);
        mNewPhone = (EditText) findViewById(R.id.number_database);
        mNewDescription = (EditText) findViewById(R.id.description_database);
        mLeader_of_group = (EditText) findViewById(R.id.leader_of_group_edittext);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mAddToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add object to database.");
                newName = mNewName.getText().toString();
                newPhone = mNewPhone.getText().toString();
                newDescription = mNewDescription.getText().toString();
                newleader_of_group = mLeader_of_group.getText().toString();
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();
                myRef.child("users").child(userID).child("name").setValue(newName);
                myRef.child("users").child(userID).child("phone_num").setValue(newPhone);
                myRef.child("users").child(userID).child("email").setValue(user.getEmail());
                myRef.child("users").child(userID).child("description").setValue(newDescription);
                myRef.child("users").child(userID).child("leader").setValue(newleader_of_group);

                //reset the text
                Log.d(TAG, "Nowe imie: "+ newName);
                Log.d(TAG, "Nowy tel: "+ newPhone);
                Log.d(TAG, "Nowy opis: " + newDescription);
                Log.d(TAG, "ID uzytkownika: " + userID);

            }
        });

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),UploadPhotos.class));

            }
        });
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

    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
