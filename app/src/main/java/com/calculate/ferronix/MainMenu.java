package com.calculate.ferronix;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import io.appmetrica.analytics.AppMetrica;
import io.appmetrica.analytics.AppMetricaConfig;
import io.appmetrica.analytics.BuildConfig;


public class MainMenu extends AppCompatActivity {


    private static final String API_KEY = ("22e70fd8-7d7a-45e6-a6d9-5b3dc28f16e3"); // я хз как его спрятать

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);




        // Creating an extended library configuration.
        AppMetricaConfig config = AppMetricaConfig.newConfigBuilder(API_KEY).build();
        // Initializing the AppMetrica SDK.

         AppMetrica.activate(this, config);


        Button btnSelectForm = (Button) findViewById(R.id.btnSelectForm);
        btnSelectForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, SelectForm.class);
                startActivity(intent);
            }
        });



}}