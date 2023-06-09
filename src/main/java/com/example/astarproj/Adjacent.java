package com.example.astarproj;

public class Adjacent implements Comparable<Adjacent> {
    private City city;
    private float distance; //kilometers

    public Adjacent(City c, float dis) {
        this.city = c;
        this.distance = dis;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "city=" + city + ", distance=" + distance;
    }

    @Override
    public int compareTo(Adjacent o) {
        return (int) Double.compare(this.distance, o.distance);
    }
}
