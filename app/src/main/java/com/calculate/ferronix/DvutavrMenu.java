package com.calculate.ferronix;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class DvutavrMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dvutavr_menu);



    }

    public void btnCustom(View view) {
        startActivity(new Intent(DvutavrMenu.this, DvutavrCustomCalculate.class));
        finish(); // Закрываем текущую активность
    }


    public void btnGost57837_2017(View view) {
        startActivity(new Intent(DvutavrMenu.this, DvutavrGost57837_2017.class));
        finish(); // Закрываем текущую активность
    }

    public void btnGost8239_89(View view) {
        startActivity(new Intent(DvutavrMenu.this, DvutavrGost8239_89.class));
        finish(); // Закрываем текущую активность
    }

    public void btnGost26020_83(View view) {
        startActivity(new Intent(DvutavrMenu.this, DvutavrGost26020_83.class));
        finish(); // Закрываем текущую активность
    }

}