package com.alohagoha.weathernote.data;

import android.widget.ImageView;
import android.widget.TextView;

public interface IDataManager {
    void initRetrofit();

    void requestRetrofit(final TextView textView, final ImageView imageView, String city, String keyApi);
}