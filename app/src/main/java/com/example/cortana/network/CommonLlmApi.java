package com.example.cortana.network;

import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;

//todo:支持多种模型
public interface CommonLlmApi {
    void getTextAnswerAsync(String question, FutureCallback<GenerateContentResponse> callback);
}
