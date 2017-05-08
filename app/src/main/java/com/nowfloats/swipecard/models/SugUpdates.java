package com.nowfloats.swipecard.models;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class SugUpdates {
    private String Name;
    private String Image;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
