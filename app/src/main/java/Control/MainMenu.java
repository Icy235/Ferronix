package Control;

import static com.calculate.ferronix.BuildConfig.API_KEY;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import Beams.BeamMenu;
import io.appmetrica.analytics.AppMetrica;
import io.appmetrica.analytics.AppMetricaConfig;
import io.appmetrica.analytics.profile.UserProfile;

import android.content.SharedPreferences;

import com.calculate.ferronix.R;
import com.calculate.ferronix.Sortament.SelectForm;

public class MainMenu extends AppCompatActivity {
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_menu);

        // Инициализация AppMetrica
        AppMetricaConfig config = AppMetricaConfig.newConfigBuilder(API_KEY).build();
        AppMetrica.activate(this, config);
        AppMetrica.enableActivityAutoTracking(this.getApplication());



        // Установка/получение ID пользователя
        userId = setPersistentUserId(this);

        // Отправка уведомления о входе
        sendUserActionNotification("Вход в приложение");

        // Инициализация профиля AppMetrica
        UserProfile userProfile = UserProfile.newBuilder().build();
        AppMetrica.setUserProfileID(userId);
        // Отправляем профиль пользователя в AppMetrica.
        AppMetrica.reportUserProfile(userProfile);
    }

    @Override
    protected void onDestroy() {
        // Отправка уведомления о выходе
        sendUserActionNotification("Выход из приложения");
        super.onDestroy();
    }

    private void sendUserActionNotification(String action) {
        Map<String, String> data = new HashMap<>();
        data.put("UserID", userId);
        data.put("Действие", action);
        data.put("Устройство", android.os.Build.MODEL);
        data.put("Время", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date()));

        String emoji = action.equals("Вход в приложение") ? "👤" : "🚪";
        NetworkHelper.sendTelegramNotification(
                this,
                data,
                emoji + " Пользователь " + (action.equals("Вход в приложение") ? "вошел" : "вышел")
        );
    }

    private String setPersistentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FerronixPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            userId = UUID.randomUUID().toString();
            prefs.edit().putString("user_id", userId).apply();
        }
        Log.d("AppMetrica", "UserID: " + userId);
        return userId;
    }

    public void btnTelegram(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Icy234"));
        startActivity(browserIntent);
    }

    public void btnCalculateMetall(View view) {
        Intent intent = new Intent(MainMenu.this, SelectForm.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnListGosts(View view) {
        Intent intent = new Intent(MainMenu.this, GostsList.class);
        startActivity(intent);

      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnBeamMenu(View view) {
        Intent intent = new Intent(MainMenu.this, BeamMenu.class); // Исправлено на BeamMenu
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}