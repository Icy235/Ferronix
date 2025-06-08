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

    private EditText editTextLength, editTextPricePerPiece, editTextQuantity;
    private TextView totalWeight, textViewLength, textViewUnit;
    private Button btnDiametr, btnGoWeight, btnGoLength;

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

        initializeViews();
        setupListeners();
        setActiveButton(btnGoWeight, btnGoLength);
    }

    private void initializeViews() {
        editTextLength = findViewById(R.id.editTextLength);
        editTextPricePerPiece = findViewById(R.id.editTextPricePerPice);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        totalWeight = findViewById(R.id.textViewWeightTotal);
        textViewLength = findViewById(R.id.textViewLength);
        textViewUnit = findViewById(R.id.textViewUnit);
        btnDiametr = findViewById(R.id.btnDiametr);
        btnGoWeight = findViewById(R.id.btnGoWeight);
        btnGoLength = findViewById(R.id.btnGoLength);
    }

    private void setupListeners() {
        findViewById(R.id.btnCalculate).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            if (textViewLength.getText().toString().equals(getString(R.string.mass_unit))) {
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
            textViewLength.setText(R.string.length_unit);
            textViewUnit.setText(R.string.unit_meter);
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

    private void showDiametrMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnDiametr);
        for (String diameter : nominalDiameters) {
            popupMenu.getMenu().add(diameter);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            btnDiametr.setText(Objects.requireNonNull(item.getTitle()));
            return true;
        });
        popupMenu.show();
    }

    private int getSelectedDiameterIndex() {
        String selectedDiametr = btnDiametr.getText().toString();
        return Arrays.asList(nominalDiameters).indexOf(selectedDiametr);
    }

    private DecimalFormat getCostFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        return new DecimalFormat("#,##0.00", symbols);
    }

    private void calculateLength() {
        try {
            String inputStr = editTextLength.getText().toString().trim();
            if (inputStr.isEmpty()) {
                totalWeight.setText(R.string.error_empty_length);
                return;
            }

            double length = Double.parseDouble(inputStr);
            if (length <= 0) {
                totalWeight.setText(R.string.error_negative_length);
                return;
            }

            int index = getSelectedDiameterIndex();
            if (index == -1) {
                totalWeight.setText(R.string.error_no_diameter);
                return;
            }

            double massPerMeter = armaturaMassPerMeter[index];
            double weight = massPerMeter * length;
            StringBuilder result = new StringBuilder();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (quantityStr.isEmpty()) {
                result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weight));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText(R.string.error_negative_quantity);
                    return;
                }

                double totalWeightValue = weight * quantity;
                result.append(String.format(Locale.getDefault(), getString(R.string.mass_unit_format), weight))
                        .append(String.format(Locale.getDefault(), getString(R.string.total_mass_unit_format), totalWeightValue));

                String priceStr = editTextPricePerPiece.getText().toString().trim();
                if (!priceStr.isEmpty()) {
                    double pricePerPiece = Double.parseDouble(priceStr);
                    double totalCost = pricePerPiece * quantity;
                    result.append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerPiece))
                            .append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                }
            }

            sendAnalytics(getString(R.string.analytics_type_length));
            totalWeight.setText(result.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CalcError", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    private void calculateWeight() {
        try {
            String inputStr = editTextLength.getText().toString().trim();
            if (inputStr.isEmpty()) {
                totalWeight.setText(R.string.error_empty_mass);
                return;
            }

            double mass = Double.parseDouble(inputStr);
            if (mass <= 0) {
                totalWeight.setText(R.string.error_negative_mass);
                return;
            }

            int index = getSelectedDiameterIndex();
            if (index == -1) {
                totalWeight.setText(R.string.error_no_diameter);
                return;
            }

            double massPerMeter = armaturaMassPerMeter[index];
            double length = mass / massPerMeter;
            StringBuilder result = new StringBuilder();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (quantityStr.isEmpty()) {
                result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), length));
            } else {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    totalWeight.setText(R.string.error_negative_quantity);
                    return;
                }

                double totalLength = length * quantity;
                result.append(String.format(Locale.getDefault(), getString(R.string.length_unit_format), length))
                        .append(String.format(Locale.getDefault(), getString(R.string.total_length_unit_format), totalLength));

                String priceStr = editTextPricePerPiece.getText().toString().trim();
                if (!priceStr.isEmpty()) {
                    double pricePerPiece = Double.parseDouble(priceStr);
                    double totalCost = pricePerPiece * quantity;
                    result.append(String.format(Locale.getDefault(), getString(R.string.unit_cost_format), pricePerPiece))
                            .append(String.format(Locale.getDefault(), getString(R.string.total_unit_cost_format), totalCost));
                }
            }

            sendAnalytics(getString(R.string.analytics_type_weight));
            totalWeight.setText(result.toString());

        } catch (NumberFormatException e) {
            totalWeight.setText(R.string.error_number_format);
            Log.e("CalcError", getString(R.string.log_parsing_error) + e.getMessage());
        }
    }

    private void sendAnalytics(String calculationType) {
        Map<String, String> analytics = new HashMap<>();
        analytics.put(getString(R.string.analytics_key_type), calculationType);
        analytics.put(getString(R.string.analytics_key_template), getString(R.string.analytics_template));
        NetworkHelper.sendCalculationData(this, analytics);
    }

    public void btnBack(View view) {
        startActivity(new Intent(this, SelectForm.class));
        finish();
    }

    public void btnGost34028_2016_pdf(View view) {
        startActivity(new Intent(this, Gost34028_2016_pdf.class));
        finish();
    }
}