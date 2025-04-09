package com.example.main_screen.bottomnav.plus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;
import com.example.main_screen.adapter.LearningAdapter;
import com.example.main_screen.data.LocalLearningData;
import com.example.main_screen.databinding.FragmentPlusBinding;
import com.example.main_screen.model.LearningItem;

import java.util.List;

public class PlusFragment extends Fragment {
    private FragmentPlusBinding binding;
    private RecyclerView gamesRecycler, videosRecycler, articlesRecycler;
    private TextView directionText;
    
    private List<LearningItem> gamesList;
    private List<LearningItem> videosList;
    private List<LearningItem> articlesList;
    
    private LearningAdapter gamesAdapter;
    private LearningAdapter videosAdapter;
    private LearningAdapter articlesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlusBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        
        // Инициализация элементов
        initViews(view);
        
        // Настройка адаптеров
        setupRecyclers();
        
        return view;
    }
    
    private void initViews(View view) {
        directionText = view.findViewById(R.id.direction_text);
        gamesRecycler = view.findViewById(R.id.games_recycler);
        videosRecycler = view.findViewById(R.id.videos_recycler);
        articlesRecycler = view.findViewById(R.id.articles_recycler);
        
        // Установка направления (временное решение)
        directionText.setText("Программирование");
    }
    
    private void setupRecyclers() {
        // Получение данных
        gamesList = LocalLearningData.getGames();
        videosList = LocalLearningData.getVideos();
        articlesList = LocalLearningData.getArticles();
        
        // Настройка RecyclerView для игр
        gamesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        gamesAdapter = new LearningAdapter("game");
        gamesRecycler.setAdapter(gamesAdapter);
        
        // Настройка RecyclerView для видео
        videosRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        videosAdapter = new LearningAdapter("video");
        videosRecycler.setAdapter(videosAdapter);
        
        // Настройка RecyclerView для статей
        articlesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        articlesAdapter = new LearningAdapter("article");
        articlesRecycler.setAdapter(articlesAdapter);
    }
}
