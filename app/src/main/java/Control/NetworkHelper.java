package Control;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;
import static com.calculate.ferronix.BuildConfig.BOT_TOKEN;
import static com.calculate.ferronix.BuildConfig.CHAT_ID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkHelper {



    private static final String TAG = "TelegramHelper";

    public static void sendCalculationData(Context context, Map<String, String> data) {
        sendTelegramNotification(context, data, "📊 Новый расчет");
    }

    public static void sendTelegramNotification(Context context, Map<String, String> data, String title) {
        if (!shouldSendNotifications(context)) {
            Log.d(TAG, "Уведомления отключены в настройках");
            return;
        }

        new SendTask(context, data, title).execute();
    }

    private static boolean shouldSendNotifications(Context context) {
        return true;
    }

    private static class SendTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final Map<String, String> data;
        private final String title;

        SendTask(Context context, Map<String, String> data, String title) {
            this.context = context.getApplicationContext();
            this.data = data;
            this.title = title;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                StringBuilder message = new StringBuilder(title + ":\n\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        message.append("• ").append(entry.getKey()).append(": ")
                                .append(entry.getValue()).append("\n");
                    }
                }

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("chat_id", CHAT_ID)
                        .add("text", message.toString())
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Ошибка отправки: " + response.code());
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка сети", e);
            }
            return null;
        }
    }
}