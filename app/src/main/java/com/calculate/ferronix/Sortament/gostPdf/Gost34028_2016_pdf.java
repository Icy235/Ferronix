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
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.calculate.ferronix.R;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;

public class Gost34028_2016_pdf extends AppCompatActivity {
    private static final String PDF_URL = "https://files.stroyinf.ru/Data2/1/4293746/4293746568.pdf";
    private static final String PDF_NAME = "gost_34028_2016.pdf";
    private static final int TIMEOUT = 30_000;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private PDFView pdfView;
    private ProgressBar progressBar;
    private TextView timeOutText;

    private long downloadId = -1;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable timeoutRunnable = this::showTimeoutMessage;
    private BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gost34028_2016_pdf);

        initializeViews();
        setupDownloadReceiver();

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
        progressBar = findViewById(R.id.progressBar);
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
                    progressBar.setVisibility(View.GONE);
                    pdfView.setVisibility(View.VISIBLE);
                    displayPdf(getPdfFile());
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (downloadReceiver != null) {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            // Явно указываем RECEIVER_EXPORTED для системного широковещательного действия
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
            unregisterReceiver(downloadReceiver);
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
                showError("Permission denied. Cannot download PDF.");
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
            showError("Download service unavailable");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        pdfView.setVisibility(View.GONE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(PDF_URL))
                .setTitle("Downloading GOST 5264-80")
                .setDescription("Downloading PDF file")
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
                    .onLoad(nbPages -> Log.d("PDF", "Loaded " + nbPages + " pages"))
                    .onError(error -> {
                        Log.e("PDF", "Error loading PDF: " + error.getMessage());
                        showError("Failed to load PDF");
                    })
                    .load();
            pdfView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e("PDF", "Exception in displayPdf: " + e.getMessage());
            showError("Error displaying PDF");
        }
    }

    private void showError(String message) {
        timeOutText.setVisibility(View.VISIBLE);
        timeOutText.setText(message);
        pdfView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showTimeoutMessage() {
        showError("No internet connection or download timed out");
        Log.d("Download", "Download timeout after " + TIMEOUT + "ms");
    }

    public void btnBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}