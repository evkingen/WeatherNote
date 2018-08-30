package com.alohagoha.weathernote;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.res.Configuration;
import android.graphics.Bitmap;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alohagoha.weathernote.data.DataManager;
import com.alohagoha.weathernote.data.IDataManager;
import com.alohagoha.weathernote.plan.PlanFragment;
import com.alohagoha.weathernote.service.GeoInfoService;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity
        implements BaseView.View, BaseFragment.Callback, NavigationView.OnNavigationItemSelectedListener {
    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";
    public final static String  SENSOR_VAL = "SENSOR_VAL";
    private static final int PERMISSION_REQUEST_CODE = 10;
    //инициализация переменных
    private FloatingActionButton fab;
    private TextView textView;
    private static final String TEXT = "TEXT";
    private static String contry;
    private boolean isChecked = false;

    private TextView textHumidity;
    private TextView textTemperature;
    private TextView gps_locality;
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor sensorTemperature;
    private Sensor sensorHumidity;
    private BroadcastReceiver broadcastReceiver;
    private static BaseActivity instance;

    public static BaseActivity getInstance() {
        return instance;
    }

    public void updateTheTextView(final String t) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = findViewById(R.id.bigTemp);
                tv.setText(t);
                View alertView = LayoutInflater.from(instance).inflate(R.layout.dialog_registration,null);
                if (alertView!=null) {
                    Toast.makeText(getBaseContext(), "alertView", Toast.LENGTH_LONG).show();
                    TextView et_code = alertView.findViewById(R.id.et_code);

                    if(et_code!=null) {
                        Toast.makeText(getBaseContext(),"et",Toast.LENGTH_LONG).show();
                        et_code.setText(t);
                    }
                }else{
                    Toast.makeText(getBaseContext(),"noalert",Toast.LENGTH_LONG).show();
                }
//                LayoutInflater factory = LayoutInflater.from(getBaseContext());
//                View alertView = factory.inflate(R.layout.dialog_registration, null);
//                EditText et_code = alertView.findViewById(R.id.et_code);
//
//                if(et_code!=null) et_code.setText(t);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (savedInstanceState != null) {
            TextView tv = findViewById(R.id.tvUsername);
            contry = savedInstanceState.getString("NAME");
        }
        setContentView(R.layout.activity_base);

        initLayout();

        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
        Intent intent = new Intent(BaseActivity.this, GeoInfoService.class);
        startService(intent);
        IntentFilter intentValue = new IntentFilter(BROADCAST_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String value = String.valueOf(intent.getDoubleArrayExtra(SENSOR_VAL)[0]);
                Log.d(SENSOR_VAL, value);
            }
        };

        registerReceiver(broadcastReceiver, intentValue);
        //}
    }



    private void initLayout() {
        //permissions
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"Yes", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"No", Toast.LENGTH_LONG).show();
            requestForReceiveSMSPermission();
        }


        //устанавливает тулбар
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);
        //устанавливаем drawer (выездное меню)
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //анимация клавищи (три палочки сверху) выездного меня
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, bottomAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //инициализация навигации
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(new CreateActionFragment());
            }
        });

        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.ivProfile);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new android.widget.PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.avatar_settings);
                popupMenu.show();
            }
        });
        //получение координат местоположения
        gps_locality = findViewById(R.id.gps_locality);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //получение датчиков влажоости и температуры
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (sensorTemperature==null) {
            Toasty.success(this, "Temperature sensor is nothing!").show();
        }
        if (sensorHumidity==null) {
            Toasty.success(this, "Humidity sensor is nothing!").show();
        }
        textTemperature = findViewById(R.id.bigTemp);
        textHumidity = findViewById(R.id.tv_humidity);

        //addFragment(new WeatherFragment());
        startWeatherFragment(contry);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            addFragment1(new CreateActionFragment());
        }


    }

    private void requestForReceiveSMSPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},PERMISSION_REQUEST_CODE);
        }
    }

    SensorEventListener listenerTemperature = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            showTemperatureSensors(sensorEvent);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    SensorEventListener listenerHumidity = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            showHumiditySensors(sensorEvent);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    public void showTemperatureSensors(SensorEvent event) {
        textTemperature.setText(String.format("%.4f",event.values[0]));
    }

    public void showHumiditySensors(SensorEvent event) {
        textHumidity.setText(String.format("%.4f",event.values[0]));
    }

    public void requestForLocationPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode == PERMISSION_REQUEST_CODE) {
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        }else{
            requestForLocationPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                showLocation(locationManager.getLastKnownLocation(provider));
            }else{
                requestForLocationPermission();
            }

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        final Location flocation = location;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gps_locality = findViewById(R.id.gps_locality);
                if (gps_locality!=null)
                gps_locality.setText(formatLocation(flocation));
            }
        });

    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format("Населенный пункт:\n%1$.4f , %2$.4f",location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        outState.putString("NAME", ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvUsername)).getText().toString());
        super.onSaveInstanceState(outState);
    }


    private void addFragment(Fragment fragment) {
        //вызываем SupportFragmentManager и указываем в каком контейнере будет находиться наш фрагмент
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                //.addToBackStack("")
                .commit();
    }

    private void addFragment1(Fragment fragment) {
        //вызываем SupportFragmentManager и указываем в каком контейнере будет находиться наш фрагмент
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame2, fragment)
                //.addToBackStack("")
                .commit();
    }


    private Fragment getCurrentFragment() {
        //получаем наименование фрагмента находящегося в контейнере в данных момент
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    @Override
    public void onBackPressed() {
        //закрываем drawer если он был открыт при нажатии на аппаратную клавишу назад
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getCurrentFragment() instanceof CreateActionFragment) {
            addFragment(new WeatherFragment());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_registration:
                dialogRegistration();
                return true;
            case R.id.action_category:
                return true;
            case R.id.action_select:
                if (isChecked) {
                    isChecked = false;
                    item.setChecked(isChecked);
                } else {
                    isChecked = true;
                    item.setChecked(isChecked);
                }
                return true;
            case R.id.action_one:
                item.setChecked(true);
                return true;
            case R.id.action_two:
                item.setChecked(true);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogRegistration() {
        //BroadcastReceiver
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertView = factory.inflate(R.layout.dialog_registration, null);
        Button acceptButton = alertView.findViewById(R.id.accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_code = alertView.findViewById(R.id.et_code);
                EditText et_phone = alertView.findViewById(R.id.et_phonenumber);
                if(!et_phone.getText().toString().equals("")) {
                    et_code.setText("123563");
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertView);
        builder.setTitle(R.string.alert_title_registration);
        builder.setPositiveButton(R.string.alert_ok, null);
        builder.show();


    }


    //работаем с навигацией
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
        } else if (id == R.id.nav_info) {
            // Handle the camera action
        } else if (id == R.id.nav_note) {
            addFragment(new PlanFragment());
        } else if (id == R.id.nav_home) {
            addFragment(new WeatherFragment());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Boolean inNetworkAvailable() {
        return true;
    }

    @Override
    public void initDrawer(String username, Bitmap profileImage) {
        Toasty.success(this, "Init drawer!!").show();
    }

    @Override
    public void onFragmentAttached() {


    }

    @Override
    public void onFragmentDetached(String tag) {

    }


    public void startWeatherFragment(String country) {
        //запускаем WeatherFragment и передаем туда country
        addFragment(WeatherFragment.newInstance(country));
        //cntry = country;


    }

    public Fragment getAnotherFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);

    }
}
