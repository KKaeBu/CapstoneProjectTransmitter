package com.example.gpstransmitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dto.GPSDto;
import com.example.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    //    private TextView txtResult;

    double longitude = 0.0;
    double latitude = 0.0;
    double altitude = 0.0;
//    Location location = null;
    RetrofitService retrofitAPI = null;

    private GPSDto gpsDto;

    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
//        txtResult = (TextView)findViewById(R.id.txtResult);

        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.200.60.0:8080/") // aws
//                .baseUrl("http://10.0.2.2:8080/") // local
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitService.class);

        button1.setBackgroundColor(Color.BLUE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    button1.setText("산책 시작");
                    button1.setBackgroundColor(Color.BLUE);
                    check = false;
                    lm.removeUpdates(gpsLocationListener);
                }else {
                    button1.setText("산책 종료");
                    button1.setBackgroundColor(Color.RED);
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                0);
                    } else {
                        // 위치정보를 원하는 시간, 거리마다 갱신해준다.
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                30000,
                                7,
                                gpsLocationListener);
                    }
                    check = true;
                }
            }
        });

    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitude = Math.floor(location.getLatitude()*1000000)/1000000.0;
                longitude = Math.floor(location.getLongitude()*1000000)/1000000.0;
                altitude = location.getAltitude();

                gpsDto = new GPSDto(latitude, longitude, altitude);

                Call<Long> setGPS = retrofitAPI.setGPS(gpsDto);
                setGPS.enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        if (response.isSuccessful()) {
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
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}