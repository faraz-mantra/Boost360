package com.nowfloats.sync.model;

/**
 * Created by RAJA on 20-06-2016.
 */
public class SearchQueries {
    private int Id;
    private String SearchQueries;
    private String date;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getSearchQueries() {
        return SearchQueries;
    }

    public void setSearchQueries(String searchQueries) {
        SearchQueries = searchQueries;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
