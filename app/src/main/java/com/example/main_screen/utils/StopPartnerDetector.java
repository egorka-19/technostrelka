package com.example.main_screen.utils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Эвристика: кафе/рестораны/отели и т.п. — показываем кнопку «Получить подарок».
 * ТИЦ и чистые достопримечательности — без подарка.
 */
public final class StopPartnerDetector {

    private static final Pattern PARTNER = Pattern.compile(
            "кафе|ресторан|фуд\\s*-?\\s*холл|холл|пельмен|перепеч|отель|гостиниц|хостел|"
                    + "столовая|кофейн|суши|пицц|бар\\b|общепит|тыр\\s+корка|terminal",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern EXCLUDE = Pattern.compile(
            "туристско-информационн|информационн(ый|ого)\\s+центр|"
                    + "музей|памятник|площадь\\s+оружейник|пруд\\b|собор|храм|церковь|"
                    + "пушка\\b|музейно|национальн|крокодил|арт-объект|скульптур|монумент|галерея",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private StopPartnerDetector() {
    }

    public static boolean isPartnerPoi(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        String t = title.trim();
        if (EXCLUDE.matcher(t).find()) {
            return false;
        }
        return PARTNER.matcher(t).find();
    }
}
