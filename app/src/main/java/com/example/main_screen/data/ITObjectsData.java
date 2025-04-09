package com.example.main_screen.data;

import com.example.main_screen.R;
import com.example.main_screen.model.ArtObject;
import java.util.ArrayList;
import java.util.List;

public class ITObjectsData {
    public static List<ArtObject> getITObjects() {
        List<ArtObject> itObjects = new ArrayList<>();

        // 1. Площадь Максима Горького
        itObjects.add(new ArtObject(
            "it_1",
            "IT-Площадь",
            "Современное пространство для технологических мероприятий и хакатонов. Здесь проходят крупнейшие IT-конференции города.",
            R.drawable.it_place_1,
            56.3268, 44.0065,
            "Площадь Максима Горького",
            R.raw.it_audio_1
        ));

        // 2. Большая Покровская
        itObjects.add(new ArtObject(
            "it_2",
            "Цифровая улица",
            "Историческая улица, где традиции встречаются с инновациями. Здесь расположены офисы ведущих IT-компаний.",
            R.drawable.it_place_2,
            56.3268, 44.0065,
            "Большая Покровская",
            R.raw.it_audio_2
        ));

        // 3. Магазин Дирижабль
        itObjects.add(new ArtObject(
            "it_3",
            "Технологический хаб",
            "Современное пространство, где можно найти последние технологические новинки и гаджеты.",
            R.drawable.it_place_3,
            56.3268, 44.0065,
            "Магазин Дирижабль",
            R.raw.it_audio_3
        ));

        // 4. Музей художественных промыслов
        itObjects.add(new ArtObject(
            "it_4",
            "Цифровое наследие",
            "Музей, где традиционные промыслы представлены через призму современных технологий.",
            R.drawable.it_place_4,
            56.3268, 44.0065,
            "Музей художественных промыслов",
            R.raw.it_audio_4
        ));

        // 5. Кафе Лепи тесто
        itObjects.add(new ArtObject(
            "it_5",
            "IT-Кухня",
            "Место встречи IT-специалистов, где обсуждаются новые проекты и идеи.",
            R.drawable.it_place_5,
            56.3268, 44.0065,
            "Кафе Лепи тесто",
            R.raw.it_audio_5
        ));

        // 6. Институт Филологии и Журналистики
        itObjects.add(new ArtObject(
            "it_6",
            "Центр цифровых медиа",
            "Место, где готовят специалистов по цифровым медиа и контенту.",
            R.drawable.it_place_6,
            56.3268, 44.0065,
            "Институт Филологии и Журналистики",
            R.raw.it_audio_6
        ));

        // 8. Ресторан Счастливые люди
        itObjects.add(new ArtObject(
            "it_8",
            "IT-Сообщество",
            "Популярное место встреч IT-специалистов и стартаперов.",
            R.drawable.it_place_8,
            56.3268, 44.0065,
            "Ресторан Счастливые люди",
            R.raw.it_audio_8
        ));

        return itObjects;
    }
} 