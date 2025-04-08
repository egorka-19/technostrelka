package com.example.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Category extends AppCompatActivity {

    private ArrayList<Integer> selectedCheckBoxIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Получаем ссылки на все чекбоксы
        CheckBox checkBoxAll = findViewById(R.id.checkBoxAll);
        CheckBox checkBoxMovies = findViewById(R.id.checkBoxMovies);
        CheckBox checkBoxTheaters = findViewById(R.id.checkBoxTheaters);
        CheckBox checkBoxRestaurants = findViewById(R.id.checkBoxRestaurants);
        CheckBox checkBoxParks = findViewById(R.id.checkBoxParks);
        CheckBox checkBoxMuseums = findViewById(R.id.checkBoxMuseums);

        // Устанавливаем слушатели нажатия на чекбоксы
        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxAll.isChecked()) {
                    // Если выбран чекбокс "Все", выбираем все остальные чекбоксы
                    checkBoxMovies.setChecked(true);
                    checkBoxTheaters.setChecked(true);
                    checkBoxRestaurants.setChecked(true);
                    checkBoxParks.setChecked(true);
                    checkBoxMuseums.setChecked(true);
                    // Очищаем список и добавляем все идентификаторы чекбоксов
                    selectedCheckBoxIds.clear();
                    selectedCheckBoxIds.add(R.id.checkBoxMovies);
                    selectedCheckBoxIds.add(R.id.checkBoxTheaters);
                    selectedCheckBoxIds.add(R.id.checkBoxRestaurants);
                    selectedCheckBoxIds.add(R.id.checkBoxParks);
                    selectedCheckBoxIds.add(R.id.checkBoxMuseums);
                } else {
                    // Если чекбокс "Все" не выбран, снимаем выбор со всех остальных чекбоксов
                    checkBoxMovies.setChecked(false);
                    checkBoxTheaters.setChecked(false);
                    checkBoxRestaurants.setChecked(false);
                    checkBoxParks.setChecked(false);
                    checkBoxMuseums.setChecked(false);
                    // Очищаем список выбранных чекбоксов
                    selectedCheckBoxIds.clear();
                }
            }
        });

        // Устанавливаем слушатели нажатия на остальные чекбоксы
        checkBoxMovies.setOnClickListener(onCheckBoxClickListener);
        checkBoxTheaters.setOnClickListener(onCheckBoxClickListener);
        checkBoxRestaurants.setOnClickListener(onCheckBoxClickListener);
        checkBoxParks.setOnClickListener(onCheckBoxClickListener);
        checkBoxMuseums.setOnClickListener(onCheckBoxClickListener);
    }

    // Общий слушатель нажатия на чекбоксы, добавляет/удаляет идентификаторы из списка
    private View.OnClickListener onCheckBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;
            int checkBoxId = checkBox.getId();
            if (checkBox.isChecked()) {
                selectedCheckBoxIds.add(checkBoxId);
            } else {
                selectedCheckBoxIds.remove(Integer.valueOf(checkBoxId));
            }
        }
    };

    // Метод для перехода на новое активити для отображения выбранных чекбоксов
    public void showSelectedCheckBoxes(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putIntegerArrayListExtra("selectedCheckBoxIds", selectedCheckBoxIds);
        startActivity(intent);
    }
}
