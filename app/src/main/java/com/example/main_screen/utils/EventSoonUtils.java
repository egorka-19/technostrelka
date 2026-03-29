package com.example.main_screen.utils;

import com.example.main_screen.api.dto.EventItemDto;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Локально: отбор предстоящих событий и сортировка по ближайшей дате по тексту из API
 * ({@code date_caption}, {@code schedule}).
 */
public final class EventSoonUtils {

    private static final ZoneId ZONE = ZoneId.systemDefault();
    private static final Locale RU = Locale.forLanguageTag("ru-RU");

    private static final Pattern ISO_DATE = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final Pattern DMY_DOT = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})");
    private static final Pattern DMY_RU = Pattern.compile(
            "(\\d{1,2})\\s+(января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)\\s+(\\d{4})",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Map<String, Integer> RU_MONTH = new HashMap<>();

    static {
        String[] names = {"января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        for (int i = 0; i < names.length; i++) {
            RU_MONTH.put(names[i].toLowerCase(RU), i + 1);
        }
    }

    private static final DateTimeFormatter DMY_FMT = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
            .appendLiteral('.')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
            .appendLiteral('.')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter();

    private EventSoonUtils() {
    }

    /**
     * Оставляет события, которые ещё не закончились (по разобранным датам), сортирует по «ближайшему» дню.
     * События без распознанных дат попадают в конец списка.
     */
    public static List<EventItemDto> filterUpcomingAndSort(List<EventItemDto> events) {
        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        }
        LocalDate today = LocalDate.now(ZONE);
        List<Scored> scored = new ArrayList<>();
        for (EventItemDto e : events) {
            if (e == null) {
                continue;
            }
            DateRange range = parseDateRange(e);
            if (range == null) {
                scored.add(new Scored(e, Long.MAX_VALUE, 1));
                continue;
            }
            if (range.end.isBefore(today)) {
                continue;
            }
            LocalDate sortDay;
            if (!range.start.isBefore(today)) {
                sortDay = range.start;
            } else {
                sortDay = today;
            }
            long sortMillis = sortDay.atStartOfDay(ZONE).toInstant().toEpochMilli();
            scored.add(new Scored(e, sortMillis, 0));
        }
        Collections.sort(scored, (a, b) -> {
            int c = Long.compare(a.sortMillis, b.sortMillis);
            if (c != 0) {
                return c;
            }
            return Integer.compare(a.bucket, b.bucket);
        });
        List<EventItemDto> out = new ArrayList<>(scored.size());
        for (Scored s : scored) {
            out.add(s.dto);
        }
        return out;
    }

    private static DateRange parseDateRange(EventItemDto e) {
        String a = e.dateCaption != null ? e.dateCaption : "";
        String b = e.schedule != null ? e.schedule : "";
        String text = (a + " " + b).trim();
        if (text.isEmpty()) {
            return null;
        }
        List<LocalDate> dates = extractDates(text);
        if (dates.isEmpty()) {
            return null;
        }
        LocalDate start = Collections.min(dates);
        LocalDate end = Collections.max(dates);
        return new DateRange(start, end);
    }

    private static List<LocalDate> extractDates(String text) {
        List<LocalDate> out = new ArrayList<>();
        Matcher m = ISO_DATE.matcher(text);
        while (m.find()) {
            try {
                out.add(LocalDate.parse(m.group(), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (DateTimeParseException ignored) {
            }
        }
        m = DMY_DOT.matcher(text);
        while (m.find()) {
            try {
                String chunk = m.group(1) + '.' + m.group(2) + '.' + m.group(3);
                out.add(LocalDate.parse(chunk, DMY_FMT));
            } catch (DateTimeParseException ignored) {
            }
        }
        m = DMY_RU.matcher(text);
        while (m.find()) {
            int day = Integer.parseInt(m.group(1));
            String monName = m.group(2).toLowerCase(RU);
            int year = Integer.parseInt(m.group(3));
            Integer mon = RU_MONTH.get(monName);
            if (mon != null) {
                try {
                    out.add(LocalDate.of(year, mon, day));
                } catch (Exception ignored) {
                }
            }
        }
        return out;
    }

    private static final class DateRange {
        final LocalDate start;
        final LocalDate end;

        DateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
    }

    private static final class Scored {
        final EventItemDto dto;
        final long sortMillis;
        /** 0 — дата известна, 1 — в конец (не распознали). */
        final int bucket;

        Scored(EventItemDto dto, long sortMillis, int bucket) {
            this.dto = dto;
            this.sortMillis = sortMillis;
            this.bucket = bucket;
        }
    }
}
