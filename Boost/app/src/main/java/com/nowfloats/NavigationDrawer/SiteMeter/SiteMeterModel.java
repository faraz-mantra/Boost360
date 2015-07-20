package com.nowfloats.NavigationDrawer.SiteMeter;

/**
 * Created by guru on 17-06-2015.
 */
public class SiteMeterModel {
    public String Title;
    public String Desc;
    public String Percentage;
    public boolean status;

    public void setStatus(boolean status) {
        this.status = status;
    }

    public SiteMeterModel(String s, String s1, String s2, boolean status) {
        this.Title = s;
        this.Desc = s1;
        this.Percentage = s2;
        this.status = status;
    }
}
