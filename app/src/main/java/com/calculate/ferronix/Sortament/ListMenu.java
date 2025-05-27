package com.calculate.ferronix.Sortament;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.calculate.ferronix.R;

public class ListMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.list_menu);



    }

    public void btnCustom(View view) {
        startActivity(new Intent(ListMenu.this, ListCustom.class));
        finish(); // Закрываем текущую активность
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnGost24045_94(View view) {
        startActivity(new Intent(ListMenu.this, ListGost24045_94.class));
        finish(); // Закрываем текущую активность
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void btnGost8568_77chechevich(View view) {
        startActivity(new Intent(ListMenu.this, ListGost8568_77_chechevich.class));
        finish(); // Закрываем текущую активность
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void btnGost8568_77romb(View view) {
        startActivity(new Intent(ListMenu.this, ListGost8568_77_romb.class));
        finish(); // Закрываем текущую активность
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}