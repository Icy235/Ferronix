package com.calculate.ferronix.Sortament;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.calculate.ferronix.R;
import com.calculate.ferronix.Sortament.gostPdf.Gost8568_77_pdf;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Control.NetworkHelper;

public class ListGost8568_77_chechevich extends AppCompatActivity {

    private EditText editTextLengthA, editTextLengthB, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight;
    private Button btnMark;

    private String[] mark;
    // Инициализация массивов для ГОСТ 8568-77 С чечевичным рифлением
    private final String[] ListMark = {
            "2.5", "3", "4", "5", "6", "8", "10", "12"
    };

    private final double[] ListMass1Kg = {
            20.1, 24.2, 32.2, 40.5, 48.5, 64.9, 80.9, 96.8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.list_gost_8568_77_chehevich_calculate);

        // Инициализация массива mark
        mark = ListMark.clone(); // Используем клон массива ListMark

        // Инициализация элементов интерфейса
        editTextLengthA = findViewById(R.id.editTextLengthA);
        editTextLengthB = findViewById(R.id.editTextLengthB);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMark = findViewById(R.id.btnMark);

        // Проверка на null
        if (editTextLengthA == null || editTextLengthB == null || editTextPricePerKg == null || editTextQuantity == null) {
            Log.e("InitError", "One or more EditText fields are null!");
            finish();
        }

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> {
            // Выполнение тактильной обратной связи
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            calculateWeight();
        });

        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMarkMenuClick();
        });
    }

    private void showMarkMenuClick() {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        for (String material : mark) {
            popupMenu.getMenu().add(material);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedMark = Objects.requireNonNull(item.getTitle()).toString(); // Получаем выбранную марку
            btnMark.setText(selectedMark); // Устанавливаем текст кнопки на выбранную марку

            // Находим индекс выбранной марки в массиве mark
            int index = Arrays.asList(mark).indexOf(selectedMark);
            if (index != -1 && index < ListMass1Kg.length) {
                double mass = ListMass1Kg[index]; // Получаем массу для выбранной марки
                Log.d("Выбранная марка", "Выбран " + selectedMark + ", его масса " + mass + " кг/м");
            } else {
                Log.e("Ошибка", "Не удалось найти массу для выбранной марки");
            }
            return true;
        });
        popupMenu.show();
    }

    private void calculateWeight() {
        try {
            // Получаем и проверяем значения
            String lengthAStr = editTextLengthA.getText().toString().trim();
            String lengthBStr = editTextLengthB.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (lengthAStr.isEmpty() || lengthBStr.isEmpty()) {
                totalWeight.setText("Заполните все поля!");
                return;
            }

            // Парсим значения
            double lengthA = Double.parseDouble(lengthAStr); // длина листа в миллиметрах
            double lengthB = Double.parseDouble(lengthBStr); // ширина листа в миллиметрах

            // Проверка положительных значений
            if (lengthA <= 0 || lengthB <= 0) {
                totalWeight.setText("Длина и ширина должны быть положительными!");
                return;
            }

            // Конвертация миллиметров в метры
            double lengthAMeters = lengthA / 1000.0; // длина листа в метрах
            double lengthBMeters = lengthB / 1000.0; // ширина листа в метрах

            // Получаем выбранный профиль
            String selectedMark = btnMark.getText().toString();
            int index = Arrays.asList(ListMark).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите толщину профиля!");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = ListMass1Kg[index];

            // Вес листа
            double weight = massPerMeter * lengthAMeters * lengthBMeters; // кг

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Масса листа: %.2f кг", weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    totalWeight.setText("Количество должно быть > 0");
                    return;
                }

                double totalWeightValue = weight * quantity; // общая масса
                resultText.append(String.format(Locale.US, "Масса одного листа: %.2f кг", weight));
                resultText.append(String.format(Locale.US, "\nОбщая масса: %.2f кг", totalWeightValue));

                // Если цена за кг указана, добавляем стоимость
                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    double totalCost = pricePerKg * totalWeightValue; // общая стоимость
                    double pricePerUnit = totalCost / quantity; // цена за один лист
                    // Форматирование стоимости с разделением тысяч
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    symbols.setGroupingSeparator(' '); // Устанавливаем пробел как разделитель тысяч
                    DecimalFormat costFormatter = new DecimalFormat("#,##0.00", symbols);

                    resultText.append(String.format(Locale.US, "\nСтоимость одного листа: %s руб", costFormatter.format(pricePerUnit)));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %s руб", costFormatter.format(totalCost)));
                }
            }
            Map<String, String> analytics = new HashMap<>();
            analytics.put("Тип", "Масса");
            analytics.put("Шаблон", "Лист Чечевичный ГОСТ 8568-77");
            NetworkHelper.sendCalculationData(this, analytics);
            // Выводим результат
            totalWeight.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText("Ошибка в формате чисел");
            Log.e("CalcError", "Parsing error: " + e.getMessage());
        }
    }
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
    public void btnGost8568_77_pdf(View view) {
        startActivity(new Intent(this, Gost8568_77_pdf.class));
        finish();
    }

}