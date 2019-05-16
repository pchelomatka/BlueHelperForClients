package com.example.bluehelperforclients.response_body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseGetPoints {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("response")
    @Expose
    private List<ResponsePoints> response = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<ResponsePoints> getResponse() {
        return response;
    }

    public void setResponse(List<ResponsePoints> response) {
        this.response = response;
    }
}
