package com.nowfloats.NavigationDrawer.model;

import com.google.gson.annotations.SerializedName;

public class VisitAnalytics {
    @SerializedName("Visits")
    private int visits;
    @SerializedName("Visitors")
    private int visitors;
    @SerializedName("Year")
    private int year;
    @SerializedName("Month")
    private int month;
    @SerializedName("Day")
    private int day;
    @SerializedName("Week")
    private String week;


    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getVisitors() {
        return visitors;
    }

    public void setVisitors(int visitors) {
        this.visitors = visitors;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}