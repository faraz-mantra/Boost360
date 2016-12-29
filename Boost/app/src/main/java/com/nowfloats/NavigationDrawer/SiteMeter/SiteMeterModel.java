package com.nowfloats.NavigationDrawer.SiteMeter;

/**
 * Created by guru on 17-06-2015.
 */
public class SiteMeterModel implements Comparable {
    public String Title;
    public String Desc;
    public String Percentage;
    public boolean status;
    public int sortChar;
    public int position;
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setSortChar(int c){this.sortChar = c;}

    public SiteMeterModel(int position ,String s, String s1, String s2, boolean status,int c) {
        this.position = position;
        this.Title = s;
        this.Desc = s1;
        this.Percentage = s2;
        this.status = status;
        this.sortChar = c;
    }

    @Override
    public int compareTo(Object another) {
        int cVal = ((SiteMeterModel)another).sortChar;
        return (cVal)-this.sortChar;
    }
}
