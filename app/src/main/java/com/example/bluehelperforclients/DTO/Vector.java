package com.example.bluehelperforclients.DTO;

public class Vector {

    private String id;
    private String startPoint;
    private String endPoint;
    private String distance;
    private String direction;

    public Vector(String id, String startPoint, String endPoint, String distance, String direction) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distance = distance;
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
