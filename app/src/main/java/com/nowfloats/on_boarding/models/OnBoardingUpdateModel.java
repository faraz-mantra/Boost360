package com.nowfloats.on_boarding.models;

/**
 * Created by Admin on 22-03-2018.
 */

public class OnBoardingUpdateModel {
    public String Query;

    public String UpdateValue;

    public Boolean Multi = true;

    public String getQuery() {
        return Query;
    }

    public void setQuery(String query) {
        Query = query;
    }

    public String getUpdateValue() {
        return UpdateValue;
    }

    public void setUpdateValue(String updateValue) {
        UpdateValue = updateValue;
    }

}
