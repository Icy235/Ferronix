package com.calculate.ferronix.Sortament;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.calculate.ferronix.R;

public class ProfileMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_menu);



    }

    public void btnSquareProfile(View view) {
        startActivity(new Intent(ProfileMenu.this, SquareProfileCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnPriamiugolniyProfile(View view) {
        startActivity(new Intent(ProfileMenu.this, PryamougolniyProfileCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }




}