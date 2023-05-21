package com.example.gpstransmitter;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.dto.GPSDto;
import com.example.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackgroundService extends Service {
    private static final String TAG = BackgroundService.class.getSimpleName();
    private Handler handler;
    private Runnable runnable;
    private GPSDto gpsDto;
    private RetrofitService retrofitAPI;
    private LocationManager lm;

    @Override
    public void onCreate() {
        Log.i("onCreate", "onCreate 시작");
        super.onCreate();
        handler = new Handler();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.200.60.0:8080/") // aws
//                .baseUrl("http://10.0.2.2:8080/") // local
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onStartCommand", "onStartCommand 시작");
        startTransmitGPS();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy", "onDestroy 시작");

        super.onDestroy();
        stopTransmitGPS();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTransmitGPS() {
        Log.i("startTransmitGPS", "startTransmitGPS 시작");

        runnable = new Runnable() {
            @Override
            public void run() {
                locationUpdate();
            }
        };
    }

    private void stopTransmitGPS() {
        Log.i("stopTransmitGPS", "stopTransmitGPS 시작");

        lm.removeUpdates(gpsLocationListener);
        handler.removeCallbacks(runnable);
    }

    private void locationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsLocationListener);

    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.i("onLocationChanged", "onLocationChanged 시작");


            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            gpsDto = new GPSDto(latitude, longitude, altitude);

            Call<Long> setGPS = retrofitAPI.setGPS(gpsDto);
            setGPS.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    if(response.isSuccessful()){
                        Long result = response.body();
                        Log.d("result", "result: " + result);
                    }
                }
                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            gpsDto = null;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}
