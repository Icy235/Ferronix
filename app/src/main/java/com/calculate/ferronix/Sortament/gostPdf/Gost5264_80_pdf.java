package com.calculate.ferronix.Sortament.gostPdf;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.calculate.ferronix.R;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;
import java.util.Objects;

import android.app.Dialog;
import android.content.SharedPreferences;


public class Gost5264_80_pdf extends AppCompatActivity {
    private static final String PDF_URL = "https://nposanef.ru/upload/gost/5264-80.pdf";
    private static final String PDF_NAME = "gost_5264_80.pdf";
    private static final int TIMEOUT = 30_000;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final String PREFS_NAME = "GostPrefs";
    private static final String LAST_SHOWN_KEY = "last_shown_timestamp";
    private static final long THREE_DAYS_MS = 3 * 24 * 60 * 60 * 1000L; // 3 дня в миллисекундах
    private PDFView pdfView;

    private TextView timeOutText;
    private long downloadId = -1;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable timeoutRunnable = this::showTimeoutMessage;
    private BroadcastReceiver downloadReceiver;
    private Dialog infoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gost_5264_80_pdf);

        initializeViews();
        setupDownloadReceiver();
        showInfoDialogIfNeeded();

        File pdfFile = getPdfFile();
        if (pdfFile.exists()) {
            displayPdf(pdfFile);
        } else if (checkStoragePermission()) {
            downloadPdf();
        } else {
            requestStoragePermission();
        }
    }

    private void initializeViews() {
        pdfView = findViewById(R.id.pdfView);

        timeOutText = findViewById(R.id.TimeOut);
    }

    private void setupDownloadReceiver() {
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.d("Download", "Received broadcast for download ID: " + receivedDownloadId);
                if (receivedDownloadId == downloadId) {
                    handler.removeCallbacks(timeoutRunnable);

                    pdfView.setVisibility(View.VISIBLE);
                    displayPdf(getPdfFile());
                }
            }
        };
    }

    private void showInfoDialogIfNeeded() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long lastShown = prefs.getLong(LAST_SHOWN_KEY, 0);
        long currentTime = System.currentTimeMillis();

        // Показываем диалог при первом запуске или если прошло 3 дня
        if (lastShown == 0 || currentTime - lastShown >= THREE_DAYS_MS) {
            infoDialog = new Dialog(this);
            infoDialog.setContentView(R.layout.gost_5264_80_dialog_info);
            infoDialog.setCancelable(false); // Запрещаем закрытие по кнопке "Назад"
            Objects.requireNonNull(infoDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            infoDialog.show();

            // Сохраняем время показа
            prefs.edit().putLong(LAST_SHOWN_KEY, currentTime).apply();
        }
    }

    public void btnCloseDialog(View view) {
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (downloadReceiver != null) {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? Context.RECEIVER_EXPORTED
                    : 0;
            ContextCompat.registerReceiver(this, downloadReceiver, filter, ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS);
            Log.d("Download", "BroadcastReceiver registered with flags: " + flags);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (downloadReceiver != null) {
            try {
                unregisterReceiver(downloadReceiver);
            } catch (IllegalArgumentException e) {
                Log.e("Download", "Receiver not registered: " + e.getMessage());
            }
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPdf();
            } else {
                showError("Разрешение на доступ к хранилищу отклонено");
            }
        }
    }

    private File getPdfFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), PDF_NAME);
        } else {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), PDF_NAME);
        }
    }

    private void downloadPdf() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            showError("Служба загрузки недоступна");
            return;
        }


        pdfView.setVisibility(View.GONE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(PDF_URL))
                .setTitle("Загрузка ГОСТ 5264-80")
                .setDescription("Загрузка PDF-файла")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, PDF_NAME);
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, PDF_NAME);
        }

        downloadId = downloadManager.enqueue(request);
        handler.postDelayed(timeoutRunnable, TIMEOUT);
        Log.d("Download", "Started download with ID: " + downloadId);
    }

    private void displayPdf(File file) {
        try {
            pdfView.fromFile(file)
                    .onLoad(nbPages -> Log.d("PDF", "Загружено страниц: " + nbPages))
                    .onError(error -> {
                        Log.e("PDF", "Ошибка загрузки PDF: " + error.getMessage());
                        showError("Ошибка отображения PDF");
                    })
                    .load();
            pdfView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e("PDF", "Исключение в displayPdf: " + e.getMessage());
            showError("Ошибка отображения PDF");
        }
    }

    private void showError(String message) {
        timeOutText.setVisibility(View.VISIBLE);
        timeOutText.setText(message);
        pdfView.setVisibility(View.GONE);

    }

    private void showTimeoutMessage() {
        showError("Отсутствует интернет-соединение или время ожидания истекло");
        Log.d("Download", "Таймаут загрузки после " + TIMEOUT + " мс");
    }

    public void btnBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
        }
    }
}