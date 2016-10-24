package com.nowfloats.NavigationDrawer.model;

/**
 * Created by guru on 11-05-2015.
 */
public class UploadPostEvent {
    public String path;
    public String msg;
    public String mSocialShare;
    public UploadPostEvent(String path, String msg, String socialShare){
        this.path = path;
        this.msg = msg;
        this.mSocialShare = socialShare;
    }
}
