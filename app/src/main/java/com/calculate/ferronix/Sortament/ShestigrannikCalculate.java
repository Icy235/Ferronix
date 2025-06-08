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

public class ShestigrannikCalculate extends AppCompatActivity {

    private EditText editTextDensity, editTextLength, editTextSide, editTextPricePerKg, editTextQuantity;
    private TextView totalOutputTextView, textViewLengthLabel, textViewUnitLabel; // Renamed for clarity
    private Button btnMaterial, btnMark, btnGoWeight, btnGoLength;

    private String[] materials;

    // Material arrays (will be initialized from string resources in onCreate)
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

    // Unit conversion constants
    private static final double MM_TO_CM = 0.1;
    private static final double G_PER_CM3_TO_KG_PER_CM3 = 0.001;
    private static final double CM_TO_M = 0.01; // 1 cm = 0.01 m

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.shestigrannik_calculate);

        // --- Initialize material and grade arrays from string resources ---
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
        // --- End of material array initialization ---

        initializeViews(); // Initialize all UI elements
        setupListeners(); // Set up event listeners for buttons and input fields

        // Set the active button on launch (default: calculate weight from length)
        textViewLengthLabel.setText(R.string.length); // Label "Length" above length input field
        textViewUnitLabel.setText(R.string.unit_meter); // Unit "m"
        editTextLength.setHint(R.string.length); // Hint "Length"
        setActiveButton(btnGoWeight, btnGoLength); // Highlight "Calculate Weight" button
    }

    // Method to initialize all UI elements
    private void initializeViews() {
        editTextDensity = findViewById(R.id.editTextDensity);
        editTextLength = findViewById(R.id.editTextLength);
        editTextSide = findViewById(R.id.editTextSide);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalOutputTextView = findViewById(R.id.textViewLengthTotal);
        textViewLengthLabel = findViewById(R.id.textViewLength);
        textViewUnitLabel = findViewById(R.id.textViewUnit);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnMark = findViewById(R.id.btnMark);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);

        // Robust null check for all critical UI elements
        if (editTextDensity == null || editTextLength == null || editTextSide == null ||
                editTextPricePerKg == null || editTextQuantity == null || totalOutputTextView == null ||
                textViewLengthLabel == null || textViewUnitLabel == null || btnCalculate == null ||
                btnMaterial == null || btnMark == null || btnGoWeight == null || btnGoLength == null) {

            Toast.makeText(this, R.string.log_init_error, Toast.LENGTH_LONG).show();
            finish(); // Close the activity if critical UI elements are missing
        }
    }

    // Method to set up event listeners
    private void setupListeners() {
        // Listener for the "Calculate" button
        findViewById(R.id.btnCalculate).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Determine which calculation to perform based on the current label of textViewLengthLabel
            if (textViewLengthLabel.getText().toString().equals(getString(R.string.mass))) {
                calculateLengthOutput(); // User inputs mass, calculate length
            } else {
                calculateWeightOutput(); // User inputs length, calculate mass
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

        // Listener for switching to "Calculate Weight" mode
        btnGoWeight.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Switch to "Calculate Weight" mode: user inputs LENGTH, gets WEIGHT
            textViewLengthLabel.setText(R.string.length); // Change label to "Length"
            textViewUnitLabel.setText(R.string.unit_meter); // Change unit to "m"
            editTextLength.setHint(R.string.length); // Change hint to "Length"
            setActiveButton(btnGoWeight, btnGoLength); // Highlight "Calculate Weight"
        });

        // Listener for switching to "Calculate Length" mode
        btnGoLength.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Switch to "Calculate Length" mode: user inputs MASS, gets LENGTH
            textViewLengthLabel.setText(R.string.mass); // Change label to "Mass"
            textViewUnitLabel.setText(R.string.unit_kg); // Change unit to "kg"
            editTextLength.setHint(R.string.mass); // Change hint to "Mass"
            setActiveButton(btnGoLength, btnGoWeight); // Highlight "Calculate Length"
        });
    }

    // Method to set the active/inactive state of calculation mode buttons
    @SuppressLint("ResourceAsColor")
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    // Handles the click on the "Mark" (grade) selection button
    private void handleMarkButtonClick() {
        String material = btnMaterial.getText().toString();
        // Check if a material is selected (not the default "Material" text)
        if (material.equals(getString(R.string.material))) {
            Toast.makeText(this, R.string.error_no_material, Toast.LENGTH_SHORT).show();
        } else {
            showGradeMenu(material); // Show grade selection menu for the selected material
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
            btnMark.setText(R.string.mark); // Reset the grade to default
            editTextDensity.setText(""); // Clear density field
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
        } else if (material.equals(getString(R.string.material_stainless_steel))) {
            grades = stainlessSteelGrades;
            densities = stainlessSteelDensities;
        } else if (material.equals(getString(R.string.material_aluminum))) {
            grades = aluminumGrades;
            densities = aluminumDensities;
        } else {
            // This case ideally shouldn't be reached if the material menu works correctly
            Log.e("ShestigrannikCalculate", getString(R.string.error_unknown_material) + ": " + material);
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
                // Format density to two decimal places, using Locale.US for consistent decimal point
                String formattedDensity = String.format(Locale.US, "%.2f", densities[index]);
                editTextDensity.setText(formattedDensity);
            } else {
                Log.e("ShestigrannikCalculate", getString(R.string.log_density_error) + ": Grade=" + grade + ", Material=" + material);
                Toast.makeText(this, R.string.log_density_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    // Common input validation method for hexagonal profile
    private boolean validateInputs(String densityStr, String mainInputStr, String sideStr) {
        // Check for empty fields
        if (densityStr.isEmpty() || mainInputStr.isEmpty() || sideStr.isEmpty()) {
            totalOutputTextView.setText(R.string.error_empty_fields);
            return false;
        }

        try {
            double density = Double.parseDouble(densityStr);
            double mainValue = Double.parseDouble(mainInputStr); // This is either length or mass
            double side = Double.parseDouble(sideStr);

            // Check for non-positive values
            if (density <= 0 || mainValue <= 0 || side <= 0) {
                totalOutputTextView.setText(R.string.error_negative_values);
                return false;
            }
            return true; // Validation passed
        } catch (NumberFormatException e) {
            totalOutputTextView.setText(R.string.error_number_format);
            Log.e("ShestigrannikCalculate", getString(R.string.log_parsing_error) + e.getMessage());
            return false; // Validation failed due to number format
        }
    }

    // Method to calculate weight when the user inputs length
    private void calculateWeightOutput() {
        // Get and trim all input strings
        String densityStr = editTextDensity.getText().toString().trim();
        String lengthInputStr = editTextLength.getText().toString().trim(); // User inputs length (meters)
        String sideStr = editTextSide.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(densityStr, lengthInputStr, sideStr)) {
            return; // Exit if validation fails
        }

        try {
            // Parse values to double
            double density = Double.parseDouble(densityStr); // g/cm³
            double lengthInputMeters = Double.parseDouble(lengthInputStr); // meters
            double side = Double.parseDouble(sideStr); // mm

            // Convert units
            double sideCm = side * MM_TO_CM; // mm -> cm
            double lengthInputCm = lengthInputMeters / CM_TO_M; // meters -> cm (lengthInputMeters * 100)
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // g/cm³ -> kg/cm³

            // Cross-sectional area of a hexagon: A = (3 * sqrt(3) / 2) * side^2
            double areaCm2 = (3 * Math.sqrt(3) / 2) * Math.pow(sideCm, 2);

            // Volume of material (cm³)
            double volumeCm3 = areaCm2 * lengthInputCm;

            // Weight of one piece (kg)
            double weightPerPiece = volumeCm3 * densityKgPerCm3;

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weightPerPiece));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty();

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        totalOutputTextView.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    totalOutputTextView.setText(R.string.error_number_format);
                    Log.e("ShestigrannikCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Display total mass if quantity is provided
            if (isQuantityProvided) {
                double totalWeightValue = weightPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_mass_unit_format), totalWeightValue));
            }

            // Calculate and display cost if price per kg is provided
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        totalOutputTextView.setText(R.string.error_negative_price);
                        return;
                    }

                    double pricePerUnit = pricePerKg * weightPerPiece; // Cost of one unit
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));

                    // Display total cost only if quantity is provided
                    if (isQuantityProvided) {
                        double totalCost = pricePerKg * weightPerPiece * totalQuantity;
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    totalOutputTextView.setText(R.string.error_number_format);
                    Log.e("ShestigrannikCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight)); // Send analytics for weight calculation
            totalOutputTextView.setText(result.toString()); // Display the final result

        } catch (NumberFormatException e) {
            totalOutputTextView.setText(R.string.error_number_format);
            Log.e("ShestigrannikCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Method to calculate length when the user inputs mass
    private void calculateLengthOutput() {
        String densityStr = editTextDensity.getText().toString().trim();
        String weightInputStr = editTextLength.getText().toString().trim(); // User inputs mass (kg)
        String sideStr = editTextSide.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (!validateInputs(densityStr, weightInputStr, sideStr)) {
            return;
        }

        try {
            double density = Double.parseDouble(densityStr); // g/cm³
            double weightInputKg = Double.parseDouble(weightInputStr); // kg
            double side = Double.parseDouble(sideStr); // mm

            double sideCm = side * MM_TO_CM; // mm -> cm
            double densityKgPerCm3 = density * G_PER_CM3_TO_KG_PER_CM3; // g/cm³ -> kg/cm³

            // Cross-sectional area of a hexagon: A = (3 * sqrt(3) / 2) * side^2
            double areaCm2 = (3 * Math.sqrt(3) / 2) * Math.pow(sideCm, 2);

            // Volume of material (cm³)
            double volumeCm3 = weightInputKg / densityKgPerCm3;

            // Length of one piece (meters)
            double lengthPerPieceMeters = volumeCm3 / areaCm2 * CM_TO_M; // cm -> meters

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), lengthPerPieceMeters));

            double totalQuantity = 1.0;
            boolean isQuantityProvided = !quantityStr.isEmpty();

            if (isQuantityProvided) {
                try {
                    totalQuantity = Double.parseDouble(quantityStr);
                    if (totalQuantity <= 0) {
                        totalOutputTextView.setText(R.string.error_negative_quantity);
                        return;
                    }
                } catch (NumberFormatException e) {
                    totalOutputTextView.setText(R.string.error_number_format);
                    Log.e("ShestigrannikCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                    return;
                }
            }

            // Display total length if quantity is provided
            if (isQuantityProvided) {
                double totalLengthValueMeters = lengthPerPieceMeters * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_length_unit_format), totalLengthValueMeters));
            }

            // Calculate and display cost if price per kg is provided
            if (!pricePerKgStr.isEmpty()) {
                try {
                    double pricePerKg = Double.parseDouble(pricePerKgStr);
                    if (pricePerKg < 0) {
                        totalOutputTextView.setText(R.string.error_negative_price);
                        return;
                    }

                    double pricePerUnit = pricePerKg * weightInputKg; // Cost of one unit
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerUnit));

                    // Display total cost only if quantity is provided
                    if (isQuantityProvided) {
                        double totalCost = pricePerKg * weightInputKg * totalQuantity;
                        result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                    }
                } catch (NumberFormatException e) {
                    totalOutputTextView.setText(R.string.error_number_format);
                    Log.e("ShestigrannikCalculate", getString(R.string.log_parsing_error) + e.getMessage());
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length)); // Send analytics for length calculation
            totalOutputTextView.setText(result.toString()); // Display the final result

        } catch (NumberFormatException e) {
            totalOutputTextView.setText(R.string.error_number_format);
            Log.e("ShestigrannikCalculate", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    // Method to send analytical data
    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_shestigrannik));
        NetworkHelper.sendCalculationData(this, analytics);
    }

    // Handles the back button click, navigating to SelectForm activity
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }
}