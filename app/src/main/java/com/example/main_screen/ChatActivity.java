package com.example.main_screen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton sendButton;
    private ChatAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private Handler handler;
    private int currentStep = 0;
    private final List<String> userAnswers = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

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

        addBotMessage("👋 Привет! Я твой культурный навигатор. Ответь на три коротких вопроса, чтобы я понял, что тебе интересно. Отвечай полноценными ответами.");
        askNextQuestion();

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
        String prompt = "На основе следующих ответов определи, к какой области склонен человек: IT, творчество или история. Напиши свой ответ одним словом. Ответы: " + answers;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String response = chatGPT(prompt);
            handler.post(() -> {
                addBotMessage(response);
                saveUserCategory(response);
            });
        });
    }

    private void saveUserCategory(String response) {
        String category = "";

        response = response.toLowerCase();
        if (response.contains("it")) {
            category = "IT";
        } else if (response.contains("творч")) {
            category = "Творчество";
        } else if (response.contains("истор")) {
            category = "История";
        }

        if (!category.isEmpty() && userRef != null) {
            userRef.child("category_user").setValue(category);
        }
    }

    public static String chatGPT(String prompt) {
        String url = "https://api.naga.ac/v1/chat/completions";
        String apiKey = "ng-O5o7GnOt9AqR1rjknX08P3m6blgXs";
        String model = "gpt-3.5-turbo";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return "Большая нагрузка на сервер. Попробуйте еще раз через 30 секунд:)";
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 10;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }
}
