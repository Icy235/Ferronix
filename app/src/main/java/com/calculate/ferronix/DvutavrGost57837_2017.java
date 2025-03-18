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

public class DvutavrGost57837_2017 extends AppCompatActivity {

    private EditText editTextLength, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMark, btnGoWeight, btnGoLength;

    private String[] mark;

    // Инициализация массивов для ГОСТ 57837-2017
    private final String[] DvutavrNumber = {
            // Тип Б — Балочные нормальные двутавры
            "10Б1", "12Б1", "12Б2", "14Б1", "14Б2", "16Б1", "16Б2", "18Б1", "18Б2", "20Б0", "20Б1", "20Б2", "20Б3",
            "25Б1", "25Б2", "25Б3", "25Б4", "30Б1", "30Б2", "30Б3", "35Б1", "35Б2", "35Б3", "35Б4",
            "40Б1", "40Б2", "40Б3", "40Б4", "45Б1", "45Б2", "45Б3", "45Б4", "50Б1", "50Б2", "50Б3", "50Б4", "50Б5",
            "55Б1", "55Б2", "55Б3", "55Б4", "60Б1", "60Б2", "60Б3", "60Б4", "70Б1", "70Б2", "70Б3", "70Б4",
            // Тип Ш — Балонные широкополочные двутавры
            "20Ш1", "20Ш2", "20Ш3", "20Ш4", "20Ш5", "20Ш6", "20Ш7", "24Ш1", "24Ш2", "24Ш3", "24Ш4", "24Ш5", "24Ш6",
            "24Ш7", "24Ш8", "24Ш9", "24Ш10", "24Ш11", "24Ш12", "24Ш13", "24Ш14", "24Ш15", "24Ш16", "24Ш17", "24Ш18",
            // Тип К — Колонные двутавры
            "15К1", "15К2", "15К3", "15К4", "15К5", "20К1", "20К2", "20К3", "20К4", "20К5", "20К6", "20К7", "20К8",
            "25К1", "25К2", "25К3", "25К4", "25К5", "25К6", "25К7", "25К8", "25К9", "25К10", "30К1", "30К2", "30К3",
            "30К4", "30К5", "30К6", "30К7", "30К8", "30К9", "30К10", "30К11", "30К12", "30К13", "30К14", "30К15",
            "30К16", "30К17", "30К18", "30К19", "30К20", "30К21", "35К1", "35К2", "35К3", "35К4", "35К5", "35К6",
            "35К7", "35К8", "35К9", "35К10", "35К11", "35К12", "35К13", "35К14", "35К15", "35К16", "35К17", "35К18",
            "35К19", "35К20", "35К21", "35К22", "35К23", "35К24", "35К25", "40К1", "40К2", "40К3", "40К4", "40К5",
            "40К6", "40К7", "40К8", "40К9", "40К10", "40К11", "40К12", "40К13", "40К14", "40К15", "40К16", "40К17",
            "40К18", "40К19",
            // Тип С — Свайные двутавры
            "13С1", "20С1", "25С1", "25С2", "30С1", "30С2", "32С1", "32С2", "35С1", "35С2", "35С3", "40С1", "40С2", "40С3",
            // Тип ДБ — Дополнительные балочные двутавры
            "20ДБ1", "20ДБ2", "25ДБ1", "25ДБ2", "25ДБ3", "25ДБ4", "25ДБ5", "25ДБ6", "30ДБ1", "30ДБ2", "30ДБ3", "30ДБ4",
            "30ДБ5", "30ДБ6", "30ДБ7", "30ДБ8", "35ДБ1", "35ДБ2", "35ДБ3", "35ДБ4", "35ДБ5", "35ДБ6", "35ДБ7", "35ДБ8",
            "35ДБ9", "35ДБ10", "40ДБ1", "40ДБ2", "40ДБ3", "40ДБ4", "40ДБ5", "40ДБ6", "40ДБ7", "45ДБ1", "45ДБ2", "45ДБ3",
            "45ДБ4", "45ДБ5", "45ДБ6", "45ДБ7", "45ДБ8", "45ДБ9", "45ДБ10", "45ДБ11", "53ДБ3", "53ДБ4", "53ДБ5", "53ДБ6",
            "53ДБ7", "60ДБ1", "60ДБ2", "60ДБ3", "60ДБ4", "60ДБ5", "60ДБ6",

            // Тип ДК — Дополнительные колонные двутавры
            "10ДК1", "10ДК2", "10ДК3", "12ДК1", "12ДК2", "12ДК3", "14ДК1", "14ДК2", "14ДК3", "15ДК1", "15ДК2", "15ДК3",
            "16ДК1", "16ДК2", "16ДК3", "18ДК1", "18ДК2", "18ДК3", "20ДК1", "20ДК2", "20ДК3", "20ДК4", "20ДК5", "20ДК6",
            "25ДК1", "25ДК2", "25ДК3", "25ДК4"
    };
    private final double[] DvutavrMass1Meter = {
            // Тип Б — Балочные нормальные двутавры
            8.10, 8.70, 10.40, 10.50, 12.90, 12.70, 15.80, 15.40, 18.80, 18.20, 21.30, 25.30, 31.60,
            25.70, 29.60, 37.40, 45.30, 32.00, 36.70, 46.10, 55.60, 41.40, 49.60, 60.50, 72.90,
            56.60, 68.00, 80.10, 94.30, 66.20, 78.00, 90.60, 105.40, 72.50, 79.50, 89.70, 109.90, 133.90,
            89.00, 97.90, 116.70, 137.30, 94.50, 105.50, 118.80, 142.90, 129.30, 144.16, 165.10, 194.80,

            // Тип Ш — Балонные широкополочные двутавры
            24.40, 30.60, 38.80, 47.00, 58.90, 70.90, 86.20, 36.80, 44.20, 53.80, 67.30, 84.40, 104.70,
            128.30, 163.30, 179.90, 223.60, 289.70, 298.70, 347.60, 450.60, 518.30, 681.80, 751.20, 829.70,

            // Тип К — Колонные двутавры
            26.80, 31.50, 39.10, 46.80, 56.30, 41.40, 49.90, 57.80, 69.30, 69.30, 90.30, 102.90, 118.40,
            82.60, 72.40, 80.20, 90.10, 103.00, 115.50, 134.90, 153.10, 171.30, 197.50, 87.00, 94.00, 105.80,
            105.80, 117.40, 129.30, 142.00, 161.80, 182.20, 203.80, 225.40, 250.00, 280.40, 309.90, 340.30,
            376.60, 413.20, 455.80, 510.40, 563.60, 631.50, 109.10, 122.80, 136.50, 154.20, 172.10, 190.00,
            207.90, 233.10, 255.50, 280.90, 307.60, 343.80, 381.30, 419.00, 456.80, 503.20, 564.60, 618.80,
            681.80, 751.20, 829.70, 912.20, 1001.80, 1104.70, 1213.70, 146.70, 171.70, 200.10, 231.90, 255.60,
            290.80, 305.90, 344.10, 382.40, 429.60, 482.80, 542.60, 607.10, 678.00, 768.20, 848.90, 949.50,
            1096.60, 1331.60,

            // Тип С — Свайные двутавры
            24.74, 56.20, 64.40, 82.20, 84.50, 105.80, 180.00, 222.90, 106.20, 130.80, 155.70, 140.10, 168.30, 196.80,

            // Тип ДБ — Дополнительные балочные двутавры
            26.60, 31.40, 31.10, 37.00, 43.00, 32.70, 38.70, 44.90, 28.40, 32.80, 38.90, 44.80, 52.50, 40.30,
            46.20, 54.00, 32.80, 39.10, 45.00, 50.60, 56.70, 67.10, 91.00, 101.40, 110.40, 121.90, 39.20, 46.20,
            53.40, 56.50, 67.50, 74.90, 85.00, 52.00, 59.60, 68.50, 74.20, 82.10, 67.10, 74.20, 81.90, 89.30,
            96.60, 105.60, 92.50, 101.40, 109.00, 123.20, 138.30, 81.90, 92.30, 101.70, 113.40, 125.10, 140.10,

            // Тип ДК — Дополнительные колонные двутавры
            16.70, 20.40, 41.80, 19.90, 28.70, 52.10, 24.70, 33.70, 62.90, 22.50, 29.90, 37.30, 30.40, 42.60,
            76.20, 35.50, 51.00, 88.90, 46.00, 52.30, 59.40, 71.50, 86.80, 99.50, 72.90, 80.10, 89.60, 101.20
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dvutavr_gost_57837_2017_calculate);

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
                Log.d("Выбранная марка", "Выбран " + selectedMark + ", его масса " + mass + " кг/м");
            } else {
                Log.e("Ошибка", "Не удалось найти массу для выбранной марки");
            }

            popupWindow.dismiss();
        });

        // Показываем PopupWindow
        popupWindow.showAsDropDown(btnMark);
    }
    private void calculateLength() {
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
            double length = Double.parseDouble(lengthStr); // длина двутавра в метрах

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