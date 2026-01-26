package com.example.main_screen.bottomnav.plus;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;
import com.example.main_screen.RoutesFilterActivity;
import com.example.main_screen.adapter.RouteAdapter;
import com.example.main_screen.databinding.FragmentPlusBinding;
import com.example.main_screen.model.RouteModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlusFragment extends Fragment {

    private FragmentPlusBinding binding;
    private EditText searchBox;
    private ImageButton filterButton;
    private RecyclerView searchResultsRecycler;
    private View routesScrollView;

    // Категории маршрутов
    private Map<String, RecyclerView> categoryRecyclers = new HashMap<>();
    private Map<String, List<RouteModel>> categoryRoutes = new HashMap<>();
    private Map<String, RouteAdapter> categoryAdapters = new HashMap<>();

    // Все маршруты для поиска
    private List<RouteModel> allRoutes = new ArrayList<>();
    private RouteAdapter searchAdapter;

    // Фильтры
    private String selectedGoal = null;
    private String selectedDays = null;
    private String selectedPeopleCount = null;

    private ActivityResultLauncher<Intent> filterLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Регистрация ActivityResultLauncher для фильтров
        filterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    selectedGoal = data.getStringExtra("goal");
                    selectedDays = data.getStringExtra("days");
                    selectedPeopleCount = data.getStringExtra("peopleCount");
                    
                    // Применяем фильтры
                    applyFilters();
                }
            }
        );

        initViews();
        setupSearch();
        setupFilterButton();
        loadTestRoutes(); // TODO: Заменить на загрузку из Firebase
        setupCategoryRecyclers();
    }

    private void initViews() {
        searchBox = binding.searchBox;
        filterButton = binding.filterButton;
        searchResultsRecycler = binding.searchResultsRecycler;
        routesScrollView = binding.routesScrollView;

        // Инициализация RecyclerView для результатов поиска (вертикальный список)
        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchAdapter = new RouteAdapter(getContext(), new ArrayList<>(), true); // true для вертикального списка
        searchResultsRecycler.setAdapter(searchAdapter);
    }

    private void setupSearch() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase().trim();
                if (searchText.isEmpty()) {
                    // Скрываем результаты поиска, показываем категории
                    searchResultsRecycler.setVisibility(View.GONE);
                    routesScrollView.setVisibility(View.VISIBLE);
                } else {
                    // Показываем результаты поиска, скрываем категории
                    searchRoutes(searchText);
                    searchResultsRecycler.setVisibility(View.VISIBLE);
                    routesScrollView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void searchRoutes(String searchText) {
        List<RouteModel> filteredRoutes = new ArrayList<>();
        
        for (RouteModel route : allRoutes) {
            // Поиск по части слова в названии и описании
            String name = route.getName() != null ? route.getName().toLowerCase() : "";
            String description = route.getDescription() != null ? route.getDescription().toLowerCase() : "";
            
            if (name.contains(searchText) || description.contains(searchText)) {
                filteredRoutes.add(route);
            }
        }
        
        searchAdapter.setRoutes(filteredRoutes);
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RoutesFilterActivity.class);
            filterLauncher.launch(intent);
        });
    }

    private void applyFilters() {
        // Фильтруем маршруты по выбранным критериям
        for (String category : categoryRoutes.keySet()) {
            List<RouteModel> originalRoutes = categoryRoutes.get(category);
            List<RouteModel> filtered = new ArrayList<>();
            
            for (RouteModel route : originalRoutes) {
                boolean matches = true;
                
                // Проверка цели поездки
                if (selectedGoal != null && !selectedGoal.isEmpty()) {
                    if (route.getGoal() == null || !route.getGoal().equals(selectedGoal)) {
                        matches = false;
                    }
                }
                
                // Проверка количества дней
                if (selectedDays != null && !selectedDays.isEmpty()) {
                    if (route.getDaysRange() == null || !route.getDaysRange().equals(selectedDays)) {
                        matches = false;
                    }
                }
                
                // Проверка количества человек
                if (selectedPeopleCount != null && !selectedPeopleCount.isEmpty()) {
                    if (route.getPeopleCount() == null || !route.getPeopleCount().equals(selectedPeopleCount)) {
                        matches = false;
                    }
                }
                
                if (matches) {
                    filtered.add(route);
                }
            }
            
            // Обновляем адаптер категории
            if (categoryAdapters.containsKey(category)) {
                categoryAdapters.get(category).setRoutes(filtered);
            }
        }
    }

    private void loadTestRoutes() {
        // TODO: Заменить на загрузку из Firebase
        // Тестовые данные для категории "С детьми"
        List<RouteModel> childrenRoutes = new ArrayList<>();
        childrenRoutes.add(new RouteModel(
            "Старый и Новый Ижевск",
            null,
            "Познавательный маршрут по историческим местам города",
            "С детьми",
            "Экскурсия",
            "1",
            "2-4",
            "3 часа",
            "Легкий"
        ));
        childrenRoutes.add(new RouteModel(
            "Парки и развлечения",
            null,
            "Маршрут по паркам и детским площадкам",
            "С детьми",
            "Отдых",
            "1",
            "2-4",
            "4 часа",
            "Легкий"
        ));
        childrenRoutes.add(new RouteModel(
            "Музейный день",
            null,
            "Посещение музеев Ижевска",
            "С детьми",
            "Культура",
            "1",
            "2-4",
            "5 часов",
            "Легкий"
        ));
        categoryRoutes.put("С детьми", childrenRoutes);
        allRoutes.addAll(childrenRoutes);

        // Тестовые данные для категории "Романтические"
        List<RouteModel> romanticRoutes = new ArrayList<>();
        romanticRoutes.add(new RouteModel(
            "Вечерний Ижевск",
            null,
            "Романтическая прогулка по вечернему городу",
            "Романтические",
            "Отдых",
            "1",
            "2",
            "2 часа",
            "Легкий"
        ));
        romanticRoutes.add(new RouteModel(
            "Набережная и рестораны",
            null,
            "Прогулка по набережной с ужином",
            "Романтические",
            "Отдых",
            "1",
            "2",
            "3 часа",
            "Легкий"
        ));
        categoryRoutes.put("Романтические", romanticRoutes);
        allRoutes.addAll(romanticRoutes);

        // Тестовые данные для категории "Исторические"
        List<RouteModel> historicalRoutes = new ArrayList<>();
        historicalRoutes.add(new RouteModel(
            "Исторический центр",
            null,
            "Экскурсия по историческим местам",
            "Исторические",
            "Экскурсия",
            "2-3",
            "1-10",
            "6 часов",
            "Средний"
        ));
        historicalRoutes.add(new RouteModel(
            "Оружейная столица",
            null,
            "Маршрут по местам оружейного производства",
            "Исторические",
            "Культура",
            "1",
            "1-10",
            "4 часа",
            "Средний"
        ));
        categoryRoutes.put("Исторические", historicalRoutes);
        allRoutes.addAll(historicalRoutes);
    }

    private void setupCategoryRecyclers() {
        // Настройка RecyclerView для категории "С детьми"
        RecyclerView childrenRecycler = binding.routesChildrenRecycler;
        childrenRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RouteAdapter childrenAdapter = new RouteAdapter(getContext(), categoryRoutes.get("С детьми"));
        childrenRecycler.setAdapter(childrenAdapter);
        categoryRecyclers.put("С детьми", childrenRecycler);
        categoryAdapters.put("С детьми", childrenAdapter);

        // Настройка RecyclerView для категории "Романтические"
        RecyclerView romanticRecycler = binding.routesRomanticRecycler;
        romanticRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RouteAdapter romanticAdapter = new RouteAdapter(getContext(), categoryRoutes.get("Романтические"));
        romanticRecycler.setAdapter(romanticAdapter);
        categoryRecyclers.put("Романтические", romanticRecycler);
        categoryAdapters.put("Романтические", romanticAdapter);

        // Настройка RecyclerView для категории "Исторические"
        RecyclerView historicalRecycler = binding.routesHistoricalRecycler;
        historicalRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RouteAdapter historicalAdapter = new RouteAdapter(getContext(), categoryRoutes.get("Исторические"));
        historicalRecycler.setAdapter(historicalAdapter);
        categoryRecyclers.put("Исторические", historicalRecycler);
        categoryAdapters.put("Исторические", historicalAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
