package com.example.astarproj;

public class TableEntry {

    boolean known;
    double distance;
    City path;
    public TableEntry(){
    }
    public double getDistance() {
        double dis = (int) (distance * 100) / 100.0;
        return dis;
    }
    @Override
    public String toString() {
        return path + " ";
    }

}