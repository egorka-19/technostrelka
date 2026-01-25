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
import com.example.main_screen.model.HomeCategory;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.model.ViewAllModel;
import com.example.main_screen.R;
import com.example.main_screen.adapter.HomeAdapter;
import com.example.main_screen.adapter.PopularAdapters;
import com.example.main_screen.adapter.ViewAllAdapters;
import com.example.main_screen.databinding.FragmentMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThemainscreenFragment extends Fragment {
    ProgressBar progressBar;
    ScrollView scrollView;
    private FragmentMainBinding binding;
    FirebaseFirestore db;
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
        db = FirebaseFirestore.getInstance();
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

        // Загрузка всех событий из Firebase
        // TODO: Здесь можно изменить/добавить события - они загружаются из коллекции "events" в Firebase
        loadAllEvents();

        homeCatRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        
        // Добавляем категорию "Все" первой
        categoryList.add(new HomeCategory("Все", "all"));
        // Добавляем категорию "Избранное"
        categoryList.add(new HomeCategory("Избранное", "favorite"));
        
        homeAdapter = new HomeAdapter(getActivity(), categoryList, this);
        homeCatRec.setAdapter(homeAdapter);

        // Загрузка категорий из Firebase
        db.collection("HomeCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HomeCategory homeCategory = document.toObject(HomeCategory.class);
                                categoryList.add(homeCategory);
                                homeAdapter.notifyDataSetChanged();
                            }
                        } else {
                            System.out.println("Error" + task.getException());
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

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

    /**
     * Загрузка аватарки пользователя из Firebase
     */
    private void loadUserAvatar() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("profileImage")) {
                                String profileImageUrl = snapshot.child("profileImage").getValue(String.class);
                                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                    Glide.with(requireContext())
                                            .load(profileImageUrl)
                                            .circleCrop()
                                            .error(R.drawable.profile)
                                            .into(userAvatar);
                                } else {
                                    userAvatar.setImageResource(R.drawable.profile);
                                }
                            } else {
                                userAvatar.setImageResource(R.drawable.profile);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            userAvatar.setImageResource(R.drawable.profile);
                        }
                    });
        } else {
            userAvatar.setImageResource(R.drawable.profile);
        }
    }

    /**
     * Загрузка всех событий из Firebase
     * TODO: Здесь можно изменить/добавить события - они загружаются из коллекции "events" в Firebase
     */
    private void loadAllEvents() {
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            popularModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PopularModel popularModel = document.toObject(PopularModel.class);
                                popularModelList.add(popularModel);
                            }
                            popularAdapters.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            updateEmptyState();
                        } else {
                            System.out.println("Error" + task.getException());
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            updateEmptyState();
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

        db.collection("events")
                .whereEqualTo("type", categoryType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            popularModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PopularModel popularModel = document.toObject(PopularModel.class);
                                popularModelList.add(popularModel);
                            }
                            popularAdapters.notifyDataSetChanged();
                            updateEmptyState();
                        } else {
                            System.out.println("Error" + task.getException());
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                            updateEmptyState();
                        }
                    }
                });
    }

    /**
     * Загрузка избранных событий из Firebase
     */
    private void loadFavoriteEvents() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            popularModelList.clear();
            popularAdapters.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar.setVisibility(VISIBLE);

        // Сначала загружаем все избранные события из Realtime Database
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favoriteEventNames = new ArrayList<>();
                
                // Собираем все названия событий, которые в избранном
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot eventSnapshot : categorySnapshot.getChildren()) {
                        String eventName = eventSnapshot.getKey();
                        DataSnapshot userSnapshot = eventSnapshot.child(userId);
                        if (userSnapshot.exists() && userSnapshot.hasChild("lovest")) {
                            Integer lovestValue = userSnapshot.child("lovest").getValue(Integer.class);
                            if (lovestValue != null && lovestValue == 1) {
                                favoriteEventNames.add(eventName);
                            }
                        }
                    }
                }
                
                // Теперь загружаем события из Firestore и фильтруем по избранным
                if (favoriteEventNames.isEmpty()) {
                    popularModelList.clear();
                    popularAdapters.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    updateEmptyState();
                    return;
                }
                
                db.collection("events")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    popularModelList.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        PopularModel popularModel = document.toObject(PopularModel.class);
                                        if (favoriteEventNames.contains(popularModel.getName())) {
                                            popularModelList.add(popularModel);
                                        }
                                    }
                                    popularAdapters.notifyDataSetChanged();
                                } else {
                                    System.out.println("Error" + task.getException());
                                    Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.GONE);
                                updateEmptyState();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                popularModelList.clear();
                popularAdapters.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                updateEmptyState();
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
     * Преобразование типа категории для Firebase
     */
    private String getCategoryTypeForFirebase(String type) {
        if (type == null) return "Other";
        
        switch (type) {
            case "Кино":
            case "Cinema":
                return "Cinema";
            case "Театр":
            case "Theater":
                return "Theater";
            case "Парк":
            case "Park":
                return "Park";
            case "Ресторан":
            case "Restaurant":
                return "Restaraunt";
            case "Музей":
            case "Museum":
                return "Museum";
            default:
                return "Other";
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

        // Загружаем все события и фильтруем локально для неполного совпадения
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            viewAllModelList.clear();
                            
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                PopularModel popularModel = doc.toObject(PopularModel.class);
                                
                                if (popularModel != null) {
                                    String name = popularModel.getName() != null ? popularModel.getName().toLowerCase() : "";
                                    String description = popularModel.getDescription() != null ? popularModel.getDescription().toLowerCase() : "";
                                    String place = popularModel.getPlace() != null ? popularModel.getPlace().toLowerCase() : "";
                                    
                                    // Проверяем неполное совпадение в названии, описании или адресе
                                    if (name.contains(searchText) || description.contains(searchText) || place.contains(searchText)) {
                                        // Конвертируем PopularModel в ViewAllModel для результатов поиска
                                        ViewAllModel viewAllModel = new ViewAllModel(
                                                popularModel.getName(),
                                                popularModel.getImg_url(),
                                                popularModel.getDescription(),
                                                popularModel.getAge(),
                                                popularModel.getData(),
                                                popularModel.getPlace(),
                                                popularModel.getUrl()
                                        );
                                        viewAllModel.setType(popularModel.getType());
                                        viewAllModelList.add(viewAllModel);
                                    }
                                }
                            }
                            
                            viewAllAdapters.notifyDataSetChanged();
                            
                            // Показываем результаты поиска, скрываем основной список
                            if (viewAllModelList.isEmpty()) {
                                recyclerViewSearch.setVisibility(INVISIBLE);
                                popularRec.setVisibility(VISIBLE);
                                homeCatRec.setVisibility(VISIBLE);
                                updateEmptyState();
                            } else {
                                recyclerViewSearch.setVisibility(VISIBLE);
                                popularRec.setVisibility(INVISIBLE);
                                homeCatRec.setVisibility(VISIBLE); // Фильтры оставляем видимыми
                                // Скрываем пустое состояние при поиске
                                if (emptyStateView != null) {
                                    emptyStateView.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            recyclerViewSearch.setVisibility(INVISIBLE);
                            popularRec.setVisibility(VISIBLE);
                            homeCatRec.setVisibility(VISIBLE);
                            updateEmptyState();
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
