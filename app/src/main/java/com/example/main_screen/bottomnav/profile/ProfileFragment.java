
package com.example.main_screen.bottomnav.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.main_screen.R;
import com.example.main_screen.ProfileChatActivity;
import com.example.main_screen.Settings_Activity;
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.api.dto.UserMeDto;
import com.example.main_screen.databinding.FragmentProfileBinding;
import com.example.main_screen.service.ScoreService;
import com.example.main_screen.utils.MediaUrlUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Uri filePath;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        progressBar = binding.progressBar;

        loadUserInfo();

        binding.profileImage.setOnClickListener(v -> {
            selectImage();
            Toast.makeText(getContext(), "Дождитесь загрузки фото, не выходите из приложения!", Toast.LENGTH_SHORT).show();
        });

        binding.logoutLayout.setOnClickListener(v ->
                startActivity(new Intent(ProfileFragment.this.getActivity(), Settings_Activity.class)));

        binding.supportLayout.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/flachka"));
            startActivity(browserIntent);
        });

        binding.chatLayout.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProfileChatActivity.class)));

        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        filePath = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            filePath
                                    );
                            binding.profileImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        uploadImage();
                    }
                }
            }
    );

    private void loadUserInfo() {
        if (!TokenStore.get(requireContext()).hasAccessToken()) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<UserMeDto> resp = ApiClient.get(requireContext()).getMe().execute();
                if (!isAdded() || binding == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (!resp.isSuccessful() || resp.body() == null) {
                        Toast.makeText(getContext(), "Не удалось загрузить профиль", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UserMeDto u = resp.body();
                    String name = u.username != null && !u.username.isEmpty() ? u.username : (u.email != null ? u.email : "");
                    binding.profileName.setText(name);
                    binding.profileEmail.setText(u.email != null ? u.email : "");

                    if (u.profileImageUrl != null && !u.profileImageUrl.isEmpty()) {
                        String src = MediaUrlUtils.resolveForApiClient(u.profileImageUrl);
                        Glide.with(requireContext())
                                .load(src)
                                .circleCrop()
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .into(binding.profileImage);
                    } else {
                        binding.profileImage.setImageResource(R.drawable.profile);
                    }

                    int score = ScoreService.getInstance().getScore();
                    int progress = Math.min(score, 100);
                    progressBar.setProgress(progress);
                    binding.progressText.setText(progress + "%");
                });
            } catch (Exception e) {
                if (isAdded() && getContext() != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage() {
        if (filePath == null || !TokenStore.get(requireContext()).hasAccessToken()) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                byte[] bytes = readUriBytes(filePath);
                if (bytes == null || bytes.length == 0) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Не удалось прочитать файл", Toast.LENGTH_SHORT).show());
                    return;
                }
                RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", "avatar.jpg", body);
                Response<UserMeDto> resp = ApiClient.get(requireContext()).uploadAvatar(part).execute();
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    if (resp.isSuccessful() && resp.body() != null) {
                        UserMeDto u = resp.body();
                        if (u.profileImageUrl != null && !u.profileImageUrl.isEmpty()) {
                            String src = MediaUrlUtils.resolveForApiClient(u.profileImageUrl);
                            Glide.with(requireContext())
                                    .load(src)
                                    .circleCrop()
                                    .placeholder(R.drawable.profile)
                                    .error(R.drawable.profile)
                                    .into(binding.profileImage);
                        }
                        Toast.makeText(getContext(), "Фото успешно загружено", Toast.LENGTH_SHORT).show();
                        loadUserInfo();
                    } else {
                        Toast.makeText(getContext(), "Ошибка загрузки фото", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private byte[] readUriBytes(Uri uri) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
            if (in == null) return null;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                buf.write(b, 0, n);
            }
            return buf.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int score = ScoreService.getInstance().getScore();
        int progress = Math.min(score, 100);
        progressBar.setProgress(progress);
        binding.progressText.setText(progress + "%");
        loadUserInfo();
    }
}
