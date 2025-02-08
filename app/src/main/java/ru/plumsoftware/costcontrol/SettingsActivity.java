package ru.plumsoftware.costcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = SettingsActivity.this;

        //UI mode
        SharedPreferences sharedPreferences = context.getSharedPreferences("finance_manager_settings", MODE_PRIVATE);
        boolean ui_theme = sharedPreferences.getBoolean("ui_theme", false);

        if (ui_theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_settings);

        @SuppressLint({"UseSwitchCompatOrMaterialCode", "MissingInflatedId", "LocalSuppress"}) SwitchCompat aSwitch = (SwitchCompat) findViewById(R.id.switch1);
        ImageButton imageButtonBack = (ImageButton) findViewById(R.id.imageButtonBack);

        aSwitch.setTextOn("Светлая тема");
        aSwitch.setTextOff("Темная тема");
        aSwitch.setChecked(ui_theme);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ui_theme", b);
                editor.apply();

                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                startActivity(new Intent(context, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}