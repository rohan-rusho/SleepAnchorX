package com.Xtrem.top.appX;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsualSleep, editTextTodayBed;
    private AppCompatButton btnCalculate, btnCopy, btnShare, btnReset;
    private CardView resultCard;
    private TextView textSleepAnchorXResult;
    private String resultText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsualSleep = findViewById(R.id.editTextUsualSleep);
        editTextTodayBed = findViewById(R.id.editTextTodayBed);
        btnCalculate = findViewById(R.id.btnCalculateSleepAnchorX);
        btnCopy = findViewById(R.id.btnCopySleepAnchorX);
        btnShare = findViewById(R.id.btnShareSleepAnchorX);
        btnReset = findViewById(R.id.btnResetSleepAnchorX);
        resultCard = findViewById(R.id.resultCardSleepAnchorX);
        textSleepAnchorXResult = findViewById(R.id.textSleepAnchorXResult);

        resultCard.setVisibility(View.VISIBLE);
        textSleepAnchorXResult.setText("Stability score will appear here.");
        btnCopy.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);

        btnCalculate.setOnClickListener(v -> calculateSleepAnchorX());
        btnCopy.setOnClickListener(v -> copyResult());
        btnShare.setOnClickListener(v -> shareResult());
        btnReset.setOnClickListener(v -> resetFields());
    }

    private void calculateSleepAnchorX() {
        String usualStr = editTextUsualSleep.getText().toString().trim();
        String todayStr = editTextTodayBed.getText().toString().trim();

        if (usualStr.isEmpty() || todayStr.isEmpty()) {
            Toast.makeText(this, "Enter both times.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String[] usualParts = usualStr.split(":");
            String[] todayParts = todayStr.split(":");
            if (usualParts.length != 2 || todayParts.length != 2)
                throw new Exception();

            int usualHour = Integer.parseInt(usualParts[0]);
            int usualMin = Integer.parseInt(usualParts[1]);
            int todayHour = Integer.parseInt(todayParts[0]);
            int todayMin = Integer.parseInt(todayParts[1]);

            if (usualHour < 0 || usualHour > 23 || usualMin < 0 || usualMin > 59 ||
                    todayHour < 0 || todayHour > 23 || todayMin < 0 || todayMin > 59) {
                resultText = "Use HH:mm 24h format.";
            } else {
                int usualTotal = usualHour * 60 + usualMin;
                int todayTotal = todayHour * 60 + todayMin;
                int diff = Math.abs(todayTotal - usualTotal);
                // Minimum round-the-clock difference (handles across midnight)
                if (diff > 720) diff = 1440 - diff;
                int score = 100 - (diff / 2);
                if (score < 0) score = 0;
                resultText = "Stability Score: " + score + " / 100";
            }

            textSleepAnchorXResult.setText(resultText);
            btnCopy.setVisibility(resultText.startsWith("Stability Score:") ? View.VISIBLE : View.GONE);
            btnShare.setVisibility(resultText.startsWith("Stability Score:") ? View.VISIBLE : View.GONE);

        } catch (Exception e) {
            resultText = "Enter times as HH:mm (e.g., 23:15)";
            textSleepAnchorXResult.setText(resultText);
            btnCopy.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);
        }
    }

    private void copyResult() {
        if (resultText.isEmpty() || !resultText.startsWith("Stability Score:")) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("SleepAnchorX Result", resultText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
    }

    private void shareResult() {
        if (resultText.isEmpty() || !resultText.startsWith("Stability Score:")) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);
        startActivity(Intent.createChooser(shareIntent, "Share Result"));
    }

    private void resetFields() {
        editTextUsualSleep.setText("");
        editTextTodayBed.setText("");
        resultText = "";
        textSleepAnchorXResult.setText("Stability score will appear here.");
        btnCopy.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);
    }
}