package com.nowfloats.ProductGallery.Model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;


public class Tag {

    public int id;
    public String tagId;
    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;
    public boolean isDeletable;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public float radius;
    public String deleteIcon;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public Drawable background;


    public Tag(String text, String tagId) {
        init(0, tagId, text, Color.parseColor("#7f7f7f"), 14f, Color.parseColor("#e9e9e9"), Color.parseColor("#88363636"),
                true, Color.parseColor("#7f7f7f"), 14f, 100, "×", 0f, Color.parseColor("#ffffff"));
    }

    private void init(int id, String tagId, String text, int tagTextColor, float tagTextSize,
                      int layoutColor, int layoutColorPress, boolean isDeletable,
                      int deleteIndicatorColor, float deleteIndicatorSize, float radius,
                      String deleteIcon, float layoutBorderSize, int layoutBorderColor) {
        this.id = id;
        this.tagId = tagId;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layoutColor;
        this.layoutColorPress = layoutColorPress;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
    }
}