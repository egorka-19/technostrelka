package com.example.main_screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.api.dto.RegisterBody;
import com.example.main_screen.api.dto.TokenResponseDto;
import com.example.main_screen.databinding.ActivityRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog loadingBar;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingBar = new ProgressDialog(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.minibackBtn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        binding.btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.emailEt.getText().toString().isEmpty()
                        || binding.loginEt.getText().toString().isEmpty()
                        || binding.passwordEt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingBar.setTitle("Регистрация");
                loadingBar.setMessage("Пожалуйста, подождите...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                RegisterBody body = new RegisterBody(
                        binding.emailEt.getText().toString().trim(),
                        binding.passwordEt.getText().toString(),
                        binding.loginEt.getText().toString().trim()
                );
                ApiClient.get(RegisterActivity.this).register(body).enqueue(new Callback<TokenResponseDto>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenResponseDto> call, @NonNull Response<TokenResponseDto> response) {
                        loadingBar.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            TokenResponseDto t = response.body();
                            TokenStore.get(RegisterActivity.this).saveTokens(t.accessToken, t.refreshToken);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Ошибка регистрации (email занят?)", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenResponseDto> call, @NonNull Throwable t) {
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
