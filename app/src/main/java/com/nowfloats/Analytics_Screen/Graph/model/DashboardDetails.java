package com.nowfloats.Analytics_Screen.Graph.model;

/**
 * Created by Abhi on 11/8/2016.
 */

public class DashboardDetails {
    public String startDate, endDate, fpTag, clientId;

    public DashboardDetails(String fptag) {
        this.fpTag = fptag;
    }

    public DashboardDetails() {

    }

    public void setData(String fpTag, String clientId, String startDate, String endDate) {
        this.fpTag = fpTag;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setFpTag(String tag) {
        this.fpTag = tag;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String id) {
        this.clientId = id;
    }

    public String getfpTag() {
        return fpTag;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String date) {
        this.startDate = date;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String date) {
        this.endDate = date;
    }
}
