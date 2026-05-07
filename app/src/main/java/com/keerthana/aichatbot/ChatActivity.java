package com.keerthana.aichatbot;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.keerthana.aichatbot.adapter.MessageAdapter;
import com.keerthana.aichatbot.database.ChatDatabase;
import com.keerthana.aichatbot.database.Message;
import com.keerthana.aichatbot.network.ChatRequest;
import com.keerthana.aichatbot.network.ChatResponse;
import com.keerthana.aichatbot.network.GroqApiService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvWelcome;

    private String username;
    private ChatDatabase db;
    private GroqApiService groqApiService;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String GROQ_BASE_URL = "https://api.groq.com/";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private List<ChatRequest.MessagePayload> conversationHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("USERNAME");

        tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Welcome " + username + "!");

        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = ChatDatabase.getInstance(this);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GROQ_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        groqApiService = retrofit.create(GroqApiService.class);

        conversationHistory.add(new ChatRequest.MessagePayload("system",
                "You are a helpful assistant. Keep responses concise and friendly."));

        loadChatHistory();

        btnSend.setOnClickListener(v -> {
            String userInput = etMessage.getText().toString().trim();
            if (!userInput.isEmpty()) {
                etMessage.setText("");
                sendMessage(userInput);
            }
        });
    }

    private void loadChatHistory() {
        executorService.execute(() -> {
            List<Message> history = db.messageDao().getMessagesForUser(username);
            runOnUiThread(() -> {
                messageList.addAll(history);
                adapter.notifyDataSetChanged();
                for (Message m : history) {
                    String role = m.sender.equals("USER") ? "user" : "assistant";
                    conversationHistory.add(new ChatRequest.MessagePayload(role, m.text));
                }
                scrollToBottom();
            });
        });
    }

    private void sendMessage(String userInput) {
        long timestamp = System.currentTimeMillis();

        Message userMessage = new Message(username, userInput, "USER", timestamp);
        executorService.execute(() -> db.messageDao().insertMessage(userMessage));
        adapter.addMessage(userMessage);
        scrollToBottom();

        conversationHistory.add(new ChatRequest.MessagePayload("user", userInput));

        ChatRequest request = new ChatRequest(MODEL, conversationHistory);
        android.util.Log.d("GROQ_KEY", "Key: " + BuildConfig.GROQ_API_KEY);
        groqApiService.sendMessage(BuildConfig.GROQ_API_KEY, request)
                .enqueue(new Callback<ChatResponse>() {
                    @Override
                    public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                        android.util.Log.d("GROQ_RESPONSE", "Code: " + response.code());
                        if (!response.isSuccessful()) {
                            try {
                                String errorBody = response.errorBody().string();
                                android.util.Log.e("GROQ_RESPONSE", "Error body: " + errorBody);
                                runOnUiThread(() ->
                                        Toast.makeText(ChatActivity.this,
                                                "Error " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show());
                            } catch (Exception e) {
                                android.util.Log.e("GROQ_RESPONSE", "Error reading error body: " + e.getMessage());
                            }
                            return;
                        }
                        if (response.body() != null && !response.body().choices.isEmpty()) {
                            String botReply = response.body().choices.get(0).message.content;
                            long botTimestamp = System.currentTimeMillis();
                            Message botMessage = new Message(username, botReply, "BOT", botTimestamp);
                            executorService.execute(() -> db.messageDao().insertMessage(botMessage));
                            conversationHistory.add(new ChatRequest.MessagePayload("assistant", botReply));
                            runOnUiThread(() -> {
                                adapter.addMessage(botMessage);
                                scrollToBottom();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatResponse> call, Throwable t) {
                        android.util.Log.e("GROQ_RESPONSE", "Failure: " + t.getMessage());
                        runOnUiThread(() ->
                                Toast.makeText(ChatActivity.this,
                                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show());
                    }
                });
    }

    private void scrollToBottom() {
        if (messageList.size() > 0) {
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        }
    }

}