package com.example.cortana.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cortana.BaseApplication;
import com.example.cortana.R;
import com.example.cortana.adapter.MessageAdapter;
import com.example.cortana.databinding.ActivityMainBinding;
import com.example.cortana.entity.Message;
import com.example.cortana.vm.MainViewModel;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    private MainViewModel viewModel;

    //todo: move these checks
    private String apiKey;
    private String modelType;

    private TextToSpeech mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel=new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(binding.mainToolbar);
        binding.btnSendMessage.setOnClickListener(v->{
            String question=binding.txbMySay.getText().toString();
            if(question==null||question.length()==0)
            {
                Toast.makeText(this, "我不能主动和你聊天，请输入你的问题喵~o( =∩ω∩= )m", Toast.LENGTH_SHORT).show();
            }
            else{
                viewModel.sendChatAsync(question);
                binding.txbMySay.setText("");
            }
        });
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.open_settings:{
                Intent intent=new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            default:{break;}
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        apiKey =defaultSharedPreferences.getString("signature","");
        modelType=defaultSharedPreferences.getString("model_type",BaseApplication.DEFAULT_MODEL_TYPE);

        if(apiKey==null|| apiKey.isEmpty())
        {
            //要求用户设置
            //todo：如果是本地模型那么不需要跳转
            //todo：这一段移入viewmodel实现
            MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(this);
            builder.setCancelable(false);
            builder.setTitle("未设置API Key");
            builder.setMessage("此程序依赖Google Gemini开放API，您需要申请API key并在本应用程序填写");
            builder.setPositiveButton("我有Key，去填写",((dialog, which) -> {
                Intent intent=new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }));
            builder.setNegativeButton("我没K，bulubiubulubiu~",((dialog, which) -> {
                //打开浏览器
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://makersuite.google.com/app/apikey"));
                startActivity(intent);
            }));
            builder.show();
        }
    }

    private void initView(){
        MessageAdapter adapter=new MessageAdapter(MessageAdapter.callback);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.messageList.setLayoutManager(layoutManager);
        binding.messageList.setAdapter(adapter);
        //数据绑定
        viewModel.getAllData().observe(this, o -> {
            adapter.submitList((PagedList<Message>) o);
            binding.messageList.scrollToPosition(binding.messageList.getAdapter().getItemCount()-1);
        });
        viewModel.getLoading().observe(this,o->{
            boolean loading = o.booleanValue();
            switchLoadingStatus(loading);
        });
        viewModel.getFaliure().observe(this,o->{
            boolean error=o.booleanValue();
            //System.out.println("*********************"+error);
            switchError(error);
        });
    }

    private void switchLoadingStatus(boolean loading)
    {
        if(loading)
        {
            binding.progLoading.setVisibility(View.VISIBLE);
            binding.txbHintArea.setVisibility(View.VISIBLE);
        }
        else{
            binding.progLoading.setVisibility(View.GONE);
            binding.txbHintArea.setVisibility(View.GONE);
        }
    }

    private void switchError(boolean failure)
    {
        if(failure){
            //弹对话框
            MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(this);
            builder.setTitle("OOPS！o(〒﹏〒)o");
            builder.setMessage("网络故障，请检查网络，本程序需要互联网连接，你懂的");
            builder.setPositiveButton("确定",(dialog, which) -> {
                //todo:重试
            });
            builder.show();
            binding.txbErrorArea.setVisibility(View.VISIBLE);
        }
        else{
            binding.txbErrorArea.setVisibility(View.GONE);
        }
    }
}