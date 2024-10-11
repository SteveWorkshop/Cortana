package com.example.cortana.network;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

public class GeminiApi implements CommonLlmApi{
    @Getter
    @Setter
    private String apiKey;

    @Getter
    @Setter
    private String modelType;

    public GeminiApi(String apiKey,String modelType)
    {
        this.apiKey=apiKey;
        this.modelType=modelType;
    }

    @Override
    public void getTextAnswerAsync(String question, FutureCallback<GenerateContentResponse> callback) {
        GenerativeModel gm = new GenerativeModel(modelType, apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder().addText(question).build();//实际项目由用户输入
        //todo:自己调节性能，烤机
        Executor executor=new ThreadPoolExecutor(5,10,60, TimeUnit.SECONDS,new LinkedBlockingDeque<>());
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response,callback,executor);
    }
}
