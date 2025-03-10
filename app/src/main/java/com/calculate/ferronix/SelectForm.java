package com.calculate.ferronix;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SelectForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_form);



    }

    public void btnSquareCalculate(View view) {
        startActivity(new Intent(SelectForm.this, SquareCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }

    public void btnCircleCalculate(View view) {
        startActivity(new Intent(SelectForm.this, CircleCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }

    public void btnCirclePipeCalculate(View view) {
        startActivity(new Intent(SelectForm.this, CirclePipeCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }

    public void btnProfilePipeCalculate(View view) {
        startActivity(new Intent(SelectForm.this, ProfilePipeCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }

    public void btnListCalculate(View view) {
        startActivity(new Intent(SelectForm.this, ListMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnDvutavr(View view) {
        startActivity(new Intent(SelectForm.this, DvutavrMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnCornerCalculate(View view) {
        startActivity(new Intent(SelectForm.this, CornerCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }

    public void btnShveller(View view) {
        startActivity(new Intent(SelectForm.this, ShvellerMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnShestigrannikCalculate(View view) {
        startActivity(new Intent(SelectForm.this, ShestigrannikCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }

}