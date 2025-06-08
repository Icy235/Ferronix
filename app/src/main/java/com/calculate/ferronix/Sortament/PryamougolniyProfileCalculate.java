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

public class PryamougolniyProfileCalculate extends AppCompatActivity {

    private EditText editTextDensity, editTextLength, editTextSideA, editTextSideB, editTextPricePerKg, editTextQuantity;
    private TextView total, textViewLength, textViewUnit;
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

    private String[] materials;

    // Arrays for materials (will be initialized from string resources in onCreate)
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
        setContentView(R.layout.profile_pryamoygolniy_caluclate);

        // --- Initialize material and grade arrays using string resources ---
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
        // --- End of array initialization ---

        initializeViews(); // Initialize all UI elements
        setupListeners(); // Set up event listeners for buttons and input fields

        // Set the initial active button for calculation mode
        // By default, it should calculate weight based on length input
        textViewLength.setText(R.string.length); // Display "Длина" above length input
        textViewUnit.setText(R.string.unit_mm); // Display "мм" for unit (as per your original code)
        editTextLength.setHint(R.string.length); // Set hint to "Длина"
        setActiveButton(btnGoWeight, btnGoLength); // Highlight "Calculate Weight" button
    }

    // Method to initialize all UI elements
    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLength); // This EditText's purpose changes (length or weight input)
        editTextSideA = findViewById(R.id.editTextSideA); // Side A (Width)
        editTextSideB = findViewById(R.id.editTextSideB); // Side B (Height)
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        total = findViewById(R.id.textViewTotal); // This TextView displays the total result
        textViewLength = findViewById(R.id.textViewLength); // TextView for label "Длина" or "Масса"
        textViewUnit = findViewById(R.id.textViewUnit); // TextView for unit "мм" or "кг"
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight); // Button to switch to Weight calculation mode
        btnGoLength = findViewById(R.id.btnGoLength); // Button to switch to Length calculation mode

        // Robust null check for all crucial UI elements
        if (editTextDensity == null || editTextLength == null || editTextSideA == null ||
                editTextSideB == null || editTextPricePerKg == null || editTextQuantity == null ||
                total == null || textViewLength == null || textViewUnit == null ||
                btnCalculate == null || btnMaterial == null || btnMark == null ||
                btnGoWeight == null || btnGoLength == null) {

            Toast.makeText(this, R.string.log_init_error, Toast.LENGTH_LONG).show(); // Show user-friendly toast
            finish(); // Finish activity if critical UI elements are missing
        }
    }

    // Method to set up event listeners
    private void setupListeners() {
        // Listener for the "Calculate" button
        findViewById(R.id.btnCalculate).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Determine which calculation to perform based on the current label of textViewLength
            if (textViewLength.getText().toString().equals(getString(R.string.mass))) {
                calculateLengthOutput(); // User inputs mass, calculate length
            } else {
                calculateWeightOutput(); // User inputs length, calculate weight
            }
        });

        // Listener for the "Material" button
        btnMaterial.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showMaterialMenu();
        });

        // Listener for the "Mark" button
        btnMark.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            handleMarkButtonClick();
        });

        // Listener for "Calculate Weight" mode button
        btnGoWeight.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Switch to "Calculate Weight" mode: user inputs LENGTH, gets WEIGHT
            textViewLength.setText(R.string.length); // Change label to "Длина"
            textViewUnit.setText(R.string.unit_mm); // Change unit to "мм" (as per your layout)
            editTextLength.setHint(R.string.length); // Change hint to "Длина"
            setActiveButton(btnGoWeight, btnGoLength); // Highlight "Calculate Weight"
        });

        // Listener for "Calculate Length" mode button
        btnGoLength.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Switch to "Calculate Length" mode: user inputs WEIGHT, gets LENGTH
            textViewLength.setText(R.string.mass); // Change label to "Масса"
            textViewUnit.setText(R.string.unit_kg); // Change unit to "кг"
            editTextLength.setHint(R.string.mass); // Change hint to "Масса"
            setActiveButton(btnGoLength, btnGoWeight); // Highlight "Calculate Length"
        });
    }

    // Method to set the active/inactive state of the calculation mode buttons
    @SuppressLint("ResourceAsColor") // Suppress warning as ContextCompat.getColor is used
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        // Set background tint and text color for the active button
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Set background tint and text color for the inactive button
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    // Handles the click on the "Mark" (grade) selection button
    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
        // Check if a material has been selected (not the default "Материал" text)
        if (material.equals(getString(R.string.material))) {
            Toast.makeText(this, R.string.error_no_material, Toast.LENGTH_SHORT).show();
        } else {
            showGradeMenu(material); // Show the grade selection menu for the chosen material
        }
    }

    // Displays the material selection menu
    private void showMaterialMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnMaterial);
        for (String material : materials) {
            popupMenu.getMenu().add(material);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            btnMaterial.setText(item.getTitle()); // Set the selected material
            btnMark.setText(R.string.mark); // Reset the mark to default
            editTextDensity.setText(""); // Clear the density field
            return true;
        });
        popupMenu.show();
    }

    // Displays the grade selection menu based on the selected material
    private void showGradeMenu(String material) {
        PopupMenu popupMenu = new PopupMenu(this, btnMark);
        String[] grades;
        double[] densities;

        // Determine which grade and density arrays to use
        if (material.equals(getString(R.string.material_black_metal))) {
            grades = blackMetalGrades;
            densities = blackMetalDensities;
        } else if (material.equals(getString(R.string.material_stainless_steel))) { // Changed from "Нержавейка" to string resource
            grades = stainlessSteelGrades;
            densities = stainlessSteelDensities;
        } else if (material.equals(getString(R.string.material_aluminum))) {
            grades = aluminumGrades;
            densities = aluminumDensities;
        } else {
            // This case should ideally not be reached if material menu works correctly
            Log.e("PryamougolniyProfile", getString(R.string.error_unknown_material) + ": " + material);
            Toast.makeText(this, R.string.error_unknown_material, Toast.LENGTH_SHORT).show();
            return;
        }

        for (String grade : grades) {
            popupMenu.getMenu().add(grade); // Add grades to the menu
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String grade = Objects.requireNonNull(item.getTitle()).toString();
            btnMark.setText(grade); // Set the selected grade

            // Find the index of the selected grade and set the corresponding density
            int index = Arrays.asList(grades).indexOf(grade);
            if (index != -1 && index < densities.length) {
                // Format density to two decimal places, using Locale.US for decimal point consistency
                String formattedDensity = String.format(Locale.US, "%.2f", densities[index]);
                editTextDensity.setText(formattedDensity);
            } else {
                Log.e("PryamougolniyProfile", getString(R.string.log_density_error) + ": Grade=" + grade + ", Material=" + material);
                Toast.makeText(this, R.string.log_density_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    // Common input validation method for Rectangular Profile dimensions
    private boolean validateInputs(String densityStr, String mainInputStr, String sideAStr, String sideBStr) {
        // Check for empty fields
        if (densityStr.isEmpty() || mainInputStr.isEmpty() || sideAStr.isEmpty() || sideBStr.isEmpty()) {
            total.setText(R.string.error_empty_fields);
            return false;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double mainValue = Double.parseDouble(mainInputStr); // This is either length or weight
            double sideA = Double.parseDouble(sideAStr);
            double sideB = Double.parseDouble(sideBStr);

            // Check for non-positive values
            if (density <= 0 || mainValue <= 0 || sideA <= 0 || sideB <= 0) {
                total.setText(R.string.error_negative_values);
                return false;
            }
            return true; // Validation passed
        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("PryamougolniyProfile", getString(R.string.log_parsing_error) + e.getMessage());
            return false; // Validation failed due to number format
        }
    }

    // Method to calculate weight when the user inputs length
    private void calculateWeightOutput() {
        // Get and trim all input strings
        String densityStr = editTextDensity.getText().toString().trim();
        String lengthInputStr = editTextLength.getText().toString().trim(); // User inputs length (mm)
        String sideAStr = editTextSideA.getText().toString().trim();
        String sideBStr = editTextSideB.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(densityStr, lengthInputStr, sideAStr, sideBStr)) {
            return; // Exit if validation fails
        }

        try {
            // Parse values to double
            double density = Double.parseDouble(densityStr); // g/cm³
            double lengthInputMm = Double.parseDouble(lengthInputStr); // mm
            double sideA = Double.parseDouble(sideAStr); // mm
            double sideB = Double.parseDouble(sideBStr); // mm

            // Convert units
            double sideACm = sideA * MM_TO_CM; // mm -> cm
            double sideBCm = sideB * MM_TO_CM; // mm -> cm
            double lengthInputCm = lengthInputMm * MM_TO_CM; // mm -> cm
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // g/cm³ -> kg/cm³

            // Calculate cross-sectional area (cm²)
            double areaCm2 = sideACm * sideBCm;

            // Calculate volume (cm³)
            double volumeCm3 = areaCm2 * lengthInputCm;

            // Calculate weight per piece (kg)
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
                    Log.e("PryamougolniyProfile", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Display total weight if quantity is provided
            if (isQuantityProvided) {
                double totalWeightValue = weightPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_mass_unit_format), totalWeightValue));
            }

            // Calculate and display cost if price per kg is provided
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        total.setText(R.string.error_negative_price);
                        return;
                    }

                    double pricePerUnit = pricePerKg * weightPerPiece; // Cost per single piece
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));

                    // Display total cost only if quantity is provided
                    if (isQuantityProvided) {
                        double totalCost = pricePerKg * weightPerPiece * totalQuantity;
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    total.setText(R.string.error_number_format);
                    Log.e("PryamougolniyProfile", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight)); // Send analytics for weight calculation
            total.setText(result.toString()); // Display the final result

        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("PryamougolniyProfile", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Method to calculate length when the user inputs weight
    private void calculateLengthOutput() {
        // Get and trim all input strings
        String densityStr = editTextDensity.getText().toString().trim();
        String weightInputStr = editTextLength.getText().toString().trim(); // User inputs weight (kg)
        String sideAStr = editTextSideA.getText().toString().trim();
        String sideBStr = editTextSideB.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(densityStr, weightInputStr, sideAStr, sideBStr)) {
            return; // Exit if validation fails
        }

        try {
            // Parse values to double
            double density = Double.parseDouble(densityStr); // g/cm³
            double weightInputKg = Double.parseDouble(weightInputStr); // kg
            double sideA = Double.parseDouble(sideAStr); // mm
            double sideB = Double.parseDouble(sideBStr); // mm

            // Convert units
            double sideACm = sideA * MM_TO_CM; // mm -> cm
            double sideBCm = sideB * MM_TO_CM; // mm -> cm
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // g/cm³ -> kg/cm³

            // Calculate cross-sectional area (cm²)
            double areaCm2 = sideACm * sideBCm;

            // Calculate length per piece (cm)
            double lengthPerPieceCm = weightInputKg / (areaCm2 * densityKgPerCm3);

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), lengthPerPieceCm / 100)); // Display in meters

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
                    Log.e("PryamougolniyProfile", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Display total length if quantity is provided
            if (isQuantityProvided) {
                double totalLengthValueMeters = (lengthPerPieceCm * totalQuantity) / 100;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_length_unit_format), totalLengthValueMeters));
            }

            // Calculate and display cost if price per kg is provided
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        total.setText(R.string.error_negative_price);
                        return;
                    }

                    double pricePerUnit = pricePerKg * weightInputKg; // Cost per single piece
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));

                    // Display total cost only if quantity is provided
                    if (isQuantityProvided) {
                        double totalCost = pricePerKg * weightInputKg * totalQuantity;
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    total.setText(R.string.error_number_format);
                    Log.e("PryamougolniyProfile", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length)); // Send analytics for length calculation
            total.setText(result.toString()); // Display the final result

        } catch (NumberFormatException e) {
            total.setText(R.string.error_number_format);
            Log.e("PryamougolniyProfile", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Method to send analytics data
    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_rectangular_profile)); // Use the new string resource
        NetworkHelper.sendCalculationData(this, analytics);
    }

    // Handles the back button click, navigating to SelectForm activity
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}