package com.nowfloats.NavigationDrawer.model;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class PostOfferEvent {

    public boolean success;
    public String result;
    public PostOfferEvent(boolean success, String result){
        this.success = success;
        this.result = result;
    }
}
