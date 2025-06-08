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
import android.widget.Toast; // Added Toast for user feedback

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.calculate.ferronix.R;
import com.calculate.ferronix.Sortament.gostPdf.Gost34028_2016_pdf;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Control.NetworkHelper;

public class ArmaturaCalculate extends AppCompatActivity {

    private EditText editTextMainInput, editTextPricePerPiece, editTextQuantity; // Renamed editTextLength to editTextMainInput for clarity
    private TextView totalOutputTextView, textViewMainLabel, textViewUnitLabel; // Renamed for clarity
    private Button btnDiameter, btnGoWeight, btnGoLength; // Renamed btnDiametr to btnDiameter for consistency

    // These arrays should ideally be fetched from resources if they need localization
    private final String[] nominalDiameters = {
            "4.0", "4.5", "5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5",
            "9.0", "9.5", "10.0", "11.0", "12.0", "13.0", "14.0", "15.0", "16.0",
            "17.0", "18.0", "19.0", "20.0", "22.0", "25.0", "28.0", "32.0", "36.0", "40.0"
    };

    private final double[] armaturaMassPerMeter = {
            0.099, 0.125, 0.154, 0.187, 0.222, 0.261, 0.302, 0.347, 0.395, 0.445,
            0.499, 0.556, 0.617, 0.746, 0.888, 1.042, 1.208, 1.387, 1.578, 1.782,
            1.998, 2.226, 2.466, 2.984, 3.853, 4.834, 6.313, 7.990, 9.865
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.armatura_calculate);

        initializeViews(); // Initialize UI elements
        setupListeners(); // Set up event listeners

