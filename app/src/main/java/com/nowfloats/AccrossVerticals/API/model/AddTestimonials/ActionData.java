package com.nowfloats.AccrossVerticals.API.model.AddTestimonials;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionData {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("testimonial")
    @Expose
    private String testimonial;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("ourStory")
    @Expose
    private String ourStory;
    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("profileimage")
    @Expose
    private Profileimage profileimage;
    @SerializedName("profileImage")
    @Expose
    private ProfileImagee profileImage;
    @SerializedName("img")
    @Expose
    private ProfileImagee img;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOurStory() {
        return ourStory;
    }

    public void setOurStory(String ourStory) {
        this.ourStory = ourStory;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTestimonial() {
        return testimonial;
    }

    public void setTestimonial(String testimonial) {
        this.testimonial = testimonial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Profileimage getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(Profileimage profileimage) {
        this.profileimage = profileimage;
    }

    public ProfileImagee getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ProfileImagee profileImage) {
        this.profileImage = profileImage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ProfileImagee getImg() {
        return img;
    }

    public void setImg(ProfileImagee img) {
        this.img = img;
    }
}