package com.example.bluehelperforclients.response_body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseVectors {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("start_point")
    @Expose
    private String startPoint;
    @SerializedName("end_point")
    @Expose
    private String endPoint;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;
    @SerializedName("building_id")
    @Expose
    private String buildingId;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("endp")
    @Expose
    private String endp;
    @SerializedName("editor")
    @Expose
    private String editor;

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

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEndp() {
        return endp;
    }

    public void setEndp(String endp) {
        this.endp = endp;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }
}
