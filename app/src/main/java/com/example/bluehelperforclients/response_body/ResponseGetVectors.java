package com.example.bluehelperforclients.response_body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseGetVectors {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("response")
    @Expose
    private List<ResponseVectors> response = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<ResponseVectors> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseVectors> response) {
        this.response = response;
    }
}
