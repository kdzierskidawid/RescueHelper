package com.example.bright.RescueHelper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;
    public Object test=null;
    public int logowanie =0;
    public FirebaseUser user;
    // UI references.
    private EditText mEmail, mPassword;
    private Button btnSignIn,btnSignOut,btnAddItems, btnRegister, btnAddTodatabase, btnProfileInfo;
    private TextView mRegisterTExtView;
    public int zalogowany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_login);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password_register);
        btnSignIn = (Button) findViewById(R.id.email_sign_in_button);
        btnSignOut = (Button) findViewById(R.id.btn_signout);
        //btnAddItems = (Button) findViewById(R.id.add_item_screen);
        //btnRegister = (Button) findViewById(R.id.register_button);
        //btnAddTodatabase = (Button) findViewById(R.id.add_item_screen);
        //btnProfileInfo = (Button) findViewById(R.id.view_items_screen);
        mRegisterTExtView = (TextView) findViewById(R.id.register_textview);

       // btnSignOut.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast toast = Toast.makeText(Login.this, "Successfully signed in with: " + user.getEmail(), Toast.LENGTH_SHORT);
                    toast.show();
                    userID = user.getUid();



                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast toast =Toast.makeText(Login.this,"Successfully signed out.", Toast.LENGTH_SHORT);
                    toast.show();
                }

                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                if(!email.equals("") && !pass.equals("")){
                    mAuth.signInWithEmailAndPassword(email,pass);
                   // btnSignOut.setEnabled(true);
                    Toast toast =Toast.makeText(Login.this, "Signing...", Toast.LENGTH_SHORT);
                    toast.show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else{
                    Toast toast =Toast.makeText(Login.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT);
                    toast.show();
                    //btnSignOut.setEnabled(false);
                }

            }
        });



        mRegisterTExtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast toast =Toast.makeText(Login.this, "Signing Out...", Toast.LENGTH_SHORT);
                toast.show();
                zalogowany = 0;
            }
        });
/*
        btnAddItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Switching Activities.");
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));

            }
        });

        btnAddTodatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddingToDatabase.class));

            }
        });

        btnProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Profile_Info.class));

            }
        });*/
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

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
