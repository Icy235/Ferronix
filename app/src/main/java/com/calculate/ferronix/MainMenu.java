package com.calculate.ferronix;

import static com.calculate.ferronix.BuildConfig.API_KEY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.UUID;
import io.appmetrica.analytics.AppMetrica;
import io.appmetrica.analytics.AppMetricaConfig;
import io.appmetrica.analytics.profile.UserProfile;

import android.content.SharedPreferences;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);

        // Создаем конфигурацию для AppMetrica.
        AppMetricaConfig config = AppMetricaConfig.newConfigBuilder(API_KEY).build();
        // Инициализируем SDK AppMetrica.
        AppMetrica.activate(this, config);
        AppMetrica.enableActivityAutoTracking(this.getApplication());

        Button btnSelectForm = findViewById(R.id.btnSelectForm);
        btnSelectForm.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, SelectForm.class);
            startActivity(intent);
        });

        // Настроим и установим ProfileId
        String userId = setPersistentUserId(this);

        // Создаем объект профиля пользователя.
        UserProfile userProfile = UserProfile.newBuilder()
                .build();

        // Устанавливаем ProfileId с использованием сгенерированного ID.
        AppMetrica.setUserProfileID(userId);

        // Отправляем профиль пользователя в AppMetrica.
        AppMetrica.reportUserProfile(userProfile);
    }

    // Метод для установки или генерации уникального ID пользователя
    public String setPersistentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FerronixPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            // Если ID еще нет, генерируем новый
            userId = UUID.randomUUID().toString();
            prefs.edit().putString("user_id", userId).apply();
        }
        Log.d("AppMetrica", "UserID: " + userId);
        // Возвращаем сгенерированный или сохраненный ID
        return userId;
    }
}
