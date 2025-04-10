package com.example.main_screen;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.main_screen.adapter.ChatAdapter;
import com.example.main_screen.model.Message;

public class ProfileChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton sendButton;
    private ChatAdapter adapter;
    private LinearLayout messageCont;

    private List<Message> messages = new ArrayList<>();
    private Handler handler;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.enter);
        sendButton = findViewById(R.id.send);
        messageCont = findViewById(R.id.message_container);
        handler = new Handler(Looper.getMainLooper());
        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        }

        // Приветственное сообщение
        addBotMessage("👋 Привет! Я твой культурный навигатор. С чем я могу помочь тебе сегодня?");

        sendButton.setOnClickListener(v -> {
            String userMessage = editText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String response = chatGPT(userMessage);
            handler.post(() -> {
                addBotMessage(response);
            });
        });
    }

    private String chatGPT(String prompt) {
        try {
            String apiUrl = "https://api.naga.ac/v1/chat/completions";
            String apiKey = "ng-O5o7GnOt9AqR1rjknX08P3m6blgXs";
            String model = "gpt-3.5-turbo";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            String jsonInputString = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"ты человек, который все знает о нижнем новгороде и в целом культурном коде страны. Напиши свой Ответ: " + prompt + "\"}]}";

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(jsonInputString);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            // Извлекаем только content из JSON-ответа
            String jsonResponse = response.toString();
            int contentStart = jsonResponse.indexOf("\"content\":\"") + 11;
            int contentEnd = jsonResponse.indexOf("\"", contentStart);
            if (contentStart > 10 && contentEnd > contentStart) {
                return jsonResponse.substring(contentStart, contentEnd);
            }
            return "Не удалось получить ответ";
        } catch (IOException e) {
            e.printStackTrace();
            return "Подождите 30 секунд перед следующим запросом. Высокая нагрузка на сервер";
        }
    }
} 