        // Set initial state (default to calculate weight from length)
        textViewMainLabel.setText(R.string.length_unit); // Label for input field
        textViewUnitLabel.setText(R.string.unit_meter); // Unit next to input field
        editTextMainInput.setHint(R.string.length_unit); // Hint for input field
        setActiveButton(btnGoWeight, btnGoLength); // Highlight the "Calculate Weight" button
    }

    // Initialize all UI elements
    private void initializeViews() {
        editTextMainInput = findViewById(R.id.editTextLength); // Remains as editTextLength in layout
        editTextPricePerPiece = findViewById(R.id.editTextPricePerPice);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalOutputTextView = findViewById(R.id.textViewWeightTotal); // Renamed for clarity
        textViewMainLabel = findViewById(R.id.textViewLength); // Renamed for clarity
        textViewUnitLabel = findViewById(R.id.textViewUnit); // Renamed for clarity
        btnDiameter = findViewById(R.id.btnDiametr); // Renamed for consistency
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);

        // Robust null checks for critical UI elements
        if (editTextMainInput == null || editTextPricePerPiece == null || editTextQuantity == null ||
                totalOutputTextView == null || textViewMainLabel == null || textViewUnitLabel == null ||
                btnDiameter == null || btnGoWeight == null || btnGoLength == null || findViewById(R.id.btnCalculate) == null) {


            finish(); // Close activity if critical UI elements are missing
        }
    }

    // Set up event listeners for buttons
    private void setupListeners() {
        // Listener for the main "Calculate" button
        findViewById(R.id.btnCalculate).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            // Determine calculation type based on current label of textViewMainLabel
            if (textViewMainLabel.getText().toString().equals(getString(R.string.mass_unit))) {
                calculateLength(); // User inputs mass, calculate length
            } else {
                calculateWeight(); // User inputs length, calculate weight
            }
        });

        // Listener for the "Diameter" selection button
        btnDiameter.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            showDiameterMenu();
        });

        // Listener for switching to "Calculate Weight" mode
        btnGoWeight.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            textViewMainLabel.setText(R.string.length_unit); // Change label to "Length"
            textViewUnitLabel.setText(R.string.unit_meter); // Change unit to "m"
            editTextMainInput.setHint(R.string.length_unit); // Change hint to "Length"
            setActiveButton(btnGoWeight, btnGoLength); // Highlight "Calculate Weight"
        });

        // Listener for switching to "Calculate Length" mode
        btnGoLength.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            textViewMainLabel.setText(R.string.mass_unit); // Change label to "Mass"
            textViewUnitLabel.setText(R.string.unit_kg); // Change unit to "kg"
            editTextMainInput.setHint(R.string.mass_unit); // Change hint to "Mass"
            setActiveButton(btnGoLength, btnGoWeight); // Highlight "Calculate Length"
        });
    }

    // Sets the visual state (active/inactive) of the two mode selection buttons
    @SuppressLint("ResourceAsColor")
    private void setActiveButton(Button activeButton, Button inactiveButton) {
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    // Displays the diameter selection menu
    private void showDiameterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnDiameter);
        for (String diameter : nominalDiameters) {
            popupMenu.getMenu().add(diameter);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            btnDiameter.setText(Objects.requireNonNull(item.getTitle()));
            return true;
        });
        popupMenu.show();
    }

    // Gets the index of the selected diameter in the nominalDiameters array
    private int getSelectedDiameterIndex() {
        String selectedDiameter = btnDiameter.getText().toString();
        // Check if the default button text is still present
        if (selectedDiameter.equals(getString(R.string.choose_diameter_label))) {
            return -1; // Indicates no diameter has been chosen yet
        }
        return Arrays.asList(nominalDiameters).indexOf(selectedDiameter);
    }

    // Common input validation for calculation fields
    private boolean validateCommonInputs(String inputStr, String errorEmpty, String errorNegative) {
        if (inputStr.isEmpty()) {
            totalOutputTextView.setText(errorEmpty);
            return false;
        }
        try {
            double value = Double.parseDouble(inputStr);
            if (value <= 0) {
                totalOutputTextView.setText(errorNegative);
                return false;
            }
        } catch (NumberFormatException e) {
            totalOutputTextView.setText(R.string.error_number_format);

            return false;
        }
        return true;
    }

    // Calculates weight based on input length
    private void calculateWeight() {
        // Validate main input (length)
        if (!validateCommonInputs(editTextMainInput.getText().toString().trim(),
                getString(R.string.error_empty_length), getString(R.string.error_negative_length))) {
            return;
        }

        int index = getSelectedDiameterIndex();
        if (index == -1) {
            totalOutputTextView.setText(R.string.error_no_diameter);
            return;
        }

        try {
            double length = Double.parseDouble(editTextMainInput.getText().toString().trim());
            double massPerMeter = armaturaMassPerMeter[index];
            double weightPerPiece = massPerMeter * length; // Weight of one piece

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weightPerPiece));

            String quantityStr = editTextQuantity.getText().toString().trim();
            double totalQuantity = 1.0;
            boolean isQuantityProvided = false;

            if (!quantityStr.isEmpty()) {
                if (!validateCommonInputs(quantityStr, "", getString(R.string.error_negative_quantity))) { // No empty error for quantity
                    return;
                }
                totalQuantity = Double.parseDouble(quantityStr);
                isQuantityProvided = true;
            }

            // Display total weight if quantity is provided
            if (isQuantityProvided) {
                double totalWeightValue = weightPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_mass_unit_format), totalWeightValue));
            }

            // Calculate and display cost if price per piece is provided
            String priceStr = editTextPricePerPiece.getText().toString().trim();
            if (!priceStr.isEmpty()) {
                if (!validateCommonInputs(priceStr, "", getString(R.string.error_negative_price))) { // No empty error for price
                    return;
                }
                double pricePerPiece = Double.parseDouble(priceStr);
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerPiece));

                if (isQuantityProvided) {
                    double totalCost = pricePerPiece * totalQuantity;
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight_calc)); // Specific analytics for this mode
            totalOutputTextView.setText(result.toString());

        } catch (NumberFormatException e) {
            totalOutputTextView.setText(R.string.error_number_format);

        }
    }

    // Calculates length based on input mass
    private void calculateLength() {
        // Validate main input (mass)
        if (!validateCommonInputs(editTextMainInput.getText().toString().trim(),
                getString(R.string.error_empty_mass), getString(R.string.error_negative_mass))) {
            return;
        }

        int index = getSelectedDiameterIndex();
        if (index == -1) {
            totalOutputTextView.setText(R.string.error_no_diameter);
            return;
        }

        try {
            double mass = Double.parseDouble(editTextMainInput.getText().toString().trim());
            double massPerMeter = armaturaMassPerMeter[index];
            double lengthPerPiece = mass / massPerMeter; // Length of one piece

            StringBuilder result = new StringBuilder();
            result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), lengthPerPiece));

            String quantityStr = editTextQuantity.getText().toString().trim();
            double totalQuantity = 1.0;
            boolean isQuantityProvided = false;

            if (!quantityStr.isEmpty()) {
                if (!validateCommonInputs(quantityStr, "", getString(R.string.error_negative_quantity))) {
                    return;
                }
                totalQuantity = Double.parseDouble(quantityStr);
                isQuantityProvided = true;
            }

            // Display total length if quantity is provided
            if (isQuantityProvided) {
                double totalLength = lengthPerPiece * totalQuantity;
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_length_unit_format), totalLength));
            }

            // Calculate and display cost if price per piece is provided
            String priceStr = editTextPricePerPiece.getText().toString().trim();
            if (!priceStr.isEmpty()) {
                if (!validateCommonInputs(priceStr, "", getString(R.string.error_negative_price))) {
                    return;
                }
                double pricePerPiece = Double.parseDouble(priceStr);
                result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerPiece));

                if (isQuantityProvided) {
                    double totalCost = pricePerPiece * totalQuantity;
                    result.append("\n").append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length_calc)); // Specific analytics for this mode
            totalOutputTextView.setText(result.toString());

        } catch (NumberFormatException e) {
            totalOutputTextView.setText(R.string.error_number_format);

        }
    }

    // Sends analytical data
    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template_armatura)); // Use specific template for rebar
        NetworkHelper.sendCalculationData(this, analytics);
    }

    // Navigates back to SelectForm activity
    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }

    // Opens the GOST 34028-2016 PDF document
    public void btnGost34028_2016_pdf(View view) {
        startActivity(new Intent(this, Gost34028_2016_pdf.class));
        finish();
    }
}