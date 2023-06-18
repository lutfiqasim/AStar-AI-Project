package com.example.astarproj;

public class AlgorithimEntry {

    String name;
    String timeComp;

    long actualTime;

    int distance;

    public AlgorithimEntry(String name, String timeComp, long ActualTime, int distance) {
        this.name = name;
        this.timeComp = timeComp;
        this.actualTime = ActualTime;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getTimeComp() {
        return timeComp;
    }

    public long getActualTime() {
        return actualTime;
    }

    public int getDistance() {
        return distance;
    }
}
