package com.example.mohaned.hababak.Models;

public class req_status {
    String id,name,passport,datetime,req_status,req_type,flag,destination;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public void setReq_status(String req_status) {
        this.req_status = req_status;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getDestination() {
        return destination;
    }

    public String getPassport() {
        return passport;
    }

    public String getReq_status() {
        return req_status;
    }

    public String getReq_type() {
        return req_type;
    }

    public String getFlag() {
        return flag;
    }
}
