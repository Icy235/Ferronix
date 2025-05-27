package com.calculate.ferronix.Sortament;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.calculate.ferronix.R;

public class SelectForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.select_form);



    }

    public void btnProfile(View view) {
        startActivity(new Intent(SelectForm.this, ProfileMenu.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnCircleCalculate(View view) {
        startActivity(new Intent(SelectForm.this, CircleCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnCirclePipeCalculate(View view) {
        startActivity(new Intent(SelectForm.this, CirclePipeCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnProfilePipeCalculate(View view) {
        startActivity(new Intent(SelectForm.this, ProfilePipeCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnListCalculate(View view) {
        startActivity(new Intent(SelectForm.this, ListMenu.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnDvutavr(View view) {
        startActivity(new Intent(SelectForm.this, DvutavrMenu.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnCornerCalculate(View view) {
        startActivity(new Intent(SelectForm.this, CornerCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnShveller(View view) {
        startActivity(new Intent(SelectForm.this, ShvellerMenu.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnShestigrannikCalculate(View view) {
        startActivity(new Intent(SelectForm.this, ShestigrannikCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnArmatura(View view) {
        startActivity(new Intent(SelectForm.this, ArmaturaCalculate.class));
        finish(); // Закрываем текущую активность
        // Для fade-эффекта
       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}