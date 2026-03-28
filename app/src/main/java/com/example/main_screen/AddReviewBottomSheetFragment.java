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
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.api.dto.ReviewDto;
import com.example.main_screen.api.dto.ReviewUpsertBody;
import com.example.main_screen.api.dto.UrlsResponseDto;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class AddReviewBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_EVENT_ID = "eventId";
    private static final String ARG_DISPLAY_NAME = "displayName";
    private static final int MAX_PHOTOS = 3;

    private String eventId;
    private String displayName;

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

    public static AddReviewBottomSheetFragment newInstance(String eventId, String displayName) {
        AddReviewBottomSheetFragment f = new AddReviewBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_DISPLAY_NAME, displayName != null ? displayName : "");
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID, "");
            displayName = getArguments().getString(ARG_DISPLAY_NAME, "");
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

        placeNameText.setText(displayName != null ? displayName : "");

        View[] stars = {star1, star2, star3, star4, star5};
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

        String[] labels = {"", "Плохо", "Нормально", "Хорошее место", "Отлично", "Превосходно"};
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

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(requireContext(), "Ошибка: нет id события", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TokenStore.get(requireContext()).hasAccessToken()) {
            Toast.makeText(requireContext(), "Войдите в аккаунт, чтобы оставить отзыв", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        publishBtn.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<String> uploadedUrls = new ArrayList<>();
                if (!photoUris.isEmpty()) {
                    List<MultipartBody.Part> parts = new ArrayList<>();
                    MediaType imageType = MediaType.parse("image/jpeg");
                    for (int i = 0; i < photoUris.size(); i++) {
                        Uri uri = photoUris.get(i);
                        byte[] bytes = readUriBytes(uri);
                        if (bytes == null || bytes.length == 0) {
                            continue;
                        }
                        RequestBody body = RequestBody.create(imageType, bytes);
                        parts.add(MultipartBody.Part.createFormData("files", "photo_" + i + ".jpg", body));
                    }
                    if (!parts.isEmpty()) {
                        RequestBody eventIdBody = RequestBody.create(
                                MediaType.parse("text/plain"), eventId);
                        Response<UrlsResponseDto> up = ApiClient.get(requireContext())
                                .uploadReviewPhotos(eventIdBody, parts)
                                .execute();
                        if (up.isSuccessful() && up.body() != null && up.body().urls != null) {
                            uploadedUrls.addAll(up.body().urls);
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Не удалось загрузить фото", Toast.LENGTH_SHORT).show());
                            requireActivity().runOnUiThread(() -> publishBtn.setEnabled(true));
                            return;
                        }
                    }
                }

                Response<ReviewDto> upsert = ApiClient.get(requireContext())
                        .createOrUpdateReview(eventId, new ReviewUpsertBody(selectedRating, text, uploadedUrls))
                        .execute();
                if (!upsert.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        publishBtn.setEnabled(true);
                        Toast.makeText(requireContext(), "Не удалось сохранить отзыв", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    publishBtn.setEnabled(true);
                    Toast.makeText(requireContext(), "Отзыв опубликован", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onReviewPublished();
                    }
                    dismiss();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    publishBtn.setEnabled(true);
                    Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Nullable
    private byte[] readUriBytes(Uri uri) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
            if (in == null) return null;
            return readAllBytes(in);
        } catch (IOException ignored) {
            return null;
        }
    }

    private static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] b = new byte[4096];
        int n;
        while ((n = in.read(b)) != -1) {
            buf.write(b, 0, n);
        }
        return buf.toByteArray();
    }
}
