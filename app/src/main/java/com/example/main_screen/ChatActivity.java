package com.example.main_screen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
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
    private ChatAdapter adapter;
    private final List<Message> messages = new ArrayList<>();
    private EditText inputField;
    private ImageButton sendButton;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recycler);
        inputField = findViewById(R.id.enter);
        sendButton = findViewById(R.id.send);

        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String userMessage = inputField.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addMessage(userMessage, Message.TYPE_USER);
                inputField.setText("");
                sendToBot(userMessage);
            }
        });
    }

    private void addMessage(String message, int type) {
        messages.add(new Message(message, type));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void sendToBot(String prompt) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String response = chatGPT(prompt);
            handler.post(() -> addMessage(response, Message.TYPE_BOT));
        });
    }

    private String chatGPT(String prompt) {
        try {
            String apiUrl = "https://api.naga.ac/v1/chat/completions";
            String apiKey = "ng-O5o7GnOt9AqR1rjknX08P3m6blgXs";
            String model = "gpt-3.5-turbo";

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JSONObject responseJson = new JSONObject(result.toString());
            JSONArray choices = responseJson.getJSONArray("choices");
            JSONObject messageObj = choices.getJSONObject(0).getJSONObject("message");

            return messageObj.getString("content").trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к серверу.";
        }
    }
}
