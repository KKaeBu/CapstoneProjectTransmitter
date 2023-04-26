package com.example.retrofit;

import com.example.dto.GPSDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitService {
    @POST("api/gps")
    Call<GPSDto> setGPS(@Body GPSDto gps);
}
