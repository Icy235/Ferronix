package com.calculate.ferronix;

import android.content.Intent;
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

import java.util.Arrays;
import java.util.Locale;

public class CirclePipeCalculateLength extends AppCompatActivity {

    private EditText editTextDensity, editTextMass, editTextDiametr, editTextWall, editTextPricePerKg, editTextQuantity;
    private TextView totalLength;
    private Button btnMaterial, btnMark;

    private String[] materials;
    private final String[] aluminumGrades = {"А5", "АД", "АД1", "АК4", "АК6", "АМг", "АМц", "В95", "Д1", "Д16"};
    private final double[] aluminumDensities = {2.70, 2.70, 2.70, 2.68, 2.68, 1.74, 2.55, 2.60, 2.70, 2.80};

    private final String[] stainlessSteelGrades = {"08Х17Т", "20Х13", "30Х13", "40Х13", "08Х18Н10", "12Х18Н10Т", "10Х17Н13М2Т", "06ХН28МДТ", "20Х23Н18"};
    private final double[] stainlessSteelDensities = {7.70, 7.75, 7.75, 7.75, 7.90, 7.90, 7.90, 7.95, 7.95};

    private final String[] blackMetalGrades = {
            "Сталь 3", "Сталь 10", "Сталь 20", "Сталь 40Х", "Сталь 45", "Сталь 65", "Сталь 65Г",
            "09Г2С", "15Х5М", "10ХСНД", "12Х1МФ", "ШХ15", "Р6М5", "У7", "У8", "У8А", "У10", "У10А", "У12А"
    };
    private final double[] blackMetalDensities = {7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85,
            7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85};

    private static final double MM_TO_CM = 0.1;
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_circle_pipe_lenght_calculate);

        materials = new String[]{"Черный металл", "Нержавеющая сталь", "Алюминий"};

        editTextDensity = findViewById(R.id.editTextDensity);
        editTextMass = findViewById(R.id.editTextMassCirclePipeL);
        editTextDiametr = findViewById(R.id.editTextDiametrCirclePipeL);
        editTextWall = findViewById(R.id.editTextWallCirclePipeL);
        totalLength = findViewById(R.id.textViewWeightTotal);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg); // Поле для цены за кг
        editTextQuantity = findViewById(R.id.editTextQuantity); // Поле для количества
        Button btnCalculate = findViewById(R.id.btnCalculateCirclePipeL);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);

        if (editTextDensity == null || editTextMass == null || editTextDiametr == null || editTextWall == null) {
            Log.e("InitError", "One or more EditText fields are null!");
            finish();
        }

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> {
            // Выполнение тактильной обратной связи
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            calculateLength();
        });
        btnMaterial.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMaterialMenu();
        });
        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            handleMarkButtonClick();
        });
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
            case "Нержавеющая сталь":
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
            String grade = item.getTitle().toString();
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
            String massStr = editTextMass.getText().toString().trim();
            String diametrStr = editTextDiametr.getText().toString().trim();
            String wallStr = editTextWall.getText().toString().trim(); // Стенка
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (densityStr.isEmpty() || massStr.isEmpty() || diametrStr.isEmpty() || wallStr.isEmpty()) {
                totalLength.setText("Заполните все поля!");
                return;
            }

            // Парсим значения
            double density = Double.parseDouble(densityStr); // г/см³
            double mass = Double.parseDouble(massStr); // кг
            double diametr = Double.parseDouble(diametrStr); // мм
            double wall = Double.parseDouble(wallStr); // мм

            // Проверка положительных значений
            if (density <= 0 || mass <= 0 || diametr <= 0 || wall < 0) {
                totalLength.setText("Значения должны быть > 0");
                return;
            }

            // Конвертация единиц
            double diametrCm = diametr * MM_TO_CM; // мм -> см
            double wallCm = wall * MM_TO_CM; // мм -> см
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // г/см³ -> кг/см³

            // Площадь поперечного сечения трубы
            double outerArea = Math.PI * Math.pow(diametrCm / 2, 2); // см²
            double innerDiametrCm = diametrCm - 2 * wallCm; // Внутренний диаметр
            double innerArea = Math.PI * Math.pow(innerDiametrCm / 2, 2); // см²

            double area = outerArea - innerArea; // см²

            // Длина трубы
            double lengthCm = mass / (area * densityKgPerCm3); // см
            double lengthM = lengthCm / 100; // см -> м

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (!quantityStr.isEmpty()) {
                double quantity = Double.parseDouble(quantityStr);
                // Проверка положительных значений
                if (quantity <= 0) {
                    totalLength.setText("Количество должно быть > 0");
                    return;
                }
                double massPerUnit = mass / quantity;
                resultText.append(String.format("Общая длина: %.2f м\n", lengthM));
                resultText.append(String.format("Длина еденицы: %.2f м\n", lengthM / quantity));
                resultText.append(String.format("Масса еденицы: %.2f кг\n", massPerUnit));
            } else {
                resultText.append(String.format("Длина: %.2f м\n", lengthM));
            }

            totalLength.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalLength.setText("Введите корректные значения!");
            Log.e("CalculationError", "Invalid number format", e);
        }
    }
}
