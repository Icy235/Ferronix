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

    private static final double MM_TO_CM = 0.1;
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001;

    private EditText editTextDensity, editTextLength, editTextSideA, editTextSideB, editTextWall, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

    private String[] materials;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.corner_calculate);

        materials = new String[]{
                getString(R.string.material_black_metal),
                getString(R.string.material_stainless_steel),
                getString(R.string.material_aluminum)
        };

        initializeViews();
        setupListeners();
        setActiveButton(btnGoWeight, btnGoLength);
    }

    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLengthCornerW);
        editTextSideA = findViewById(R.id.editTextSideA);
        editTextSideB = findViewById(R.id.editTextSideB);
        editTextWall = findViewById(R.id.editTextWallCornerW);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        textViewLength = findViewById(R.id.textViewLength);
        textViewUnit = findViewById(R.id.textViewUnit);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);
    }

    private void setupListeners() {
        if (editTextDensity == null || editTextLength == null || editTextSideA == null ||
                editTextSideB == null || editTextWall == null || editTextPricePerKg == null ||
                editTextQuantity == null) {
            Log.e("InitError", getString(R.string.log_init_error));
            finish();
            return;
        }

        findViewById(R.id.btnCalculateCornerW).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            if (textViewLength.getText().toString().equals(getString(R.string.mass_unit))) {
                calculateWeight();
            } else {
                calculateLength();
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
            textViewLength.setText(R.string.length_unit);
            textViewUnit.setText(R.string.unit_mm);
            editTextLength.setHint(R.string.length_unit);
            setActiveButton(btnGoWeight, btnGoLength);
        });

        btnGoLength.setOnClickListener(v -> {
            textViewLength.setText(R.string.mass_unit);
            textViewUnit.setText(R.string.unit_kg);
            editTextLength.setHint(R.string.mass_unit);
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

    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
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
            btnMark.setText(R.string.mark);
            editTextDensity.setText("");
            return true;
        });
        popupMenu.show();
    }

    private void showGradeMenu(String material) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

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
                String formattedDensity = String.format(Locale.getDefault(), "%.2f", densities[index]);
                editTextDensity.setText(formattedDensity);
            } else {
                Log.e("DensityError", getString(R.string.log_density_error));
            }
            return true;
        });
        popupMenu.show();
    }

    private boolean validateInput(String densityStr, String mainStr, String sideAStr, String sideBStr, String wallStr) {
        if (densityStr.isEmpty() || mainStr.isEmpty() || sideAStr.isEmpty() || sideBStr.isEmpty() || wallStr.isEmpty()) {
            totalWeight.setText(R.string.error_empty_fields);
            return true;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double mainValue = Double.parseDouble(mainStr);
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);
            double wall = Double.parseDouble(wallStr);

            if (density <= 0 || mainValue <= 0 || sideA <= 0 || sideB <= 0 || wall <= 0) {
                totalWeight.setText(R.string.error_negative_values);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CalcError", getString(R.string.log_parsing_error), e);
            return true;
        }
    }

    private double calculateUnitWeight(double density, double length, double sideA, double sideB, double wallThickness) {
        double sideACm = sideA * MM_TO_CM;
        double sideBCm = sideB * MM_TO_CM;
        double wallThicknessCm = wallThickness * MM_TO_CM;
        double lengthCm = length * MM_TO_CM;
        double densityInKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3;
        double crossSectionArea = (sideACm + sideBCm - wallThicknessCm) * wallThicknessCm;
        double volume = crossSectionArea * lengthCm;
        return volume * densityInKgPerCm3;
    }

    private double calculateUnitLength(double density, double weight, double sideA, double sideB, double wallThickness) {
        double sideACm = sideA * MM_TO_CM;
        double sideBCm = sideB * MM_TO_CM;
        double wallThicknessCm = wallThickness * MM_TO_CM;
        double densityInKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3;
        double crossSectionArea = (sideACm + sideBCm - wallThicknessCm) * wallThicknessCm;
        return (weight / (crossSectionArea * densityInKgPerCm3)) / 100; // см → м
    }

    private void appendQuantityAndCost(StringBuilder result, double unitValue, double quantity, String pricePerKgStr, boolean isWeight) {
        result.append(String.format(Locale.getDefault(),
                getString(isWeight ? R.string.mass_unit_format : R.string.length_unit_format), unitValue));
        double totalValue = unitValue * quantity;
        result.append(String.format(Locale.getDefault(),
                getString(isWeight ? R.string.total_mass_unit_format : R.string.total_length_unit_format), totalValue));

        if (!pricePerKgStr.isEmpty()) {
            try {
                double pricePerKg = Double.parseDouble(pricePerKgStr);
                double totalCost = pricePerKg * (isWeight ? unitValue : totalValue) * quantity;
                double pricePerUnit = totalCost / quantity;
                result.append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit))
                        .append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
            } catch (NumberFormatException e) {
                totalWeight.setText(R.string.error_number_format);
                Log.e("CalcError", getString(R.string.log_parsing_error), e);
            }
        }
    }

    private void calculateLength() {
        String densityStr = editTextDensity.getText().toString().trim();
        String lengthStr = editTextLength.getText().toString().trim();
        String sideAStr = editTextSideA.getText().toString().trim();
        String sideBStr = editTextSideB.getText().toString().trim();
        String wallStr = editTextWall.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (validateInput(densityStr, lengthStr, sideAStr, sideBStr, wallStr)) {
            return;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double length = Double.parseDouble(lengthStr);
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);
            double wallThickness = Double.parseDouble(wallStr);

            double weight = calculateUnitWeight(density, length, sideA, sideB, wallThickness);
            StringBuilder result = new StringBuilder();

            if (quantityStr.isEmpty()) {
                result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText(R.string.error_negative_quantity);
                    return;
                }
                appendQuantityAndCost(result, weight, quantity, pricePerKgStr, true);
            }

            sendAnalytics(getString(R.string.analytics_type_length));
            totalWeight.setText(result.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CalcError", getString(R.string.log_parsing_error), e);
        }
    }

    private void calculateWeight() {
        String densityStr = editTextDensity.getText().toString().trim();
        String weightStr = editTextLength.getText().toString().trim();
        String sideAStr = editTextSideA.getText().toString().trim();
        String sideBStr = editTextSideB.getText().toString().trim();
        String wallStr = editTextWall.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (validateInput(densityStr, weightStr, sideAStr, sideBStr, wallStr)) {
            return;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double weight = Double.parseDouble(weightStr);
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);
            double wallThickness = Double.parseDouble(wallStr);

            double length = calculateUnitLength(density, weight, sideA, sideB, wallThickness);
            StringBuilder result = new StringBuilder();

            if (quantityStr.isEmpty()) {
                result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), length));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText(R.string.error_negative_quantity);
                    return;
                }
                appendQuantityAndCost(result, length, quantity, pricePerKgStr, false);
            }

            sendAnalytics(getString(R.string.analytics_type_weight));
            totalWeight.setText(result.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CalcError", getString(R.string.log_parsing_error), e);
        }
    }

    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_corner));
        NetworkHelper.sendCalculationData(this, analytics);
    }

    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}