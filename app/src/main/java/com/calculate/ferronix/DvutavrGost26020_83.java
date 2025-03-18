package com.calculate.ferronix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

public class DvutavrGost26020_83 extends AppCompatActivity {

    private static final String TAG = "DvutavrGost26020_83";

    private EditText editTextLength, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMark, btnGoWeight, btnGoLength;

    private String[] mark;

    // Инициализация массивов для ГОСТ 57837-2017
    private final String[] DvutavrNumber = {
            // Тип Б — Балочные нормальные двутавры
            "10Б1", "12Б1", "12Б2", "14Б1", "14Б2", "16Б1", "16Б2", "18Б1", "18Б2", "20Б1",
            "23Б1", "26Б1", "26Б2", "30Б1", "30Б2", "35Б1", "35Б2",
            "40Б1", "40Б2", "45Б1", "45Б2", "50Б1", "50Б2", "55Б1", "55Б2",
            "60Б1", "60Б2", "70Б1", "70Б2", "80Б1", "80Б2", "90Б1", "90Б2", "100Б1","100Б2","100Б3","100Б4",
            // Тип Ш — Балонные широкополочные двутавры
            "20Ш1", "23Ш1", "26Ш1", "26Ш2", "30Ш1", "30Ш2", "30Ш3",
            "35Ш1", "35Ш2", "35Ш3", "40Ш1", "40Ш2", "40Ш3", "50Ш1", "50Ш2", "50Ш3", "50Ш4", "60Ш1", "60Ш2","60Ш3","60Ш4",
            "70Ш1","70Ш2","70Ш3","70Ш4","70Ш5",
            // Тип К — Колонные двутавры
            "20К1", "20К2", "23К1", "23К2", "26К1", "26К2", "26К3", "30К1",
            "30К2", "30К3", "35К1", "35К2", "35К3", "40К1", "40К2", "40К3", "40К4", "40К5",
            // Тип ДБ — Дополнительные
            "24ДБ1", "27ДБ1", "36ДБ1", "35ДБ1", "40ДБ1", "45ДБ1", "45ДБ2",
            "30ДШ1", "40ДШ1", "50ДШ1",
    };
    private final double[] DvutavrMass1Meter = {
            // Тип Б — Балочные нормальные двутавры
            8.10, 8.70, 10.40, 10.50, 12.90, 12.70, 15.80, 15.40, 18.80, 22.4, 25.8, 28, 31.2, 32.9, 43.3,
            48.1, 54.7, 59.8, 67.5, 73, 80.7, 89, 97.9, 106.2, 115.6, 129.3, 144.2, 159.5, 177.9, 194.0, 213.8,
            230.6, 258.2, 285.7, 314.5,

            // Тип Ш — Балонные широкополочные двутавры
            30.6, 36.2, 42.7, 49.2, 53.6, 61, 68.3, 75.1, 82.2, 91.3, 96.1, 111.1, 123.4,
            114.4, 138.7, 156.4, 174.1, 142.1, 176.9, 205.5, 234.2, 169.9, 197.6, 235.4, 268.1, 305.9,

            // Тип К — Колонные двутавры
            41.5, 46.9, 52.2, 59.5, 65.2, 73.2, 83, 84.8, 96.3, 108.9, 109.7, 125.9, 144.5,
            138, 165.6, 202.3, 242.2, 291.2,

            // Тип Д — Дополнительные
            27.8, 31.9, 49.1, 33.6, 39.7, 52.6, 65, 72.7, 124, 155
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dvutavr_gost_26020_83_calculate);

