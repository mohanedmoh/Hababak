package com.example.mohaned.hababak.Models;

public class traveller_info {
    String traveller_name,passport_no,age;

    public void setTraveller_name(String traveller_name) {
        this.traveller_name = traveller_name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPassport_no(String passport_no) {
        this.passport_no = passport_no;
    }

    public String getTraveller_name() {
        return traveller_name;
    }

    public String getPassport_no() {
        return passport_no;
    }

    public String getAge() {
        return age;
    }
}
