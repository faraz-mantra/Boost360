package com.nowfloats.Analytics_Screen.Graph.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitsModel {

    @SerializedName("batchType")
    @Expose
    private String batchType;
    @SerializedName("uniqueVisitsList")
    @Expose
    private List<UniqueVisitsList> uniqueVisitsList = null;

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    public List<UniqueVisitsList> getUniqueVisitsList() {
        return uniqueVisitsList;
    }

    public void setUniqueVisitsList(List<UniqueVisitsList> uniqueVisitsList) {
        this.uniqueVisitsList = uniqueVisitsList;
    }

    public static class UniqueVisitsList {

        @SerializedName("DataCount")
        @Expose
        private Integer dataCount;
        @SerializedName("batchNumber")
        @Expose
        private Integer batchNumber;
        @SerializedName("endDate")
        @Expose
        private String endDate;
        @SerializedName("startDate")
        @Expose
        private String startDate;

        public Integer getDataCount() {
            return dataCount;
        }

        public void setDataCount(Integer dataCount) {
            this.dataCount = dataCount;
        }

        public Integer getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(Integer batchNumber) {
            this.batchNumber = batchNumber;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

    }
}
