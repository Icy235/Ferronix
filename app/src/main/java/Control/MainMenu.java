package Control;

import static com.calculate.ferronix.BuildConfig.API_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


import io.appmetrica.analytics.AppMetrica;
import io.appmetrica.analytics.AppMetricaConfig;
import io.appmetrica.analytics.profile.UserProfile;

import com.calculate.ferronix.R;
import com.calculate.ferronix.Sortament.SelectForm;
import com.calculate.ferronix.Sortament.gostPdf.Gost5264_80_pdf;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainMenu extends AppCompatActivity {
    private String userId;
    private static final String PREFS_NAME = "FerronixPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_APP_LANGUAGE = "app_language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Загружаем сохраненный язык перед вызовом super.onCreate()
        loadLocale();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_menu);

        Button btnReportError = findViewById(R.id.btn_report_error);
        btnReportError.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:me@icymail.ru"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Ошибка в приложении");
            startActivity(Intent.createChooser(emailIntent, "Отправить отчет"));
        });

        // Инициализация AppMetrica
        AppMetricaConfig config = AppMetricaConfig.newConfigBuilder(API_KEY).build();
        AppMetrica.activate(this, config);
        AppMetrica.enableActivityAutoTracking(this.getApplication());

        // Установка/получение ID пользователя
        userId = setPersistentUserId(this);

        // Отправка уведомления о входе
        sendUserActionNotification("Вход в приложение");

        // Инициализация профиля AppMetric
        UserProfile userProfile = UserProfile.newBuilder().build();
        AppMetrica.setUserProfileID(userId);
        // Отправляем профиль пользователя в AppMetric.
        AppMetrica.reportUserProfile(userProfile);
    }

    public void showLanguageDialog(View view) {
        final List<LanguageAdapter.LanguageItem> languages = Arrays.asList(
                new LanguageAdapter.LanguageItem("English", "en", R.drawable.ic_flag_us),
                new LanguageAdapter.LanguageItem("Русский", "ru", R.drawable.ic_flag_ru),
                new LanguageAdapter.LanguageItem("Azərbaycan", "az", R.drawable.ic_flag_az),
                new LanguageAdapter.LanguageItem("Oʻzbekcha", "uz", R.drawable.ic_flag_uz),
                new LanguageAdapter.LanguageItem("中文", "zh", R.drawable.ic_flag_ch)

        );

        // Получаем текущий язык
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String currentLang = prefs.getString(KEY_APP_LANGUAGE, "");
        if (currentLang.isEmpty()) {
            currentLang = Locale.getDefault().getLanguage();
        }

        // Создаем Material диалог
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(
                this,
                R.style.LanguageDialogTheme
        );

        builder.setTitle(R.string.select_language);

        // Создаем адаптер
        LanguageAdapter adapter = new LanguageAdapter(this, languages, currentLang);

        String finalCurrentLang = currentLang;
        builder.setAdapter(adapter, (dialog, which) -> {
            String selectedCode = languages.get(which).code;
            if (!selectedCode.equals(finalCurrentLang)) { // Проверяем, действительно ли язык изменился
                setLanguageAndRecreate(selectedCode);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Настройка анимации
        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.DialogAnimation;

            // Настройка размеров
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = (int) (metrics.widthPixels * 0.9);
            int maxWidth = getResources().getDimensionPixelSize(R.dimen.dialog_max_width);
            if (width > maxWidth) width = maxWidth;

            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(R.drawable.dialog_bg_rounded);
        }
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String language = prefs.getString(KEY_APP_LANGUAGE, null);
        if (language != null) {
            setLocale(language);
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        // Deprecated в API 25+, но все еще работает для более старых версий.
        // Для новых версий, используйте createConfigurationContext.
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Сохраняем выбранный язык
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_APP_LANGUAGE, lang);
        editor.apply();
    }

    // Добавлен отсутствующий метод
    private String getCurrentLanguage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedLang = prefs.getString(KEY_APP_LANGUAGE, "");
        if (savedLang.isEmpty()) {
            return Locale.getDefault().getLanguage();
        }
        return savedLang;
    }

    private void setLanguageAndRecreate(String lang) {
        // Проверка на null для lang, хотя обычно он не будет null из диалога
        if (lang == null || lang.equals(getCurrentLanguage())) {
            return;
        }
        setLocale(lang);
        recreateWithAnimation();
    }

    private void recreateWithAnimation() {
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void sendUserActionNotification(String action) {
        Map<String, String> data = new HashMap<>();
        data.put("UserID", userId);
        data.put("Действие", action);
        data.put("Устройство", android.os.Build.MODEL);
        data.put("Время", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date()));

        String emoji = action.equals("Вход в приложение") ? "👤" : "🚪";
        // Убедитесь, что NetworkHelper.sendTelegramNotification существует и корректно реализован.
        // Если это заглушка, её нужно будет заменить на реальный код для отправки уведомлений.
        NetworkHelper.sendTelegramNotification(
                this,
                data,
                emoji + " Пользователь " + (action.equals("Вход в приложение") ? "вошел" : "вышел")
        );
    }

    private String setPersistentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);

        if (userId == null) {
            userId = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_USER_ID, userId).apply();
        }
        Log.d("AppMetrica", "UserID: " + userId);
        return userId;
    }

    public void btnCalculateMetal(View view) {
        Intent intent = new Intent(MainMenu.this, SelectForm.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnListGosts(View view) {
        // Убедитесь, что класс GostsList существует в пакете Control.
        Intent intent = new Intent(MainMenu.this, GostsList.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnWeldingSeams(View view){
        Intent intent = new Intent(MainMenu.this, Gost5264_80_pdf.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnBeamMenu(View view) {
        // Этот метод запускает MainMenu, что, вероятно, является ошибкой,
        // так как он уже находится в MainMenu. Возможно, здесь должен быть другой Activity.
        // Если вы хотели вернуться к текущей активности, то нет необходимости запускать новую.
        // Если это другое меню, измените MainMenu.class на соответствующий класс.
        // В текущем виде это просто перезапустит текущую активность.
        Intent intent = new Intent(MainMenu.this, MainMenu.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}