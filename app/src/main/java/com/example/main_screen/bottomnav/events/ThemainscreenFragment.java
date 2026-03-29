package com.example.main_screen.bottomnav.events;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.EventMapper;
import com.example.main_screen.api.ReviewRatingPrefetch;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.api.dto.EventCategoryDto;
import com.example.main_screen.api.dto.HomeCategoriesJsonParser;
import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.api.dto.FavoriteStatusResponseDto;
import com.example.main_screen.api.dto.UserMeDto;
import com.example.main_screen.model.HomeCategory;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.model.ViewAllModel;
import com.example.main_screen.R;
import com.example.main_screen.adapter.HomeAdapter;
import com.example.main_screen.adapter.PopularAdapters;
import com.example.main_screen.adapter.ViewAllAdapters;
import com.example.main_screen.databinding.FragmentMainBinding;
import com.example.main_screen.utils.MediaUrlUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.google.gson.JsonElement;

import retrofit2.Response;

public class ThemainscreenFragment extends Fragment {
    private static final int PAGE_SIZE = 100;
    private static final String TAG_HOME_CAT = "HomeCategories";

    ProgressBar progressBar;
    ScrollView scrollView;
    private FragmentMainBinding binding;
    private Uri filePath;

    private ImageButton nextButton, allCategoryBtn;
    RecyclerView popularRec, homeCatRec;
    RoundedImageView userAvatar;
    View emptyStateView;

    private CheckBox low12, bow12;
    PopularAdapters popularAdapters;
    List<PopularModel> popularModelList;

    List<HomeCategory> categoryList;
    HomeAdapter homeAdapter;
    EditText search_box;
    private List<ViewAllModel> viewAllModelList;
    private RecyclerView recyclerViewSearch;
    private ViewAllAdapters viewAllAdapters;

    public String phone;

    private int age;
    String welcome_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        popularRec = view.findViewById(R.id.pop_rec);
        homeCatRec = view.findViewById(R.id.exp_rec);
        progressBar = view.findViewById(R.id.progressbar);
        userAvatar = view.findViewById(R.id.user_avatar);
        emptyStateView = view.findViewById(R.id.empty_state);
        phone = requireActivity().getIntent().getStringExtra("phone");

        progressBar.setVisibility(VISIBLE);

        // Загрузка аватарки пользователя
        loadUserAvatar();

        popularRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        popularModelList = new ArrayList<>();
        popularAdapters = new PopularAdapters(getActivity(), popularModelList);
        // Устанавливаем слушатель для обновления избранного
        popularAdapters.setOnFavoriteChangeListener(() -> {
            // Если выбрана категория "Избранное", обновляем список
            if (homeAdapter != null && homeAdapter.getSelectedPosition() == 1) {
                loadFavoriteEvents();
            }
        });
        // Устанавливаем слушатель для показа анимации уведомления
        popularAdapters.setOnShowNotificationListener(() -> {
            showFavoriteNotification();
        });
        popularRec.setAdapter(popularAdapters);

        loadAllEvents();

        homeCatRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        
        // Добавляем категорию "Все" первой
        categoryList.add(new HomeCategory("Все", "all"));
        // Добавляем категорию "Избранное"
        categoryList.add(new HomeCategory("Избранное", "favorite"));
        
        homeAdapter = new HomeAdapter(getActivity(), categoryList, this);
        homeCatRec.setAdapter(homeAdapter);

        loadEventCategoriesFromApi();

        ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()== Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                            filePath = result.getData().getData();

