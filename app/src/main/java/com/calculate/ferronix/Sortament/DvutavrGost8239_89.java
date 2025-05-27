package com.calculate.ferronix.Sortament;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.calculate.ferronix.R;
import com.calculate.ferronix.Sortament.gostPdf.Gost8239_89_pdf;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Control.NetworkHelper;

public class DvutavrGost8239_89 extends AppCompatActivity {

    private EditText editTextLength, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMark, btnGoWeight, btnGoLength;

    private String[] mark;

    // Инициализация массивов для ГОСТ 8239-89
    private final String[] DvutavrNumber = {

            // Тип ДК — Дополнительные колонные двутавры
            "10", "12", "14", "16", "18", "20", "22", "24", "27", "30", "33", "36",
            "40", "45", "50", "55", "60",
    };
    private final double[] DvutavrMass1Meter = {

            // Тип ДК — Дополнительные колонные двутавры
            9.46, 11.50, 13.70, 15.90, 18.40, 21, 24, 27.30, 31.5, 36.5, 42.20, 48.60, 57, 66.5,
            78.50, 92.60, 108
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dvutavr_gost_8239_89_calculate);

        // Инициализация массива mark
        mark = DvutavrNumber.clone(); // Используем клон массива DvutavrNumber

        // Инициализация элементов интерфейса
        editTextLength = findViewById(R.id.editTextLength);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        textViewLength = findViewById(R.id.textViewLength);
        textViewUnit = findViewById(R.id.textViewUnit);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);

        // Проверка на null
        if (editTextLength == null || editTextPricePerKg == null || editTextQuantity == null) {
            Log.e("InitError", "One or more EditText fields are null!");
            finish();
        }

        // Устанавливаем активную кнопку при запуске
        setActiveButton(btnGoWeight, btnGoLength);

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> {
            // Выполнение тактильной обратной связи
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            if (textViewLength.getText().toString().equals("Масса")) {
                calculateWeight();
            } else {
                calculateLength();
            }
        });

        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMarkMenuClick();
        });

        btnGoWeight.setOnClickListener(v -> {
            // Переключаем на расчет массы
            textViewLength.setText("Длина");
            textViewUnit.setText("м");
            editTextLength.setHint("Длина");

            // Устанавливаем активную кнопку
            setActiveButton(btnGoWeight, btnGoLength);
        });

        btnGoLength.setOnClickListener(v -> {
            // Переключаем на расчет длины
            textViewLength.setText("Масса");
            textViewUnit.setText("кг");
            editTextLength.setHint("Масса");

            // Устанавливаем активную кнопку
            setActiveButton(btnGoLength, btnGoWeight);
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        // Устанавливаем цвет фона и текста для активной кнопки
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))); // Белый фон
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black)); // Черный текст

        // Убираем подсветку и устанавливаем цвет текста для неактивной кнопки
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))); // Прозрачный фон
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white)); // Белый текст
    }

    private void showMarkMenuClick() {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        for (String material : mark) {
            popupMenu.getMenu().add(material);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedMark = Objects.requireNonNull(item.getTitle()).toString();
            btnMark.setText(selectedMark);

            // Находим индекс выбранной марки в массиве mark
            int index = Arrays.asList(mark).indexOf(selectedMark);
            if (index != -1 && index < DvutavrMass1Meter.length) {
                double mass = DvutavrMass1Meter[index];
                Log.d("Выбранный номер", "Выбран " + selectedMark + ", его масса " + mass + " кг/м");
            } else {
                Log.e("Ошибка", "Не удалось найти массу для выбранной марки");
            }
            return true;
        });
        popupMenu.show();
    }    private void calculateLength() {
        try {
            // Получаем и проверяем значения
            String lengthStr = editTextLength.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (lengthStr.isEmpty()) {
                totalWeight.setText("Укажите длину!");
                return;
            }

            // Парсим значения
            double length = Double.parseDouble(lengthStr); // длина листа в метрах

            // Проверка положительных значений
            if (length <= 0) {
                totalWeight.setText("Длина должна быть положительной!");
                return;
            }

            // Получаем выбранный профиль
            String selectedMark = btnMark.getText().toString();
            int index = Arrays.asList(DvutavrNumber).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите номер двутавра!");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = DvutavrMass1Meter[index];

            // Вес листа
            double weight = massPerMeter * length; // кг

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Масса еденицы: %.2f кг", weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    totalWeight.setText("Количество должно быть > 0");
                    return;
                }

                double totalWeightValue = weight * quantity; // общая масса
                resultText.append(String.format(Locale.US, "Масса одной еденицы: %.2f кг", weight));
                resultText.append(String.format(Locale.US, "\nОбщая масса: %.2f кг", totalWeightValue));

                // Если цена за кг указана, добавляем стоимость
                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    double totalCost = pricePerKg * totalWeightValue; // общая стоимость
                    double pricePerUnit = totalCost / quantity; // цена за один двутавр
                    // Форматирование стоимости с разделением тысяч
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    symbols.setGroupingSeparator(' '); // Устанавливаем пробел как разделитель тысяч
                    DecimalFormat costFormatter = new DecimalFormat("#,##0.00", symbols);

                    resultText.append(String.format(Locale.US, "\nСтоимость одной еденицы: %s руб", costFormatter.format(pricePerUnit)));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %s руб", costFormatter.format(totalCost)));
                }
            }
            Map<String, String> analytics = new HashMap<>();
            analytics.put("Тип", "Длина");
            analytics.put("Шаблон", "Двутавр ГОСТ 8239-89");
            NetworkHelper.sendCalculationData(this, analytics);
            // Выводим результат
            totalWeight.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText("Ошибка в формате чисел");
            Log.e("CalcError", "Parsing error: " + e.getMessage());
        }
    }

    private void calculateWeight() {
        try {
            // Получаем и проверяем значения
            String massStr = editTextLength.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (massStr.isEmpty()) {
                totalWeight.setText("Укажите массу!");
                return;
            }

            // Парсим значения
            double mass = Double.parseDouble(massStr); // масса листа в кг

            // Проверка положительных значений
            if (mass <= 0) {
                totalWeight.setText("Масса должна быть положительной!");
                return;
            }

            // Получаем выбранный профиль
            String selectedMark = btnMark.getText().toString();
            int index = Arrays.asList(DvutavrNumber).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите номер двутавра!");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = DvutavrMass1Meter[index];

            // Длина листа
            double length = mass / massPerMeter; // м

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Длина еденицы: %.2f м", length));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    totalWeight.setText("Количество должно быть > 0");
                    return;
                }

                double totalLengthValue = length * quantity; // общая длина
                resultText.append(String.format(Locale.US, "Длина одной еденицы: %.2f м", length));
                resultText.append(String.format(Locale.US, "\nОбщая длина: %.2f м", totalLengthValue));

                // Если цена за кг указана, добавляем стоимость
                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    double totalCost = pricePerKg * mass * quantity; // общая стоимость
                    double pricePerUnit = totalCost / quantity; // цена за один метр
                    // Форматирование стоимости с разделением тысяч
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    symbols.setGroupingSeparator(' '); // Устанавливаем пробел как разделитель тысяч
                    DecimalFormat costFormatter = new DecimalFormat("#,##0.00", symbols);

                    resultText.append(String.format(Locale.US, "\nСтоимость одной еденицы: %s руб", costFormatter.format(pricePerUnit)));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %s руб", costFormatter.format(totalCost)));
                }
            }
            Map<String, String> analytics = new HashMap<>();
            analytics.put("Тип", "Масса");
            analytics.put("Шаблон", "Двутавр ГОСТ 8239-89");
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

    public void btnGost8239_89_pdf(View view) {
        startActivity(new Intent(this, Gost8239_89_pdf.class));
        finish();
    }
}