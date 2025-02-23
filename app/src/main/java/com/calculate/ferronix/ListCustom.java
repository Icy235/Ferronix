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
import java.util.Objects;

public class ListCustom extends AppCompatActivity {

    private EditText editTextDensity, editTextSideA, editTextSideB, editTextThinckness;
    private TextView totalWeight;
    private Button btnMaterial;
    private Button btnMark;

    private String[] materials;
    // Инициализация массивов для Алюминия
    private final String[] aluminumGrades = {
            "А5", "АД", "АД1", "АК4", "АК6", "АМг", "АМц", "В95", "Д1", "Д16"
    };
    private final double[] aluminumDensities = {
            2.70, 2.70, 2.70, 2.68, 2.68, 1.74, 2.55, 2.60, 2.70, 2.80
    };

    // Инициализация массивов для Нержавейки
    private final String[] stainlessSteelGrades = {
            "08Х17Т", "20Х13", "30Х13", "40Х13", "08Х18Н10", "12Х18Н10Т", "10Х17Н13М2Т", "06ХН28МДТ", "20Х23Н18"
    };
    private final double[] stainlessSteelDensities = {
            7.70, 7.75, 7.75, 7.75, 7.90, 7.90, 7.90, 7.95, 7.95
    };

    // Инициализация массивов для черного металла
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
        setContentView(R.layout.activity_list_custom_calculate);

        // Инициализация массива materials
        materials = new String[]{"Черный металл", "Нержавеющий металл", "Алюминий"};

        // Инициализация элементов интерфейса
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextSideA = findViewById(R.id.editTextSideA);
        editTextSideB = findViewById(R.id.editTextSideB);
        editTextThinckness = findViewById(R.id.editTextThicknessListCustom);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        Button btnCalculate = findViewById(R.id.btnCalculateListCustom);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);

        // Проверка на null
        if (editTextDensity == null || editTextSideA == null || editTextSideB == null || editTextThinckness == null) {
            Log.e("InitError", "One or more EditText fields are null!");
            finish();
        }

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> calculateWeight());
        btnMaterial.setOnClickListener(v -> showMaterialMenu());
        btnMark.setOnClickListener(v -> handleMarkButtonClick());
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

    private void calculateWeight() {
        try {
            // Получаем и проверяем значения
            String densityStr = editTextDensity.getText().toString().trim();// Плотность материала
            String lengthStr = editTextSideB.getText().toString().trim();// Высота листа
            String widthStr = editTextSideA.getText().toString().trim(); // Ширина листа
            String thicknessStr = editTextThinckness.getText().toString().trim(); // Толщина листа

            if (densityStr.isEmpty() || lengthStr.isEmpty() || widthStr.isEmpty() || thicknessStr.isEmpty()) {
                totalWeight.setText("Заполните все поля!");
                return;
            }

            // Парсим значения
            double density = Double.parseDouble(densityStr); // г/см³
            double length = Double.parseDouble(lengthStr); // мм
            double width = Double.parseDouble(widthStr); // мм
            double thickness = Double.parseDouble(thicknessStr); // мм

            // Проверка положительных значений
            if (density <= 0 || length <= 0 || width <= 0 || thickness <= 0) {
                totalWeight.setText("Значения должны быть > 0");
                return;
            }

            // Конвертация единиц
            double lengthCm = length * MM_TO_CM; // мм -> см
            double widthCm = width * MM_TO_CM; // мм -> см
            double thicknessCm = thickness * MM_TO_CM; // мм -> см
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // г/см³ -> кг/см³

            // Рассчитываем объем листа
            double volume = lengthCm * widthCm * thicknessCm; // объем в см³

            // Вес листа
            double weight = volume * densityKgPerCm3; // вес в кг

            // Выводим результат
            totalWeight.setText(String.format(Locale.US, "Вес: %.2f кг", weight));

        } catch (NumberFormatException e) {
            totalWeight.setText("Ошибка в формате чисел");
            Log.e("CalcError", "Parsing error: " + e.getMessage());
        }
    }
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }

    public void btnGoLength(View view) {
        startActivity(new Intent(this, ProfilePipeCalculateLength.class));
        finish();
    }
}