package com.example.main_screen.services;

import com.example.main_screen.model.UserScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ScoreService {
    private static ScoreService instance;
    private final DatabaseReference userRef;
    private static final int SCORE_INCREMENT = 10;

    private ScoreService() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(currentUser.getUid());
        } else {
            userRef = null;
        }
    }

    public static ScoreService getInstance() {
        if (instance == null) {
            instance = new ScoreService();
        }
        return instance;
    }

    public void incrementScore() {
        if (userRef == null) return;

        userRef.child("score").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int currentScore = 0;
                if (task.getResult().getValue() != null) {
                    currentScore = task.getResult().getValue(Integer.class);
                }

                int newScore = currentScore + SCORE_INCREMENT;
                long timestamp = System.currentTimeMillis();

                Map<String, Object> scoreUpdate = new HashMap<>();
                scoreUpdate.put("score", newScore);
                scoreUpdate.put("lastUpdated", timestamp);

                userRef.updateChildren(scoreUpdate);
            }
        });
    }

    public void getScore(ScoreCallback callback) {
        if (userRef == null) {
            callback.onScoreReceived(0);
            return;
        }

        userRef.child("score").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int score = 0;
                if (task.getResult().getValue() != null) {
                    score = task.getResult().getValue(Integer.class);
                }
                callback.onScoreReceived(score);
            } else {
                callback.onScoreReceived(0);
            }
        });
    }

    public interface ScoreCallback {
        void onScoreReceived(int score);
    }
} 