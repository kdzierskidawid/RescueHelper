package com.example.bright.RescueHelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static int SPLASH_TIME_OUT = 4000;
    Button options;
    Button Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_mainmenu);
        //getSupportActionBar().hide();
        options = (Button)findViewById(R.id.Options);
        Login = (Button) findViewById(R.id.logowanie);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }

    public void showOptions(View view) {
        startActivity(new Intent(getApplicationContext(),OptionsActivity.class));
    }

    public void showMyLocation(View view) {
        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
    }

    public void showAutocomplete(View view) {
        startActivity(new Intent(getApplicationContext(),Autocomplete.class));
    }

    public void showLogin(View view) {
        startActivity(new Intent(getApplicationContext(),Login.class));
    }

    public void showMyProfile(View view) {
        startActivity(new Intent(getApplicationContext(),Profile_Info.class));
    }



}

