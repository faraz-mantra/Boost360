package com.nowfloats.Analytics_Screen.model;

public class SearchRankModel {
    public String FPTag;
    public String FetchedOn;
    public String Keyword;
    public int OldRank;
    public int NewRank;

    public String getFPTag() {
        return this.FPTag;
    }

    public void setFPTag(String FPTag) {
        this.FPTag = FPTag;
    }

    public String getFetchedOn() {
        return this.FetchedOn;
    }

    public void setFetchedOn(String FetchedOn) {
        this.FetchedOn = FetchedOn;
    }

    public String getKeyword() {
        return this.Keyword;
    }

    public void setKeyword(String Keyword) {
        this.Keyword = Keyword;
    }

    public int getOldRank() {
        return this.OldRank;
    }

    public void setOldRank(int OldRank) {
        this.OldRank = OldRank;
    }

    public int getNewRank() {
        return this.NewRank;
    }

    public void setNewRank(int NewRank) {
        this.NewRank = NewRank;
    }
}
