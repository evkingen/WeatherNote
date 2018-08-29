package com.alohagoha.weathernote.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.alohagoha.weathernote.BaseActivity;

public class GeoInfoService extends Service {
    LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        return super.onStartCommand(intent, flags, startId);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            sendLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                sendLocation(locationManager.getLastKnownLocation(provider));
            }
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    private void sendLocation(Location location) {
        if (location == null)
            return;
        try {
            Intent intent = new Intent(BaseActivity.BROADCAST_ACTION);
            intent.putExtra(BaseActivity.SENSOR_VAL, new double[]{location.getLatitude(), location.getLongitude()});
            sendBroadcast(intent);
        }catch(Throwable t1) {
            Toast.makeText(getApplicationContext(),"Exception: " + t1.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
