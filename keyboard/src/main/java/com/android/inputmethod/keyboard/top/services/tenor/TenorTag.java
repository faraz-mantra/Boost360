package com.android.inputmethod.keyboard.top.services.tenor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sepehr on 3/4/17.
 */
public class TenorTag {
    public String name;
    @SerializedName("searchterm")
    private String searchTerm;
    private String path;
    private String image;
}
