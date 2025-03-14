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

public class ShvellerGost8240_97 extends AppCompatActivity {

    private EditText editTextLength, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMark, btnGoWeight, btnGoLength;

    private String[] mark;
    // Инициализация массивов для ГОСТ 8240-97
    private final String[] ShvellerNumber = {
            "5У","6.5У","8У","10У","12У","14У","16У", "16аУ", "20У", "22У","24У","27У","30У","33У","36У","40У", // Швеллеры с уклоном внутренних граней полок
            "5П","6.5П","8П","10П","12П","14П","16П", "16аП", "18П", "18аП","20П","22П","24П","27П","30П","33П","36П","40П", // Швеллеры с параллельными гранями полок
            "5Э","6.5Э","8Э","10Э","12Э","14Э","16Э", "18Э", "20Э","22Э","24Э","27Э","30Э","33Э","36Э","40Э", // Швеллеры экономичные с параллельными гранями полок
            "12Л","14Л","16Л","18Л","20Л","22Л","24Л", "27Л", "30Л", // Швеллеры легкой серии с параллельными гранями полок
            "8С","14С","14Са","16С","16Са","18С","18Са", "18Сб", "20С", "20Са", "20Сб", "24С", "26Са", "26Са", "30С", "30Са", "30Сб", // Швеллеры специальные


    };

    private final double[] ShvellerMass1Meter = {
            4.84, 5.90, 7.05, 8.59, 10.4 , 12.30, 14.20, 15.30, 16.30, 17.40, 18.40,  22,20, 23.40, 26.70, 30.60, 35.20, 40.50, 46.50, 53.40, 61.50,// У
            4.84, 5.90, 7.05, 8.59, 10.4 , 12.30, 14.20, 15.30, 16.30, 17.40, 18.40, 21, 24, 27, 31.8, 36.5, 41.9, 48.30,// П
            4.79, 5.82, 6.92, 8.47, 10.24 , 12.15, 14.01, 16.01, 18.07, 20.69, 23.69, 27.37, 31.35, 36.14, 41.53, 47.97,// Э
            5.02, 5.94, 7.1, 8.49, 10.12 , 11.86, 13.66, 16.30, 19.07, // Л
            9.26, 14.53, 16.27, 17.53, 19.74 , 20.20, 23, 26.72, 22.63, 25.77, 28.71, 34.9, 34.61, 39.72, 34.44, 39.15, 43.86// С


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shveller_gost_8240_97_calculate);

        // Инициализация массива mark
        mark = ShvellerNumber.clone(); // Используем клон массива ShvellerNumber

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
            if (index != -1 && index < ShvellerMass1Meter.length) {
                double mass = ShvellerMass1Meter[index]; // Получаем массу для выбранной марки
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
            int index = Arrays.asList(ShvellerNumber).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите марку профиля!");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = ShvellerMass1Meter[index];

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
            int index = Arrays.asList(ShvellerNumber).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите марку профиля!");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = ShvellerMass1Meter[index];

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