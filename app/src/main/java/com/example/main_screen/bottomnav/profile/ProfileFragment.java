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
import com.example.main_screen.ChatActivity;
import com.example.main_screen.ProfileChatActivity;
import com.example.main_screen.Settings_Activity;
import com.example.main_screen.databinding.FragmentProfileBinding;
import com.example.main_screen.favourite;
import com.example.main_screen.service.ScoreService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

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

        binding.logoutLayout.setOnClickListener(v -> {
            startActivity(new Intent(ProfileFragment.this.getActivity(), Settings_Activity.class));
        });

        binding.supportLayout.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/flachka"));
            startActivity(browserIntent);
        });

        // Add click listener for chat item
        binding.chatLayout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileChatActivity.class));
        });

        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                        filePath = result.getData().getData();

                        try{
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            filePath
                                    );
                            binding.profileImage.setImageBitmap(bitmap);
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        uploadImage();
                    }
                }
            }
    );

    private void loadUserInfo(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("username").getValue().toString();
                        String profileImage = snapshot.child("profileImage").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        
                        binding.profileName.setText(username);
                        binding.profileEmail.setText(email);

                        if (!profileImage.isEmpty()){
                            Glide.with(getContext())
                                .load(profileImage)
                                .circleCrop()
                                .into(binding.profileImage);
                        }

                        // Обновление прогресс бара на основе очков из ScoreService
                        int score = ScoreService.getInstance().getScore();
                        int progress = Math.min(score, 100);
                        progressBar.setProgress(progress);
                        binding.progressText.setText(progress + "%");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage(){
        if (filePath!=null){
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseStorage.getInstance().getReference().child("images/"+uid)
                    .putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Фото успешно загружено", Toast.LENGTH_SHORT).show();

                            FirebaseStorage.getInstance().getReference().child("images/"+uid).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("profileImage").setValue(uri.toString());
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем прогресс бар при возвращении на экран профиля
        int score = ScoreService.getInstance().getScore();
        int progress = Math.min(score, 100);
        progressBar.setProgress(progress);
        binding.progressText.setText(progress + "%");
    }
}
