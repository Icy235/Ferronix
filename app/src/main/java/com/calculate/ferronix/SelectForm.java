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
        startActivity(new Intent(SelectForm.this, CirclePipeCalculateWeight.class));
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


}