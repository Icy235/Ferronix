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

public class CirclePipeCalculateLength extends AppCompatActivity {

    private EditText editTextDensity, editTextMass, editTextDiametr, editTextWall;
    private TextView totalLength;
    private Button btnCalculate, btnMaterial, btnMark;

    private String[] materials;
    // Инициализация массивов для Алюминия
    private String[] aluminumGrades = {
            "А5", "АД", "АД1", "АК4", "АК6", "АМг", "АМц", "В95", "Д1", "Д16"
    };
    private double[] aluminumDensities = {
            2.70, 2.70, 2.70, 2.68, 2.68, 1.74, 2.55, 2.60, 2.70, 2.80
    };

    // Инициализация массивов для Нержавейки
    private String[] stainlessSteelGrades = {
            "08Х17Т", "20Х13", "30Х13", "40Х13", "08Х18Н10", "12Х18Н10Т", "10Х17Н13М2Т", "06ХН28МДТ", "20Х23Н18"
    };
    private double[] stainlessSteelDensities = {
            7.70, 7.75, 7.75, 7.75, 7.90, 7.90, 7.90, 7.95, 7.95
    };

    // Инициализация массивов для черного металла
    private String[] blackMetalGrades = {
            "Сталь 3", "Сталь 10", "Сталь 20", "Сталь 40Х", "Сталь 45", "Сталь 65", "Сталь 65Г",
            "09Г2С", "15Х5М", "10ХСНД", "12Х1МФ", "ШХ15", "Р6М5", "У7", "У8", "У8А", "У10", "У10А", "У12А"
    };
    private double[] blackMetalDensities = {
            7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85,
            7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_circle_pipe_lenght_calculate);

        // Инициализация массива materials
        materials = new String[]{"Черный металл", "Нержавейка", "Алюминий"};

        // Инициализация элементов интерфейса
        editTextDensity = findViewById(R.id.editTextDensityCirclePipeL);
        editTextMass = findViewById(R.id.editTextMassCirclePipeL);
        editTextDiametr = findViewById(R.id.editTextDiametrCirclePipeL);
        editTextWall = findViewById(R.id.editTextWallCirclePipeL);
        totalLength = findViewById(R.id.textViewLengthTotalCirclePipeL);
        btnCalculate = findViewById(R.id.btnCalculateCirclePipeL);
        btnMaterial = findViewById(R.id.btnMaterialCirclePipeL);
        btnMark = findViewById(R.id.btnMarkCirclePipeL);

        if (editTextDensity == null || editTextMass == null || editTextDiametr == null || editTextWall == null) {
            Log.e("CirclePipeCalculateLength", "Некоторые EditText не были найдены. Проверьте ID в XML.");
            throw new NullPointerException("Некоторые EditText не были найдены. Проверьте ID в XML.");
        }

        // Обработчик нажатия на кнопку расчета
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLength(); // Изменено на расчет длины
            }
        });

        // Обработчик для кнопки выбора материала
        btnMaterial.setOnClickListener(v -> showMaterialMenu());

        // Обработчик для кнопки выбора марки
        btnMark.setOnClickListener(v -> {
            String selectedMaterial = btnMaterial.getText().toString();
            if (selectedMaterial.equals(getString(R.string.material))) {
                // Если материал не выбран, показываем сообщение
                Toast.makeText(this, "Сначала выберите материал", Toast.LENGTH_SHORT).show();
            } else {
                showGradeMenu(selectedMaterial);
            }
        });
    }

    // Метод для отображения меню выбора материала
    private void showMaterialMenu() {
        if (materials == null) {
            Toast.makeText(this, "Массив материалов не инициализирован", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popupMenu = new PopupMenu(this, btnMaterial);
        for (String material : materials) {
            popupMenu.getMenu().add(material);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedMaterial = item.getTitle().toString();
            btnMaterial.setText(selectedMaterial);
            // Сбрасываем текст марки при изменении материала
            btnMark.setText(getString(R.string.mark));
            return true;
        });
        popupMenu.show();
    }

    // Метод для отображения меню выбора марки
    private void showGradeMenu(String selectedMaterial) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

        if (selectedMaterial.equals(getString(R.string.black_metal))) {
            grades = blackMetalGrades;
            densities = blackMetalDensities;
        } else if (selectedMaterial.equals(getString(R.string.stainless_steel))) {
            grades = stainlessSteelGrades;
            densities = stainlessSteelDensities;
        } else if (selectedMaterial.equals(getString(R.string.aluminum))) {
            grades = aluminumGrades;
            densities = aluminumDensities;
        } else {
            return; // Для других материалов не показываем меню
        }

        for (String grade : grades) {
            popupMenu.getMenu().add(grade);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedGrade = item.getTitle().toString();
            btnMark.setText(selectedGrade);

            // Обновляем плотность в зависимости от выбранной марки
            int selectedIndex = Arrays.asList(grades).indexOf(selectedGrade);
            double selectedDensity = densities[selectedIndex];
            editTextDensity.setText(String.format("%.2f", selectedDensity)); // Обновляем плотность

            return true;
        });
        popupMenu.show();
    }

    // Метод для расчета длины
    private void calculateLength() {
        try {
            // Получаем данные из EditText
            double density = Double.parseDouble(editTextDensity.getText().toString()); // г/см³
            double weight = Double.parseDouble(editTextMass.getText().toString()); // кг
            double diametr = Double.parseDouble(editTextDiametr.getText().toString()); // мм
            double wall = Double.parseDouble(editTextWall.getText().toString()); // мм

            // Переводим единицы измерения
            double diametr_cm = diametr * 0.1; // мм -> см
            double wall_cm = wall * 0.1; // мм -> см
            double density_kg_per_cm3 = density * 0.001; // г/см³ -> кг/см³

            // Проверка на корректность значений
            if (density <= 0 || weight <= 0 || diametr <= 0 || wall <= 0) {
                totalLength.setText("Ошибка: значения должны быть положительными");
                return;
            }

            // Выполняем расчет
            double S = Math.PI * (diametr_cm * diametr_cm - (diametr_cm - wall_cm) * (diametr_cm - wall_cm)) / 4; // площадь в см²
            double V = weight / density_kg_per_cm3; // объем в см³
            double length = V / S; // длина в см

            // Переводим длину в метры
            length = length / 100; // см -> м

            // Выводим результат в TextView
            totalLength.setText(String.format("Длина: %.4f м.", length));
        } catch (NumberFormatException e) {
            // Обработка ошибки, если пользователь ввел некорректные данные
            totalLength.setText("Ошибка: введите корректные числа");
            Log.e("CirclePipeCalculateLength", "Ошибка ввода: " + e.getMessage());
        }
    }
    // Метод для возврата на предыдущую активность
    public void btnBack(View view) {
        startActivity(new Intent(CirclePipeCalculateLength.this, SelectForm.class));
        finish(); // Закрываем текущую активность
    }

    public void btnGoMass(View view) {
        startActivity(new Intent(CirclePipeCalculateLength.this, CircleCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }
}
