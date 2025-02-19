package com.calculate.ferronix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class SquareCalculateLength extends AppCompatActivity {

    private EditText editTextDensity, editTextMass, editTextSquareA;
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
        setContentView(R.layout.activity_square_lenght_calculate);

        // Инициализация массива materials
        materials = new String[]{"Черный металл", "Нержавейка", "Алюминий"};

        // Инициализация элементов интерфейса
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextMass = findViewById(R.id.editTextMass);
        editTextSquareA = findViewById(R.id.editTextSquareA);
        totalLength = findViewById(R.id.textViewLengthTotal);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);

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
            // Получаем значения из EditText
            String densityText = editTextDensity.getText().toString();
            String massText = editTextMass.getText().toString();
            String squareAText = editTextSquareA.getText().toString();

            // Проверяем, что все поля заполнены
            if (densityText.isEmpty() || massText.isEmpty() || squareAText.isEmpty()) {
                totalLength.setText("Ошибка: все поля должны быть заполнены");
                return;
            }

            double density = Double.parseDouble(densityText); // кг/см³
            double mass = Double.parseDouble(massText); // кг
            double squareA = Double.parseDouble(squareAText); // мм

            // Переводим единицы измерения
            double squareA_cm = squareA * 0.1; // мм -> см
            double density_kg_per_cm3 = density * 0.001; // г/см³ -> кг/см³

            // Проверка на корректность значений
            if (density <= 0 || mass <= 0 || squareA <= 0) {
                totalLength.setText("Ошибка: значения должны быть положительными");
                return;
            }

            // Выполняем расчет
            double S = squareA_cm * squareA_cm; // площадь в см²
            double length_sm = mass / (S * density_kg_per_cm3); // длина в см
            double length_m = length_sm / 100; // Длина в метрах

            // Выводим результат в TextView
            totalLength.setText(String.format("Длина: %.2f м.", length_m));
        } catch (NumberFormatException e) {
            // Обработка ошибки, если данные введены неправильно
            totalLength.setText("Ошибка: введите корректные числа");
        }
    }


    // Метод для возврата на предыдущую активность
    public void btnBack(View view) {
        startActivity(new Intent(SquareCalculateLength.this, SelectForm.class));
        finish(); // Закрываем текущую активность
    }

    public void btnGoMass(View view) {
        startActivity(new Intent(SquareCalculateLength.this, SquareCalculateWeight.class));
        finish(); // Закрываем текущую активность
    }
}
