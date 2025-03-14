package com.calculate.ferronix;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class ListGost24045_94 extends AppCompatActivity {

    private EditText editTextLength, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMark, btnGoWeight, btnGoLength;

    private String[] mark;
    // Инициализация массивов для ГОСТ 24045-94
    private final String[] ListMark = {
            "H57-750-0,6", "H57-750-0,7", "H57-750-0,8", "H60-845-0,7", "H60-845-0,8", "H60-845-0,9", "H75-750-0,7", "H75-750-0,8", "H75-750-0,9",
            "H114-600-0,8", "H114-600-0,9", "H114-600-1,0", "H114-750-0,8", "H114-750-0,9", "H114-750-1,0",
            "HC35-1000-0,6", "HC35-1000-0,7", "HC35-1000-0,8", "HC44-1000-0,7", "HC44-1000-0,8",
            "C10-899-0,6", "C10-899-0,7", "C10-1000-0,6", "C10-1000-0,7", "C18-1000-0,6", "C18-1000-0,7",
            "C15-800-0,6", "C15-800-0,7", "C15-1000-0,6", "C15-1000-0,7", "C21-1000-0,6", "C21-1000-0,7", "C44-1000-0,7"
    };

    private final double[] ListMass1Meter = {
            7.5, 8.7, 9.8, 8.8, 9.9, 11.1, 9.8, 11.2, 12.5, // H57, H60, H75
            14, 15.6, 17.2, 12.5, 14, 15.4, // H114
            6.4, 7.4, 8.4, 8.3, 9.4, // HC35, HC44
            5.7, 6.6, 5.6, 6.5, 6.4, 7.4, // C10, C18
            6, 6.9, 6.4, 7.4, 6.4, 7.4, 7.4 // C15, C21, C44
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_gost_24045_94_calculate);

        // Инициализация массива mark
        mark = ListMark.clone(); // Используем клон массива ListMark

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
            String selectedMark = Objects.requireNonNull(item.getTitle()).toString(); // Получаем выбранную марку
            btnMark.setText(selectedMark); // Устанавливаем текст кнопки на выбранную марку

            // Находим индекс выбранной марки в массиве mark
            int index = Arrays.asList(mark).indexOf(selectedMark);
            if (index != -1 && index < ListMass1Meter.length) {
                double mass = ListMass1Meter[index]; // Получаем массу для выбранной марки
                Log.d("Выбранная марка", "Выбран " + selectedMark + ", его масса " + mass + " кг/м");
            } else {
                Log.e("Ошибка", "Не удалось найти массу для выбранной марки");
            }
            return true;
        });
        popupMenu.show();
    }

    private void calculateLength() {
        try {
            // Получаем и проверяем значения
            String lengthStr = editTextLength.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (lengthStr.isEmpty()) {
                totalWeight.setText("Укажите длину листа!");
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
            int index = Arrays.asList(ListMark).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите марку профиля!");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = ListMass1Meter[index];

            // Вес листа
            double weight = massPerMeter * length; // кг

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
                totalWeight.setText("Укажите массу листа!");
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
            int index = Arrays.asList(ListMark).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите марку профиля!");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = ListMass1Meter[index];

            // Длина листа
            double length = mass / massPerMeter; // м

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Длина листа: %.2f м", length));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    totalWeight.setText("Количество должно быть > 0");
                    return;
                }

                double totalLengthValue = length * quantity; // общая длина
                resultText.append(String.format(Locale.US, "Длина одного листа: %.2f м", length));
                resultText.append(String.format(Locale.US, "\nОбщая длина: %.2f м", totalLengthValue));

                // Если цена за кг указана, добавляем стоимость
                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    double totalCost = pricePerKg * mass * quantity; // общая стоимость
                    double pricePerUnit = totalCost / quantity; // цена за один лист
                    // Форматирование стоимости с разделением тысяч
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    symbols.setGroupingSeparator(' '); // Устанавливаем пробел как разделитель тысяч
                    DecimalFormat costFormatter = new DecimalFormat("#,##0.00", symbols);

                    resultText.append(String.format(Locale.US, "\nСтоимость одного листа: %s руб", costFormatter.format(pricePerUnit)));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %s руб", costFormatter.format(totalCost)));
                }
            }

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
}