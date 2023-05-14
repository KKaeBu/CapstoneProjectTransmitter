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
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dto.ActivityDto;
import com.example.dto.GPSDto;
import com.example.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private TextView txtResult;
    private GPSDto gpsDto;
    private ActivityDto activityDto;

    private boolean check = false;

    private long startTime; //산책 시작 시간
    private long endTime; //산책 종료 시간
    private long durationTimeSec; //산책 걸린 시간

    private RetrofitService retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
        txtResult = (TextView)findViewById(R.id.txtResult);

        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://43.200.60.0:8080/") // aws
                .baseUrl("http://10.0.2.2:8080/") // local
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitService.class);

        button1.setBackgroundColor(Color.BLUE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    endTime = System.currentTimeMillis(); // 산책 종료 시간 측정
                    durationTimeSec = endTime - startTime; // 종료 시간 - 시작 시간 = 걸린 시간
                    System.out.println("startTime: " + startTime);
                    System.out.println("endTime: " + endTime);
                    System.out.println(durationTimeSec + "m/s");
                    System.out.println((durationTimeSec/1000) + "sec");


                    button1.setText("산책 시작");
                    button1.setBackgroundColor(Color.BLUE);
                    check = false;
                    lm.removeUpdates(gpsLocationListener);
                    PostEndOfWalk(durationTimeSec);

                    startTime = 0; //시작 시간 초기화
                    endTime = 0; //종료 시간 초기화
                    durationTimeSec = 0; //걸린 시간 초기화
                }else {
                    startTime = System.currentTimeMillis(); // 산책 시작 시간 측정
                    button1.setText("산책 종료");
                    button1.setBackgroundColor(Color.RED);
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                0);
                    } else {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, gpsLocationListener);
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

    // 산책 종료시
    public void PostEndOfWalk(long durationTime) {
        activityDto = new ActivityDto(durationTime);
        Call<Void> endOfWalk = retrofitAPI.endOfWalk(activityDto);
        endOfWalk.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d("success", "result: " + response);
                }else{
                    Log.d("fail", "result: " + response);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
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

//            Call<Void> savePing = retrofitAPI.savePing(gpsDto);
//            savePing.enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call<Void> call, Response<Void> response) {
//                    if(response.isSuccessful()){
//                        Log.d("success", "result: " + response);
//                    }else{
//                        Log.d("fail", "result: " + response);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Void> call, Throwable t) {
//                    t.printStackTrace();
//                }
//            });

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
