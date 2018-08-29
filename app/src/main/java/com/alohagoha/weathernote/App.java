package com.alohagoha.weathernote;

import android.app.Application;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import es.dmoral.toasty.Toasty;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Настройка всплывающих окон оповещений
        Toasty.Config.getInstance().setSuccessColor(getResources().getColor(R.color.blue)).apply();
    }
}
