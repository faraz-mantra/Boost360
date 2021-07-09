package com.nowfloats.AccrossVerticals.API.model.GetTestimonials;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profileimage {

  @SerializedName("url")
  @Expose
  private String url = "";
  // for review title -> doctors
  @SerializedName("description")
  @Expose
  private String description;

  public String getUrl() {
    if (url == null) return "";
    else return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDescription() {
    if (description == null) return "";
    else return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
