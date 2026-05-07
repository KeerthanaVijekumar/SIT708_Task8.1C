package com.keerthana.aichatbot.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GroqApiService {

    @POST("openai/v1/chat/completions")
    Call<ChatResponse> sendMessage(
            @Header("Authorization") String authHeader,
            @Body ChatRequest request
    );
}