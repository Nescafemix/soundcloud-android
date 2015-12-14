package com.brianuosseph.soundcloudapp;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new MaterialModule());
    }
}
