package com.nowfloats.customerassistant.models;

import java.io.Serializable;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class SugUpdates implements Serializable {
    public String Name;
    public String Image;
    public String updateUrl;
    public boolean isSelected;
    public boolean viewMore = true;

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

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

    public boolean isViewMore() {
        return viewMore;
    }

    public void setViewMore(boolean viewMore) {
        this.viewMore = viewMore;
    }
}
