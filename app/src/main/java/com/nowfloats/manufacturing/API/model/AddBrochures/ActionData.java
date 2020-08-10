package com.nowfloats.manufacturing.API.model.AddBrochures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionData {

    @SerializedName("uploadpdf")
    @Expose
    private Uploadpdf uploadpdf;
    @SerializedName("title")
    @Expose
    private String title;

    public Uploadpdf getUploadpdf() {
        return uploadpdf;
    }

    public void setUploadpdf(Uploadpdf uploadpdf) {
        this.uploadpdf = uploadpdf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}