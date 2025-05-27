package Beams;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.calculate.ferronix.R;

public class BeamMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.beam_menu);



    }

    public void btnSquareBeam(View view) {
        startActivity(new Intent(BeamMenu.this, ProfileBeam.class));
        finish(); // Закрываем текущую активность
    }

    public void btnCircleBeam(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnCirclePipeBeam(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnProfilePipeBeam(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnArmaturaBeam(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnDvutavrBeam(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnCornerBeam(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnShveller(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

    public void btnShestigrannik(View view) {
        startActivity(new Intent(BeamMenu.this, BeamMenu.class));
        finish(); // Закрываем текущую активность
    }

}