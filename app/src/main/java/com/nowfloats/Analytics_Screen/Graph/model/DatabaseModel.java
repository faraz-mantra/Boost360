package com.nowfloats.Analytics_Screen.Graph.model;

/**
 * Created by Abhi on 11/10/2016.
 */

public class DatabaseModel {
    int[] year, month, week;
    String date;
    int yearCount, monthCount, weekCount;

    public DatabaseModel() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int[] getYear() {
        return year;
    }

    public void setYear(int[] year) {
        this.year = year;
    }

    public int[] getMonth() {
        return month;
    }

    public void setMonth(int[] month) {
        this.month = month;
    }

    public int[] getWeek() {
        return week;
    }

    public void setWeek(int[] week) {
        this.week = week;
    }

    public int getYearCount() {
        return yearCount;
    }

    public void setYearCount(int yearCount) {
        this.yearCount = yearCount;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }
}

