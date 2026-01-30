package com.example.main_screen;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddReviewBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_EVENT_NAME = "eventName";
    private static final String ARG_CATEGORY = "category";
    private static final int MAX_PHOTOS = 3;

    private String eventName;
    private String category;

    private TextView placeNameText;
    private ImageView star1, star2, star3, star4, star5;
    private TextView starLabel;
    private EditText reviewText;
    private LinearLayout photosContainer;
    private ImageView photoPreview1, photoPreview2, photoPreview3;
    private View cameraBtn;
    private TextView publishBtn;

    private int selectedRating = 0;
    private final List<Uri> photoUris = new ArrayList<>();

    private ActivityResultLauncher<String> pickImageLauncher;

    public interface OnReviewPublishedListener {
        void onReviewPublished();
    }

    private OnReviewPublishedListener listener;

    public void setOnReviewPublishedListener(OnReviewPublishedListener listener) {
        this.listener = listener;
    }

    public static AddReviewBottomSheetFragment newInstance(String eventName, String category) {
        AddReviewBottomSheetFragment f = new AddReviewBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_NAME, eventName);
        args.putString(ARG_CATEGORY, category);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventName = getArguments().getString(ARG_EVENT_NAME, "");
            category = getArguments().getString(ARG_CATEGORY, "Other");
        }
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && photoUris.size() < MAX_PHOTOS) {
                        photoUris.add(uri);
                        updatePhotoPreviews();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeNameText = view.findViewById(R.id.add_review_place_name);
        star1 = view.findViewById(R.id.star_1);
        star2 = view.findViewById(R.id.star_2);
        star3 = view.findViewById(R.id.star_3);
        star4 = view.findViewById(R.id.star_4);
        star5 = view.findViewById(R.id.star_5);
        starLabel = view.findViewById(R.id.add_review_star_label);
        reviewText = view.findViewById(R.id.add_review_text);
        photosContainer = view.findViewById(R.id.add_review_photos_container);
        photoPreview1 = view.findViewById(R.id.photo_preview_1);
        photoPreview2 = view.findViewById(R.id.photo_preview_2);
        photoPreview3 = view.findViewById(R.id.photo_preview_3);
        cameraBtn = view.findViewById(R.id.add_review_camera_btn);
        publishBtn = view.findViewById(R.id.add_review_publish_btn);

        placeNameText.setText(eventName != null ? eventName : "");

        View[] stars = { star1, star2, star3, star4, star5 };
        for (int i = 0; i < stars.length; i++) {
            final int rating = i + 1;
            stars[i].setOnClickListener(v -> setRating(rating));
        }

        cameraBtn.setOnClickListener(v -> {
            if (photoUris.size() >= MAX_PHOTOS) {
                Toast.makeText(requireContext(), "Можно загрузить не более 3 фотографий", Toast.LENGTH_SHORT).show();
                return;
            }
            pickImageLauncher.launch("image/*");
        });

        publishBtn.setOnClickListener(v -> publishReview());
    }

    private void setRating(int rating) {
        selectedRating = rating;
        int gold = 0xFFD700;
        int gray = 0xFFC0C0C0;
        star1.setColorFilter(rating >= 1 ? gold : gray);
        star2.setColorFilter(rating >= 2 ? gold : gray);
        star3.setColorFilter(rating >= 3 ? gold : gray);
        star4.setColorFilter(rating >= 4 ? gold : gray);
        star5.setColorFilter(rating >= 5 ? gold : gray);

        String[] labels = { "", "Плохо", "Нормально", "Хорошее место", "Отлично", "Превосходно" };
        starLabel.setText(rating >= 0 && rating < labels.length ? labels[rating] : "");
    }

    private void updatePhotoPreviews() {
        if (photoUris.isEmpty()) {
            photosContainer.setVisibility(View.GONE);
            return;
        }
        photosContainer.setVisibility(View.VISIBLE);
        Glide.with(this).load(photoUris.get(0)).centerCrop().into(photoPreview1);
        photoPreview1.setVisibility(View.VISIBLE);
        if (photoUris.size() > 1) {
            Glide.with(this).load(photoUris.get(1)).centerCrop().into(photoPreview2);
            photoPreview2.setVisibility(View.VISIBLE);
        } else {
            photoPreview2.setVisibility(View.GONE);
        }
        if (photoUris.size() > 2) {
            Glide.with(this).load(photoUris.get(2)).centerCrop().into(photoPreview3);
            photoPreview3.setVisibility(View.VISIBLE);
        } else {
            photoPreview3.setVisibility(View.GONE);
        }
    }

    private void publishReview() {
        if (selectedRating <= 0) {
            Toast.makeText(requireContext(), "Выберите оценку (звёзды)", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = reviewText.getText() != null ? reviewText.getText().toString().trim() : "";
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(requireContext(), "Напишите текст отзыва", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(requireContext(), "Ошибка: неизвестное место", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Войдите в аккаунт, чтобы оставить отзыв", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        publishBtn.setEnabled(false);

        // Сначала загружаем имя пользователя из Firebase (Users/username), чтобы не подставлять почту
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = null;
                if (snapshot.hasChild("username")) {
                    Object val = snapshot.child("username").getValue();
                    if (val != null) {
                        userName = String.valueOf(val).trim();
                    }
                }
                if (TextUtils.isEmpty(userName)) {
                    userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                }
                if (TextUtils.isEmpty(userName)) {
                    userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                }
                if (TextUtils.isEmpty(userName)) {
                    userName = "Пользователь";
                }
                String date = new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(new Date());
                String avatarUrl = "";
                if (snapshot.hasChild("profileImage")) {
                    Object img = snapshot.child("profileImage").getValue();
                    if (img != null) avatarUrl = String.valueOf(img);
                }
                if (photoUris.isEmpty()) {
                    saveReviewToFirebase(uid, userName, date, avatarUrl, selectedRating, text, new ArrayList<>());
                } else {
                    uploadPhotosAndSave(uid, userName, date, avatarUrl, selectedRating, text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                publishBtn.setEnabled(true);
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (TextUtils.isEmpty(userName)) userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                if (TextUtils.isEmpty(userName)) userName = "Пользователь";
                String date = new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(new Date());
                if (photoUris.isEmpty()) {
                    saveReviewToFirebase(uid, userName, date, "", selectedRating, text, new ArrayList<>());
                } else {
                    uploadPhotosAndSave(uid, userName, date, "", selectedRating, text);
                }
            }
        });
    }

    private void uploadPhotosAndSave(String uid, String userName, String date, String avatarUrl, int rating, String text) {
        String basePath = "ReviewPhotos/" + category + "/" + eventName + "/" + uid + "/" + System.currentTimeMillis();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final String[] photoUrls = new String[photoUris.size()];
        final int[] uploaded = { 0 };
        final int total = photoUris.size();

        for (int i = 0; i < photoUris.size(); i++) {
            final int index = i;
            Uri uri = photoUris.get(i);
            StorageReference ref = storageRef.child(basePath + "_" + i + ".jpg");
            ref.putFile(uri).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                photoUrls[index] = downloadUri.toString();
                uploaded[0]++;
                if (uploaded[0] == total) {
                    List<String> list = new ArrayList<>();
                    for (String u : photoUrls) if (u != null) list.add(u);
                    saveReviewToFirebase(uid, userName, date, avatarUrl, rating, text, list);
                }
            }).addOnFailureListener(e -> {
                uploaded[0]++;
                if (uploaded[0] == total) {
                    List<String> list = new ArrayList<>();
                    for (String u : photoUrls) if (u != null) list.add(u);
                    saveReviewToFirebase(uid, userName, date, avatarUrl, rating, text, list);
                }
            })).addOnFailureListener(e -> {
                uploaded[0]++;
                if (uploaded[0] == total) {
                    List<String> list = new ArrayList<>();
                    for (String u : photoUrls) if (u != null) list.add(u);
                    saveReviewToFirebase(uid, userName, date, avatarUrl, rating, text, list);
                }
            });
        }
    }

    private void saveReviewToFirebase(String uid, String userName, String date, String avatarUrl, int rating, String text, List<String> photoUrls) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Reviews")
                .child(category)
                .child(eventName)
                .child(uid);

        Map<String, Object> review = new HashMap<>();
        review.put("rating", rating);
        review.put("text", text);
        review.put("userName", userName);
        review.put("date", date);
        review.put("avatarUrl", avatarUrl);
        review.put("photoUrls", photoUrls);

        ref.setValue(review).addOnCompleteListener(task -> {
            publishBtn.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Отзыв опубликован", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onReviewPublished();
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Ошибка публикации", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
