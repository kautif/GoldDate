package com.example.golddate.util;

import java.util.ArrayList;
import java.util.List;

public class Profile extends ProfileID {

//    All has to match key names in Firebase
    private String name;
    private int birth_day;
    private int birth_month;
    private int birth_year;
    private String img_url;
    private String age;

    public List<String> getInterest() {
        return interest;
    }

    public void setInterest(ArrayList<String> interest) {
        this.interest = interest;
    }

    private List<String> interest;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String desc;

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }



    public int getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(int birth_day) {
        this.birth_day = birth_day;
    }

    public int getBirth_month() {
        return birth_month;
    }

    public void setBirth_month(int birth_month) {
        this.birth_month = birth_month;
    }


    public int getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(int birth_year) {
        this.birth_year = birth_year;
    }


    private String location;

//    public Profile(String name, String img_url, String age, String location) {
//        this.name = name;
//        this.img_url = img_url;
//        this.age = age;
//        this.location = location;
//    }

    public Profile() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
