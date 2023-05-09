package com.example.gpstransmitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
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
    private GPSDto gpsDto;

    private boolean check = false;

    private RetrofitService retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
//        txtResult = (TextView)findViewById(R.id.txtResult);

        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.200.60.0:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitService.class);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    button1.setText("중지중");
                    check = false;
                    lm.removeUpdates(gpsLocationListener);
                }else {
                    button1.setText("실행중");
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                0);
                    } else {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, gpsLocationListener);
//                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

//                    if(lm != null) {
//                        String provider = lm.getProvider();
//                        double longitude = lm.get();
//                        double latitude = lm.getLatitude();
//                        double altitude = lm.getAltitude();
//
//
//                    }
//                    // 위치정보를 원하는 시간, 거리마다 갱신해준다.
//                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                            1000,
//                            1,
//                            gpsLocationListener);
//                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                            1000,
//                            1,
//                            gpsLocationListener);
                    }
                    check = true;
                }
            }
        });

    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

//            txtResult.setText("위치정보 : " + provider + "\n" +
//                    "위도 : " + latitude + "\n" +
//                    "경도 : " + longitude + "\n" +
//                    "고도  : " + altitude);
            gpsDto = new GPSDto(latitude, longitude, altitude);

            Call<GPSDto> setGPS = retrofitAPI.setGPS(gpsDto);
            setGPS.enqueue(new Callback<GPSDto>() {
                @Override
                public void onResponse(Call<GPSDto> call, Response<GPSDto> response) {
                    if(response.isSuccessful()){
                        GPSDto result = response.body();
                    }
                }

                @Override
                public void onFailure(Call<GPSDto> call, Throwable t) {
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
