package com.nowfloats.AccrossVerticals.API.model.AddTestimonials;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionData {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("profileimage")
    @Expose
    private Profileimage profileimage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Profileimage getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(Profileimage profileimage) {
        this.profileimage = profileimage;
    }

}