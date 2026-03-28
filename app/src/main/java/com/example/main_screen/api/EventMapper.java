package com.example.main_screen.api;

import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.model.RouteModel;
import com.example.main_screen.model.ViewAllModel;

import java.util.List;

public final class EventMapper {
    private EventMapper() {
    }

    public static String pickCoverImage(EventItemDto e) {
        if (e == null) {
            return "";
        }
        if (e.imgUrl != null && !e.imgUrl.trim().isEmpty()) {
            return e.imgUrl.trim();
        }
        if (e.imageUrls != null) {
            for (String u : e.imageUrls) {
                if (u != null && !u.trim().isEmpty()) {
                    return u.trim();
                }
            }
        }
        return "";
    }

    public static PopularModel toPopular(EventItemDto e) {
        PopularModel p = new PopularModel();
        if (e == null) {
            return p;
        }
        p.setServerId(e.id != null ? e.id : "");
        p.setName(e.name != null ? e.name : "");
        p.setImg_url(pickCoverImage(e));
        p.setDescription(e.description);
        p.setAge(e.age);
        String dateCap = e.dateCaption != null ? e.dateCaption : "";
        String sched = e.schedule != null ? e.schedule : "";
        p.setSchedule(!sched.isEmpty() ? sched : dateCap);
        p.setData(!dateCap.isEmpty() ? dateCap : sched);
        p.setPlace(e.place);
        p.setUrl(e.url != null ? e.url : "");
        String r = e.rating;
        p.setRating(r != null && !r.trim().isEmpty() ? r.trim() : "—");
        p.setFavorite(false);
        p.setStatus(e.status != null ? e.status : "");
        p.setType(e.type != null ? e.type : "");
        return p;
    }

    public static ViewAllModel toViewAll(EventItemDto e) {
        return toViewAll(toPopular(e));
    }

    public static ViewAllModel toViewAll(PopularModel p) {
        if (p == null) {
            return new ViewAllModel();
        }
        ViewAllModel v = new ViewAllModel(
                p.getName(),
                p.getImg_url(),
                p.getDescription(),
                p.getAge(),
                p.getData(),
                p.getPlace(),
                p.getUrl()
        );
        v.setServerId(p.getServerId());
        v.setType(p.getType());
        v.setFavorite(p.isFavorite());
        v.setRating(p.getRating());
        return v;
    }

    /**
     * Вкладка «Маршруты»: на бекенде нет {@code /routes}, показываем события как карточки маршрутов.
     */
    public static RouteModel eventToRoute(EventItemDto e) {
        RouteModel m = new RouteModel();
        if (e == null) {
            return m;
        }
        m.setId(e.id != null ? e.id : "");
        m.setName(e.name);
        m.setImageUrl(pickCoverImage(e));
        m.setDescription(e.description != null ? e.description : "");
        m.setCategory(e.type != null && !e.type.isEmpty() ? e.type : "События");
        m.setGoal("Афиша");
        m.setDaysRange(e.dateCaption != null ? e.dateCaption : "");
        m.setPeopleCount("");
        m.setDuration(e.schedule != null ? e.schedule : "");
        m.setDifficulty(e.age != null ? e.age : "");
        m.setUrl(e.url != null ? e.url : "");
        m.setPlace(e.place != null ? e.place : "");
        return m;
    }
}
