package com.example.dto;

import java.util.Date;
import java.util.List;

public class WalkDto {
    private String roadMapName;
    private long walkedTime;
    private float travelDistance;
    private int burnedCaloried;
    private List<GPSDto> pingList;
    private Date walkDate;

    public WalkDto(String roadMapName, long walkedTime, float travelDistance, int burnedCaloried, List<GPSDto> pingList, Date walkDate) {
        this.roadMapName = roadMapName;
        this.walkedTime = walkedTime;
        this.travelDistance = travelDistance;
        this.burnedCaloried = burnedCaloried;
        this.pingList = pingList;
        this.walkDate = walkDate;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "PostResult{" +
                "durationTime=" +
                "}";
    }


}
