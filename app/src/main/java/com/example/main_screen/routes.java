package com.example.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.main_screen.adapter.ArtObjectAdapter;
import com.example.main_screen.data.ArtObjectsData;
import com.example.main_screen.data.HistoryObjectsData;
import com.example.main_screen.data.ITObjectsData;
import com.example.main_screen.model.ArtObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class routes extends AppCompatActivity implements ArtObjectAdapter.OnArtObjectClickListener {
    private DatabaseReference userRef;
    private String userCategory;
    private RecyclerView recyclerView;
    private ArtObjectAdapter adapter;
    private List<ArtObject> artObjects;
    private TextView titleTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        
        backButton = findViewById(R.id.back_button);
        titleTextView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView);

        artObjects = new ArrayList<>();
        adapter = new ArtObjectAdapter(this, artObjects, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Get current user's reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(currentUser.getUid());
            
            // Load user's category
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userCategory = dataSnapshot.child("category_user").getValue(String.class);
                    if (userCategory != null && !userCategory.isEmpty()) {
                        loadArtObjects();
                    } else {
                        Toast.makeText(routes.this, "Категория не выбрана", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(routes.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            });
        }

        backButton.setOnClickListener(v -> finish());
    }

    private void loadArtObjects() {
        artObjects.clear();
        
        if (userCategory != null) {
            switch (userCategory.toLowerCase()) {
                case "it":
                    titleTextView.setText("IT-маршрут по городу");
                    artObjects.addAll(ITObjectsData.getITObjects());
                    break;
                case "искусство":
                case "творчество":
                    titleTextView.setText("Арт-маршрут по городу");
                    artObjects.addAll(ArtObjectsData.getArtObjects());
                    break;
                case "история":
                    titleTextView.setText("Исторический маршрут по городу");
                    artObjects.addAll(HistoryObjectsData.getHistoryObjects());
                    break;
                default:
                    titleTextView.setText("Маршрут по городу");
                    artObjects.addAll(ArtObjectsData.getArtObjects());
                    break;
            }
        }
        
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onArtObjectClick(ArtObject artObject) {
        // TODO: Implement navigation to art object details
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopPlayback();
        }
    }
}
