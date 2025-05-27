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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.calculate.ferronix.R;
import com.calculate.ferronix.Sortament.gostPdf.Gost34028_2016;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Control.NetworkHelper;

public class ArmaturaCalculate extends AppCompatActivity {

    private EditText editTextLength, editTextPricePerPice, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnDiametr, btnGoWeight, btnGoLength;

    // Массивы для арматуры
    private final String[] NominalDiametr = {
            "4.0", "4.5", "5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5",
            "9.0", "9.5", "10.0", "11.0", "12.0", "13.0", "14.0", "15.0", "16.0",
            "17.0", "18.0", "19.0", "20.0", "22.0", "25.0", "28.0", "32.0", "36.0", "40.0"
    };

    private final double[] ArmaturaMass1Meter = {
            0.099, 0.125, 0.154, 0.187, 0.222, 0.261, 0.302, 0.347, 0.395, 0.445,
            0.499, 0.556, 0.617, 0.746, 0.888, 1.042, 1.208, 1.387, 1.578, 1.782,
            1.998, 2.226, 2.466, 2.984, 3.853, 4.834, 6.313, 7.990, 9.865
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.armatura_calculate);

        // Инициализация элементов интерфейса
        editTextLength = findViewById(R.id.editTextLength);
        editTextPricePerPice = findViewById(R.id.editTextPricePerPice);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        textViewLength = findViewById(R.id.textViewLength);
        textViewUnit = findViewById(R.id.textViewUnit);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnDiametr = findViewById(R.id.btnDiametr);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);

        // Устанавливаем активную кнопку при запуске
        setActiveButton(btnGoWeight, btnGoLength);

        // Обработчики кликов
        btnCalculate.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            if (textViewLength.getText().toString().equals("Масса")) {
                calculateWeight();
            } else {
                calculateLength();
            }
        });

        btnDiametr.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showDiametrMenu();
        });

        btnGoWeight.setOnClickListener(v -> {
            textViewLength.setText("Длина");
            textViewUnit.setText("м");
            editTextLength.setHint("Длина");
            setActiveButton(btnGoWeight, btnGoLength);
        });

        btnGoLength.setOnClickListener(v -> {
            textViewLength.setText("Масса");
            textViewUnit.setText("кг");
            editTextLength.setHint("Масса");
            setActiveButton(btnGoLength, btnGoWeight);
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void showDiametrMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnDiametr);
        for (String diameter : NominalDiametr) {
            popupMenu.getMenu().add(diameter);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedDiametr = Objects.requireNonNull(item.getTitle()).toString();
            btnDiametr.setText(selectedDiametr);
            return true;
        });
        popupMenu.show();
    }

    private void calculateLength() {
        try {
            String lengthStr = editTextLength.getText().toString().trim();
            String pricePerPiceStr = editTextPricePerPice.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (lengthStr.isEmpty()) {
                totalWeight.setText("Укажите длину арматуры!");
                return;
            }

            double length = Double.parseDouble(lengthStr);
            if (length <= 0) {
                totalWeight.setText("Длина должна быть > 0");
                return;
            }

            String selectedDiametr = btnDiametr.getText().toString();
            int index = Arrays.asList(NominalDiametr).indexOf(selectedDiametr);
            if (index == -1) {
                totalWeight.setText("Выберите диаметр арматуры!");
                return;
            }

            double massPerMeter = ArmaturaMass1Meter[index];
            double weight = massPerMeter * length;

            StringBuilder resultText = new StringBuilder();
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Масса арматуры: %.3f кг", weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText("Количество должно быть > 0");
                    return;
                }

                double totalWeightValue = weight * quantity;
                resultText.append(String.format(Locale.US, "Масса еденицы: %.3f кг", weight));
                resultText.append(String.format(Locale.US, "\nОбщая масса: %.3f кг", totalWeightValue));

                if (!pricePerPiceStr.isEmpty()) {
                    double pricePerPiece = Double.parseDouble(pricePerPiceStr);
                    double totalCost = pricePerPiece * quantity;
                    double pricePerUnit = pricePerPiece;


                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    symbols.setGroupingSeparator(' ');
                    DecimalFormat costFormatter = new DecimalFormat("#,##0.00", symbols);

                    resultText.append(String.format(Locale.US, "\nЦена за еденицу: %s руб", costFormatter.format(pricePerUnit)));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %s руб", costFormatter.format(totalCost)));
                }
            }

            Map<String, String> analytics = new HashMap<>();
            analytics.put("Тип", "Длина");
            analytics.put("Шаблон", "Арматура");
            NetworkHelper.sendCalculationData(this, analytics);

            totalWeight.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText("Ошибка в формате чисел");
            Log.e("CalcError", "Parsing error: " + e.getMessage());
        }
    }

    private void calculateWeight() {
        try {
            String massStr = editTextLength.getText().toString().trim();
            String pricePerPiceStr = editTextPricePerPice.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (massStr.isEmpty()) {
                totalWeight.setText("Укажите массу арматуры!");
                return;
            }

            double mass = Double.parseDouble(massStr);
            if (mass <= 0) {
                totalWeight.setText("Масса должна быть > 0");
                return;
            }

            String selectedDiametr = btnDiametr.getText().toString();
            int index = Arrays.asList(NominalDiametr).indexOf(selectedDiametr);
            if (index == -1) {
                totalWeight.setText("Выберите диаметр арматуры!");
                return;
            }

            double massPerMeter = ArmaturaMass1Meter[index];
            double length = mass / massPerMeter;

            StringBuilder resultText = new StringBuilder();
            if (quantityStr.isEmpty()) {
                resultText.append(String.format(Locale.US, "Длина арматуры: %.2f м", length));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText("Количество должно быть > 0");
                    return;
                }

                double totalLengthValue = length * quantity;
                resultText.append(String.format(Locale.US, "Длина еденицы: %.2f м", length));
                resultText.append(String.format(Locale.US, "\nОбщая длина: %.2f м", totalLengthValue));

                if (!pricePerPiceStr.isEmpty()) {
                    double pricePerPiece = Double.parseDouble(pricePerPiceStr);
                    double totalCost = pricePerPiece * quantity;
                    double pricePerUnit = pricePerPiece;

                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    symbols.setGroupingSeparator(' ');
                    DecimalFormat costFormatter = new DecimalFormat("#,##0.00", symbols);

                    resultText.append(String.format(Locale.US, "\nЦена за еденицу: %s руб", costFormatter.format(pricePerUnit)));
                    resultText.append(String.format(Locale.US, "\nОбщая стоимость: %s руб", costFormatter.format(totalCost)));
                }
            }

            Map<String, String> analytics = new HashMap<>();
            analytics.put("Тип", "Вес");
            analytics.put("Шаблон", "Арматура");
            NetworkHelper.sendCalculationData(this, analytics);

            totalWeight.setText(resultText.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText("Ошибка в формате чисел");
            Log.e("CalcError", "Parsing error: " + e.getMessage());
        }
    }

    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
    public void btnGost34028_2016_pdf(View view) {
        startActivity(new Intent(this, Gost34028_2016.class));
        finish();
    }
}