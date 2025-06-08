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

import Control.NetworkHelper;

public class CornerCalculate extends AppCompatActivity {

    private static final double MM_TO_CM = 0.1; // Коэффициент перевода миллиметров в сантиметры
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001; // Коэффициент перевода граммов/см³ в килограммы/см³

    // Объявление UI-элементов
    private EditText editTextDensity, editTextLength, editTextSideA, editTextSideB, editTextWall, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit; // textViewLength теперь служит меткой для поля ввода (Длина/Масса)
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

    // Объявление массивов для материалов и марок. Инициализация перенесена в onCreate().
    private String[] materials;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Включение режима EdgeToEdge для лучшего использования экрана
        setContentView(R.layout.corner_calculate); // Установка макета для расчета уголка

        // --- Инициализация массивов строковых ресурсов здесь, в onCreate() ---
        // Это необходимо, так как getString() требует инициализированного Context.
        materials = new String[]{
                getString(R.string.material_black_metal),
                getString(R.string.material_stainless_steel),
                getString(R.string.material_aluminum)
        };

        // Инициализация марок алюминия из строковых ресурсов
        aluminumGrades = new String[]{
                getString(R.string.grade_A5), getString(R.string.grade_AD), getString(R.string.grade_AD1),
                getString(R.string.grade_AK4), getString(R.string.grade_AK6), getString(R.string.grade_AMg),
                getString(R.string.grade_AMc), getString(R.string.grade_V95), getString(R.string.grade_D1),
                getString(R.string.grade_D16)
        };

        // Инициализация марок нержавеющей стали из строковых ресурсов
        stainlessSteelGrades = new String[]{
                getString(R.string.grade_08X17T), getString(R.string.grade_20X13), getString(R.string.grade_30X13),
                getString(R.string.grade_40X13), getString(R.string.grade_08X18N10), getString(R.string.grade_12X18N10T),
                getString(R.string.grade_10X17N13M2T), getString(R.string.grade_06XH28MDT), getString(R.string.grade_20X23N18)
        };

        // Инициализация марок черного металла из строковых ресурсов
        blackMetalGrades = new String[]{
                getString(R.string.steel_3), getString(R.string.steel_10), getString(R.string.steel_20),
                getString(R.string.steel_40X), getString(R.string.steel_45), getString(R.string.steel_65),
                getString(R.string.steel_65G), getString(R.string.grade_09G2S), getString(R.string.grade_15X5M),
                getString(R.string.grade_10XCSND), getString(R.string.grade_12X1MF), getString(R.string.grade_SHX15),
                getString(R.string.grade_R6M5), getString(R.string.grade_U7), getString(R.string.grade_U8),
                getString(R.string.grade_U8A), getString(R.string.grade_U10), getString(R.string.grade_U10A),
                getString(R.string.grade_U12A)
        };
        // --- Конец инициализации массивов ---

        initializeViews(); // Инициализация всех UI-элементов
        setupListeners(); // Настройка слушателей событий для кнопок и полей ввода

