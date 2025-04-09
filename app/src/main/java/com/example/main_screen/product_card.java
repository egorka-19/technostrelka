package com.example.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.main_screen.model.PlaceModel;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.model.ViewAllModel;
import com.example.main_screen.add_response;

public class product_card extends AppCompatActivity {
    ImageView detailedImg;
    TextView price, description, name, raiting;
    ImageButton get_reviews;
    ImageView back_stata;

    ViewAllModel viewAllModel = null;
    PopularModel popularModel = null;
    PlaceModel placeModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_card);

        get_reviews = findViewById(R.id.get_reviews);
        back_stata = findViewById(R.id.back_statistics);

        get_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(product_card.this, add_response.class);
                startActivity(intent);
            }
        });

        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel) {
            viewAllModel = (ViewAllModel) object;
        }
        if (object instanceof PopularModel) {
            popularModel = (PopularModel) object;
        }
        if (object instanceof PlaceModel) {
            placeModel = (PlaceModel) object;
        }

        detailedImg = findViewById(R.id.pro_card_img);
        price = findViewById(R.id.price);
        raiting = findViewById(R.id.raiting);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);

        if (viewAllModel != null) {
            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            price.setText(viewAllModel.getPrice());
            raiting.setText(viewAllModel.getRaiting());
            description.setText(viewAllModel.getDescription());
            name.setText(viewAllModel.getName());
        }
        if (popularModel != null) {
            Glide.with(getApplicationContext()).load(popularModel.getImg_url()).into(detailedImg);
            price.setText(popularModel.getPrice());
            raiting.setText(popularModel.getRaiting());
            description.setText(popularModel.getDescription());
            name.setText(popularModel.getName());
        }
        if (placeModel != null) {
            detailedImg.setImageResource(placeModel.getImageResourceId());
            name.setText(placeModel.getName());
            description.setText(placeModel.getDescription());
            raiting.setText(placeModel.getType());
            price.setText(""); // Очищаем цену, так как для мест она не нужна
        }
    }
}