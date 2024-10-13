package com.example.cortana.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TTSUtil {

    private static final String TAG = "TTSUtil";
    
    private static final String SEG_PREFIX = "seg"; // 段落前缀
    private static final int ANDROID_VERSION_NOT_SUPPORT = -3; // 版本不支持

    private int ttsErrorStatus=0;
    private TextToSpeech tts;

    public TTSUtil(Context context)
    {
        TextToSpeech tts=new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS){
                ttsErrorStatus=TextToSpeech.SUCCESS;
            }
            else {
                ttsErrorStatus=TextToSpeech.ERROR;
            }
        });
        this.tts=tts;

        if (ttsErrorStatus == TextToSpeech.SUCCESS){
            //todo:多语言更好的处理（好像无解，大多数开源项目也是这样不了了之）
            Set<Locale> languages = tts.getAvailableLanguages();
            if (languages == null||languages.isEmpty())
            {
                ttsErrorStatus=TextToSpeech.LANG_NOT_SUPPORTED;
            }
            else if(languages.contains(Locale.CHINESE)){
                ttsErrorStatus=tts.setLanguage(Locale.CHINESE);
            }
            else if(languages.contains(Locale.SIMPLIFIED_CHINESE)){
                ttsErrorStatus=tts.setLanguage(Locale.SIMPLIFIED_CHINESE);
            }
            else if(languages.contains(Locale.TRADITIONAL_CHINESE))
            {
                ttsErrorStatus=tts.setLanguage(Locale.TRADITIONAL_CHINESE);
            }
            else{
                ttsErrorStatus=TextToSpeech.LANG_NOT_SUPPORTED;
            }
        }
    }

    public void setPitch(float value)
    {
        if(tts!=null)
        {
            tts.setPitch(value);
        }
    }

    public void setSpeechRate(float value)
    {
        if(tts!=null){
            tts.setSpeechRate(value);
        }
    }

   public void readAloud(String content,GetLastSegUtteranceProgressListener utteranceProgressListener)
   {
        if(tts==null){return;}
        tts.setOnUtteranceProgressListener(utteranceProgressListener);
        switch (ttsErrorStatus){
            case TextToSpeech.LANG_MISSING_DATA:{
                Log.d(TAG, "readAloud: voice_missing_data");
                break;
            }
            case TextToSpeech.LANG_NOT_SUPPORTED:{
                Log.d(TAG, "readAloud: voice_lang_not_supported");
                break;
            }
            default:{
                Log.d(TAG, "readAloud: DEFAULT_LOG");
                if (tts.isSpeaking()) {
                    tts.stop();
                } else {
                    List<String> textSeg = genSegment(content, 10);
                    utteranceProgressListener.setLastSegId(SEG_PREFIX + (textSeg.size() - 1));
                    for (String text : textSeg) {
                        tts.speak(text,TextToSpeech.QUEUE_ADD,null,SEG_PREFIX+1);
                    }
                }
                break;
            }
        }
   }

    private List<String> genSegment(String originStr,Integer segmentLength){
        if(segmentLength==null)
        {
            segmentLength=3999;//默认值
        }
        int originLength = originStr.length();
        int arraySize = originLength / segmentLength + 1;
        List<String> ret=new ArrayList<>(arraySize);
        for(int i=0;i<arraySize;i++)
        {
            ret.set(i,originStr.substring(i * segmentLength, Math.min((i + 1) * segmentLength, originLength)));
        }
        return ret;
    }

    public int getSpeechLength(String content) {
        int totalSecond = (int)(content.length() / 3.7);
        return totalSecond / 60;
    }

    public abstract static class GetLastSegUtteranceProgressListener extends UtteranceProgressListener {
        private String lastSegId = "";
        public void setLastSegId(String lastSegId) {
            this.lastSegId = lastSegId;
        }

        //todo:优化
        @Override
        public void onDone(String utteranceId) {
            onDone(utteranceId, lastSegId == utteranceId);
        }
        public abstract void onDone(String onDoneutteranceId, Boolean isLastSeg);
    }
}
