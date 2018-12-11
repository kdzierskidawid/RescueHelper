package com.example.bright.RescueHelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static int SPLASH_TIME_OUT = 4000;
    Button options;
    Button Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        options = (Button)findViewById(R.id.Options);
        Login = (Button) findViewById(R.id.logowanie);
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
        setContentView(R.layout.activity_login);

    }


}

