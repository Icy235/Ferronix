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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class SquareCalculate extends AppCompatActivity {

    private EditText editTextDensity, editTextLength, editTextSquareA, editTextPricePerKg, editTextQuantity;
    private TextView total, textViewLength, textViewUnit;
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

    private String[] materials;

    // Массивы для материалов
    private final String[] aluminumGrades = {
            "А5", "АД", "АД1", "АК4", "АК6", "АМг", "АМц", "В95", "Д1", "Д16"
    };
    private final double[] aluminumDensities = {
            2.70, 2.70, 2.70, 2.68, 2.68, 1.74, 2.55, 2.60, 2.70, 2.80
    };

    private final String[] stainlessSteelGrades = {
            "08Х17Т", "20Х13", "30Х13", "40Х13", "08Х18Н10", "12Х18Н10Т", "10Х17Н13М2Т", "06ХН28МДТ", "20Х23Н18"
    };
    private final double[] stainlessSteelDensities = {
            7.70, 7.75, 7.75, 7.75, 7.90, 7.90, 7.90, 7.95, 7.95
    };

    private final String[] blackMetalGrades = {
            "Сталь 3", "Сталь 10", "Сталь 20", "Сталь 40Х", "Сталь 45", "Сталь 65", "Сталь 65Г",
            "09Г2С", "15Х5М", "10ХСНД", "12Х1МФ", "ШХ15", "Р6М5", "У7", "У8", "У8А", "У10", "У10А", "У12А"
    };
    private final double[] blackMetalDensities = {
            7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85,
            7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85
    };

    // Константы для преобразования единиц
    private static final double MM_TO_CM = 0.1;
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_square_weight_calculate);

        // Инициализация массива materials
        materials = new String[]{"Черный металл", "Нержавейка", "Алюминий"};

        // Инициализация элементов интерфейса
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLength);
        editTextSquareA = findViewById(R.id.editTextSquareA);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        total = findViewById(R.id.textViewTotal);
        textViewLength = findViewById(R.id.textViewLength);
        textViewUnit = findViewById(R.id.textViewUnit);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);

        // Проверка на null
        if (editTextDensity == null || editTextLength == null || editTextSquareA == null) {
            Log.e("InitError", "One or more EditText fields are null!");
            finish();
        }

        // Устанавливаем активную кнопку при запуске
        setActiveButton(btnGoWeight, btnGoLength);

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            if (textViewLength.getText().toString().equals("Масса")) {
                calculateWeight();
            } else {
                calculateLength();
            }
        });

        btnMaterial.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMaterialMenu();
        });

        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            handleMarkButtonClick();
        });

        btnGoWeight.setOnClickListener(v -> {
            // Переключаем на расчет массы
            textViewLength.setText("Длина");
            textViewUnit.setText("мм");
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

    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
        if (material.equals(getString(R.string.material))) {
            Toast.makeText(this, "Сначала выберите материал", Toast.LENGTH_SHORT).show();
        } else {
            showGradeMenu(material);
        }
    }

    private void showMaterialMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnMaterial);
        for (String material : materials) {
            popupMenu.getMenu().add(material);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            btnMaterial.setText(item.getTitle());
            btnMark.setText(R.string.mark);
            editTextDensity.setText(""); // Сброс плотности при смене материала
            return true;
        });
        popupMenu.show();
    }

    private void showGradeMenu(String material) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

        switch (material) {
            case "Черный металл":
                grades = blackMetalGrades;
                densities = blackMetalDensities;
                break;
            case "Нержавейка":
                grades = stainlessSteelGrades;
                densities = stainlessSteelDensities;
                break;
            case "Алюминий":
                grades = aluminumGrades;
                densities = aluminumDensities;
                break;
            default:
                return;
        }

        for (String grade : grades) {
            popupMenu.getMenu().add(grade);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String grade = Objects.requireNonNull(item.getTitle()).toString();
            btnMark.setText(grade);

            int index = Arrays.asList(grades).indexOf(grade);
            if (index != -1 && index < densities.length) {
                String formattedDensity = String.format(Locale.US, "%.2f", densities[index]);
                editTextDensity.setText(formattedDensity);
            } else {
                Log.e("DensityError", "Invalid index for density array");
            }
            return true;
        });
        popupMenu.show();
    }

    private void calculateLength() {
        try {
            // Получаем и проверяем значения
            String densityStr = editTextDensity.getText().toString().trim();
            String lengthStr = editTextLength.getText().toString().trim();
            String squareAStr = editTextSquareA.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (densityStr.isEmpty() || lengthStr.isEmpty() || squareAStr.isEmpty()) {
                total.setText("Заполните все поля!");
                return;
            }

            // Парсим значения
            double density = Double.parseDouble(densityStr); // г/см³
            double length = Double.parseDouble(lengthStr); // мм
            double squareA = Double.parseDouble(squareAStr); // мм

            // Проверка положительных значений
            if (density <= 0 || length <= 0 || squareA <= 0) {
                total.setText("Значения должны быть > 0");
                return;
            }

            // Конвертация единиц
            double squareACm = squareA * MM_TO_CM; // мм -> см
            double lengthCm = length * MM_TO_CM; // мм -> см
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // г/см³ -> кг/см³

            // Площадь поперечного сечения
            double area = squareACm * squareACm; // площадь в см²

            // Объем материала
            double volume = area * lengthCm; // объем в см³

            // Вес профиля
            double weight = volume * densityKgPerCm3; // вес в кг

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Масса: %.2f кг", weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    total.setText("Количество должно быть > 0");
                    return;
                }
                double totalValue = weight * quantity; // общая масса

                resultText.append(String.format(Locale.US, "Масса единицы: %.2f кг", weight));
                resultText.append(String.format(Locale.US, "\nОбщая масса: %.2f кг", totalValue));

                // Если цена за кг указана, добавляем стоимость
                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    double totalCost = pricePerKg * totalValue; // общая стоимость
                    double pricePerUnit = totalCost / quantity; // цена за одну штуку

                    resultText.append(String.format(Locale.US, "\nСтоимость единицы: %.2f руб", pricePerUnit));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %.2f руб", totalCost));
                }
            }

            // Выводим результат
            total.setText(resultText.toString());

        } catch (NumberFormatException e) {
            total.setText("Ошибка в формате чисел");
            Log.e("CalcError", "Parsing error: " + e.getMessage());
        }
    }

    private void calculateWeight() {
        try {
            // Получаем и проверяем значения
            String densityStr = editTextDensity.getText().toString().trim();
            String weightStr = editTextLength.getText().toString().trim();
            String squareAStr = editTextSquareA.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (densityStr.isEmpty() || weightStr.isEmpty() || squareAStr.isEmpty()) {
                total.setText("Заполните все поля!");
                return;
            }

            // Парсим значения
            double density = Double.parseDouble(densityStr); // г/см³
            double weight = Double.parseDouble(weightStr); // кг
            double squareA = Double.parseDouble(squareAStr); // мм

            // Проверка положительных значений
            if (density <= 0 || weight <= 0 || squareA <= 0) {
                total.setText("Значения должны быть > 0");
                return;
            }

            // Конвертация единиц
            double squareACm = squareA * MM_TO_CM; // мм -> см
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // г/см³ -> кг/см³

            // Площадь поперечного сечения
            double area = squareACm * squareACm; // площадь в см²

            // Длина профиля
            double length = weight / (area * densityKgPerCm3); // см

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Длина: %.2f м", length / 100)); // Переводим см в метры
            } else {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    total.setText("Количество должно быть > 0");
                    return;
                }
                double totalLengthValue = length * quantity / 100; // общая длина в метрах

                resultText.append(String.format(Locale.US, "Длина единицы: %.2f м", length / 100));
                resultText.append(String.format(Locale.US, "\nОбщая длина: %.2f м", totalLengthValue));

                // Если цена за кг указана, добавляем стоимость
                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    double totalCost = pricePerKg * weight * quantity; // общая стоимость
                    double pricePerUnit = totalCost / quantity; // цена за одну штуку

                    resultText.append(String.format(Locale.US, "\nСтоимость единицы: %.2f руб", pricePerUnit));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %.2f руб", totalCost));
                }
            }

            // Выводим результат
            total.setText(resultText.toString());

        } catch (NumberFormatException e) {
            total.setText("Ошибка в формате чисел");
            Log.e("CalcError", "Parsing error: " + e.getMessage());
        }
    }

    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }


}