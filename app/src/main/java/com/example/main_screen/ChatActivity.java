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

import com.example.main_screen.adapter.ChatAdapter;
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.dto.RouteQuizRequestDto;
import com.example.main_screen.api.dto.RouteQuizResponseDto;
import com.example.main_screen.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton sendButton, btnnext;
    private ChatAdapter adapter;
    private LinearLayout messageCont;

    private List<Message> messages = new ArrayList<>();
    private Handler handler;
    private int currentStep = 0;
    private final List<String> userAnswers = new ArrayList<>();

    private final String[] questions = {
            "1️⃣ Что тебе больше по душе?\nРешать логические задачи\nПридумывать и рисовать\nУзнавать о прошлом",
            "2️⃣ Какой отдых тебе ближе?\nИграть в стратегии или собирать схемы\nСмотреть фильмы, рисовать, писать\nЧитать исторические книги или посещать музеи",
            "3️⃣ С кем бы ты хотел встретиться?\nС инженером из будущего\nС режиссёром или художником\nС историком или археологом"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.enter);
        sendButton = findViewById(R.id.send);
        btnnext = findViewById(R.id.btn_next);
        messageCont = findViewById(R.id.message_container);
        handler = new Handler(Looper.getMainLooper());
        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addBotMessage("👋 Привет! Я твой культурный навигатор. Ответь на три коротких вопроса, чтобы я понял, что тебе интересно. Отвечай полноценными ответами.");
        askNextQuestion();
        btnnext.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, MainActivity.class)));

        sendButton.setOnClickListener(v -> {
            String userMessage = editText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addUserMessage(userMessage);
                editText.setText("");
                if (currentStep < questions.length) {
                    userAnswers.add(userMessage);
                    askNextQuestion();
                } else if (currentStep == questions.length) {
                    userAnswers.add(userMessage);
                    askAIAndRespond(userAnswers);
                    currentStep++;
                    messageCont.setVisibility(INVISIBLE);
                }
            }
        });
    }

    private void askNextQuestion() {
        if (currentStep < questions.length) {
            addBotMessage(questions[currentStep]);
            currentStep++;
        }
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

    private void askAIAndRespond(List<String> answers) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Map<String, Object> payload = new HashMap<>();
                int n = Math.min(answers.size(), questions.length);
                for (int i = 0; i < n; i++) {
                    payload.put("question_" + (i + 1), questions[i]);
                    payload.put("answer_" + (i + 1), answers.get(i));
                }
                Response<RouteQuizResponseDto> resp = ApiClient.get(ChatActivity.this)
                        .routeQuiz(new RouteQuizRequestDto(payload, true))
                        .execute();
                handler.post(() -> {
                    String cat = "история";
                    if (resp.isSuccessful() && resp.body() != null && resp.body().category != null
                            && !resp.body().category.isEmpty()) {
                        cat = resp.body().category;
                    }
                    addBotMessage("Ваше направление: " + cat);
                    btnnext.setVisibility(VISIBLE);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    addBotMessage("Не удалось определить направление. Попробуйте позже.");
                    btnnext.setVisibility(VISIBLE);
                });
            }
        });
    }
}
