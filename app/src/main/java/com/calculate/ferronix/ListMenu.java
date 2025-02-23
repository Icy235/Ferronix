package com.calculate.ferronix;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ListMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_menu);



    }

    public void btnCustom(View view) {
        startActivity(new Intent(ListMenu.this, ListCustom.class));
        finish(); // Закрываем текущую активность
    }




}