package com.nowfloats.Analytics_Screen.model;

public class SearchAnalyticsSummaryForFP {
    public int TotalNoOfSearchQueries;
    public int TotalNoOfImpressions;
    public int TotalNoOfClicks;
    public int CTR;

    public int getTotalNoOfSearchQueries() {
        return TotalNoOfSearchQueries;
    }

    public void setTotalNoOfSearchQueries(int totalNoOfSearchQueries) {
        TotalNoOfSearchQueries = totalNoOfSearchQueries;
    }

    public int getTotalNoOfImpressions() {
        return TotalNoOfImpressions;
    }

    public void setTotalNoOfImpressions(int totalNoOfImpressions) {
        TotalNoOfImpressions = totalNoOfImpressions;
    }

    public int getTotalNoOfClicks() {
        return TotalNoOfClicks;
    }

    public void setTotalNoOfClicks(int totalNoOfClicks) {
        TotalNoOfClicks = totalNoOfClicks;
    }

    public int getCTR() {
        return CTR;
    }

    public void setCTR(int CTR) {
        this.CTR = CTR;
    }
}
