package com.example.main_screen.bottomnav.home;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main_screen.CoursePage;
import com.example.main_screen.R;
import com.example.main_screen.bottomnav.events.ThemainscreenFragment;
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
import java.util.Map;

public class home_fragment extends Fragment {

    private MapView mapView;
    private FragmentHomeBinding binding;
    private MapObjectCollection mapObjects;
    MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ThemainscreenFragment eventFragment = new ThemainscreenFragment();
                fragmentTransaction.replace(R.id.fragment_container, eventFragment);
                fragmentTransaction.addToBackStack(null); // Добавляем транзакцию в стек возврата
                fragmentTransaction.commit();

            }return true;
        }
    };
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


        // And to show what can be done with it, we move the camera to the center of the target location.
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
    public void updatePlacemarkMapObjectVisibility(ArrayList<Integer> selectedCheckBoxIds) {
        if (selectedCheckBoxIds.contains(R.id.checkBoxParks)) {
            ImageProvider imageProvider_2 = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.green_pin);
            Point mappoint_4= new Point(56.334274, 43.854942);
            PlacemarkMapObject placemark_4 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_4), imageProvider_2);
            placemark_4.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_4.addTapListener(mapObjectTapListener);

            Point mappoint_5= new Point(56.329333, 44.016239);
            PlacemarkMapObject placemark_5 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_5), imageProvider_2);
            placemark_5.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_5.addTapListener(mapObjectTapListener);

            Point mappoint_6= new Point(56.329576, 44.009824);
            PlacemarkMapObject placemark_6 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_6), imageProvider_2);
            placemark_6.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_6.addTapListener(mapObjectTapListener);
        }
        if (selectedCheckBoxIds.contains(R.id.checkBoxTheaters)) {
            ImageProvider imageProvider = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.location_pin);
            Point mappoint_1= new Point(56.325716, 44.004898);
            PlacemarkMapObject placemark_1 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_1), imageProvider);
            placemark_1.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_1.addTapListener(mapObjectTapListener);

            Point mappoint_2= new Point(56.333123, 43.902013);
            PlacemarkMapObject placemark_2 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_2), imageProvider);
            placemark_2.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_2.addTapListener(mapObjectTapListener);

            Point mappoint_3= new Point(56.324411, 44.003085);
            PlacemarkMapObject placemark_3 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_3), imageProvider);
            placemark_3.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_3.addTapListener(mapObjectTapListener);

        } if (selectedCheckBoxIds.contains(R.id.checkBoxMuseums)){
            ImageProvider imageProvider_4 = ImageProvider.fromResource(home_fragment.this.getActivity(), R.drawable.blue_pin);
            Point mappoint_10= new Point(56.320417, 43.946482);
            PlacemarkMapObject placemark_10 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_10), imageProvider_4);
            placemark_10.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_10.addTapListener(mapObjectTapListener);

            Point mappoint_11= new Point(56.318164, 43.995279);
            PlacemarkMapObject placemark_11 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_11), imageProvider_4);
            placemark_11.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_11.addTapListener(mapObjectTapListener);

            Point mappoint_12= new Point(56.328139, 44.006500);
            PlacemarkMapObject placemark_12 = mapView.getMapWindow().getMap().getMapObjects().addPlacemark((mappoint_12), imageProvider_4);
            placemark_12.setIconStyle(new IconStyle().setScale(0.1f));
            placemark_12.addTapListener(mapObjectTapListener);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Integer> selectedCheckBoxIds = requireActivity().getIntent().getIntegerArrayListExtra("selectedCheckBoxIds");
        updatePlacemarkMapObjectVisibility(selectedCheckBoxIds);
    }
}


