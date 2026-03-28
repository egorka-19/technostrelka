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
import com.example.main_screen.api.dto.LoginBody;
import com.example.main_screen.api.dto.TokenResponseDto;
import com.example.main_screen.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog loadingBar;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingBar = new ProgressDialog(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.minibackBtn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        binding.btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.emailEt.getText().toString().isEmpty() || binding.passwordEt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingBar.setTitle("Вход в приложение");
                loadingBar.setMessage("Пожалуйста, подождите...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                LoginBody body = new LoginBody(
                        binding.emailEt.getText().toString().trim(),
                        binding.passwordEt.getText().toString()
                );
                ApiClient.get(LoginActivity.this).login(body).enqueue(new Callback<TokenResponseDto>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenResponseDto> call, @NonNull Response<TokenResponseDto> response) {
                        loadingBar.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            TokenResponseDto t = response.body();
                            TokenStore.get(LoginActivity.this).saveTokens(t.accessToken, t.refreshToken);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Неверный email или пароль", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenResponseDto> call, @NonNull Throwable t) {
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
