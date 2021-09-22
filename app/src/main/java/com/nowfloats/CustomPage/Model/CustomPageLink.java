package com.nowfloats.CustomPage.Model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CustomPageLink {

    @SerializedName("Total")
    private Integer total;

    @SerializedName("Skip")
    private int skip;

    @SerializedName("Items")
    private List<ItemsItem> items;

    @SerializedName("Limit")
    private int limit;

    @SerializedName("Count")
    private Integer count;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public List<ItemsItem> getItems() {
        return items;
    }

    public void setItems(List<ItemsItem> items) {
        this.items = items;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}