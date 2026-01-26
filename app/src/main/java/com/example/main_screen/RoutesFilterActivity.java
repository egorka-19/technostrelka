package com.example.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class RoutesFilterActivity extends AppCompatActivity {

    private RadioGroup goalRadioGroup;
    private RadioGroup daysRadioGroup;
    private EditText peopleCountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_filter);

        goalRadioGroup = findViewById(R.id.goal_radio_group);
        daysRadioGroup = findViewById(R.id.days_radio_group);
        peopleCountInput = findViewById(R.id.people_count_input);

        TextView applyButton = findViewById(R.id.apply_filter_button);
        applyButton.setOnClickListener(v -> {
            // Получаем выбранные значения
            String selectedGoal = getSelectedGoal();
            String selectedDays = getSelectedDays();
            String peopleCount = peopleCountInput.getText().toString().trim();

            // Создаем Intent с результатами фильтрации
            Intent resultIntent = new Intent();
            resultIntent.putExtra("goal", selectedGoal);
            resultIntent.putExtra("days", selectedDays);
            resultIntent.putExtra("peopleCount", peopleCount);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private String getSelectedGoal() {
        int selectedId = goalRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.goal_rest) {
            return "Отдых";
        } else if (selectedId == R.id.goal_excursion) {
            return "Экскурсия";
        } else if (selectedId == R.id.goal_active) {
            return "Активный отдых";
        } else if (selectedId == R.id.goal_culture) {
            return "Культура";
        }
        return null;
    }

    private String getSelectedDays() {
        int selectedId = daysRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.days_1) {
            return "1";
        } else if (selectedId == R.id.days_2_3) {
            return "2-3";
        } else if (selectedId == R.id.days_3_5) {
            return "3-5";
        } else if (selectedId == R.id.days_7_plus) {
            return ">7";
        }
        return null;
    }
}
