package com.example.cortana.vm;

import android.content.SharedPreferences;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.preference.PreferenceManager;

import com.example.cortana.BaseApplication;
import com.example.cortana.config.DBConfig;
import com.example.cortana.dao.MessageDao;
import com.example.cortana.entity.Message;
import com.example.cortana.http.CommonLlmApi;
import com.example.cortana.http.GeminiApi;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;

import lombok.Getter;
import lombok.Setter;

public class MainViewModel extends BaseViewModel{

    //todo:自定义设置
    public static final int PAGE_SIZE = 15;
    public static final  boolean ENABLE_PLACEHOLDERS = false;

    private MessageDao messageDao;
    private CommonLlmApi api;

    private String apiKey;
    private String modelType=BaseApplication.DEFAULT_MODEL_TYPE;

    @Getter
    @Setter
    private LiveData allData;

    @Getter
    @Setter
    private MutableLiveData<Boolean> loading=new MutableLiveData<>(false);

    @Getter
    @Setter
    private MutableLiveData<Boolean> faliure=new MutableLiveData<>(false);

    //todo: 是否需要这个？
    public MainViewModel(LifecycleOwner owner){
        init();
    }

    public MainViewModel(){
       init();
    }

    private void init()
    {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getApplication());
        apiKey =defaultSharedPreferences.getString("signature","");
        modelType=defaultSharedPreferences.getString("model_type",BaseApplication.DEFAULT_MODEL_TYPE);
        messageDao= DBConfig.getInstance(BaseApplication.getApplication()).getMessageDao();
        loadDataByPage();
        api=new GeminiApi(apiKey,modelType);
    }

    public void loadDataByPage()
    {
       PagedList.Config.Builder builder=new PagedList.Config.Builder();
        builder.setPageSize(PAGE_SIZE);                       //配置分页加载的数量
        builder.setEnablePlaceholders(ENABLE_PLACEHOLDERS);     //配置是否启动PlaceHolders
        builder.setInitialLoadSizeHint(PAGE_SIZE);
        LivePagedListBuilder livePagedListBuilder = new LivePagedListBuilder(messageDao.getAll_v2(), builder.build());
        allData = livePagedListBuilder.build();
    }

    //在这里写业务逻辑
    //异步无所谓反正是观察者模式

    public void sendChatAsync(String content)
    {
        loading.setValue(true);
        //插入提问数据

        Message message=new Message();
        message.setType(Message.MESSAGE_SENT);
        message.setTextContent(content);
        //插入消息
        messageDao.insertMessage(message);
        //todo：等待gpt回应
        //warning:线程操作注意！
        api.getTextAnswerAsync(content, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                loading.postValue(false);
                //插入回复消息
                System.out.println("????????????????????????????????????????????????");
                Message recvMessage=new Message();
                recvMessage.setType(Message.MESSAGE_RECEIVED);
                String resultText = result.getText();
                recvMessage.setTextContent(resultText);
                messageDao.insertMessage(recvMessage);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("=====================================");
                t.printStackTrace();
                loading.postValue(false);
                faliure.postValue(true);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                faliure.postValue(false);
            }

            //好像没用了
            public void resetStatus()
            {
                faliure.setValue(false);
            }
        });
    }
}

