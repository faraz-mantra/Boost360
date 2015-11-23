package com.nowfloats.CustomPage.Model;

/**
 * Created by guru on 25/08/2015.
 */
public class CustomPageModel {
    public String CreatedOn;
    public String DisplayName;
    public String PageId;

    public int getSel() {
        return sel;
    }

    public void setSel(int sel) {
        this.sel = sel;
    }

    public int sel;

    public CustomPageModel(String CreatedOn,String DisplayName,String PageId){
        this.CreatedOn = CreatedOn;
        this.PageId = PageId;
        this.DisplayName = DisplayName;
    }
}
