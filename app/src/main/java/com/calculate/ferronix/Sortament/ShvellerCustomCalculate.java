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

public class ShvellerCustomCalculate extends AppCompatActivity {

    private EditText editTextDensity, editTextLength, editTextHeightH, editTextWidthB, editTextThicknessS, editTextThicknessT, editTextPricePerKg, editTextQuantity;
    private TextView total, textViewLength, textViewUnit;
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

    private String[] materials;

    // Массивы для материалов (будут инициализированы из строковых ресурсов в onCreate)
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
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.shveller_custom_calculate);

        // --- Инициализация массивов материалов и марок из строковых ресурсов ---
        materials = new String[]{
                getString(R.string.material_black_metal),
                getString(R.string.material_stainless_steel),
                getString(R.string.material_aluminum)
        };

        aluminumGrades = new String[]{
                getString(R.string.grade_A5), getString(R.string.grade_AD), getString(R.string.grade_AD1),
                getString(R.string.grade_AK4), getString(R.string.grade_AK6), getString(R.string.grade_AMg),
                getString(R.string.grade_AMc), getString(R.string.grade_V95), getString(R.string.grade_D1),
                getString(R.string.grade_D16)
        };

        stainlessSteelGrades = new String[]{
                getString(R.string.grade_08X17T), getString(R.string.grade_20X13), getString(R.string.grade_30X13),
                getString(R.string.grade_40X13), getString(R.string.grade_08X18N10), getString(R.string.grade_12X18N10T),
                getString(R.string.grade_10X17N13M2T), getString(R.string.grade_06XH28MDT), getString(R.string.grade_20X23N18)
        };

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

        initializeViews(); // Инициализация всех элементов UI
        setupListeners(); // Настройка слушателей событий для кнопок и полей ввода

        // Устанавливаем активную кнопку при запуске (по умолчанию - расчет массы по длине)
        textViewLength.setText(R.string.length); // Надпись "Длина" над полем ввода длины
        textViewUnit.setText(R.string.unit_meter); // Единица измерения "м"
        editTextLength.setHint(R.string.length); // Подсказка "Длина"
        setActiveButton(btnGoWeight, btnGoLength); // Выделяем кнопку "Рассчитать Вес"
    }

    // Метод для инициализации всех элементов UI
    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLength); // Это поле ввода будет использоваться либо для длины, либо для массы
        editTextHeightH = findViewById(R.id.editTextHeightH); // Высота H
        editTextWidthB = findViewById(R.id.editTextWidthB); // Ширина B
        editTextThicknessS = findViewById(R.id.editTextThicknessS); // Толщина стенки S
        editTextThicknessT = findViewById(R.id.editTextThicknessT); // Толщина полки T
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        total = findViewById(R.id.textViewWeightTotal); // Это TextView отображает общий результат
        textViewLength = findViewById(R.id.textViewLength); // TextView для метки "Длина" или "Масса"
        textViewUnit = findViewById(R.id.textViewUnit); // TextView для единицы измерения "м" или "кг"
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight); // Кнопка для переключения в режим расчета массы
        btnGoLength = findViewById(R.id.btnGoLength); // Кнопка для переключения в режим расчета длины

        // Надежная проверка на null для всех критических элементов UI
        if (editTextDensity == null || editTextLength == null || editTextHeightH == null ||
                editTextWidthB == null || editTextThicknessS == null || editTextThicknessT == null ||
                editTextPricePerKg == null || editTextQuantity == null || total == null ||
                textViewLength == null || textViewUnit == null || btnCalculate == null ||
                btnMaterial == null || btnMark == null || btnGoWeight == null || btnGoLength == null) {

            Toast.makeText(this, R.string.log_init_error, Toast.LENGTH_LONG).show(); // Показ удобного для пользователя сообщения
            finish(); // Закрываем активность, если критические элементы UI отсутствуют
        }
    }

    // Метод для настройки слушателей событий
    private void setupListeners() {
        // Слушатель для кнопки "Рассчитать"
        findViewById(R.id.btnCalculate).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Определяем, какой расчет выполнить, исходя из текущей метки textViewLength
            if (textViewLength.getText().toString().equals(getString(R.string.mass))) {
                calculateLengthOutput(); // Пользователь вводит массу, рассчитываем длину
            } else {
                calculateWeightOutput(); // Пользователь вводит длину, рассчитываем массу
            }
        });

        // Слушатель для кнопки "Материал"
        btnMaterial.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMaterialMenu();
        });

        // Слушатель для кнопки "Марка"
        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            handleMarkButtonClick();
        });

        // Слушатель для кнопки переключения в режим "Рассчитать Массу"
        btnGoWeight.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Переключаемся в режим "Рассчитать Массу": пользователь вводит ДЛИНУ, получает МАССУ
            textViewLength.setText(R.string.length); // Меняем метку на "Длина"
            textViewUnit.setText(R.string.unit_meter); // Меняем единицу измерения на "м"
            editTextLength.setHint(R.string.length); // Меняем подсказку на "Длина"
            setActiveButton(btnGoWeight, btnGoLength); // Выделяем "Рассчитать Массу"
        });

        // Слушатель для кнопки переключения в режим "Рассчитать Длину"
        btnGoLength.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Переключаемся в режим "Рассчитать Длину": пользователь вводит МАССУ, получает ДЛИНУ
            textViewLength.setText(R.string.mass); // Меняем метку на "Масса"
            textViewUnit.setText(R.string.unit_kg); // Меняем единицу измерения на "кг"
            editTextLength.setHint(R.string.mass); // Меняем подсказку на "Масса"
            setActiveButton(btnGoLength, btnGoWeight); // Выделяем "Рассчитать Длину"
        });
    }

    // Метод для установки активного/неактивного состояния кнопок выбора режима расчета
    @SuppressLint("ResourceAsColor") // Подавляем предупреждение, так как используется ContextCompat.getColor
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        // Устанавливаем цвет фона и текста для активной кнопки
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Устанавливаем цвет фона и текста для неактивной кнопки
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    // Обрабатывает нажатие на кнопку выбора "Марки" (сплава)
    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
        // Проверяем, выбран ли материал (не текст по умолчанию "Материал")
        if (material.equals(getString(R.string.material))) {
            Toast.makeText(this, R.string.error_no_material, Toast.LENGTH_SHORT).show();
        } else {
            showGradeMenu(material); // Показываем меню выбора марки для выбранного материала
        }
    }

    // Отображает меню выбора материала
    private void showMaterialMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnMaterial);
        for (String material : materials) {
            popupMenu.getMenu().add(material);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            btnMaterial.setText(item.getTitle()); // Устанавливаем выбранный материал
            btnMark.setText(R.string.mark); // Сбрасываем марку на значение по умолчанию
            editTextDensity.setText(""); // Очищаем поле плотности
            return true;
        });
        popupMenu.show();
    }

    // Отображает меню выбора марки в зависимости от выбранного материала
    private void showGradeMenu(String material) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

        // Определяем, какие массивы марок и плотностей использовать
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
            // Этот случай в идеале не должен быть достигнут, если меню материалов работает корректно
            Log.e("ShvellerCustom", getString(R.string.error_unknown_material) + ": " + material);
            Toast.makeText(this, R.string.error_unknown_material, Toast.LENGTH_SHORT).show();
            return;
        }

        for (String grade : grades) {
            popupMenu.getMenu().add(grade); // Добавляем марки в меню
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String grade = Objects.requireNonNull(item.getTitle()).toString();
            btnMark.setText(grade); // Устанавливаем выбранную марку

            // Находим индекс выбранной марки и устанавливаем соответствующую плотность
            int index = Arrays.asList(grades).indexOf(grade);
            if (index != -1 && index < densities.length) {
                // Форматируем плотность до двух знаков после запятой, используя Locale.US для единообразия десятичной точки
                String formattedDensity = String.format(Locale.US, "%.2f", densities[index]);
                editTextDensity.setText(formattedDensity);
            } else {
                Log.e("ShvellerCustom", getString(R.string.log_density_error) + ": Grade=" + grade + ", Material=" + material);
                Toast.makeText(this, R.string.log_density_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    // Общий метод валидации ввода для размеров швеллера
    private boolean validateInputs(String densityStr, String mainInputStr, String heightHStr, String widthBStr, String thicknessSStr, String thicknessTStr) {
        // Проверка на пустые поля
        if (densityStr.isEmpty() || mainInputStr.isEmpty() || heightHStr.isEmpty() || widthBStr.isEmpty() || thicknessSStr.isEmpty() || thicknessTStr.isEmpty()) {
            total.setText(R.string.error_empty_fields);
            return false;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double mainValue = Double.parseDouble(mainInputStr); // Это либо длина, либо масса
            double heightH = Double.parseDouble(heightHStr);
            double widthB = Double.parseDouble(widthBStr);
            double thicknessS = Double.parseDouble(thicknessSStr);
            double thicknessT = Double.parseDouble(thicknessTStr);

            // Проверка на неотрицательные значения
            if (density <= 0 || mainValue <= 0 || heightH <= 0 || widthB <= 0 || thicknessS <= 0 || thicknessT <= 0) {
                total.setText(R.string.error_negative_values);
                return false;
            }

            // Специфическая валидация для швеллера: толщина стенки не должна быть больше высоты полки,
            // и толщина полки не должна быть больше половины ширины полки.
            // Также высота Н должна быть больше толщины полки Т.
            if (thicknessS >= heightH || 2 * thicknessT >= widthB || thicknessT >= heightH) {
                total.setText(R.string.error_shveller_dimensions_invalid); // Новое строковое значение
                return false;
            }

            return true; // Валидация пройдена
        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("ShvellerCustom", getString(R.string.log_parsing_error) + e.getMessage());
            return false; // Валидация не пройдена из-за формата числа
        }
    }

    // Метод для расчета площади поперечного сечения швеллера в см²
    private double calculateCrossSectionalAreaCm2(double heightH_MM, double widthB_MM, double thicknessS_MM, double thicknessT_MM) {
        double heightHCm = heightH_MM * MM_TO_CM;
        double widthBCm = widthB_MM * MM_TO_CM;
        double thicknessSCm = thicknessS_MM * MM_TO_CM;
        double thicknessTCm = thicknessT_MM * MM_TO_CM;

        // Площадь стенки (H - 2T - если считать H как общую высоту)
        // Для швеллера (П-образного) площадь считается как H*s + 2*(B-s)*t
        // Или (H * s) + 2 * (B * t) - 2 * (t * s) если не вычитаем толщину стенки из ширины полки.
        // Ваш оригинальный расчет: (heightHCm - thicknessTCm) * thicknessSCm + 2 * (widthBCm * thicknessTCm)
        // Этот расчет предполагает, что H - это общая высота, S - толщина стенки, B - ширина полки, T - толщина полки.
        // Площадь = (высота_полки_без_толщины_стенки * толщина_стенки) + (2 * ширина_полки * толщина_полки)
        // Корректнее: Площадь = (H * s) + 2 * (B - s) * t -- это для швеллера, где толщина стенки вычитается из ширины полки.
        // Или для простоты (без учета скруглений и внутренних углов):
        // Площадь = (ширина_полки_B * толщина_полки_T * 2) + ( (высота_H - 2*толщина_полки_T) * толщина_стенки_S)
        // Давайте придерживаться формулы для площади швеллера: A = (H*s) + 2*(B*t) - 2*(s*t) если это по центральным линиям,
        // или (H*s) + 2*(B-s)*t для "чистых" прямоугольников.
        // Исходя из вашего оригинального кода, который был (heightHCm - thicknessTCm) * thicknessSCm + 2 * (widthBCm * thicknessTCm),
        // это похоже на: площадь стенки + площадь двух полок, где высота стенки уменьшена на толщину полки.
        // Это может быть упрощенной моделью, но давайте переформулируем для ясности.
        // Если H - это общая высота, B - общая ширина, S - толщина стенки, T - толщина полки:
        double areaWeb = heightHCm * thicknessSCm; // Площадь прямоугольника, если бы это была просто пластина высотой H
        double areaFlange = widthBCm * thicknessTCm; // Площадь одной полки

        // Площадь поперечного сечения швеллера (прямоугольники):
        // Две полки и одна стенка. Центральная часть стенки имеет высоту H - 2*T.
        // Площадь = (толщина_стенки * (высота_H - 2*толщина_полки_T)) + (2 * (ширина_полки_B * толщина_полки_T))
        return (thicknessSCm * (heightHCm - 2 * thicknessTCm)) + (2 * (widthBCm * thicknessTCm));
    }


    // Метод для расчета массы, когда пользователь вводит длину
    private void calculateWeightOutput() {
        // Получаем и обрезаем все входные строки
        String densityStr = editTextDensity.getText().toString().trim();
        String lengthInputStr = editTextLength.getText().toString().trim(); // Пользователь вводит длину (м)
        String heightHStr = editTextHeightH.getText().toString().trim();
        String widthBStr = editTextWidthB.getText().toString().trim();
        String thicknessSStr = editTextThicknessS.getText().toString().trim();
        String thicknessTStr = editTextThicknessT.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Валидация ввода
        if (!validateInputs(densityStr, lengthInputStr, heightHStr, widthBStr, thicknessSStr, thicknessTStr)) {
            return; // Выходим, если валидация не пройдена
        }

        try {
            // Парсим значения в double
            double density = Double.parseDouble(densityStr); // г/см³
            double lengthInputMeters = Double.parseDouble(lengthInputStr); // метры
            double heightH = Double.parseDouble(heightHStr); // мм
            double widthB = Double.parseDouble(widthBStr); // мм
            double thicknessS = Double.parseDouble(thicknessSStr); // мм
            double thicknessT = Double.parseDouble(thicknessTStr); // мм

            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // Конвертируем плотность в кг/см³

            // Рассчитываем площадь поперечного сечения материала в см²
            double materialAreaCm2 = calculateCrossSectionalAreaCm2(heightH, widthB, thicknessS, thicknessT);

            // Рассчитываем общий объем для одной единицы (см³)
            double volumeCm3 = materialAreaCm2 * (lengthInputMeters * 100); // Длина из метров в см

            // Рассчитываем вес одной единицы (кг)
            double weightPerPiece = volumeCm3 * densityKgPerCm3;

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weightPerPiece));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty();

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        total.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    total.setText(R.string.error_number_format);
                    Log.e("ShvellerCustom", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Отображаем общую массу, если количество указано
            if (isQuantityProvided) {
                double totalWeightValue = weightPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_mass_unit_format), totalWeightValue));
            }

            // Рассчитываем и отображаем стоимость, если цена за кг указана
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        total.setText(R.string.error_negative_price);
                        return;
                    }

                    double pricePerUnit = pricePerKg * weightPerPiece; // Стоимость одной единицы
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));

                    // Отображаем общую стоимость только если количество указано
                    if (isQuantityProvided) {
                        double totalCost = pricePerKg * weightPerPiece * totalQuantity;
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    total.setText(R.string.error_number_format);
                    Log.e("ShvellerCustom", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight)); // Отправляем аналитику для расчета массы
            total.setText(result.toString()); // Отображаем конечный результат

        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("ShvellerCustom", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Метод для расчета длины, когда пользователь вводит массу
    private void calculateLengthOutput() {
        // Получаем и обрезаем все входные строки
        String densityStr = editTextDensity.getText().toString().trim();
        String weightInputStr = editTextLength.getText().toString().trim(); // Пользователь вводит массу (кг)
        String heightHStr = editTextHeightH.getText().toString().trim();
        String widthBStr = editTextWidthB.getText().toString().trim();
        String thicknessSStr = editTextThicknessS.getText().toString().trim();
        String thicknessTStr = editTextThicknessT.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Валидация ввода
        if (!validateInputs(densityStr, weightInputStr, heightHStr, widthBStr, thicknessSStr, thicknessTStr)) {
            return; // Выходим, если валидация не пройдена
        }

        try {
            // Парсим значения в double
            double density = Double.parseDouble(densityStr); // г/см³
            double weightInputKg = Double.parseDouble(weightInputStr); // кг
            double heightH = Double.parseDouble(heightHStr); // мм
            double widthB = Double.parseDouble(widthBStr); // мм
            double thicknessS = Double.parseDouble(thicknessSStr); // мм
            double thicknessT = Double.parseDouble(thicknessTStr); // мм

            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // Конвертируем плотность в кг/см³

            // Рассчитываем площадь поперечного сечения материала в см²
            double materialAreaCm2 = calculateCrossSectionalAreaCm2(heightH, widthB, thicknessS, thicknessT);

            // Рассчитываем общий объем из введенной массы (см³)
            double volumeCm3 = weightInputKg / densityKgPerCm3;

            // Рассчитываем длину одной единицы (метры)
            double lengthPerPieceMeters = volumeCm3 / materialAreaCm2 / 100; // Конвертируем см в метры

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), lengthPerPieceMeters));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty();

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        total.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    total.setText(R.string.error_number_format);
                    Log.e("ShvellerCustom", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Отображаем общую длину, если количество указано
            if (isQuantityProvided) {
                double totalLengthValueMeters = lengthPerPieceMeters * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_length_unit_format), totalLengthValueMeters));
            }

            // Рассчитываем и отображаем стоимость, если цена за кг указана
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        total.setText(R.string.error_negative_price);
                        return;
                    }

                    double pricePerUnit = pricePerKg * weightInputKg; // Стоимость одной единицы
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));

                    // Отображаем общую стоимость только если количество указано
                    if (isQuantityProvided) {
                        double totalCost = pricePerKg * weightInputKg * totalQuantity;
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    total.setText(R.string.error_number_format);
                    Log.e("ShvellerCustom", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length)); // Отправляем аналитику для расчета длины
            total.setText(result.toString()); // Отображаем конечный результат

        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("ShvellerCustom", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Метод для отправки аналитических данных
    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_shveller_custom)); // Используем новый строковый ресурс
        NetworkHelper.sendCalculationData(this, analytics);
    }

    // Обрабатывает нажатие кнопки "Назад", переходя к активности SelectForm
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}