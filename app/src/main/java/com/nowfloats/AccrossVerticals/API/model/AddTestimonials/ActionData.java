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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTestimonial() {
        return testimonial;
    }

    public void setTestimonial(String testimonial) {
        this.testimonial = testimonial;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("testimonials")
    @Expose
    private String testimonial;

    @SerializedName("customerName")
    @Expose
    private String customerName;

    public ProfileImagee getProfileImage() {
        return profileImage;
    }

    public void setProfileImagee(ProfileImagee profileImage) {
        this.profileImage = profileImage;
    }

    @SerializedName("profileImage")
    @Expose
    private ProfileImagee profileImage;

    public String getOurStory() {
        return ourStory;
    }

    public void setOurStory(String ourStory) {
        this.ourStory = ourStory;
    }

    @SerializedName("ourStory")
    @Expose
    private String ourStory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("name")
    @Expose
    private String name;

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