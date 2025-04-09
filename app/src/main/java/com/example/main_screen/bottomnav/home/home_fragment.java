package com.example.main_screen.bottomnav.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.main_screen.CoursePage;
import com.example.main_screen.R;
import com.example.main_screen.bottomnav.events.ThemainscreenFragment;
import com.example.main_screen.model.PlaceModel;
import com.example.main_screen.product_card;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.main_screen.databinding.FragmentHomeBinding;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.main_screen.adapter.CategoryAdapter;
import com.example.main_screen.model.CategoryModel;

public class home_fragment extends Fragment {

    private MapView mapView;
    private FragmentHomeBinding binding;
    private MapObjectCollection mapObjects;
    private RecyclerView categoriesRecycler;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryList;
    private boolean showAllMarkers = true;

    // Map для хранения информации о местах
    private Map<String, PlaceModel> placesInfo = new HashMap<>();

    // Инициализация данных о местах
    private void initPlacesInfo() {
        // Театры
        placesInfo.put("56.324128,44.001458",
                new PlaceModel("Театр драмы", "Нижегородский государственный академический театр драмы им. М. Горького", R.drawable.location_pin, "Театр"));
        placesInfo.put("56.315830,44.016955",
                new PlaceModel("Театр оперы", "Нижегородский государственный академический театр оперы и балета", R.drawable.location_pin, "Театр"));
        placesInfo.put("56.318407,43.995999",
                new PlaceModel("Театр кукол", "Нижегородский государственный театр кукол", R.drawable.location_pin, "Театр"));
        placesInfo.put("56.316545,44.010511",
                new PlaceModel("Театр юного зрителя", "Нижегородский театр юного зрителя", R.drawable.location_pin, "Театр"));
        placesInfo.put("56.320387,44.002252",
                new PlaceModel("Театр комедии", "Нижегородский театр комедии", R.drawable.location_pin, "Театр"));

        // Парки
        placesInfo.put("56.327844,44.035364",
                new PlaceModel("Парк Победы", "Парк культуры и отдыха в Нижнем Новгороде", R.drawable.green_pin, "Парк"));
        placesInfo.put("56.274489,43.973353",
                new PlaceModel("Парк Швейцария", "Крупнейший парк в Нижнем Новгороде", R.drawable.green_pin, "Парк"));
        placesInfo.put("56.315192,44.008244",
                new PlaceModel("Парк Кулибина", "Парк имени И.П. Кулибина", R.drawable.green_pin, "Парк"));
        placesInfo.put("56.311604,43.935067",
                new PlaceModel("Парк 1 Мая", "Парк культуры и отдыха 1 Мая", R.drawable.green_pin, "Парк"));
        placesInfo.put("56.268348,43.919829",
                new PlaceModel("Парк Дубки", "Парк Дубки с вековыми дубами", R.drawable.green_pin, "Парк"));
        placesInfo.put("56.339099,43.857030",
                new PlaceModel("Парк Сормовский", "Сормовский парк культуры и отдыха", R.drawable.green_pin, "Парк"));

        // Музеи
        placesInfo.put("56.324340,43.882085",
                new PlaceModel("Художественный музей", "Нижегородский государственный художественный музей", R.drawable.blue_pin, "Музей"));
        placesInfo.put("56.255355,43.894809",
                new PlaceModel("Музей истории", "Музей истории города Нижнего Новгорода", R.drawable.blue_pin, "Музей"));
        placesInfo.put("56.320417,43.946482",
                new PlaceModel("Музей науки", "Музей науки и техники", R.drawable.blue_pin, "Музей"));
        placesInfo.put("56.322769,44.018617",
                new PlaceModel("Музей Горького", "Музей-квартира А.М. Горького", R.drawable.blue_pin, "Музей"));
        placesInfo.put("56.322736,43.998282",
                new PlaceModel("Музей Добролюбова", "Музей Н.А. Добролюбова", R.drawable.blue_pin, "Музей"));
        placesInfo.put("56.327350,44.017002",
                new PlaceModel("Музей речного флота", "Музей истории речного флота", R.drawable.blue_pin, "Музей"));

        // Достопримечательности
        placesInfo.put("56.328624,44.002842",
                new PlaceModel("Кремль", "Нижегородский кремль - исторический центр города", R.drawable.location_pin, "Локация"));
        placesInfo.put("56.330872,44.009461",
                new PlaceModel("Чкаловская лестница", "Знаменитая лестница с видом на Волгу", R.drawable.location_pin, "Локация"));
        placesInfo.put("56.317088,43.994829",
                new PlaceModel("Покровка", "Главная пешеходная улица города", R.drawable.location_pin, "Локация"));
        placesInfo.put("56.327306,43.984992",
                new PlaceModel("Рождественская церковь", "Церковь Рождества Иоанна Предтечи", R.drawable.location_pin, "Локация"));
        placesInfo.put("56.357886,43.869049",
                new PlaceModel("Собор Александра Невского", "Кафедральный собор Александра Невского", R.drawable.location_pin, "Локация"));

        // Кафе и рестораны
        placesInfo.put("56.177811,44.177048",
                new PlaceModel("Ресторан Волга", "Ресторан с панорамным видом на реку", R.drawable.yellow_pin, "Ресторан"));
    }

    MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            if (mapObject instanceof PlacemarkMapObject) {
                PlacemarkMapObject placemark = (PlacemarkMapObject) mapObject;
                Point geometry = placemark.getGeometry();
                String key = String.format(Locale.US, "%.6f,%.6f", geometry.getLatitude(), geometry.getLongitude());

                PlaceModel placeInfo = placesInfo.get(key);

                if (placeInfo != null && getActivity() != null) {
                    Intent intent = new Intent(getActivity(), product_card.class);
                    intent.putExtra("detail", placeInfo);
                    startActivity(intent);
                }
            }
            return true;
        }
    };

    private void showPlaceInfoDialog(PlaceModel info) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View dialogView = getLayoutInflater().inflate(R.layout.place_info_dialog, null);

            ImageView imageView = dialogView.findViewById(R.id.place_image);
            TextView titleView = dialogView.findViewById(R.id.place_title);
            TextView descriptionView = dialogView.findViewById(R.id.place_description);

            imageView.setImageResource(info.getImageResourceId());
            titleView.setText(info.getName());
            descriptionView.setText(info.getDescription());

            builder.setView(dialogView)
                    .setPositiveButton("Закрыть", null);

            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d("Dialog", "Диалог успешно показан");
        } catch (Exception e) {
            Log.e("Dialog", "Ошибка при показе диалога", e);
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> dictionary = new HashMap<>();
        dictionary.put("mappoint_1", 1);
        dictionary.put("mappoint_2", 2);
        dictionary.put("mappoint_3", 3);
        dictionary.put("mappoint_4", 4);
        dictionary.put("mappoint_5", 5);
        dictionary.put("mappoint_6", 6);
        dictionary.put("mappoint_7", 7);
        dictionary.put("mappoint_8", 8);
        dictionary.put("mappoint_9", 9);
        dictionary.put("mappoint_10", 10);
        dictionary.put("mappoint_11", 11);
        dictionary.put("mappoint_12", 12);}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            MapKitFactory.setApiKey("8ecdd28d-ed83-4d94-baa0-afa3c921c403");
        } catch (AssertionError e) {
            Log.e("AssertionError", "An assertion error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        View fragment_home = inflater.inflate(R.layout.fragment_home, container, false);

        super.onCreate(savedInstanceState);
        mapView = fragment_home.findViewById(R.id.mapView);
        MapKitFactory.initialize(home_fragment.this.getActivity());

        // Инициализация RecyclerView
        categoriesRecycler = fragment_home.findViewById(R.id.categories_recycler);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Инициализация списка категорий
        categoryList = new ArrayList<>();
        categoryList.add(new CategoryModel("Все", "all"));
        categoryList.add(new CategoryModel("Музеи", "Музей"));
        categoryList.add(new CategoryModel("Театры", "Театр"));
        categoryList.add(new CategoryModel("Парки", "Парк"));
        categoryList.add(new CategoryModel("Локации", "Локация"));
        categoryList.add(new CategoryModel("Рестораны", "Ресторан"));

        // Инициализация адаптера
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(int position, String categoryType) {
                if (categoryType.equals("all")) {
                    showAllMarkers = true;
                } else {
                    showAllMarkers = false;
                }
                updatePlacemarkMapObjectVisibility();
            }
        });

        categoriesRecycler.setAdapter(categoryAdapter);

        // Инициализация информации о местах
        initPlacesInfo();

        // Добавляем все метки на карту
        updatePlacemarkMapObjectVisibility();

        // Устанавливаем начальную позицию камеры
        if (mapView != null) {
            mapView.getMapWindow().getMap().move(
                    new CameraPosition(new Point(56.326797, 44.006516), 14.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 5),
                    null);
        }
        return fragment_home;
    }
    @Override
    public void onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    public void updatePlacemarkMapObjectVisibility() {
        // Очищаем все существующие метки
        mapView.getMapWindow().getMap().getMapObjects().clear();

        if (showAllMarkers) {
            // Показываем все метки
            addAllMarkers();
        } else {
            // Показываем только выбранную категорию
            String selectedType = categoryList.get(categoryAdapter.getSelectedPosition()).getType();
            if (selectedType.equals("Музей")) {
                addMuseumMarkers();
            } else if (selectedType.equals("Театр")) {
                addTheaterMarkers();
            } else if (selectedType.equals("Парк")) {
                addParkMarkers();
            } else if (selectedType.equals("Локация")) {
                addLocationMarkers();
            } else if (selectedType.equals("Ресторан")) {
                addRestaurantMarkers();
            }
        }
    }

    private void addAllMarkers() {
        addMuseumMarkers();
        addTheaterMarkers();
        addParkMarkers();
        addLocationMarkers();
        addRestaurantMarkers();
    }

    private void addMuseumMarkers() {
        ImageProvider imageProvider = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.blue_pin);
        for (Map.Entry<String, PlaceModel> entry : placesInfo.entrySet()) {
            if (entry.getValue().getType().equals("Музей")) {
                String[] coords = entry.getKey().split(",");
                Point point = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                PlacemarkMapObject placemark = mapView.getMapWindow().getMap().getMapObjects().addPlacemark(point, imageProvider);
                placemark.setIconStyle(new IconStyle().setScale(0.1f));
                placemark.addTapListener(mapObjectTapListener);
            }
        }
    }

    private void addTheaterMarkers() {
        ImageProvider imageProvider = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.location_pin);
        for (Map.Entry<String, PlaceModel> entry : placesInfo.entrySet()) {
            if (entry.getValue().getType().equals("Театр")) {
                String[] coords = entry.getKey().split(",");
                Point point = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                PlacemarkMapObject placemark = mapView.getMapWindow().getMap().getMapObjects().addPlacemark(point, imageProvider);
                placemark.setIconStyle(new IconStyle().setScale(0.1f));
                placemark.addTapListener(mapObjectTapListener);
            }
        }
    }

    private void addParkMarkers() {
        ImageProvider imageProvider = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.green_pin);
        for (Map.Entry<String, PlaceModel> entry : placesInfo.entrySet()) {
            if (entry.getValue().getType().equals("Парк")) {
                String[] coords = entry.getKey().split(",");
                Point point = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                PlacemarkMapObject placemark = mapView.getMapWindow().getMap().getMapObjects().addPlacemark(point, imageProvider);
                placemark.setIconStyle(new IconStyle().setScale(0.1f));
                placemark.addTapListener(mapObjectTapListener);
            }
        }
    }

    private void addLocationMarkers() {
        ImageProvider imageProvider = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.yellow_pin);
        for (Map.Entry<String, PlaceModel> entry : placesInfo.entrySet()) {
            if (entry.getValue().getType().equals("Локация")) {
                String[] coords = entry.getKey().split(",");
                Point point = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                PlacemarkMapObject placemark = mapView.getMapWindow().getMap().getMapObjects().addPlacemark(point, imageProvider);
                placemark.setIconStyle(new IconStyle().setScale(0.1f));
                placemark.addTapListener(mapObjectTapListener);
            }
        }
    }

    private void addRestaurantMarkers() {
        ImageProvider imageProvider = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.location_pin);
        for (Map.Entry<String, PlaceModel> entry : placesInfo.entrySet()) {
            if (entry.getValue().getType().equals("Ресторан")) {
                String[] coords = entry.getKey().split(",");
                Point point = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                PlacemarkMapObject placemark = mapView.getMapWindow().getMap().getMapObjects().addPlacemark(point, imageProvider);
                placemark.setIconStyle(new IconStyle().setScale(0.1f));
                placemark.addTapListener(mapObjectTapListener);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updatePlacemarkMapObjectVisibility();
    }
}


