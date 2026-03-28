package com.example.main_screen.bottomnav.plus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.EventMapper;
import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.data.visitudmurtia.VisitUdmurtiaRoutesAssetLoader;
import com.example.main_screen.databinding.FragmentPlusBinding;
import com.example.main_screen.model.RouteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class PlusFragment extends Fragment {

    private FragmentPlusBinding binding;
    private EditText searchBox;
    private ImageButton filterButton;
    private RecyclerView searchResultsRecycler;
    private ScrollView routesScrollView;
    private LinearLayout categoriesContainer;
    private TextView routesEmpty;

    private final Map<String, RecyclerView> categoryRecyclers = new HashMap<>();
    private final Map<String, RouteAdapter> categoryAdapters = new HashMap<>();
    private final Map<String, View> categorySectionRoots = new HashMap<>();

    private List<RouteModel> allRoutes = new ArrayList<>();
    private RouteAdapter searchAdapter;

    private String selectedGoal;
    private String selectedDays;
    private String selectedPeopleCount;

    private ActivityResultLauncher<Intent> filterLauncher;

    private static final Comparator<String> CATEGORY_ORDER = (a, b) -> {
        int ia = categoryRank(a);
        int ib = categoryRank(b);
        if (ia != ib) {
            return Integer.compare(ia, ib);
        }
        return a.compareToIgnoreCase(b);
    };

    private static int categoryRank(String name) {
        if (name == null) {
            return 100;
        }
        switch (name) {
            case "С детьми":
                return 0;
            case "Исторические":
                return 1;
            case "Романтические":
                return 2;
            case "Ижевск":
                return 3;
            case "Удмуртия":
                return 4;
            case "Воткинск":
            case "Сарапул":
                return 5;
            case "Башкортостан-Удмуртия":
                return 6;
            default:
                return 20;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        selectedGoal = data.getStringExtra("goal");
                        selectedDays = data.getStringExtra("days");
                        selectedPeopleCount = data.getStringExtra("peopleCount");
                        applyFilters();
                    }
                });

        initViews();
        setupSearch();
        setupFilterButton();
        loadRoutesFromApi();
    }

    private void initViews() {
        searchBox = binding.searchBox;
        filterButton = binding.filterButton;
        searchResultsRecycler = binding.searchResultsRecycler;
        routesScrollView = binding.routesScrollView;
        categoriesContainer = binding.routesCategoriesContainer;
        routesEmpty = binding.routesEmpty;

        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchAdapter = new RouteAdapter(getContext(), new ArrayList<>(), true);
        searchResultsRecycler.setAdapter(searchAdapter);
    }

    private void setupSearch() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase().trim();
                if (searchText.isEmpty()) {
                    searchResultsRecycler.setVisibility(View.GONE);
                    routesScrollView.setVisibility(View.VISIBLE);
                } else {
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
        for (String category : categoryRoutesKeySet()) {
            List<RouteModel> originalRoutes = getRoutesForCategory(category);
            List<RouteModel> filtered = new ArrayList<>();
            for (RouteModel route : originalRoutes) {
                if (matchesFilters(route)) {
                    filtered.add(route);
                }
            }
            RouteAdapter adapter = categoryAdapters.get(category);
            if (adapter != null) {
                adapter.setRoutes(filtered);
            }
            View section = categorySectionRoots.get(category);
            if (section != null) {
                section.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
        updateEmptyState();
    }

    private boolean matchesFilters(RouteModel route) {
        if (selectedGoal != null && !selectedGoal.isEmpty()) {
            if (route.getGoal() == null || !route.getGoal().equals(selectedGoal)) {
                return false;
            }
        }
        if (selectedDays != null && !selectedDays.isEmpty()) {
            if (route.getDaysRange() == null || !route.getDaysRange().equals(selectedDays)) {
                return false;
            }
        }
        if (selectedPeopleCount != null && !selectedPeopleCount.isEmpty()) {
            if (route.getPeopleCount() == null || !route.getPeopleCount().equals(selectedPeopleCount)) {
                return false;
            }
        }
        return true;
    }

    /** Снимок категорий после загрузки (без учёта фильтра в UI). */
    private final Map<String, List<RouteModel>> categoryRoutes = new HashMap<>();

    private Iterable<String> categoryRoutesKeySet() {
        return categoryRoutes.keySet();
    }

    private List<RouteModel> getRoutesForCategory(String category) {
        List<RouteModel> list = categoryRoutes.get(category);
        return list != null ? list : Collections.emptyList();
    }

    private void loadRoutesFromApi() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Context appCtx = requireContext().getApplicationContext();
            List<RouteModel> embedded = VisitUdmurtiaRoutesAssetLoader.load(appCtx);
            Response<List<EventItemDto>> resp = null;
            try {
                resp = ApiClient.get(requireContext())
                        .listEvents(null, 100, 0)
                        .execute();
            } catch (Exception ignored) {
            }
            if (getActivity() == null) {
                return;
            }
            Response<List<EventItemDto>> responseFinal = resp;
            getActivity().runOnUiThread(() -> applyLoadedRoutes(embedded, responseFinal));
        });
    }

    private void addRoutesToCategories(List<RouteModel> routes) {
        for (RouteModel m : routes) {
            if (m == null) {
                continue;
            }
            allRoutes.add(m);
            String cat = m.getCategory() != null && !m.getCategory().isEmpty() ? m.getCategory() : "Удмуртия";
            categoryRoutes.computeIfAbsent(cat, k -> new ArrayList<>()).add(m);
        }
    }

    private void applyLoadedRoutes(List<RouteModel> embedded, Response<List<EventItemDto>> resp) {
        categoryRoutes.clear();
        allRoutes.clear();
        if (embedded != null && !embedded.isEmpty()) {
            addRoutesToCategories(embedded);
        }
        if (resp != null && resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
            List<RouteModel> apiRoutes = new ArrayList<>();
            for (EventItemDto e : resp.body()) {
                apiRoutes.add(EventMapper.eventToRoute(e));
            }
            addRoutesToCategories(apiRoutes);
        } else if (allRoutes.isEmpty()) {
            loadTestRoutesFallback();
        }
        setupCategoryRecyclers();
    }

    private void loadTestRoutesFallback() {
        categoryRoutes.clear();
        allRoutes.clear();

        List<RouteModel> childrenRoutes = new ArrayList<>();
        childrenRoutes.add(route("fb-1", "Старый и Новый Ижевск", null,
                "Познавательный маршрут по историческим местам города: от старой застройки до современных кварталов.",
                "С детьми", "Экскурсия", "1", "2-4", "3 часа", "Легкий"));
        childrenRoutes.add(route("fb-2", "Парки и развлечения", null,
                "Маршрут по паркам и зонам отдыха — удобно гулять с детьми и делать остановки.",
                "С детьми", "Отдых", "1", "2-4", "4 часа", "Легкий"));
        childrenRoutes.add(route("fb-3", "Музейный день", null,
                "Несколько музеев Ижевска в одном дне: короткие переходы и насыщенная программа.",
                "С детьми", "Культура", "1", "2-4", "5 часов", "Легкий"));
        categoryRoutes.put("С детьми", childrenRoutes);
        allRoutes.addAll(childrenRoutes);

        List<RouteModel> romanticRoutes = new ArrayList<>();
        romanticRoutes.add(route("fb-4", "Вечерний Ижевск", null,
                "Спокойная вечерняя прогулка по подсвеченным улицам и набережным.",
                "Романтические", "Отдых", "1", "2", "2 часа", "Легкий"));
        romanticRoutes.add(route("fb-5", "Набережная и рестораны", null,
                "Маршрут вдоль воды с остановками в кафе и ресторанах.",
                "Романтические", "Отдых", "1", "2", "3 часа", "Легкий"));
        categoryRoutes.put("Романтические", romanticRoutes);
        allRoutes.addAll(romanticRoutes);

        List<RouteModel> historicalRoutes = new ArrayList<>();
        historicalRoutes.add(route("fb-6", "Исторический Ижевск", null,
                "Ключевые памятники и места, связанные с историей столицы Удмуртии.",
                "Исторические", "Экскурсия", "2-3", "1-10", "6 часов", "Средний"));
        historicalRoutes.add(route("fb-7", "Оружейная столица", null,
                "Промышленное и военно-историческое наследие города-оружейников.",
                "Исторические", "Культура", "1", "1-10", "4 часа", "Средний"));
        categoryRoutes.put("Исторические", historicalRoutes);
        allRoutes.addAll(historicalRoutes);
    }

    private static RouteModel route(String id, String name, String imageUrl, String description,
                                    String category, String goal, String daysRange, String peopleCount,
                                    String duration, String difficulty) {
        RouteModel m = new RouteModel(name, imageUrl, description, category, goal, daysRange, peopleCount, duration, difficulty);
        m.setId(id);
        m.setPlace("Ижевск");
        return m;
    }

    private void setupCategoryRecyclers() {
        categoryRecyclers.clear();
        categoryAdapters.clear();
        categorySectionRoots.clear();
        categoriesContainer.removeAllViews();

        List<String> ordered = new ArrayList<>(categoryRoutes.keySet());
        Collections.sort(ordered, CATEGORY_ORDER);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (String cat : ordered) {
            List<RouteModel> routes = categoryRoutes.get(cat);
            if (routes == null || routes.isEmpty()) {
                continue;
            }
            View section = inflater.inflate(R.layout.item_route_category_section, categoriesContainer, false);
            TextView title = section.findViewById(R.id.section_title);
            RecyclerView rv = section.findViewById(R.id.section_recycler);
            title.setText(cat);
            rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            RouteAdapter adapter = new RouteAdapter(getContext(), new ArrayList<>(routes));
            rv.setAdapter(adapter);
            categoryRecyclers.put(cat, rv);
            categoryAdapters.put(cat, adapter);
            categorySectionRoots.put(cat, section);
            categoriesContainer.addView(section);
        }

        if (selectedGoal != null || selectedDays != null || selectedPeopleCount != null) {
            applyFilters();
        } else {
            updateEmptyState();
        }
    }

    private void updateEmptyState() {
        boolean any = false;
        for (int i = 0; i < categoriesContainer.getChildCount(); i++) {
            if (categoriesContainer.getChildAt(i).getVisibility() == View.VISIBLE) {
                any = true;
                break;
            }
        }
        routesEmpty.setVisibility(any ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
