package com.example.main_screen.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProgressManager {
    private static final int MAX_PROGRESS = 100;
    private static final int ARTICLE_POINTS = 5;
    private static final int VIDEO_POINTS = 10;

    public static void updateProgress(String contentId, String contentType) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(currentUser.getUid());

        // Получаем текущий прогресс
        userRef.child("progress").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int currentProgress = 0;
                if (task.getResult().getValue() != null) {
                    currentProgress = task.getResult().getValue(Integer.class);
                }

                // Вычисляем новые очки
                int newPoints = contentType.equals("article") ? ARTICLE_POINTS : VIDEO_POINTS;
                int newProgress = Math.min(currentProgress + newPoints, MAX_PROGRESS);

                // Обновляем прогресс
                userRef.child("progress").setValue(newProgress);
            }
        });
    }

    public static void resetProgress() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(currentUser.getUid());

        userRef.child("progress").setValue(0);
    }
} 