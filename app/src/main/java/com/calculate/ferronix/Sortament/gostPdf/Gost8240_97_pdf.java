package com.calculate.ferronix.Sortament.gostPdf;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.calculate.ferronix.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

public class Gost8240_97_pdf extends AppCompatActivity {

    private static final String PDF_NAME = "gost_8240_97.pdf";
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gost_8240_97_pdf);

        pdfView = findViewById(R.id.pdfView);
        loadPdfFromAssets();
    }

    private void loadPdfFromAssets() {
        pdfView.fromAsset(PDF_NAME)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Log.d("PDF", "PDF успешно загружен, страниц: " + nbPages);
                    }
                })
                .onError(error -> {
                    Log.e("PDF", "Ошибка загрузки PDF: " + error.getMessage());
                    Toast.makeText(Gost8240_97_pdf.this, "Ошибка загрузки PDF", Toast.LENGTH_SHORT).show();
                })
                .load();
    }

    public void btnBack(View view) {
        finish();
    }
}