        Log.d(TAG, "Метод onCreate начал выполнение");

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
            Log.e(TAG, "Один или несколько EditText полей равны null!");
            finish();
        }

        // Устанавливаем активную кнопку при запуске
        setActiveButton(btnGoWeight, btnGoLength);

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка расчета нажата");
            // Выполнение тактильной обратной связи
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            if (textViewLength.getText().toString().equals("Масса")) {
                calculateWeight();
            } else {
                calculateLength();
            }
        });

        btnMark.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка выбора марки нажата");
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMarkMenuClick();
        });

        btnGoWeight.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка переключения на расчет массы нажата");
            // Переключаем на расчет массы
            textViewLength.setText("Длина");
            textViewUnit.setText("м");
            editTextLength.setHint("Длина");

            // Устанавливаем активную кнопку
            setActiveButton(btnGoWeight, btnGoLength);
        });

        btnGoLength.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка переключения на расчет длины нажата");
            // Переключаем на расчет длины
            textViewLength.setText("Масса");
            textViewUnit.setText("кг");
            editTextLength.setHint("Масса");

            // Устанавливаем активную кнопку
            setActiveButton(btnGoLength, btnGoWeight);
        });

        Log.d(TAG, "Метод onCreate завершил выполнение");
    }

    @SuppressLint("ResourceAsColor")
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        Log.d(TAG, "Метод setActiveButton начал выполнение");
        // Устанавливаем цвет фона и текста для активной кнопки
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))); // Белый фон
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black)); // Черный текст

        // Убираем подсветку и устанавливаем цвет текста для неактивной кнопки
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))); // Прозрачный фон
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white)); // Белый текст
        Log.d(TAG, "Метод setActiveButton завершил выполнение");
    }

    private void showMarkMenuClick() {
        Log.d(TAG, "Метод showMarkMenuClick начал выполнение");
        // Создаем PopupWindow
        PopupWindow popupWindow = new PopupWindow(this);

        // Создаем LinearLayout как контейнер для PopupWindow
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Создаем EditText для поиска
        EditText editTextSearch = new EditText(this);
        editTextSearch.setHint("Поиск номера...");
        editTextSearch.setTextColor(Color.WHITE); // Используем Color.WHITE
        editTextSearch.setHintTextColor(Color.LTGRAY); // Серый цвет для подсказки
        editTextSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.addView(editTextSearch, params);

        // Создаем ListView для отображения марок
        ListView listViewMarks = new ListView(this);
        layout.addView(listViewMarks, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Устанавливаем макет для PopupWindow
        popupWindow.setContentView(layout);

        // Настраиваем размеры PopupWindow
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        // Создаем адаптер для ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mark);
        listViewMarks.setAdapter(adapter);

        // Добавляем фильтрацию при вводе текста
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Обработка выбора марки
        listViewMarks.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMark = adapter.getItem(position);
            btnMark.setText(selectedMark);

            // Находим индекс выбранной марки в массиве mark
            int index = Arrays.asList(mark).indexOf(selectedMark);
            if (index != -1 && index < DvutavrMass1Meter.length) {
                double mass = DvutavrMass1Meter[index]; // Получаем массу для выбранной марки
                Log.d(TAG, "Выбран " + selectedMark + ", его масса " + mass + " кг/м");
            } else {
                Log.e(TAG, "Не удалось найти массу для выбранной марки");
            }

            popupWindow.dismiss();
        });

        // Показываем PopupWindow
        popupWindow.showAsDropDown(btnMark);
        Log.d(TAG, "Метод showMarkMenuClick завершил выполнение");
    }

    private void calculateLength() {
        Log.d(TAG, "Метод calculateLength начал выполнение");
        try {
            // Получаем и проверяем значения
            String lengthStr = editTextLength.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            Log.d(TAG, "Длина: " + lengthStr + ", Цена за кг: " + pricePerKgStr + ", Количество: " + quantityStr);

            if (lengthStr.isEmpty()) {
                totalWeight.setText("Укажите длину!");
                Log.e(TAG, "Длина не указана");
                return;
            }

            // Парсим значения
            double length = Double.parseDouble(lengthStr); // длина двутавра в метрах

            // Проверка положительных значений
            if (length <= 0) {
                totalWeight.setText("Длина должна быть положительной!");
                Log.e(TAG, "Длина должна быть положительной");
                return;
            }

            // Получаем выбранный профиль
            String selectedMark = btnMark.getText().toString();
            int index = Arrays.asList(DvutavrNumber).indexOf(selectedMark);

            if (index == -1) {
                totalWeight.setText("Выберите номер двутавра!");
                Log.e(TAG, "Номер двутавра не выбран");
                return;
            }

            // Масса 1 метра длины для выбранного профиля
            double massPerMeter = DvutavrMass1Meter[index];

            // Вес двутавра
            double weight = massPerMeter * length; // кг

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Масса двутавра: %.2f кг", weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    totalWeight.setText("Количество должно быть > 0");
                    Log.e(TAG, "Количество должно быть больше 0");
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

            // Выводим результат
            totalWeight.setText(resultText.toString());
            Log.d(TAG, "Результат расчета длины: " + resultText);

        } catch (NumberFormatException e) {
            totalWeight.setText("Ошибка в формате чисел");
            Log.e(TAG, "Ошибка в формате чисел: " + e.getMessage());
        }
        Log.d(TAG, "Метод calculateLength завершил выполнение");
    }

    private void calculateWeight() {
        Log.d(TAG, "Метод calculateWeight начал выполнение");
        try {
            // Получаем и проверяем значения
            String massStr = editTextLength.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            Log.d(TAG, "Масса: " + massStr + ", Цена за кг: " + pricePerKgStr + ", Количество: " + quantityStr);

            if (massStr.isEmpty()) {
                totalWeight.setText("Укажите массу!");
                return;
            }

            // Парсим значения
            double mass = Double.parseDouble(massStr); // масса двутавра в кг

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

            // Длина двутавра
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