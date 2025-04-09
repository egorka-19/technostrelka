package com.example.main_screen.data;

import com.example.main_screen.R;
import com.example.main_screen.model.ArtObject;
import java.util.ArrayList;
import java.util.List;

public class HistoryObjectsData {
    public static List<ArtObject> getHistoryObjects() {
        List<ArtObject> historyObjects = new ArrayList<>();

        // 1. Чкаловская лестница
        historyObjects.add(new ArtObject(
            "hist_1",
            "Чкаловская лестница",
            "Одна из самых длинных лестниц в России, построенная в честь победы в Сталинградской битве. Соединяет Верхне-Волжскую набережную с Нижне-Волжской.",
            R.drawable.hist_place_1,
            56.3286, 44.0075,
            "Чкаловская лестница",
            R.raw.hist_audio_1
        ));

        // 2. Площадь Минина и Пожарского
        historyObjects.add(new ArtObject(
            "hist_2",
            "Площадь Минина и Пожарского",
            "Главная площадь города, названная в честь героев народного ополчения 1612 года. Здесь проходят все важные городские мероприятия.",
            R.drawable.hist_place_2,
            56.3268, 44.0065,
            "Площадь Минина и Пожарского",
            R.raw.hist_audio_2
        ));

        // 3. Здание нижегородской думы
        historyObjects.add(new ArtObject(
            "hist_3",
            "Здание нижегородской думы",
            "Историческое здание городской думы, построенное в XIX веке. Яркий пример архитектуры того времени.",
            R.drawable.hist_place_3,
            56.3268, 44.0065,
            "Здание нижегородской думы",
            R.raw.hist_audio_3
        ));

        // 4. Здание театра драмы им. М. Горького
        historyObjects.add(new ArtObject(
            "hist_4",
            "Театр драмы им. М. Горького",
            "Один из старейших театров России, основанный в 1798 году. Здесь ставились первые пьесы Максима Горького.",
            R.drawable.hist_place_4,
            56.3268, 44.0065,
            "Здание театра драмы им. М. Горького",
            R.raw.hist_audio_4
        ));

        // 5. Здание государственного банка
        historyObjects.add(new ArtObject(
            "hist_5",
            "Государственный банк",
            "Уникальное здание в неорусском стиле, построенное в начале XX века. Символ финансовой мощи города.",
            R.drawable.hist_place_5,
            56.3268, 44.0065,
            "Здание государственного банка",
            R.raw.hist_audio_5
        ));

        // 6. Ильинская (Започаинская) слобода
        historyObjects.add(new ArtObject(
            "hist_6",
            "Ильинская слобода",
            "Исторический район города, сохранивший атмосферу старого Нижнего Новгорода. Здесь можно увидеть старинные купеческие дома.",
            R.drawable.hist_place_6,
            56.3268, 44.0065,
            "Ильинская (Започаинская) слобода",
            R.raw.hist_audio_6
        ));

        // 7. Строгановская церковь
        historyObjects.add(new ArtObject(
            "hist_7",
            "Строгановская церковь",
            "Уникальный памятник архитектуры XVII века, построенный на средства купцов Строгановых. Яркий пример русского барокко.",
            R.drawable.hist_place_7,
            56.3268, 44.0065,
            "Строгановская церковь",
            R.raw.hist_audio_7
        ));

        // 8. Здание Блиновского пассажа
        historyObjects.add(new ArtObject(
            "hist_8",
            "Блиновский пассаж",
            "Один из первых торговых пассажей в России, построенный в XIX веке. Символ купеческого прошлого города.",
            R.drawable.hist_place_8,
            56.3268, 44.0065,
            "Здание Блиновского пассажа",
            R.raw.hist_audio_8
        ));

        return historyObjects;
    }
} 