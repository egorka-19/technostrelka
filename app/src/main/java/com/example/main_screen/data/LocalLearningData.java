package com.example.main_screen.data;

import com.example.main_screen.R;
import com.example.main_screen.model.LearningItem;
import java.util.ArrayList;
import java.util.List;

public class LocalLearningData {
    public static List<LearningItem> getGames() {
        List<LearningItem> games = new ArrayList<>();
        games.add(new LearningItem(
                "1",
                "Верю-не верю",
                "Проверьте свои знания в области программирования",
                R.drawable.thisorthat, // Замените на реальный ID ресурса
                "game",
                "com.example.main_screen.ChatActivity"
        ));
        games.add(new LearningItem(
                "2",
                "Угадай-ка!",
                "Соберите алгоритм из частей",
                R.drawable.ugadai, // Замените на реальный ID ресурса
                "game",
                "com.example.main_screen.PuzzleGameActivity"
        ));
        games.add(new LearningItem(
                "3",
                "Арт-Искууство",
                "Проверьте знание структур данных",
                R.drawable.art, // Замените на реальный ID ресурса
                "game",
                "com.example.main_screen.DataStructuresGameActivity"
        ));
        return games;
    }

    public static List<LearningItem> getVideos() {
        List<LearningItem> videos = new ArrayList<>();
        videos.add(new LearningItem(
                "4",
                "Основы программирования",
                "Введение в программирование для начинающих",
                R.drawable.thisorthat, // Замените на реальный ID ресурса
                "video",
                "com.example.main_screen.VideoPlayerActivity"
        ));
        videos.add(new LearningItem(
                "5",
                "Алгоритмы и структуры данных",
                "Подробный разбор основных алгоритмов",
                R.drawable.thisorthat, // Замените на реальный ID ресурса
                "video",
                "com.example.main_screen.VideoPlayerActivity"
        ));
        return videos;
    }

    public static List<LearningItem> getArticles() {
        List<LearningItem> articles = new ArrayList<>();
        articles.add(new LearningItem(
                "6",
                "Современные технологии",
                "Обзор последних технологических трендов",
                R.drawable.thisorthat, // Замените на реальный ID ресурса
                "article",
                "com.example.main_screen.ArticleActivity"
        ));
        articles.add(new LearningItem(
                "7",
                "Карьера в IT",
                "Как построить успешную карьеру в IT",
                R.drawable.thisorthat, // Замените на реальный ID ресурса
                "article",
                "com.example.main_screen.ArticleActivity"
        ));
        return articles;
    }
} 