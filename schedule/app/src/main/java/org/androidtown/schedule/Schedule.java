package org.androidtown.schedule;

import java.io.Serializable;

/**
 * Created by ohji1 on 2017-07-08.
 */

//this is DTO
public class Schedule implements Serializable
{
    private int year;
    private int mounth;
    private int day;
    private int hour;
    private int minute;
    private String body;
    private String title;
    private String uid;
    private String name;

    public Schedule(){};

    public Schedule(int year, int mounth, int day, int hour, int minute, String title, String body, String uid, String name) {
        this.year = year;
        this.mounth = mounth;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.body = body;
        this.title = title;
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
