package com.example.bluehelperforclients.activities;

import java.util.HashMap;
import java.util.Map;

public class Point {
    public String name;
    public Map<String, String> beacons = new HashMap<>();

    Point(String name) {
        this.name = name;
    }

    public void addBeacon(String adress, String value) {
        this.beacons.put(adress, value);
    }

    public int getBeaconsCount() {
        return this.beacons.size();
    }
}
