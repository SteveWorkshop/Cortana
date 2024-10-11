package com.example.cortana;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.google.android.material.color.DynamicColors;

public class BaseApplication extends Application {
    public static final String DEFAULT_MODEL_TYPE="gemini-1.5-flash";

    private static volatile Context context;

    public static synchronized Context getApplication()
    {
        return context;
    }


    public static void newWindow(Context context,Class clazz)
    {
        Intent intent=new Intent(context,clazz);
        context.startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}