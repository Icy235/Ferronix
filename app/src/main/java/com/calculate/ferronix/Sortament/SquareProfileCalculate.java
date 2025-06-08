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

public class SquareProfileCalculate extends AppCompatActivity {

    private EditText editTextDensity, editTextLength, editTextSquareA, editTextPricePerKg, editTextQuantity;
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
        setContentView(R.layout.profile_square_calculate);

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
        textViewUnit.setText(R.string.unit_mm); // Единица измерения "мм"
        editTextLength.setHint(R.string.length); // Подсказка "Длина"
        setActiveButton(btnGoWeight, btnGoLength); // Выделяем кнопку "Рассчитать Вес"
    }

    // Метод для инициализации всех UI элементов
    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLength); // Это поле ввода будет использоваться либо для длины, либо для массы
        editTextSquareA = findViewById(R.id.editTextSquareA); // Сторона квадрата A
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        total = findViewById(R.id.textViewTotal); // Это TextView отображает общий результат
        textViewLength = findViewById(R.id.textViewLength); // TextView для метки "Длина" или "Масса"
        textViewUnit = findViewById(R.id.textViewUnit); // TextView для единицы измерения "мм" или "кг"
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight); // Кнопка для переключения в режим расчета массы
        btnGoLength = findViewById(R.id.btnGoLength); // Кнопка для переключения в режим расчета длины

        // Надежная проверка на null для всех критических элементов UI
        if (editTextDensity == null || editTextLength == null || editTextSquareA == null ||
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
            textViewUnit.setText(R.string.unit_mm); // Меняем единицу измерения на "мм"
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
            Log.e("SquareProfile", getString(R.string.error_unknown_material) + ": " + material);
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
                Log.e("SquareProfile", getString(R.string.log_density_error) + ": Grade=" + grade + ", Material=" + material);
                Toast.makeText(this, R.string.log_density_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    // Общий метод валидации ввода для квадратного профиля
    private boolean validateInputs(String densityStr, String mainInputStr, String squareAStr) {
        // Проверка на пустые поля
        if (densityStr.isEmpty() || mainInputStr.isEmpty() || squareAStr.isEmpty()) {
            total.setText(R.string.error_empty_fields);
            return false;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double mainValue = Double.parseDouble(mainInputStr); // Это либо длина, либо масса
            double squareA = Double.parseDouble(squareAStr);

            // Проверка на неотрицательные значения
            if (density <= 0 || mainValue <= 0 || squareA <= 0) {
                total.setText(R.string.error_negative_values);
                return false;
            }
            return true; // Валидация пройдена
        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("SquareProfile", getString(R.string.log_parsing_error) + e.getMessage());
            return false; // Валидация не пройдена из-за формата числа
        }
    }

    // Метод для расчета массы, когда пользователь вводит длину
    private void calculateWeightOutput() {
        // Получаем и обрезаем все входные строки
        String densityStr = editTextDensity.getText().toString().trim();
        String lengthInputStr = editTextLength.getText().toString().trim(); // Пользователь вводит длину (мм)
        String squareAStr = editTextSquareA.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Валидация ввода
        if (!validateInputs(densityStr, lengthInputStr, squareAStr)) {
            return; // Выходим, если валидация не пройдена
        }

        try {
            // Парсим значения в double
            double density = Double.parseDouble(densityStr); // г/см³
            double lengthInputMm = Double.parseDouble(lengthInputStr); // мм
            double squareA = Double.parseDouble(squareAStr); // мм

            // Конвертируем единицы
            double squareACm = squareA * MM_TO_CM; // мм -> см
            double lengthInputCm = lengthInputMm * MM_TO_CM; // мм -> см
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // г/см³ -> кг/см³

            // Площадь поперечного сечения (см²)
            double areaCm2 = squareACm * squareACm;

            // Объем материала (см³)
            double volumeCm3 = areaCm2 * lengthInputCm;

            // Вес одной единицы (кг)
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
                    Log.e("SquareProfile", getString(R.string.log_parsing_error) + e.getMessage());
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
                    Log.e("SquareProfile", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight)); // Отправляем аналитику для расчета массы
            total.setText(result.toString()); // Отображаем конечный результат

        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("SquareProfile", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Метод для расчета длины, когда пользователь вводит массу
    private void calculateLengthOutput() {
        // Получаем и обрезаем все входные строки
        String densityStr = editTextDensity.getText().toString().trim();
        String weightInputStr = editTextLength.getText().toString().trim(); // Пользователь вводит массу (кг)
        String squareAStr = editTextSquareA.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Валидация ввода
        if (!validateInputs(densityStr, weightInputStr, squareAStr)) {
            return; // Выходим, если валидация не пройдена
        }

        try {
            // Парсим значения в double
            double density = Double.parseDouble(densityStr); // г/см³
            double weightInputKg = Double.parseDouble(weightInputStr); // кг
            double squareA = Double.parseDouble(squareAStr); // мм

            // Конвертируем единицы
            double squareACm = squareA * MM_TO_CM; // мм -> см
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // г/см³ -> кг/см³

            // Площадь поперечного сечения (см²)
            double areaCm2 = squareACm * squareACm;

            // Объем материала (см³)
            double volumeCm3 = weightInputKg / densityKgPerCm3;

            // Длина одной единицы (метры)
            double lengthPerPieceMeters = volumeCm3 / areaCm2 / 100; // Конвертируем см в метры

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
                    Log.e("SquareProfile", getString(R.string.log_parsing_error) + e.getMessage());
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
                    Log.e("SquareProfile", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length)); // Отправляем аналитику для расчета длины
            total.setText(result.toString()); // Отображаем конечный результат

        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("SquareProfile", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Метод для отправки аналитических данных
    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_square_profile)); // Используем новый строковый ресурс
        NetworkHelper.sendCalculationData(this, analytics);
    }

    // Обрабатывает нажатие кнопки "Назад", переходя к активности SelectForm
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}