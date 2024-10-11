package com.example.cortana.vm;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.cortana.BaseApplication;
import com.example.cortana.config.DBConfig;
import com.example.cortana.dao.MessageDao;
import com.example.cortana.entity.Message;

import lombok.Getter;
import lombok.Setter;

public class MainViewModel extends BaseViewModel{

    //todo:自定义设置
    public static final int PAGE_SIZE = 15;
    public static final  boolean ENABLE_PLACEHOLDERS = false;

    private MessageDao messageDao;

    @Getter
    @Setter
    private LiveData allData;

    public MainViewModel(LifecycleOwner owner){
        init();
    }

    public MainViewModel(){
       init();
    }

    private void init()
    {
        messageDao= DBConfig.getInstance(BaseApplication.getApplication()).getMessageDao();
        loadDataByPage();
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
        //插入提问数据
        Message message=new Message();
        message.setType(Message.MESSAGE_SENT);
        message.setTextContent(content);
        //插入消息
        messageDao.insertMessage(message);
        //todo：等待gpt回应
    }
}

