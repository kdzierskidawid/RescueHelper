package com.example.bright.RescueHelper;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

public class OptionsActivity extends AppCompatActivity {
    SwitchCompat switch_1, switch_2, switch_3, switch_4;

    static boolean stanSwitch1, stanSwitch2, stanSwitch3, stanSwitch4;
    SharedPreferences preferences;
    public GoogleMap mMap2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        //getSupportActionBar().hide();


        preferences = getSharedPreferences("PRESS", 0);
        stanSwitch1 = preferences.getBoolean("switch1", false);
        stanSwitch2 = preferences.getBoolean("switch2", false);
        stanSwitch3 = preferences.getBoolean("switch3", false);
        stanSwitch4 = preferences.getBoolean("switch4", false);

        switch_1 = (SwitchCompat) findViewById(R.id.switch_1);
        switch_2 = (SwitchCompat) findViewById(R.id.switch_2);
        switch_3 = (SwitchCompat) findViewById(R.id.switch_3);
        switch_4 = (SwitchCompat) findViewById(R.id.switch_4);

        switch_1.setChecked(stanSwitch1);
        switch_2.setChecked(stanSwitch2);
        switch_3.setChecked(stanSwitch3);
        switch_4.setChecked(stanSwitch4);
        switch_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stanSwitch1 = !stanSwitch1;
                switch_1.setChecked(stanSwitch1);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("switch1", stanSwitch1);
                editor.apply();
            }
        });

        switch_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stanSwitch2 = !stanSwitch2;
                switch_2.setChecked(stanSwitch2);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("switch2", stanSwitch2);
                editor.apply();
            }
        });

        switch_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stanSwitch3 = !stanSwitch3;
                switch_3.setChecked(stanSwitch3);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("switch3", stanSwitch3);
                editor.apply();
            }
        });

        switch_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stanSwitch4 = !stanSwitch4;
                switch_4.setChecked(stanSwitch4);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("switch4", stanSwitch4);
                editor.apply();
            }
        });
    }
}
