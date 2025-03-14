package com.calculate.ferronix;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ShvellerMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shveller_menu);



    }

    public void btnCustom(View view) {
        startActivity(new Intent(ShvellerMenu.this, ShvellerCustomCalculate.class));
        finish(); // Закрываем текущую активность
    }

    public void btnShvellerGost8240_97(View view) {
        startActivity(new Intent(ShvellerMenu.this, ShvellerGost8240_97.class));
        finish(); // Закрываем текущую активность
    }




}