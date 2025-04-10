package com.example.main_screen.services;

import com.example.main_screen.model.ViewedContent;
import com.example.main_screen.utils.ProgressManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ContentTrackingService {
    private static ContentTrackingService instance;
    private final DatabaseReference viewedContentRef;

    private ContentTrackingService() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            viewedContentRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(currentUser.getUid())
                    .child("viewedContent");
        } else {
            viewedContentRef = null;
        }
    }

    public static ContentTrackingService getInstance() {
        if (instance == null) {
            instance = new ContentTrackingService();
        }
        return instance;
    }

    public void trackContent(String contentId, String contentType) {
        if (viewedContentRef == null) return;

        // Проверяем, не был ли контент уже просмотрен
        viewedContentRef.child(contentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().exists()) {
                // Создаем запись о просмотренном контенте
                ViewedContent viewedContent = new ViewedContent(
                        contentId,
                        contentType,
                        System.currentTimeMillis(),
                        true
                );

                // Сохраняем в Firebase
                Map<String, Object> contentValues = new HashMap<>();
                contentValues.put("contentId", viewedContent.getContentId());
                contentValues.put("contentType", viewedContent.getContentType());
                contentValues.put("timestamp", viewedContent.getTimestamp());
                contentValues.put("isCompleted", viewedContent.isCompleted());

                viewedContentRef.child(contentId).setValue(contentValues);

                // Обновляем прогресс
                ProgressManager.updateProgress(contentId, contentType);
            }
        });
    }
} 