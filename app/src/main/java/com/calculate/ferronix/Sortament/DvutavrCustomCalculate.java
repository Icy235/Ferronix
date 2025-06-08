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

public class DvutavrCustomCalculate extends AppCompatActivity {

    private EditText editTextDensity, editTextLength, editTextHeightH, editTextWidthB, editTextThicknessS, editTextThicknessT, editTextPricePerKg, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

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

    // Constants for unit conversion
    private static final double MM_TO_CM = 0.1;
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dvutavr_custom_calculate);

        // Initialize material and grade arrays using string resources
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

        initializeViews();
        setupListeners();

        // Set initial UI state: calculate weight by length by default
        textViewLength.setText(R.string.length);
        textViewUnit.setText(R.string.unit_meter);
        editTextLength.setHint(R.string.length);
        setActiveButton(btnGoWeight, btnGoLength);
    }

    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLength);
        editTextHeightH = findViewById(R.id.editTextHeightH);
        editTextWidthB = findViewById(R.id.editTextWidthB);
        editTextThicknessS = findViewById(R.id.editTextThicknessS);
        editTextThicknessT = findViewById(R.id.editTextThicknessT);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        textViewLength = findViewById(R.id.textViewLength);
        textViewUnit = findViewById(R.id.textViewUnit);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);

        // Robust null check for all crucial UI elements
        if (editTextDensity == null || editTextLength == null || editTextHeightH == null ||
                editTextWidthB == null || editTextThicknessS == null || editTextThicknessT == null ||
                editTextPricePerKg == null || editTextQuantity == null || totalWeight == null ||
                textViewLength == null || textViewUnit == null || btnCalculate == null ||
                btnMaterial == null || btnMark == null || btnGoWeight == null || btnGoLength == null) {

            Toast.makeText(this, R.string.log_init_error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupListeners() {
        findViewById(R.id.btnCalculate).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Determine calculation mode based on the current label of editTextLength
            if (textViewLength.getText().toString().equals(getString(R.string.mass))) {
                calculateLengthOutput(); // User inputs mass, calculate length
            } else {
                calculateWeightOutput(); // User inputs length, calculate weight
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
            // Switch to "Calculate Weight" mode: user inputs LENGTH, gets WEIGHT
            textViewLength.setText(R.string.length);
            textViewUnit.setText(R.string.unit_meter);
            editTextLength.setHint(R.string.length);
            setActiveButton(btnGoWeight, btnGoLength);
        });

        btnGoLength.setOnClickListener(v -> {
            // Switch to "Calculate Length" mode: user inputs WEIGHT, gets LENGTH
            textViewLength.setText(R.string.mass);
            textViewUnit.setText(R.string.unit_kg);
            editTextLength.setHint(R.string.mass);
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
            editTextDensity.setText(""); // Reset density when material changes
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
            Log.e("DvutavrCustomCalculate", getString(R.string.error_unknown_material) + ": " + material);
            Toast.makeText(this, R.string.error_unknown_material, Toast.LENGTH_SHORT).show();
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
                String formattedDensity = String.format(Locale.US, "%.2f", densities[index]); // Use US locale for decimal point
                editTextDensity.setText(formattedDensity);
            } else {
                Log.e("DvutavrCustomCalculate", getString(R.string.log_density_error) + ": Mark=" + grade + ", Material=" + material);
                Toast.makeText(this, R.string.log_density_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    // Common input validation method for Dvutavr
    private boolean validateInputs(String densityStr, String mainInputStr, String heightHStr, String widthBStr, String thicknessSStr, String thicknessTStr) {
        if (densityStr.isEmpty() || mainInputStr.isEmpty() || heightHStr.isEmpty() || widthBStr.isEmpty() || thicknessSStr.isEmpty() || thicknessTStr.isEmpty()) {
            totalWeight.setText(R.string.error_empty_fields);
            return false;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double mainValue = Double.parseDouble(mainInputStr); // This is either length or weight
            double heightH = Double.parseDouble(heightHStr);
            double widthB = Double.parseDouble(widthBStr);
            double thicknessS = Double.parseDouble(thicknessSStr); // Web thickness
            double thicknessT = Double.parseDouble(thicknessTStr); // Flange thickness

            if (density <= 0 || mainValue <= 0 || heightH <= 0 || widthB <= 0 || thicknessS <= 0 || thicknessT <= 0) {
                totalWeight.setText(R.string.error_negative_values);
                return false;
            }

            // Additional validation for Dvutavr dimensions
            if (2 * thicknessT >= heightH) {
                totalWeight.setText(R.string.error_flange_thickness_too_large); // New string
                return false;
            }
            if (thicknessS >= widthB) {
                totalWeight.setText(R.string.error_web_thickness_too_large); // New string
                return false;
            }
            if (thicknessS >= heightH) {
                totalWeight.setText(R.string.error_web_thickness_height_relation); // New string
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("DvutavrCustomCalculate", getString(R.string.log_parsing_error) + e.getMessage());
            return false;
        }
    }

    // Helper method to calculate cross-sectional area of Dvutavr in cm²
    private double calculateCrossSectionalAreaCm2(double heightH_MM, double widthB_MM, double thicknessS_MM, double thicknessT_MM) {
        double heightHCm = heightH_MM * MM_TO_CM;
        double widthBCm = widthB_MM * MM_TO_CM;
        double thicknessSCm = thicknessS_MM * MM_TO_CM;
        double thicknessTCm = thicknessT_MM * MM_TO_CM;

        // Area of two flanges + Area of web
        return 2 * (widthBCm * thicknessTCm) + (heightHCm - 2 * thicknessTCm) * thicknessSCm;
    }

    // Method to calculate weight when user inputs length
    private void calculateWeightOutput() {
        String densityStr = editTextDensity.getText().toString().trim();
        String lengthInputStr = editTextLength.getText().toString().trim(); // User inputs length (meters)
        String heightHStr = editTextHeightH.getText().toString().trim();
        String widthBStr = editTextWidthB.getText().toString().trim();
        String thicknessSStr = editTextThicknessS.getText().toString().trim();
        String thicknessTStr = editTextThicknessT.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (!validateInputs(densityStr, lengthInputStr, heightHStr, widthBStr, thicknessSStr, thicknessTStr)) {
            return;
        }

        try {
            double density = Double.parseDouble(densityStr); // g/cm³
            double lengthInputMeters = Double.parseDouble(lengthInputStr); // meters
            double heightH_MM = Double.parseDouble(heightHStr);
            double widthB_MM = Double.parseDouble(widthBStr);
            double thicknessS_MM = Double.parseDouble(thicknessSStr);
            double thicknessT_MM = Double.parseDouble(thicknessTStr);

            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // kg/cm³

            // Calculate cross-sectional area in cm²
            double crossSectionAreaCm2 = calculateCrossSectionalAreaCm2(heightH_MM, widthB_MM, thicknessS_MM, thicknessT_MM);

            // Calculate weight per piece (kg)
            double weightPerPiece = crossSectionAreaCm2 * (lengthInputMeters * 100) * densityKgPerCm3;

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weightPerPiece));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty();

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        totalWeight.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("DvutavrCustomCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            if (isQuantityProvided) {
                double totalWeightValue = weightPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_mass_unit_format), totalWeightValue));
            }

            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        totalWeight.setText(R.string.error_negative_price);
                        return;
                    }

                    double totalMassForCost = weightPerPiece * totalQuantity;
                    double totalCost = pricePerKg * totalMassForCost;
                    double pricePerUnit = totalCost / totalQuantity;

                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));
                    if (isQuantityProvided) {
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("DvutavrCustomCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight));
            totalWeight.setText(result.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("DvutavrCustomCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Method to calculate length when user inputs weight
    private void calculateLengthOutput() {
        String densityStr = editTextDensity.getText().toString().trim();
        String weightInputStr = editTextLength.getText().toString().trim(); // User inputs weight (kg)
        String heightHStr = editTextHeightH.getText().toString().trim();
        String widthBStr = editTextWidthB.getText().toString().trim();
        String thicknessSStr = editTextThicknessS.getText().toString().trim();
        String thicknessTStr = editTextThicknessT.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (!validateInputs(densityStr, weightInputStr, heightHStr, widthBStr, thicknessSStr, thicknessTStr)) {
            return;
        }

        try {
            double density = Double.parseDouble(densityStr); // g/cm³
            double weightInputKg = Double.parseDouble(weightInputStr); // kg
            double heightH_MM = Double.parseDouble(heightHStr);
            double widthB_MM = Double.parseDouble(widthBStr);
            double thicknessS_MM = Double.parseDouble(thicknessSStr);
            double thicknessT_MM = Double.parseDouble(thicknessTStr);

            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // kg/cm³

            // Calculate cross-sectional area in cm²
            double crossSectionAreaCm2 = calculateCrossSectionalAreaCm2(heightH_MM, widthB_MM, thicknessS_MM, thicknessT_MM);

            // Calculate volume from weight and density
            double volumeCm3 = weightInputKg / densityKgPerCm3;

            // Calculate length per piece (meters)
            double lengthPerPiece = volumeCm3 / crossSectionAreaCm2 / 100; // cm -> meters

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), lengthPerPiece));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty();

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        totalWeight.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("DvutavrCustomCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            if (isQuantityProvided) {
                double totalLengthValue = lengthPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_length_unit_format), totalLengthValue));
            }

            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        totalWeight.setText(R.string.error_negative_price);
                        return;
                    }

                    double totalMassForCost = weightInputKg * totalQuantity; // Use input weight for cost calculation
                    double totalCost = pricePerKg * totalMassForCost;
                    double pricePerUnit = totalCost / totalQuantity;

                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));
                    if (isQuantityProvided) {
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    totalWeight.setText(R.string.error_number_format);
                    Log.e("DvutavrCustomCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length));
            totalWeight.setText(result.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("DvutavrCustomCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_dvutavr));
        NetworkHelper.sendCalculationData(this, analytics);
    }

    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}