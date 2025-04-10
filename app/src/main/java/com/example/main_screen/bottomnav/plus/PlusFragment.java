package com.example.main_screen.bottomnav.plus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;
import com.example.main_screen.adapter.LearningAdapter;
import com.example.main_screen.data.LocalLearningData;
import com.example.main_screen.databinding.FragmentPlusBinding;
import com.example.main_screen.databinding.FragmentPlusTutorialBinding;
import com.example.main_screen.model.LearningItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PlusFragment extends Fragment {
    private static final String TAG = "PlusFragment";
    private FragmentPlusBinding binding;
    private FragmentPlusTutorialBinding tutorialBinding;
    private RecyclerView gamesRecycler, videosRecycler, articlesRecycler;
    private List<LearningItem> gamesList;
    private List<LearningItem> videosList;
    private List<LearningItem> articlesList;
    private LearningAdapter gamesAdapter;
    private LearningAdapter videosAdapter;
    private LearningAdapter articlesAdapter;
    private MediaPlayer mediaPlayer;
    private boolean isTutorialShown = false;
    private static final String PREFS_NAME = "TutorialPrefs";
    private static final String KEY_TUTORIAL_SHOWN = "tutorial_shown";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlusBinding.inflate(inflater, container, false);
        tutorialBinding = FragmentPlusTutorialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Проверяем, был ли показан туториал
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isTutorialShown = prefs.getBoolean(KEY_TUTORIAL_SHOWN, false);

        if (!isTutorialShown) {
            showTutorial();
        } else {
            showMainContent();
        }
    }

    private void showTutorial() {
        try {
            // Устанавливаем изображение для туториала
            tutorialBinding.tutorialImage.setImageResource(R.drawable.tutorial_background);
            Log.d(TAG, "Tutorial image resource set");
            
            // Добавляем туториал в контейнер
            binding.getRoot().addView(tutorialBinding.getRoot());
            Log.d(TAG, "Tutorial view added to container");
            
            // Настраиваем кнопку пропуска
            tutorialBinding.continueButton.setOnClickListener(v -> {
                Log.d(TAG, "Skip button clicked");
                stopBackgroundMusic();
                showMainContent();
                saveTutorialShown();
            });

            // Запускаем анимации
            startAnimations();
            Log.d(TAG, "Animations started");
            
            // Запускаем фоновую музыку
            startBackgroundMusic();
            Log.d(TAG, "Background music started");
        } catch (Exception e) {
            Log.e(TAG, "Error in showTutorial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showMainContent() {
        try {
            // Удаляем туториал из контейнера
            if (tutorialBinding.getRoot().getParent() != null) {
                ((ViewGroup) tutorialBinding.getRoot().getParent()).removeView(tutorialBinding.getRoot());
            }
            
            // Инициализируем основной контент
            initViews();
            setupRecyclers();
            
            // Показываем основной контент
            binding.getRoot().setVisibility(View.VISIBLE);
            Log.d(TAG, "Main content shown");
        } catch (Exception e) {
            Log.e(TAG, "Error in showMainContent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initViews() {
        gamesRecycler = binding.gamesRecycler;
        videosRecycler = binding.videosRecycler;
        articlesRecycler = binding.articlesRecycler;
    }
    
    private void setupRecyclers() {
        // Получение данных
        gamesList = LocalLearningData.getGames();
        videosList = LocalLearningData.getVideos();
        articlesList = LocalLearningData.getArticles();
        
        // Настройка RecyclerView для игр
        gamesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        gamesAdapter = new LearningAdapter("game");
        gamesAdapter.setItems(gamesList);
        gamesRecycler.setAdapter(gamesAdapter);
        
        // Настройка RecyclerView для видео
        videosRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        videosAdapter = new LearningAdapter("video");
        videosAdapter.setItems(videosList);
        videosRecycler.setAdapter(videosAdapter);
        
        // Настройка RecyclerView для статей
        articlesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        articlesAdapter = new LearningAdapter("article");
        articlesAdapter.setItems(articlesList);
        articlesRecycler.setAdapter(articlesAdapter);
    }

    private void startAnimations() {
        // Анимация появления изображения
        ObjectAnimator imageAnimator = ObjectAnimator.ofFloat(tutorialBinding.tutorialImage, "alpha", 0f, 1f);
        imageAnimator.setDuration(1500);
        imageAnimator.start();

        // Анимация появления текста
        AlphaAnimation textAnimation = new AlphaAnimation(0f, 1f);
        textAnimation.setDuration(1500);
        textAnimation.setStartOffset(500);
        tutorialBinding.tutorialText.startAnimation(textAnimation);

        // Анимация появления кнопки
        AlphaAnimation buttonAnimation = new AlphaAnimation(0f, 1f);
        buttonAnimation.setDuration(1500);
        buttonAnimation.setStartOffset(1000);
        tutorialBinding.continueButton.startAnimation(buttonAnimation);
    }

    private void startBackgroundMusic() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(requireContext(), R.raw.tutorial_music);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(false);
                    mediaPlayer.setOnCompletionListener(mp -> {
                        Log.d(TAG, "Music completed");
                        showMainContent();
                        saveTutorialShown();
                    });
                    mediaPlayer.start();
                    Log.d(TAG, "Music started successfully");
                } else {
                    Log.e(TAG, "Failed to create MediaPlayer");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in startBackgroundMusic: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "Music stopped and released");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in stopBackgroundMusic: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveTutorialShown() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_TUTORIAL_SHOWN, true);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopBackgroundMusic();
        binding = null;
        tutorialBinding = null;
    }
}
