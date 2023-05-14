package com.example.dto;

public class ActivityDto {
    private long durationTime;

    public ActivityDto(long durationTime) {
        this.durationTime = durationTime;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "PostResult{" +
                "durationTime=" + durationTime +
                "}";
    }


}
