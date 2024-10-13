package com.example.cortana.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cortana.BaseApplication;
import com.example.cortana.R;
import com.example.cortana.entity.Message;
import com.example.cortana.util.RandomUtil;
import com.example.cortana.util.TTSUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageAdapter extends PagedListAdapter<Message,MessageAdapter.ViewHolder> {

    public static final Callback callback=new Callback();

    public MessageAdapter(@NonNull DiffUtil.ItemCallback<Message> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_detail_item, parent, false);
        ViewHolder holder=new ViewHolder(view);
        //todo: 事件回调
        view.findViewById(R.id.btn_share).setOnClickListener(v->{
            String content =holder.leftMessage.getText().toString();
            Intent sendIntent=new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, content);//分享的文本内容
            sendIntent.setType("text/plain");
            parent.getContext().startActivity(Intent.createChooser(sendIntent, "分享到"));
        });
        view.findViewById(R.id.btn_copy_content).setOnClickListener(v->{
            ClipboardManager clipboardManager=(ClipboardManager) BaseApplication.getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
            String content =holder.leftMessage.getText().toString();
            ClipData clipData=ClipData.newPlainText("gpt answer",content);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(parent.getContext(), "成功复制到剪贴板", Toast.LENGTH_SHORT).show();
        });
        view.findViewById(R.id.btn_read_aloud).setOnClickListener(v->{
            TTSUtil ttsUtil = new TTSUtil(parent.getContext());
            String content=holder.leftMessage.getText().toString();
            ttsUtil.readAloud(content,new TTSUtil.GetLastSegUtteranceProgressListener(){

                @Override
                public void onStart(String utteranceId) {
                    //todo:埋点打log
                }

                @Override
                public void onStop(String utteranceId, boolean interrupted) {
                    //todo:埋点打log
                    super.onStop(utteranceId, interrupted);
                }

                @Override
                public void onError(String utteranceId) {
                    //todo:埋点打log
                }

                @Override
                public void onDone(String utteranceId, Boolean isLastSeg) {
                    //todo:埋点打log
                }

            });
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = getItem(position);
        if(message!=null){
            String strDateFormat = "yyyy/MM/dd HH:mm";
            SimpleDateFormat spf=new SimpleDateFormat(strDateFormat);

            if (message.getType()==Message.MESSAGE_RECEIVED)
            {
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
                //todo: support markdown format
                holder.leftMessage.setText(message.getTextContent());
                holder.recvTime.setText(spf.format(new Date(message.getCreateTime())));
            }
            else{
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightMessage.setText(message.getTextContent());
                holder.sendTime.setText(spf.format(new Date(message.getCreateTime())));
            }

        }

    }

    public static  class Callback extends DiffUtil.ItemCallback<Message>{

        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.equals(newItem);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        View messageView;

        LinearLayout leftLayout;
        LinearLayout rightLayout;

        TextView leftMessage;
        TextView rightMessage;

        Button btnRead;
        Button btnCopy;
        Button btnShare;

        TextView recvTime;
        TextView sendTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageView=itemView;

            leftLayout=itemView.findViewById(R.id.message_receive_block);
            rightLayout=itemView.findViewById(R.id.message_send_block);

            leftMessage=itemView.findViewById(R.id.txb_message_content_r);
            rightMessage=itemView.findViewById(R.id.txb_message_content_s);

            btnRead=itemView.findViewById(R.id.btn_read_aloud);
            btnCopy=itemView.findViewById(R.id.btn_copy_content);
            btnShare=itemView.findViewById(R.id.btn_share);

            recvTime=itemView.findViewById(R.id.msg_recv_timestamp);
            sendTime=itemView.findViewById(R.id.msg_send_timestamp);
        }
    }
}
