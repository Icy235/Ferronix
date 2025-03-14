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
        setContentView(R.layout.activity_dvutavt_menu);



    }

    public void btnCustom(View view) {
        startActivity(new Intent(DvutavrMenu.this, DvutavrCustomCalculate.class));
        finish(); // Закрываем текущую активность
    }




}