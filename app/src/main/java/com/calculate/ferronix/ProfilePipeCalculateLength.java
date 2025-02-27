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

public class ProfilePipeCalculateLength extends AppCompatActivity {

    private EditText editTextDensity, editTextMass, editTextSideA, editTextSideB, editTextWall, editTextPricePerKg, editTextQuantity;
    private TextView totalLength;
    private Button btnMaterial, btnMark;

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
        setContentView(R.layout.activity_profile_pipe_lenght_calculate);

        // Инициализация массива materials
        materials = new String[]{"Черный металл", "Нержавеющий металл", "Алюминий"};

        // Инициализация элементов интерфейса
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextMass = findViewById(R.id.editTextMassProfilePipeL);
        editTextSideA = findViewById(R.id.editTextSideA);
        editTextSideB = findViewById(R.id.editTextSideB);
        editTextWall = findViewById(R.id.editTextWallProfilePipeL);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg); // Поле для цены за кг
        editTextQuantity = findViewById(R.id.editTextQuantity); // Поле для количества
        totalLength = findViewById(R.id.textViewLengthTotal);
        Button btnCalculate = findViewById(R.id.btnCalculateProfilePipeL);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);

        // Проверка на null
        if (editTextDensity == null || editTextMass == null || editTextSideA == null || editTextSideB == null || editTextWall == null) {
            Log.e("InitError", "One or more EditText fields are null!");
            finish();
        }

        // Обработчики кликов
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
            case "Нержавеющий металл":
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
            String sideAStr = editTextSideA.getText().toString().trim();
            String sideBStr = editTextSideB.getText().toString().trim();
            String wallStr = editTextWall.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (densityStr.isEmpty() || massStr.isEmpty() || sideAStr.isEmpty() || sideBStr.isEmpty() || wallStr.isEmpty()) {
                totalLength.setText("Заполните все поля!");
                return;
            }

            // Парсим значения
            double density = Double.parseDouble(densityStr); // г/см³
            double mass = Double.parseDouble(massStr); // кг
            double externalWidth = Double.parseDouble(sideAStr); // мм
            double externalHeight = Double.parseDouble(sideBStr); // мм
            double wallThickness = Double.parseDouble(wallStr); // мм

            // Проверка положительных значений
            if (density <= 0 || mass <= 0 || externalWidth <= 0 || externalHeight <= 0 || wallThickness <= 0) {
                totalLength.setText("Значения должны быть > 0");
                return;
            }

            // Конвертация единиц
            double externalWidthCm = externalWidth * MM_TO_CM; // мм -> см
            double externalHeightCm = externalHeight * MM_TO_CM; // мм -> см
            double wallThicknessCm = wallThickness * MM_TO_CM; // мм -> см
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // г/см³ -> кг/см³

            // Рассчитываем внутренние размеры
            double internalWidthCm = externalWidthCm - 2 * wallThicknessCm; // внутренняя ширина
            double internalHeightCm = externalHeightCm - 2 * wallThicknessCm; // внутренняя высота

            // Площадь сечения материала (внешняя площадь минус внутренняя)
            double externalArea = externalWidthCm * externalHeightCm; // внешняя площадь
            double internalArea = internalWidthCm * internalHeightCm; // внутренняя площадь
            double materialArea = externalArea - internalArea; // площадь сечения материала

            // Объем материала
            double volume = mass / densityKgPerCm3; // объем в см³

            // Длина профиля
            double lengthCm = volume / materialArea; // длина в см
            double lengthM = lengthCm / 100; // длина в метрах

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

            // Выводим результат
            totalLength.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalLength.setText("Введите корректные значения!");
            Log.e("CalculationError", "Invalid number format", e);
        }
    }

    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }

    public void btnGoMass(View view) {
        startActivity(new Intent(this, ProfilePipeCalculateWeight.class));
        finish();
    }
}