package com.keerthana.aichatbot.network;

import java.util.List;

public class ChatResponse {

    public List<Choice> choices;

    public static class Choice {
        public MessageContent message;
    }

    public static class MessageContent {
        public String role;
        public String content;
    }
}