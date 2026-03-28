package com.example.main_screen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.adapter.ChatAdapter;
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.api.dto.AssistantChatRequestDto;
import com.example.main_screen.api.dto.AssistantChatResponseDto;
import com.example.main_screen.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class ProfileChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton sendButton;
    private ChatAdapter adapter;

    private List<Message> messages = new ArrayList<>();
    private Handler handler;

    private final java.util.concurrent.ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.enter);
        sendButton = findViewById(R.id.send);
        handler = new Handler(Looper.getMainLooper());
        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addBotMessage("👋 Привет! Я твой культурный навигатор. С чем я могу помочь тебе сегодня?");

        sendButton.setOnClickListener(v -> {
            String userMessage = editText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                if (!TokenStore.get(this).hasAccessToken()) {
                    Toast.makeText(this, "Войдите в аккаунт, чтобы писать ассистенту", Toast.LENGTH_SHORT).show();
                    return;
                }
                addUserMessage(userMessage);
                editText.setText("");
                askAIAndRespond(userMessage);
            }
        });
    }

    private void addUserMessage(String message) {
        messages.add(new Message(message, 0));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addBotMessage(String message) {
        messages.add(new Message(message, 1));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void askAIAndRespond(String userMessage) {
        executor.execute(() -> {
            try {
                Response<AssistantChatResponseDto> resp = ApiClient.get(ProfileChatActivity.this)
                        .assistantChat(new AssistantChatRequestDto(userMessage))
                        .execute();
                handler.post(() -> {
                    if (resp.isSuccessful() && resp.body() != null && resp.body().reply != null) {
                        addBotMessage(resp.body().reply);
                    } else {
                        addBotMessage("Ошибка ответа сервера.");
                    }
                });
            } catch (Exception e) {
                handler.post(() -> addBotMessage("Ошибка сети: " + e.getMessage()));
            }
        });
    }
}
