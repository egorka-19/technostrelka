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

import com.bumptech.glide.Glide;
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

    private int placeImageRes(String drawableName) {
        if (getContext() == null) return R.drawable.ab1;
        int id = getResources().getIdentifier("ic_" + drawableName, "drawable", getContext().getPackageName());
        return id != 0 ? id : R.drawable.ab1;
    }

    // Инициализация данных о местах (Ижевск)
    private void initPlacesInfo() {
        // Театры (3)
        placesInfo.put("56.844125,53.199509",
                new PlaceModel("Робин Гуд", "Русский драматический театр Удмуртии", placeImageRes("robin"), "Театр", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.850470,53.199591",
                new PlaceModel("Любовь и голуби", "ДК «Аксион»", placeImageRes("loveandgolub"), "Театр", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.845329,53.198977",
                new PlaceModel("Алые паруса", "Национальный театр", placeImageRes("redparus"), "Театр", "0+", "10.04.2025", "Ижевск"));

        // Рестораны (3)
        placesInfo.put("56.848942,53.195590",
                new PlaceModel("Panorama", "Открыт до 23:00", placeImageRes("panorama"), "Ресторан", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.866523,53.207575",
                new PlaceModel("Penthouse", "Открыт до 00:00", placeImageRes("penthouse"), "Ресторан", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.848160,53.205816",
                new PlaceModel("Каре", "Открыт до 00:00", placeImageRes("kare"), "Ресторан", "0+", "10.04.2025", "Ижевск"));

        // Парки (3)
        placesInfo.put("56.864117,53.163655",
                new PlaceModel("Парк имени С. М. Кирова", "Открыт до 23:00", placeImageRes("kirova"), "Парк", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.846736,53.197960",
                new PlaceModel("Летний сад им. М. Горького", "Открыт до 22:00", placeImageRes("gorkogo"), "Парк", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.887326,53.249373",
                new PlaceModel("Парк Космонавтов", "Открыт до 21:00", placeImageRes("cosmos"), "Парк", "0+", "10.04.2025", "Ижевск"));

        // Музеи (3)
        placesInfo.put("56.843974,53.198077",
                new PlaceModel("Музей ИЖМАШ", "ул. Свердлова, 32", placeImageRes("izhmash"), "Музей", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.860644,53.182360",
                new PlaceModel("Музей почты Удмуртии", "ул. Кирова, 7", placeImageRes("pochta"), "Музей", "0+", "10.04.2025", "Ижевск"));
        placesInfo.put("56.845400,53.206505",
                new PlaceModel("Ижевский Мотомузей Кожушковых", "Советская ул., 9", placeImageRes("kozhushkovi"), "Музей", "0+", "10.04.2025", "Ижевск"));
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

            if (info.getImageUrl() != null && !info.getImageUrl().isEmpty()) {
                Glide.with(requireActivity()).load(info.getImageUrl()).into(imageView);
            } else {
                imageView.setImageResource(info.getImageResourceId());
            }
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
                    new CameraPosition(new Point(56.852924, 53.210754), 14.0f, 0.0f, 0.0f),
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


