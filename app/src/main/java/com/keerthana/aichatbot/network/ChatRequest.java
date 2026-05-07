package com.keerthana.aichatbot.network;

import java.util.List;

public class ChatRequest {

    public String model;
    public List<MessagePayload> messages;
    public double temperature;

    public ChatRequest(String model, List<MessagePayload> messages) {
        this.model = model;
        this.messages = messages;
        this.temperature = 0.7;
    }

    public static class MessagePayload {
        public String role;
        public String content;

        public MessagePayload(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}