package com.example.main_screen.data;

import com.example.main_screen.R;
import com.example.main_screen.model.ArtObject;
import java.util.ArrayList;
import java.util.List;

public class ArtObjectsData {
    public static List<ArtObject> getArtObjects() {
        List<ArtObject> artObjects = new ArrayList<>();

        // 1. NDZW «Острова» на Ильинской
        artObjects.add(new ArtObject(
            "1",
            "NDZW «Острова» на Ильинской",
            "Современная инсталляция, представляющая собой серию островов на улице Ильинской",
            R.drawable.ndzw,
            56.844125,
            53.199509,
            "ул. Ильинская",
            R.raw.ostrova_audio
        ));

        // 2. Свеча как символ ничто не вечно
        artObjects.add(new ArtObject(
            "2",
            "Свеча как символ ничто не вечно",
            "Арт-объект, символизирующий быстротечность времени",
                R.drawable.svecha,
            56.850470,
            53.199591,
            "ул. Пушкинская",
            R.raw.svecha_audio
        ));

        // 3. Охота на динозавра
        artObjects.add(new ArtObject(
            "3",
            "Охота на динозавра",
            "Интерактивная инсталляция, посвященная теме охоты и выживания",
                R.drawable.dino,
            56.845329,
            53.198977,
            "ул. Советская",
            R.raw.dino_audio
        ));

        // 4. Ashes to ashes
        artObjects.add(new ArtObject(
            "4",
            "Ashes to ashes",
            "Концептуальная работа о цикличности жизни",
                R.drawable.ashes,
            56.848942,
            53.195590,
            "ул. Карла Маркса",
            R.raw.ashes
        ));

        // 5. Покрас лампас
        artObjects.add(new ArtObject(
            "5",
            "Покрас лампас",
            "Современная интерпретация традиционных элементов",
            R.drawable.lamps,
            56.866523,
            53.207575,
            "ул. Ленина",
            R.raw.lamps
        ));

        // 6. Арт серия «Звонок звучал для всех»
        artObjects.add(new ArtObject(
            "6",
            "Арт серия «Звонок звучал для всех»",
            "Серия работ, посвященная теме коммуникации и связи",
                R.drawable.art,
            56.848160,
            53.205816,
            "ул. Горького",
            R.raw.art
        ));

        // 7. Рогатка
        artObjects.add(new ArtObject(
            "7",
            "Рогатка",
            "Интерактивная инсталляция, приглашающая к игре",
                R.drawable.rogatka,
            56.844125,
            53.199509,
            "ул. Удмуртская",
            R.raw.rogatka
        ));

        return artObjects;
    }
} 