package com.calculate.ferronix.Sortament;

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

import com.calculate.ferronix.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Control.NetworkHelper; // Убедитесь, что этот класс существует и корректно работает

public class ListCustom extends AppCompatActivity {

    private EditText editTextDensity, editTextSideA, editTextSideB, editTextThinckness, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight;
    private Button btnMaterial, btnMark;

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

    // Константы для преобразования единиц
    private static final double MM_TO_CM = 0.1;
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.list_custom_calculate);

        // --- Инициализация массивов строковых ресурсов здесь, в onCreate() ---
        // Это необходимо, так как getString() требует инициализированного Context.
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

        initializeViews(); // Инициализация всех UI-элементов
        setupListeners(); // Настройка слушателей событий для кнопок и полей ввода
    }

    // Метод для инициализации всех UI-элементов
    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextSideA = findViewById(R.id.editTextSideA); // Ширина
        editTextSideB = findViewById(R.id.editTextSideB); // Длина
        editTextThinckness = findViewById(R.id.editTextThicknessListCustom);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        Button btnCalculate = findViewById(R.id.btnCalculateListCustom);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);

        // Базовая проверка на null для критически важных UI-элементов
        if (editTextDensity == null || editTextSideA == null || editTextSideB == null ||
                editTextThinckness == null || editTextPricePerKg == null ||
                editTextQuantity == null || totalWeight == null || btnCalculate == null ||
                btnMaterial == null || btnMark == null) {

            Toast.makeText(this, R.string.log_init_error, Toast.LENGTH_LONG).show(); // Показываем Toast пользователю
            finish(); // Завершаем активность, так как она не может нормально функционировать
        }
    }

    // Метод для настройки слушателей событий
    private void setupListeners() {
        // Слушатель для кнопки "Рассчитать"
        findViewById(R.id.btnCalculateListCustom).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            calculateWeightOutput(); // Переименовал для ясности
        });
        // Слушатель для кнопки выбора материала
        btnMaterial.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMaterialMenu();
        });
        // Слушатель для кнопки выбора марки
        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            handleMarkButtonClick();
        });
    }

    // Обработка нажатия на кнопку "Марка"
    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
        // Проверяем, выбран ли материал (не равно ли значению по умолчанию "Материал")
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
            popupMenu.getMenu().add(material);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            btnMaterial.setText(item.getTitle()); // Установить выбранный материал
            btnMark.setText(R.string.mark); // Сбросить марку (пользователь должен выбрать ее заново)
            editTextDensity.setText(""); // Очистить поле плотности
            return true;
        });
        popupMenu.show();
    }

    // Показать меню выбора марки в зависимости от выбранного материала
    private void showGradeMenu(String material) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

        // Определяем, какой массив марок и плотностей использовать
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
            // Этого не должно произойти, если меню материала работает корректно
            Log.e("ListCustom", getString(R.string.error_unknown_material) + ": " + material);
            Toast.makeText(this, R.string.error_unknown_material, Toast.LENGTH_SHORT).show();
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
            } else {
                Log.e("ListCustom", getString(R.string.log_density_error) + ": Марка=" + grade + ", Материал=" + material);
                Toast.makeText(this, R.string.log_density_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    // Метод для валидации всех числовых полей ввода
    private boolean validateInputs(String densityStr, String sideAStr, String sideBStr, String thicknessStr) {
        if (densityStr.isEmpty() || sideAStr.isEmpty() || sideBStr.isEmpty() || thicknessStr.isEmpty()) {
            totalWeight.setText(R.string.error_empty_fields); // Сообщение об ошибке: "Заполните все поля!"
            return false; // Валидация не пройдена
        }

        try {
            double density = Double.parseDouble(densityStr);
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);
            double thickness = Double.parseDouble(thicknessStr);

            if (density <= 0 || sideA <= 0 || sideB <= 0 || thickness <= 0) {
                totalWeight.setText(R.string.error_negative_values); // Сообщение об ошибке: "Значения должны быть > 0"
                return false; // Валидация не пройдена
            }
            return true; // Валидация пройдена
        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format); // Сообщение об ошибке: "Ошибка в формате чисел"
            Log.e("ListCustom", getString(R.string.log_parsing_error) + e.getMessage()); // Логирование ошибки парсинга
            return false; // Валидация не пройдена
        }
    }

    // Метод для расчета веса и отображения результатов
    private void calculateWeightOutput() {
        // Получаем значения из полей ввода
        String densityStr = editTextDensity.getText().toString().trim();
        String sideAStr = editTextSideA.getText().toString().trim(); // Ширина
        String sideBStr = editTextSideB.getText().toString().trim(); // Длина
        String thicknessStr = editTextThinckness.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Выполняем валидацию
        if (!validateInputs(densityStr, sideAStr, sideBStr, thicknessStr)) {
            return; // Если валидация не пройдена, выходим
        }

        try {
            // Парсим значения в double
            double density = Double.parseDouble(densityStr); // г/см³
            double length = Double.parseDouble(sideBStr); // мм
            double width = Double.parseDouble(sideAStr); // мм
            double thickness = Double.parseDouble(thicknessStr); // мм

            // Конвертация единиц для расчета объема (все в см)
            double lengthCm = length * MM_TO_CM;
            double widthCm = width * MM_TO_CM;
            double thicknessCm = thickness * MM_TO_CM;
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // Плотность в кг/см³

            // Рассчитываем объем листа
            double volumeCm3 = lengthCm * widthCm * thicknessCm; // Объем в см³

            // Вес одного листа
            double weightPerPiece = volumeCm3 * densityKgPerCm3; // Вес в кг

            StringBuilder result = new StringBuilder();

            // Вывод массы одного листа
            result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weightPerPiece));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty(); // Проверяем, введено ли количество

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        totalWeight.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("ListCustom", getString(R.string.log_parsing_error) + e.getMessage());
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

                    // Общая стоимость рассчитывается по общей массе (weightPerPiece * totalQuantity)
                    // Если количество не введено, totalQuantity будет 1.0, и расчет будет для одной единицы.
                    double totalMassForCost = weightPerPiece * totalQuantity;
                    double totalCost = pricePerKg * totalMassForCost;

                    // Стоимость одной единицы (листа, который весит weightPerPiece кг)
                    double pricePerUnit = totalCost / totalQuantity;

                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));
                    // Общую стоимость отображаем только если введено количество (и цена)
                    if (isQuantityProvided) {
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("ListCustom", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight_list)); // Отправка аналитики (расчет массы листа)
            totalWeight.setText(result.toString()); // Выводим итоговый результат

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("ListCustom", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Метод для отправки данных аналитики
    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType); // Тип расчета (Масса листа)
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_list)); // Шаблон (Лист)
        NetworkHelper.sendCalculationData(this, analytics); // Отправка данных
    }

    // Метод для возврата на предыдущую активность
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}