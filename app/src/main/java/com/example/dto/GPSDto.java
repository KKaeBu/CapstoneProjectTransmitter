package com.example.dto;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class GPSDto {
    private double latitude;
    private double longitude;
    private double altitude;

    public GPSDto(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "PostResult{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                "}";
    }


}
