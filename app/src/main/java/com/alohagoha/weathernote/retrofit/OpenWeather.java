package com.alohagoha.weathernote.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//8c9412b50f6fa41f93d72c7d62bfdb25
public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
}
