package com.alohagoha.weathernote.data;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.alohagoha.weathernote.retrofit.OpenWeather;
import com.alohagoha.weathernote.retrofit.WeatherRequest;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataManager implements  IDataManager{
    private static final String BASE_URL_ICON = "http://openweathermap.org/img/w/";
    private static final String BASE_URL = "http://api.openweathermap.org/";
    private static final String TAG = "RETROFIT";
    OpenWeather openWeather;
    public void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);

    }

    public int toC(float t) {
        return Math.round(t-273.15f);
    }

    public void requestRetrofit(final TextView temp, final ImageView weatherIcon, String city, String keyApi) {
        openWeather.loadWeather(city,keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if(response.body()!=null) {
                            Log.d(TAG,Float.toString(response.body().getMain().getTemp()));
                            temp.setText(Integer.toString(toC(response.body().getMain().getTemp())));
                            Picasso.get().load(BASE_URL_ICON + response.body().getWeathers()[0].getIcon() +".png").into(weatherIcon);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                            Log.d(TAG,"oshibka");
                    }
                });
    }
}
