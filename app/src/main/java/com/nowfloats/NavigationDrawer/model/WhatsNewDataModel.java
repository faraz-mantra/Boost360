package com.nowfloats.NavigationDrawer.model;

/**
 * Created by NowFloats on 02-08-2016.
 */
public class WhatsNewDataModel {
    public int imageResource;
    public String headerText;
    public String bodyText;

    public WhatsNewDataModel(int resourceId, String headerText, String bodyText) {
        this.imageResource = resourceId;
        this.headerText = headerText;
        this.bodyText = bodyText;
    }

}
