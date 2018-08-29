package com.alohagoha.weathernote;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alohagoha.weathernote.data.DataManager;
import com.alohagoha.weathernote.data.IDataManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeatherFragment extends BaseFragment implements CreateActionFragment.OnHeadlineSelectedListener {

    private static final String ARG_COUNTRY = "ARG_COUNTRY";
    private String country;
    private TextView textView;
    private ImageView imageView;
    private PrefsHelper prefsHelper;
    public WeatherFragment() {
//        Особенностью поведения android-а состоит в том, что в любой момент
//        он может убить конкретный фрагмент (с случаи нехватки памяти например)
//        и потом попытаться восстановить его, используя конструктор без параметров,
//                следовательно передача параметров через конструкторы черевата
//        крэшами приложения в произвольный момент времени.
    }

    public static WeatherFragment newInstance(String country) {
//        Для того что бы положить требуемые значения во фрагмент,
//        нужно обернуть их в Bundle и передать через метод setArguments.
//        Стандартным способом передачи параметров считается создание статического
//        метода newInstance (...),
//        а для восстановление параметров используется метод getArguments(...),вызываемый в
//        методе жизненного цикла onCreate (...) .
        Bundle args = new Bundle();
        args.putString(ARG_COUNTRY, country);
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            country = getArguments().getString(ARG_COUNTRY);
        }

        prefsHelper = new PrefsData(getBaseActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_layout, container, false);
    }

    @Override
    protected void initLayout(View view, Bundle savedInstanceState) {
        textView = view.findViewById(R.id.tv_country);

        //проверяем нашу переменную если она не пустая показываем город, если наоборот - ничего не показываем
        if (country != null && country.length() > 0) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(country);
        } else {
            textView.setVisibility(View.GONE);
        }

        String getSP = prefsHelper.getSharedPreferences(Constants.CITY, Constants.HOME);
        if (!getSP.equals("")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getSP);
        }

        imageView = view.findViewById(R.id.ivWeather);
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);


        ((TextView) getBaseActivity().findViewById(R.id.tv_humidity)).setText("30%");
        ((TextView) getBaseActivity().findViewById(R.id.tv_pressure)).setText("752mmHg");

        initRetrofit();

    }
    private void initRetrofit() {
        IDataManager dataManager = new DataManager();
        dataManager.initRetrofit();
        dataManager.requestRetrofit((TextView) getBaseActivity().findViewById(R.id.bigTemp), imageView,"Moscow,RU", "8c9412b50f6fa41f93d72c7d62bfdb25");
    }
    @Override
    public void onArticleSelected(ArrayList<String> citiesList) {
        textView.setVisibility(View.VISIBLE);
        String cities = citiesList.toString();
        textView.setText(cities.substring(cities.indexOf("[") + 1, cities.indexOf("]")));
    }
}
