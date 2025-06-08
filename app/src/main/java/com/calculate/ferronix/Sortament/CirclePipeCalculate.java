package com.calculate.ferronix.Sortament;

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

import com.calculate.ferronix.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Control.NetworkHelper; // Убедитесь, что этот класс существует и корректно работает

public class CirclePipeCalculate extends AppCompatActivity {

    private EditText editTextDensity, editTextLength, editTextDiametr, editTextWall, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

    private String[] materials; // Объявлен, будет инициализирован в onCreate

    // Объявляем эти массивы, но НЕ инициализируем их здесь вызовами getString()
    private String[] aluminumGrades;
    private final double[] aluminumDensities = {
            2.70, 2.70, 2.70, 2.68, 2.68, 1.74, 2.55, 2.60, 2.70, 2.80
    };

    private String[] stainlessSteelGrades;
    private final double[] stainlessSteelDensities = {
            7.70, 7.75, 7.75, 7.75, 7.90, 7.90, 7.90, 7.95, 7.95
    };

    private String[] blackMetalGrades;
    private final double[] blackMetalDensities = {
            7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85,
            7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85, 7.85
    };

    // Константы для преобразования единиц
    private static final double MM_TO_CM = 0.1;
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001; // 1 г/см³ = 0.001 кг/см³

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.circle_pipe_calculate);

        // --- Инициализируем все массивы, использующие getString(), здесь, внутри onCreate() ---
        materials = new String[]{
                getString(R.string.material_black_metal),
                getString(R.string.material_stainless_steel),
                getString(R.string.material_aluminum)
        };

        aluminumGrades = new String[]{
                getString(R.string.grade_A5),
                getString(R.string.grade_AD),
                getString(R.string.grade_AD1),
                getString(R.string.grade_AK4),
                getString(R.string.grade_AK6),
                getString(R.string.grade_AMg),
                getString(R.string.grade_AMc),
                getString(R.string.grade_V95),
                getString(R.string.grade_D1),
                getString(R.string.grade_D16)
        };

        stainlessSteelGrades = new String[]{
                getString(R.string.grade_08X17T),
                getString(R.string.grade_20X13),
                getString(R.string.grade_30X13),
                getString(R.string.grade_40X13),
                getString(R.string.grade_08X18N10),
                getString(R.string.grade_12X18N10T),
                getString(R.string.grade_10X17N13M2T),
                getString(R.string.grade_06XH28MDT),
                getString(R.string.grade_20X23N18)
        };

        blackMetalGrades = new String[]{
                getString(R.string.steel_3),
                getString(R.string.steel_10),
                getString(R.string.steel_20),
                getString(R.string.steel_40X),
                getString(R.string.steel_45),
                getString(R.string.steel_65),
                getString(R.string.steel_65G),
                getString(R.string.grade_09G2S),
                getString(R.string.grade_15X5M),
                getString(R.string.grade_10XCSND),
                getString(R.string.grade_12X1MF),
                getString(R.string.grade_SHX15),
                getString(R.string.grade_R6M5),
                getString(R.string.grade_U7),
                getString(R.string.grade_U8),
                getString(R.string.grade_U8A),
                getString(R.string.grade_U10),
                getString(R.string.grade_U10A),
                getString(R.string.grade_U12A)
        };
        // --- Конец инициализации массивов в onCreate() ---


        // Инициализация элементов UI
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLength);
        editTextWall = findViewById(R.id.editTextWallCirclePipeW);
        editTextDiametr = findViewById(R.id.editTextDiametrCirclePipeW);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        textViewLength = findViewById(R.id.textViewLength);
        textViewUnit = findViewById(R.id.textViewUnit);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);
        Button btnCalculate = findViewById(R.id.btnCalculateCirclePipeW);

        // Проверка, что все необходимые UI-элементы были найдены
        if (editTextDensity == null || editTextLength == null || editTextWall == null || editTextDiametr == null ||
                totalWeight == null || textViewLength == null || textViewUnit == null ||
                btnMaterial == null || btnMark == null || btnGoWeight == null || btnGoLength == null || btnCalculate == null) {

            // Используем более конкретное сообщение для лога
            Log.e("CirclePipeCalculate", "One or more essential UI elements could not be found by ID.");
            finish(); // Завершаем активность, так как она не может нормально функционировать
            return; // Предотвращаем дальнейшее выполнение кода
        }

        // Устанавливаем активную кнопку при запуске (по умолчанию "Расчет массы")
        // NOTE: textViewLength.setText(R.string.length) here might seem counter-intuitive
        // if you want to default to "Mass calculation" (which implies calculating mass based on length).
        // Let's assume you want "Length" in the field and calculate "Weight" as output by default.
        // If "Mass" is displayed, it means calculate "Length" as output.
        // The original code implies default to "calculateWeight" if textViewLength text is "mass".
        // Let's ensure the initial state matches this logic.
        textViewLength.setText(R.string.length); // Поле для ввода длины (чтобы можно было рассчитать массу)
        textViewUnit.setText(R.string.unit_meter); // Единица измерения длины
        editTextLength.setHint(R.string.length);
        setActiveButton(btnGoWeight, btnGoLength); // Активна кнопка "Расчет массы"

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Проверяем текущий текст на textViewLength, чтобы определить режим расчета
            // Если textViewLength показывает "Масса" (т.е. поле для ввода - масса), то рассчитываем длину.
            // Иначе, если textViewLength показывает "Длина" (т.е. поле для ввода - длина), то рассчитываем массу.
            if (textViewLength.getText().toString().equals(getString(R.string.mass))) {
                calculateLength(); // Рассчитать ДЛИНУ по МАССЕ
            } else {
                calculateWeight(); // Рассчитать МАССУ по ДЛИНЕ
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
            // Переключаем на режим "Расчет массы": пользователь вводит ДЛИНУ, получает МАССУ
            textViewLength.setText(R.string.length); // Заголовок поля ввода меняется на "Длина"
            textViewUnit.setText(R.string.unit_meter); // Единица измерения для "Длина" - "м"
            editTextLength.setHint(R.string.length);

            // Устанавливаем активную кнопку
            setActiveButton(btnGoWeight, btnGoLength);
        });

        btnGoLength.setOnClickListener(v -> {
            // Переключаем на режим "Расчет длины": пользователь вводит МАССУ, получает ДЛИНУ
            textViewLength.setText(R.string.mass); // Заголовок поля ввода меняется на "Масса"
            textViewUnit.setText(R.string.unit_kg); // Единица измерения для "Масса" - "кг"
            editTextLength.setHint(R.string.mass);

            // Устанавливаем активную кнопку
            setActiveButton(btnGoLength, btnGoWeight);
        });
    }

    @SuppressLint("ResourceAsColor") // Это приемлемо здесь, так как вы используете ContextCompat
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        // Устанавливаем цвет фона и текста для активной кнопки
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Убираем подсветку и устанавливаем цвет текста для неактивной кнопки
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
        // Сравниваем с текстовым ресурсом "Материал", а не с жестко закодированной строкой
        if (material.equals(getString(R.string.material))) {
            Toast.makeText(this, R.string.error_no_material, Toast.LENGTH_SHORT).show();
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
            btnMark.setText(R.string.mark); // Сбросить марку при смене материала
            editTextDensity.setText(""); // Очистить плотность при смене материала
            return true;
        });
        popupMenu.show();
    }

    private void showGradeMenu(String material) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

        // Используем строковые ресурсы для сравнения материалов
        if (material.equals(getString(R.string.material_black_metal))) {
            grades = blackMetalGrades;
            densities = blackMetalDensities;
        } else if (material.equals(getString(R.string.material_stainless_steel))) {
            grades = stainlessSteelGrades;
            densities = stainlessSteelDensities;
        } else if (material.equals(getString(R.string.material_aluminum))) {
            grades = aluminumGrades;
            densities = aluminumDensities;
        } else {
            // Этого не должно произойти, если меню материала работает правильно, но для безопасности
            Log.e("CirclePipeCalculate", "Неизвестный материал выбран: " + material);
            Toast.makeText(this, "Произошла внутренняя ошибка с выбором материала.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String grade : grades) {
            popupMenu.getMenu().add(grade);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String grade = Objects.requireNonNull(item.getTitle()).toString();
            btnMark.setText(grade);

            // Находим индекс, используя Arrays.asList, а затем получаем плотность
            int index = Arrays.asList(grades).indexOf(grade);
            if (index != -1 && index < densities.length) {
                String formattedDensity = String.format(Locale.US, "%.2f", densities[index]);
                editTextDensity.setText(formattedDensity);
            }
            return true;
        });
        popupMenu.show();
    }

    // Расчет длины (пользователь вводит массу, получает длину)
    private void calculateLength() {
        try {
            // Получаем и проверяем значения
            String densityStr = editTextDensity.getText().toString().trim();
            String weightStr = editTextLength.getText().toString().trim(); // Здесь это масса
            String diametrStr = editTextDiametr.getText().toString().trim();
            String wallStr = editTextWall.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (densityStr.isEmpty() || weightStr.isEmpty() || diametrStr.isEmpty() || wallStr.isEmpty()) {
                totalWeight.setText(R.string.error_empty_fields);
                return;
            }

            // Парсим значения
            double density = Double.parseDouble(densityStr);
            double weight = Double.parseDouble(weightStr);
            double diametr = Double.parseDouble(diametrStr);
            double wall = Double.parseDouble(wallStr);

            // Проверка положительных значений
            if (density <= 0 || weight <= 0 || diametr <= 0 || wall <= 0) {
                totalWeight.setText(R.string.error_negative_values);
                return;
            }

            // Конвертация единиц
            double diametrCm = diametr * MM_TO_CM;
            double wallCm = wall * MM_TO_CM;

            // Проверка на корректную толщину стенки (стенка не должна быть больше половины диаметра)
            if (wallCm * 2 >= diametrCm) {
                totalWeight.setText(R.string.error_invalid_wall_thickness); // Нужно добавить эту строку в strings.xml
                return;
            }
            double innerDiametrCm = diametrCm - 2 * wallCm;

            // Площадь поперечного сечения трубы (Area of cross-section of the pipe)
            // Формула площади кольца: PI * (R_outer^2 - R_inner^2)
            double outerRadiusCm = diametrCm / 2;
            double innerRadiusCm = innerDiametrCm / 2;
            double area = Math.PI * (Math.pow(outerRadiusCm, 2) - Math.pow(innerRadiusCm, 2));

            // Длина трубы (Length of the pipe)
            // weight (kg) = volume (cm^3) * density (g/cm^3) * 0.001 (kg/g)
            // volume (cm^3) = weight (kg) / (density (g/cm^3) * 0.001)
            // length (cm) = volume (cm^3) / area (cm^2)
            double lengthCm = weight / (area * density * G_PER_CM3_TO_KG_PER_CM3);
            double lengthMeters = lengthCm / 100; // Конвертируем в метры для отображения

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, getString(R.string.length_unit_format), lengthMeters));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText(R.string.error_negative_quantity);
                    return;
                }

                double totalLengthValue = lengthMeters * quantity;
                resultText.append(String.format(Locale.US, getString(R.string.length_unit_format), lengthMeters));
                resultText.append("\n"); // Новая строка для лучшей читаемости
                resultText.append(String.format(Locale.US, getString(R.string.total_length_unit_format), totalLengthValue));

                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) { // Цена за кг не может быть отрицательной
                        totalWeight.setText(R.string.error_negative_price); // Нужно добавить эту строку в strings.xml
                        return;
                    }
                    double totalCost = pricePerKg * weight * quantity; // Общая масса уже учтена weight * quantity
                    double pricePerUnit = totalCost / quantity; // Стоимость за одну штуку

                    resultText.append("\n"); // Новая строка
                    resultText.append(String.format(Locale.US, getString(R.string.unit_cost_format), pricePerUnit));
                    resultText.append("\n"); // Новая строка
                    resultText.append(String.format(Locale.US, getString(R.string.total_unit_cost_format), totalCost));
                }
            }

            Map<String, String> analytics = new HashMap<>();
            // Здесь мы рассчитываем ДЛИНУ, поэтому тип аналитики должен быть analytics_type_length
            analytics.put(getString(R.string.analytics_key_type), getString(R.string.analytics_type_length));
            analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_pipe));
            NetworkHelper.sendCalculationData(this, analytics);

            totalWeight.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CirclePipeCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Расчет массы (пользователь вводит длину, получает массу)
    private void calculateWeight() {
        try {
            // Получаем и проверяем значения
            String densityStr = editTextDensity.getText().toString().trim();
            String lengthStr = editTextLength.getText().toString().trim(); // Здесь это длина
            String diametrStr = editTextDiametr.getText().toString().trim();
            String wallStr = editTextWall.getText().toString().trim();
            String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (densityStr.isEmpty() || lengthStr.isEmpty() || diametrStr.isEmpty() || wallStr.isEmpty()) {
                totalWeight.setText(R.string.error_empty_fields);
                return;
            }

            // Парсим значения
            double density = Double.parseDouble(densityStr);
            double length = Double.parseDouble(lengthStr); // Длина в метрах
            double diametr = Double.parseDouble(diametrStr);
            double wall = Double.parseDouble(wallStr);

            // Проверка положительных значений
            if (density <= 0 || length <= 0 || diametr <= 0 || wall <= 0) {
                totalWeight.setText(R.string.error_negative_values);
                return;
            }

            // Конвертация единиц
            double diametrCm = diametr * MM_TO_CM;
            double wallCm = wall * MM_TO_CM;

            // Проверка на корректную толщину стенки
            if (wallCm * 2 >= diametrCm) {
                totalWeight.setText(R.string.error_invalid_wall_thickness); // Нужно добавить эту строку в strings.xml
                return;
            }
            double innerDiametrCm = diametrCm - 2 * wallCm;

            // Площадь поперечного сечения трубы (Area of cross-section of the pipe)
            // Формула площади кольца: PI * (R_outer^2 - R_inner^2)
            double outerRadiusCm = diametrCm / 2;
            double innerRadiusCm = innerDiametrCm / 2;
            double area = Math.PI * (Math.pow(outerRadiusCm, 2) - Math.pow(innerRadiusCm, 2));

            // Объем трубы (Volume of the pipe)
            double volume = area * (length * 100); // Переводим длину из метров в сантиметры

            // Вес одной штуки (Weight of one piece)
            double weight = volume * density * G_PER_CM3_TO_KG_PER_CM3; // объем в см^3, плотность в г/см^3 -> вес в кг

            // Форматируем итоговый текст
            StringBuilder resultText = new StringBuilder();

            // Проверяем, введено ли количество
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, getString(R.string.mass_unit_format), weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText(R.string.error_negative_quantity);
                    return;
                }
                double totalWeightValue = weight * quantity;

                resultText.append(String.format(Locale.US, getString(R.string.mass_unit_format), weight));
                resultText.append("\n"); // Новая строка
                resultText.append(String.format(Locale.US, getString(R.string.total_mass_unit_format), totalWeightValue));

                if (!pricePerKgStr.isEmpty()) {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) { // Цена за кг не может быть отрицательной
                        totalWeight.setText(R.string.error_negative_price); // Нужно добавить эту строку в strings.xml
                        return;
                    }
                    double totalCost = pricePerKg * totalWeightValue; // totalWeightValue уже учитывает количество
                    double pricePerUnit = totalCost / quantity; // Стоимость за одну штуку

                    resultText.append("\n"); // Новая строка
                    resultText.append(String.format(Locale.US, getString(R.string.unit_cost_format), pricePerUnit));
                    resultText.append("\n"); // Новая строка
                    resultText.append(String.format(Locale.US, getString(R.string.total_unit_cost_format), totalCost));
                }
            }

            Map<String, String> analytics = new HashMap<>();
            analytics.put(getString(R.string.analytics_key_type), getString(R.string.analytics_type_weight));
            analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_pipe));
            NetworkHelper.sendCalculationData(this, analytics);

            totalWeight.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CirclePipeCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}