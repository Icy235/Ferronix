package com.calculate.ferronix.Sortament.gostPdf;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.calculate.ferronix.R;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;

public class Gost34028_2016 extends AppCompatActivity {

    private static final String PDF_URL = "https://files.stroyinf.ru/Data2/1/4293746/4293746568.pdf";
    private static final String PDF_NAME = "gost_34028_2016.pdf";
    private PDFView pdfView;
    private ProgressBar progressBar;
    private DownloadManager downloadManager;
    private long downloadId;
    private final Handler handler = new Handler();
    private static final int TIMEOUT = 30000; // 30 секунд
    private Runnable timeoutRunnable;
    private TextView TimeOut;
    private BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gost34028_2016_pdf);

        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);
        TimeOut = findViewById(R.id.TimeOut);

        File pdfFile = getPdfFile();
        Log.d("PDF", "Путь к файлу: " + pdfFile.getAbsolutePath()); // <- Логируем путь

        if (pdfFile.exists()) {
            displayPdf(pdfFile);
        } else {
            downloadPdf();
        }

        // Инициализация BroadcastReceiver
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {
                    handler.removeCallbacks(timeoutRunnable);
                    progressBar.setVisibility(View.GONE);
                    pdfView.setVisibility(View.VISIBLE);
                    displayPdf(getPdfFile());
                }
            }
        };

        // Регистрация ресивера (для Android 13+ нужен RECEIVER_EXPORTED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        // Таймаут загрузки
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                showTimeoutMessage();
            }
        };
    }

    private File getPdfFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Для Android 10+ — в приватную папку приложения
            return new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), PDF_NAME);
        } else {
            // Для старых версий — в общий Downloads (требует WRITE_EXTERNAL_STORAGE)
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), PDF_NAME);
        }
    }
    private void downloadPdf() {
        progressBar.setVisibility(View.VISIBLE);
        pdfView.setVisibility(View.GONE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(PDF_URL));
        request.setTitle("Downloading GOST 34028-2016")
                .setDescription("Downloading PDF file")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false);

        // Указываем путь сохранения (в зависимости от версии Android)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, PDF_NAME);
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, PDF_NAME);
        }

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);

        // Запускаем таймер
        handler.postDelayed(timeoutRunnable, TIMEOUT);
        Log.d("TimeOut", "Started Timer ");
    }

    private void displayPdf(File file) {
        pdfView.recycle(); // Важно: очищаем предыдущий PDF
        if (file.exists()) {
            pdfView.fromFile(file)
                    .onLoad(nbPages -> Log.d("PDF", "Успешно загружено страниц: " + nbPages))
                    .onError(error -> {
                        Log.e("PDF", "Ошибка: " + error.getMessage());
                        showError("Ошибка загрузки PDF");
                    })
                    .load();
            pdfView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            showError("Файл не найден");
        }
    }
    private void showError(String message) {
        TimeOut.setVisibility(View.VISIBLE);
        TimeOut.setText(message);
        pdfView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    private void showTimeoutMessage() {
        progressBar.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        TimeOut.setVisibility(View.VISIBLE);
        Log.d("TimeOut", "Timeout 30 s done ");
        TimeOut.setText("Возможно, у вас нет интернета...");


    }

    public void btnBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Отменяем регистрацию ресивера и таймер
        if (downloadReceiver != null) {
            unregisterReceiver(downloadReceiver);
        }
        handler.removeCallbacks(timeoutRunnable);
    }
}