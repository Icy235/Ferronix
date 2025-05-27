package Control;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.calculate.ferronix.R;
import com.calculate.ferronix.Sortament.gostPdf.Gost24045_94_pdf;
import com.calculate.ferronix.Sortament.gostPdf.Gost26020_83_pdf;
import com.calculate.ferronix.Sortament.gostPdf.Gost34028_2016;
import com.calculate.ferronix.Sortament.gostPdf.Gost57837_2017_pdf;
import com.calculate.ferronix.Sortament.gostPdf.Gost8240_97_pdf;
import com.calculate.ferronix.Sortament.gostPdf.Gost8568_77_pdf;

public class GostsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.gost_list_main);



    }

    public void btnGost8240_97_pdf(View view) {
        startActivity(new Intent(GostsList.this, Gost8240_97_pdf.class));
        finish(); // Закрываем текущую активность
    }

    public void  btnGost26020_83_pdf(View view) {
        startActivity(new Intent(GostsList.this, Gost26020_83_pdf.class));
        finish(); // Закрываем текущую активность
    }

    public void btnGost57836_2017_pdf(View view) {
        startActivity(new Intent(GostsList.this, Gost57837_2017_pdf.class));
        finish(); // Закрываем текущую активность
    }

    public void  btnGost24045_94_pdf(View view) {
        startActivity(new Intent(GostsList.this, Gost24045_94_pdf.class));
        finish(); // Закрываем текущую активность
    }
    public void  btnGost8568_77_pdf(View view) {
        startActivity(new Intent(GostsList.this, Gost8568_77_pdf.class));
        finish(); // Закрываем текущую активность
    }


    public void   btnGost34028_2016_pdf(View view) {
        startActivity(new Intent(GostsList.this, Gost34028_2016.class));
        finish(); // Закрываем текущую активность
    }
}