        // Установка начального состояния UI: по умолчанию рассчитываем массу по длине
        textViewLength.setText(R.string.length); // Метка для поля ввода: "Длина"
        textViewUnit.setText(R.string.unit_meter); // Единица измерения: "м" (или ваша default-единица длины)
        editTextLength.setHint(R.string.length); // Подсказка для поля ввода: "Длина"
        setActiveButton(btnGoWeight, btnGoLength); // Визуально активируем кнопку "Расчет массы"
    }

    // Метод для инициализации всех UI-элементов по их ID
    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLengthCornerW);
        editTextSideA = findViewById(R.id.editTextSideA);
        editTextSideB = findViewById(R.id.editTextSideB);
        editTextWall = findViewById(R.id.editTextWallCornerW);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal); // Результат расчета отображается здесь
        textViewLength = findViewById(R.id.textViewLength); // Метка для поля ввода длины/массы
        textViewUnit = findViewById(R.id.textViewUnit); // Единица измерения рядом с полем ввода
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);
        Button btnCalculate = findViewById(R.id.btnCalculateCornerW); // Кнопка "Рассчитать"

        // Базовая проверка на null для всех критически важных UI-элементов.
        // Если какой-то элемент не найден, это указывает на проблему в макете,
        // и приложение не сможет функционировать корректно.
        if (editTextDensity == null || editTextLength == null || editTextSideA == null ||
                editTextSideB == null || editTextWall == null || editTextPricePerKg == null ||
                editTextQuantity == null || totalWeight == null || textViewLength == null ||
                textViewUnit == null || btnMaterial == null || btnMark == null ||
                btnGoWeight == null || btnGoLength == null || btnCalculate == null) {


            finish(); // Завершаем активность
        }
    }

    // Метод для настройки слушателей событий для UI-элементов
    private void setupListeners() {
        // Слушатель для кнопки "Рассчитать"
        findViewById(R.id.btnCalculateCornerW).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS); // Тактильная обратная связь
            // Проверяем текущий текст на метке поля ввода, чтобы определить режим расчета
            if (textViewLength.getText().toString().equals(getString(R.string.mass))) {
                // Если метка "Масса", значит пользователь вводит массу, и мы рассчитываем ДЛИНУ
                calculateLengthOutput();
            } else {
                // Если метка "Длина", значит пользователь вводит длину, и мы рассчитываем МАССУ
                calculateWeightOutput();
            }
        });

        // Слушатель для кнопки выбора материала
        btnMaterial.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMaterialMenu(); // Показать меню выбора материала
        });

        // Слушатель для кнопки выбора марки
        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            handleMarkButtonClick(); // Обработка нажатия кнопки "Марка"
        });

        // Слушатель для кнопки "Рассчитать массу" (пользователь вводит длину)
        btnGoWeight.setOnClickListener(v -> {
            // Переключаемся в режим "Расчет массы": пользователь вводит ДЛИНУ, получает МАССУ
            textViewLength.setText(R.string.length); // Метка поля ввода: "Длина"
            textViewUnit.setText(R.string.unit_meter); // Единица измерения: "м" (или ваша default-единица длины)
            editTextLength.setHint(R.string.length); // Подсказка для поля ввода: "Длина"
            setActiveButton(btnGoWeight, btnGoLength); // Визуально активируем эту кнопку
        });

        // Слушатель для кнопки "Рассчитать длину" (пользователь вводит массу)
        btnGoLength.setOnClickListener(v -> {
            // Переключаемся в режим "Расчет длины": пользователь вводит МАССУ, получает ДЛИНУ
            textViewLength.setText(R.string.mass); // Метка поля ввода: "Масса"
            textViewUnit.setText(R.string.unit_kg); // Единица измерения: "кг"
            editTextLength.setHint(R.string.mass); // Подсказка для поля ввода: "Масса"
            setActiveButton(btnGoLength, btnGoWeight); // Визуально активируем эту кнопку
        });
    }

    // Метод для визуального выделения активной кнопки
    @SuppressLint("ResourceAsColor") // Аннотация для подавления предупреждения, так как ContextCompat.getColor() используется безопасно
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        // Установка цвета фона и текста для активной кнопки
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        // Установка цвета фона и текста для неактивной кнопки
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    // Обработка нажатия на кнопку "Марка"
    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
        // Проверяем, был ли выбран материал (не равно ли значению по умолчанию "Материал")
        if (material.equals(getString(R.string.material))) {
            Toast.makeText(this, R.string.error_no_material, Toast.LENGTH_SHORT).show();
        } else {
            showGradeMenu(material); // Показать меню выбора марки для выбранного материала
        }
    }

    // Показать меню выбора материала
    private void showMaterialMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnMaterial);
        for (String material : materials) {
            popupMenu.getMenu().add(material); // Добавляем все доступные материалы в меню
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            btnMaterial.setText(item.getTitle()); // Установить выбранный материал
            btnMark.setText(R.string.mark); // Сбросить марку (пользователь должен выбрать ее заново)
            editTextDensity.setText(""); // Очистить поле плотности, так как оно зависит от марки
            return true;
        });
        popupMenu.show();
    }

    // Показать меню выбора марки в зависимости от выбранного материала
    private void showGradeMenu(String material) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

        // Определяем, какой массив марок и плотностей использовать, исходя из выбранного материала
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
            // Этого не должно произойти, если меню материала работает корректно,
            // но на всякий случай логируем ошибку.
            Log.e("CornerCalculate", "Выбран неизвестный материал: " + material);
            Toast.makeText(this, R.string.error_unknown_material, Toast.LENGTH_SHORT).show(); // Новая строка
            return;
        }

        for (String grade : grades) {
            popupMenu.getMenu().add(grade); // Добавляем марки в меню
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String grade = Objects.requireNonNull(item.getTitle()).toString();
            btnMark.setText(grade); // Установить выбранную марку

            // Находим индекс выбранной марки и устанавливаем соответствующую плотность
            int index = Arrays.asList(grades).indexOf(grade);
            if (index != -1 && index < densities.length) {
                // Форматируем плотность до двух знаков после запятой, используя Locale.US для десятичной точки
                String formattedDensity = String.format(Locale.US, "%.2f", densities[index]);
                editTextDensity.setText(formattedDensity);
            }
            return true;
        });
        popupMenu.show();
    }

    // Метод для общей валидации числовых полей ввода для уголка
    private boolean validateCommonInputs(String densityStr, String mainInputStr, String sideAStr, String sideBStr, String wallStr) {
        if (densityStr.isEmpty() || mainInputStr.isEmpty() || sideAStr.isEmpty() || sideBStr.isEmpty() || wallStr.isEmpty()) {
            totalWeight.setText(R.string.error_empty_fields); // Сообщение об ошибке: "Заполните все поля!"
            return false; // Валидация не пройдена
        }

        try {
            double density = Double.parseDouble(densityStr);
            double mainValue = Double.parseDouble(mainInputStr); // Это либо длина, либо масса
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);
            double wall = Double.parseDouble(wallStr);

            if (density <= 0 || mainValue <= 0 || sideA <= 0 || sideB <= 0 || wall <= 0) {
                totalWeight.setText(R.string.error_negative_values); // Сообщение об ошибке: "Значения должны быть > 0"
                return false; // Валидация не пройдена
            }

            // Дополнительная валидация для уголка: толщина стенки не должна быть больше или равна сторонам
            if (wall >= sideA || wall >= sideB) {
                totalWeight.setText(R.string.error_wall_thickness_too_large_corner); // Новая строка
                return false;
            }
            return true; // Валидация пройдена
        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format); // Сообщение об ошибке: "Ошибка в формате чисел"
            Log.e("CornerCalculate", getString(R.string.log_parsing_error) + e.getMessage()); // Логирование ошибки парсинга
            return false; // Валидация не пройдена
        }
    }

    // Расчет веса одной единицы уголка (по длине)
    private double calculateUnitWeight(double density, double lengthMeters, double sideA_MM, double sideB_MM, double wallThickness_MM) {
        double sideACm = sideA_MM * MM_TO_CM;
        double sideBCm = sideB_MM * MM_TO_CM;
        double wallThicknessCm = wallThickness_MM * MM_TO_CM;
        double lengthCm = lengthMeters * 100; // Длина в см (т.к. lengthMeters приходит в метрах)
        double densityInKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3;

        // Площадь поперечного сечения уголка: (sideA + sideB - wallThickness) * wallThickness
        double crossSectionAreaCm2 = (sideACm + sideBCm - wallThicknessCm) * wallThicknessCm;
        double volumeCm3 = crossSectionAreaCm2 * lengthCm; // Объем в см³

        return volumeCm3 * densityInKgPerCm3; // Вес в кг
    }

    // Расчет длины одной единицы уголка (по массе)
    private double calculateUnitLength(double density, double weightKg, double sideA_MM, double sideB_MM, double wallThickness_MM) {
        double sideACm = sideA_MM * MM_TO_CM;
        double sideBCm = sideB_MM * MM_TO_CM;
        double wallThicknessCm = wallThickness_MM * MM_TO_CM;
        double densityInKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3;

        // Площадь поперечного сечения уголка: (sideA + sideB - wallThickness) * wallThickness
        double crossSectionAreaCm2 = (sideACm + sideBCm - wallThicknessCm) * wallThicknessCm;

        // Объем: V = Weight / Density (в нужных единицах)
        double volumeCm3 = weightKg / densityInKgPerCm3;

        // Длина: Length = Volume / Area
        double lengthCm = volumeCm3 / crossSectionAreaCm2;
        return lengthCm / 100; // Длина в метрах
    }

    // Метод для расчета ДЛИНЫ (когда пользователь вводит МАССУ)
    private void calculateLengthOutput() {
        String densityStr = editTextDensity.getText().toString().trim();
        String weightInputStr = editTextLength.getText().toString().trim(); // Это масса, которую ввел пользователь
        String sideAStr = editTextSideA.getText().toString().trim();
        String sideBStr = editTextSideB.getText().toString().trim();
        String wallStr = editTextWall.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (!validateCommonInputs(densityStr, weightInputStr, sideAStr, sideBStr, wallStr)) {
            return; // Если валидация не пройдена, выходим
        }

        try {
            double density = Double.parseDouble(densityStr);
            double weightInput = Double.parseDouble(weightInputStr); // Масса одной "единицы" (уголка), введенная пользователем
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);
            double wallThickness = Double.parseDouble(wallStr);

            double lengthPerPiece = calculateUnitLength(density, weightInput, sideA, sideB, wallThickness); // Рассчитанная длина одной такой "единицы"
            StringBuilder result = new StringBuilder();

            // Вывод длины одной единицы
            result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), lengthPerPiece));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty(); // Проверка, введено ли количество

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        totalWeight.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("CornerCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Отображаем общую длину только если введено количество
            if (isQuantityProvided) {
                double totalLength = lengthPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_length_unit_format), totalLength));
            }


            // Расчет стоимости (только если введена цена за кг)
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) { // Проверка на отрицательную цену
                        totalWeight.setText(R.string.error_negative_price);
                        return;
                    }

                    // Общая стоимость рассчитывается по ОБЩЕЙ МАССЕ (weightInput * totalQuantity)
                    // Если количество не введено, totalQuantity будет 1.0, и расчет будет для одной единицы.
                    double totalMassForCost = weightInput * totalQuantity;
                    double totalCost = pricePerKg * totalMassForCost;

                    // Стоимость одной единицы (уголка, которая весит weightInput кг)
                    double pricePerUnit = totalCost / totalQuantity;

                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));
                    // Общую стоимость отображаем только если введено количество (и цена)
                    if (isQuantityProvided) {
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("CornerCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length)); // Отправка аналитики (расчет длины)
            totalWeight.setText(result.toString()); // Отображение результата

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CornerCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Метод для расчета МАССЫ (когда пользователь вводит ДЛИНУ)
    private void calculateWeightOutput() {
        String densityStr = editTextDensity.getText().toString().trim();
        String lengthInputStr = editTextLength.getText().toString().trim(); // Это длина, которую ввел пользователь
        String sideAStr = editTextSideA.getText().toString().trim();
        String sideBStr = editTextSideB.getText().toString().trim();
        String wallStr = editTextWall.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (!validateCommonInputs(densityStr, lengthInputStr, sideAStr, sideBStr, wallStr)) {
            return; // Если валидация не пройдена, выходим
        }

        try {
            double density = Double.parseDouble(densityStr);
            double lengthInput = Double.parseDouble(lengthInputStr); // Длина одной "единицы" (уголка), введенная пользователем
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);
            double wallThickness = Double.parseDouble(wallStr);

            double weightPerPiece = calculateUnitWeight(density, lengthInput, sideA, sideB, wallThickness); // Рассчитанная масса одной такой "единицы"
            StringBuilder result = new StringBuilder();

            // Вывод массы одной единицы
            result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weightPerPiece));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty(); // Проверка, введено ли количество

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        totalWeight.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("CornerCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Отображаем общую массу только если введено количество
            if (isQuantityProvided) {
                double totalWeightValue = weightPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_mass_unit_format), totalWeightValue));
            }


            // Расчет стоимости (только если введена цена за кг)
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) { // Проверка на отрицательную цену
                        totalWeight.setText(R.string.error_negative_price);
                        return;
                    }
                    // Общая стоимость рассчитывается по ОБЩЕЙ МАССЕ (weightPerPiece * totalQuantity)
                    // Если количество не введено, totalQuantity будет 1.0, и расчет будет для одной единицы.
                    double totalMassForCost = weightPerPiece * totalQuantity;
                    double totalCost = pricePerKg * totalMassForCost;

                    // Стоимость одной единицы (уголка, который весит weightPerPiece кг)
                    double pricePerUnit = totalCost / totalQuantity;

                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));
                    // Общую стоимость отображаем только если введено количество (и цена)
                    if (isQuantityProvided) {
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("CornerCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight)); // Отправка аналитики (расчет массы)
            totalWeight.setText(result.toString()); // Отображение результата

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CornerCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Метод для отправки данных аналитики
    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType); // Тип расчета (Масса/Длина)
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_corner)); // Шаблон (Уголок)
        NetworkHelper.sendCalculationData(this, analytics); // Отправка данных
    }

    // Метод для возврата на предыдущую активность
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class)); // Переход к SelectForm
        finish(); // Завершение текущей активности
    }
}