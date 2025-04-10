package com.example.main_screen.bottomnav.fav;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.main_screen.R;
import com.example.main_screen.routes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class fav_fragment extends Fragment {
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private TextView categoryTitle;
    private TextView routeName;
    private TextView routeDescription;
    private ImageView routeImage;
    private ImageButton continueButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_simple, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(currentUser.getUid());
        }

        categoryTitle = view.findViewById(R.id.category_title);
        routeName = view.findViewById(R.id.route_name);
        routeDescription = view.findViewById(R.id.route_description);
        routeImage = view.findViewById(R.id.route_image);
        continueButton = view.findViewById(R.id.continue_button);

        if (currentUser != null) {
            loadUserCategory();
        }

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), routes.class);
            startActivity(intent);
        });
    }

    private void loadUserCategory() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userCategory = dataSnapshot.child("category_user").getValue(String.class);
                if (userCategory != null && !userCategory.isEmpty()) {
                    switch (userCategory.toLowerCase()) {
                        case "it":
                            categoryTitle.setText("IT-маршрут");
                            routeName.setText("Технологический Нижний");
                            routeDescription.setText("Погрузитесь в мир инноваций и технологий. Исследуйте современные IT-пространства и исторические места, связанные с развитием технологий в городе.");
                            routeImage.setImageResource(R.drawable.alexey_im);
                            break;
                        case "искусство":
                        case "творчество":
                            categoryTitle.setText("Арт-маршрут");
                            routeName.setText("Сокровища древнего города");
                            routeDescription.setText("Динамичный тур по самым ярким и необычным арт-объектам города.");
                            routeImage.setImageResource(R.drawable.alexey_im);
                            break;
                        case "история":
                            categoryTitle.setText("Исторический маршрут");
                            routeName.setText("Страницы истории");
                            routeDescription.setText("Путешествие по самым значимым историческим местам Нижнего Новгорода. Откройте для себя богатое прошлое города через его архитектурные памятники и исторические здания.");
                            routeImage.setImageResource(R.drawable.alexey_im);
                            break;
                        default:
                            categoryTitle.setText("Маршрут по городу");
                            routeName.setText("Сокровища древнего города");
                            routeDescription.setText("Погрузитесь в атмосферу средневековья, исследуя старинные улочки и архитектурные памятники. Откройте для себя тайны древних мастеров и легенды, которые хранят стены старинных зданий.");
                            routeImage.setImageResource(R.drawable.alexey_im);
                            break;
                    }
                }
        }

        @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