                            try{
                                Bitmap bitmap = MediaStore.Images.Media
                                        .getBitmap(
                                                requireContext().getContentResolver(),
                                                filePath
                                        );
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        // Поиск с улучшенной функциональностью (неполное совпадение)
        recyclerViewSearch = view.findViewById(R.id.search_rec);
        search_box = view.findViewById(R.id.search_box);
        viewAllModelList = new ArrayList<>();
        viewAllAdapters = new ViewAllAdapters(getContext(), viewAllModelList);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewSearch.setAdapter(viewAllAdapters);
        recyclerViewSearch.setHasFixedSize(true);
        
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                popularRec.setVisibility(VISIBLE);
                homeCatRec.setVisibility(VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                popularRec.setVisibility(VISIBLE);
                homeCatRec.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                popularRec.setVisibility(VISIBLE);
                homeCatRec.setVisibility(VISIBLE);
                if (s.toString().isEmpty()){
                    viewAllModelList.clear();
                    viewAllAdapters.notifyDataSetChanged();
                    recyclerViewSearch.setVisibility(INVISIBLE);
                } else {
                    // Улучшенный поиск: ищем по неполному совпадению в названии
                    searchProductImproved(s.toString().toLowerCase().trim());
                }
            }
        });

        return view;
    }

    private void loadEventCategoriesFromApi() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                if (!isAdded()) {
                    return;
                }
                Response<JsonElement> resp = ApiClient.get(requireContext()).getHomeCategories().execute();
                if (!isAdded() || getActivity() == null) {
                    return;
                }
                if (!resp.isSuccessful()) {
                    final int code = resp.code();
                    getActivity().runOnUiThread(() -> {
                        String msg = code == 401
                                ? "Войдите в аккаунт, чтобы загрузить категории"
                                : "Не удалось загрузить категории (код " + code + ")";
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    });
                    return;
                }
                JsonElement body = resp.body();
                List<EventCategoryDto> parsed = HomeCategoriesJsonParser.parse(body);
                List<EventCategoryDto> sorted = new ArrayList<>(parsed);
                Collections.sort(sorted, Comparator.comparingInt(c ->
                        c.sortOrder != null ? c.sortOrder : 0));
                getActivity().runOnUiThread(() -> {
                    if (!isAdded() || homeAdapter == null) {
                        return;
                    }
                    for (EventCategoryDto c : sorted) {
                        String typeForEvents = trimOrNull(c.type);
                        if (typeForEvents == null) {
                            typeForEvents = trimOrNull(c.name);
                        }
                        if (typeForEvents == null) {
                            continue;
                        }
                        String display = firstNonBlank(c.name, c.title, typeForEvents);
                        HomeCategory hc = new HomeCategory();
                        hc.setId(trimOrEmpty(c.id));
                        hc.setName(display);
                        hc.setType(typeForEvents);
                        categoryList.add(hc);
                    }
                    homeAdapter.notifyDataSetChanged();
                    if (parsed.isEmpty() && body != null && !body.isJsonNull()) {
                        boolean emptyArray = body.isJsonArray() && body.getAsJsonArray().size() == 0;
                        if (!emptyArray) {
                            Toast.makeText(getActivity(),
                                    "Категории не распознаны. Откройте Logcat по тегу " + TAG_HOME_CAT,
                                    Toast.LENGTH_LONG).show();
                            Log.w(TAG_HOME_CAT, "home-categories body: " + body);
                        }
                    }
                });
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private static String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String trimOrEmpty(String s) {
        return s != null ? s.trim() : "";
    }

    /** Первое непустое значение (для подписи категории на главном экране). */
    private static String firstNonBlank(String a, String b, String c) {
        String x = trimOrNull(a);
        if (x != null) {
            return x;
        }
        x = trimOrNull(b);
        if (x != null) {
            return x;
        }
        return c != null ? c : "";
    }

    /**
     * Аватар с backend (GET /users/me).
     */
    private void loadUserAvatar() {
        if (!TokenStore.get(requireContext()).hasAccessToken()) {
            userAvatar.setImageResource(R.drawable.profile);
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<UserMeDto> resp = ApiClient.get(requireContext()).getMe().execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().profileImageUrl != null
                        && !resp.body().profileImageUrl.isEmpty() && getActivity() != null) {
                    String url = MediaUrlUtils.resolveForApiClient(resp.body().profileImageUrl);
                    getActivity().runOnUiThread(() ->
                            Glide.with(requireContext())
                                    .load(url)
                                    .circleCrop()
                                    .placeholder(R.drawable.profile)
                                    .error(R.drawable.profile)
                                    .into(userAvatar));
                } else if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> userAvatar.setImageResource(R.drawable.profile));
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> userAvatar.setImageResource(R.drawable.profile));
                }
            }
        });
    }

    private void mergeFavoriteStatusIntoPopularList() {
        if (!TokenStore.get(requireContext()).hasAccessToken() || popularModelList == null || popularModelList.isEmpty()) {
            return;
        }
        List<String> ids = new ArrayList<>();
        for (PopularModel m : popularModelList) {
            if (m.getServerId() != null && !m.getServerId().isEmpty()) {
                ids.add(m.getServerId());
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<FavoriteStatusResponseDto> r =
                        ApiClient.get(requireContext()).getFavoritesStatus(ids).execute();
                if (!r.isSuccessful() || r.body() == null || r.body().favorites == null) {
                    return;
                }
                Map<String, Boolean> map = r.body().favorites;
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    for (PopularModel m : popularModelList) {
                        Boolean b = map.get(m.getServerId());
                        if (b != null) {
                            m.setFavorite(b);
                        }
                    }
                    if (popularAdapters != null) {
                        popularAdapters.notifyDataSetChanged();
                    }
                });
            } catch (Exception ignored) {
            }
        });
    }

    private void mergeFavoriteStatusIntoViewAllList() {
        if (!TokenStore.get(requireContext()).hasAccessToken() || viewAllModelList == null || viewAllModelList.isEmpty()) {
            return;
        }
        List<String> ids = new ArrayList<>();
        for (ViewAllModel m : viewAllModelList) {
            if (m.getServerId() != null && !m.getServerId().isEmpty()) {
                ids.add(m.getServerId());
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<FavoriteStatusResponseDto> r =
                        ApiClient.get(requireContext()).getFavoritesStatus(ids).execute();
                if (!r.isSuccessful() || r.body() == null || r.body().favorites == null) {
                    return;
                }
                Map<String, Boolean> map = r.body().favorites;
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    for (ViewAllModel m : viewAllModelList) {
                        Boolean b = map.get(m.getServerId());
                        if (b != null) {
                            m.setFavorite(b);
                        }
                    }
                    if (viewAllAdapters != null) {
                        viewAllAdapters.notifyDataSetChanged();
                    }
                });
            } catch (Exception ignored) {
            }
        });
    }

    private void loadAllEvents() {
        progressBar.setVisibility(VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<EventItemDto>> resp = ApiClient.get(requireContext())
                        .listEvents(null, PAGE_SIZE, 0)
                        .execute();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (resp.isSuccessful() && resp.body() != null) {
                        popularModelList.clear();
                        for (EventItemDto e : resp.body()) {
                            popularModelList.add(EventMapper.toPopular(e));
                        }
                        popularAdapters.notifyDataSetChanged();
                        mergeFavoriteStatusIntoPopularList();
                        ReviewRatingPrefetch.prefetchForPopularModels(requireContext(), popularModelList,
                                () -> {
                                    if (popularAdapters != null) {
                                        popularAdapters.notifyDataSetChanged();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Не удалось загрузить события", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    updateEmptyState();
                });
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        updateEmptyState();
                    });
                }
            }
        });
    }

    public void showAllItems() {
        loadAllEvents();
    }

    /**
     * Фильтрация событий по категории
     */
    public void filterItemsByCategory(String categoryType) {
        if (categoryType.equals("all")) {
            loadAllEvents();
            return;
        }

        if (categoryType.equals("favorite")) {
            loadFavoriteEvents();
            return;
        }

        progressBar.setVisibility(VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<EventItemDto>> resp = ApiClient.get(requireContext())
                        .listEvents(categoryType, PAGE_SIZE, 0)
                        .execute();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (resp.isSuccessful() && resp.body() != null) {
                        popularModelList.clear();
                        for (EventItemDto e : resp.body()) {
                            popularModelList.add(EventMapper.toPopular(e));
                        }
                        popularAdapters.notifyDataSetChanged();
                        mergeFavoriteStatusIntoPopularList();
                        ReviewRatingPrefetch.prefetchForPopularModels(requireContext(), popularModelList,
                                () -> {
                                    if (popularAdapters != null) {
                                        popularAdapters.notifyDataSetChanged();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Не удалось загрузить события", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    updateEmptyState();
                });
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        updateEmptyState();
                    });
                }
            }
        });
    }

    private void loadFavoriteEvents() {
        if (!TokenStore.get(requireContext()).hasAccessToken()) {
            popularModelList.clear();
            popularAdapters.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            updateEmptyState();
            return;
        }

        progressBar.setVisibility(VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<EventItemDto>> resp = ApiClient.get(requireContext())
                        .listMyFavorites()
                        .execute();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (resp.isSuccessful() && resp.body() != null) {
                        popularModelList.clear();
                        for (EventItemDto e : resp.body()) {
                            PopularModel pm = EventMapper.toPopular(e);
                            pm.setFavorite(true);
                            popularModelList.add(pm);
                        }
                        popularAdapters.notifyDataSetChanged();
                        ReviewRatingPrefetch.prefetchForPopularModels(requireContext(), popularModelList,
                                () -> {
                                    if (popularAdapters != null) {
                                        popularAdapters.notifyDataSetChanged();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Войдите, чтобы видеть избранное", Toast.LENGTH_LONG).show();
                        popularModelList.clear();
                        popularAdapters.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                    updateEmptyState();
                });
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        updateEmptyState();
                    });
                }
            }
        });
    }

    /**
     * Обновление состояния пустого списка
     */
    private void updateEmptyState() {
        if (emptyStateView == null) return;
        
        // Проверяем, пуст ли список (исключая поиск)
        boolean isEmpty = popularModelList.isEmpty() && recyclerViewSearch.getVisibility() != View.VISIBLE;
        
        if (isEmpty) {
            emptyStateView.setVisibility(View.VISIBLE);
            popularRec.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            popularRec.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Улучшенный поиск: ищет по неполному совпадению в названии события
     * Ищет даже если введено неполное слово
     */
    private void searchProductImproved(String searchText) {
        if (searchText.isEmpty()) {
            viewAllModelList.clear();
            viewAllAdapters.notifyDataSetChanged();
            recyclerViewSearch.setVisibility(INVISIBLE);
            popularRec.setVisibility(VISIBLE);
            homeCatRec.setVisibility(VISIBLE);
            updateEmptyState();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<EventItemDto>> resp = ApiClient.get(requireContext())
                        .searchEvents(searchText, PAGE_SIZE, 0)
                        .execute();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    viewAllModelList.clear();
                    if (resp.isSuccessful() && resp.body() != null) {
                        for (EventItemDto e : resp.body()) {
                            viewAllModelList.add(EventMapper.toViewAll(e));
                        }
                        mergeFavoriteStatusIntoViewAllList();
                        ReviewRatingPrefetch.prefetchForViewAllModels(requireContext(), viewAllModelList,
                                () -> {
                                    if (viewAllAdapters != null) {
                                        viewAllAdapters.notifyDataSetChanged();
                                    }
                                });
                    }
                    viewAllAdapters.notifyDataSetChanged();
                    if (viewAllModelList.isEmpty()) {
                        recyclerViewSearch.setVisibility(INVISIBLE);
                        popularRec.setVisibility(VISIBLE);
                        homeCatRec.setVisibility(VISIBLE);
                        updateEmptyState();
                    } else {
                        recyclerViewSearch.setVisibility(VISIBLE);
                        popularRec.setVisibility(INVISIBLE);
                        homeCatRec.setVisibility(VISIBLE);
                        if (emptyStateView != null) {
                            emptyStateView.setVisibility(View.GONE);
                        }
                    }
                });
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        recyclerViewSearch.setVisibility(INVISIBLE);
                        popularRec.setVisibility(VISIBLE);
                        homeCatRec.setVisibility(VISIBLE);
                        updateEmptyState();
                    });
                }
            }
        });
    }

    /**
     * Показ анимации уведомления "Добавлено в избранное!"
     */
    private void showFavoriteNotification() {
        if (getActivity() == null) return;

        // Получаем корневой контейнер Activity для показа поверх всех окон
        ViewGroup rootView = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
        if (rootView == null) return;

        // Ищем или создаем контейнер для уведомлений
        // Используем generateViewId() для динамического ID
        FrameLayout notificationContainer = null;
        int containerId = View.generateViewId();
        
        // Проверяем, есть ли уже контейнер (ищем по тегу)
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            if (child instanceof FrameLayout && child.getTag() != null && child.getTag().equals("notification_container")) {
                notificationContainer = (FrameLayout) child;
                break;
            }
        }
        
        if (notificationContainer == null) {
            // Создаем новый контейнер для уведомлений поверх всего
            notificationContainer = new FrameLayout(getContext());
            notificationContainer.setId(containerId);
            notificationContainer.setTag("notification_container");
            FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            notificationContainer.setLayoutParams(containerParams);
            notificationContainer.setClickable(false);
            notificationContainer.setFocusable(false);
            rootView.addView(notificationContainer);
        }

        // Сохраняем ссылку на контейнер как final для использования во внутренних классах
        final FrameLayout finalContainer = notificationContainer;

        // Загружаем layout уведомления
        View notificationView = LayoutInflater.from(getContext())
                .inflate(R.layout.favorite_notification, finalContainer, false);

        // Настраиваем параметры для позиционирования сверху по центру
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = 100; // Отступ сверху
        notificationView.setLayoutParams(params);

        // Начальное состояние - скрыто сверху
        notificationView.setAlpha(0f);
        notificationView.setTranslationY(-200f);
        finalContainer.addView(notificationView);

        // Анимация появления - вылет сверху
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(notificationView, "alpha", 0f, 1f);
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(notificationView, "translationY", -200f, 0f);
        
        android.animation.AnimatorSet animatorSet = new android.animation.AnimatorSet();
        animatorSet.playTogether(fadeIn, slideDown);
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // После появления ждем 2 секунды и скрываем
                notificationView.postDelayed(() -> {
                    // Анимация исчезновения
                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(notificationView, "alpha", 1f, 0f);
                    ObjectAnimator slideUp = ObjectAnimator.ofFloat(notificationView, "translationY", 0f, -200f);
                    
                    android.animation.AnimatorSet hideAnimator = new android.animation.AnimatorSet();
                    hideAnimator.playTogether(fadeOut, slideUp);
                    hideAnimator.setDuration(300);
                    hideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    
                    hideAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(android.animation.Animator animation) {
                            finalContainer.removeView(notificationView);
                        }
                    });
                    
                    hideAnimator.start();
                }, 2000);
            }
        });
        
        animatorSet.start();
    }
}
