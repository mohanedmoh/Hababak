package com.example.mohaned.hababak.Models;

import org.json.JSONObject;

public class Response {
    boolean status;
    JSONObject response;

    public Response(boolean status, JSONObject response) {
        this.status = status;
        this.response = response;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }
}
