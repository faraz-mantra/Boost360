package com.nowfloats.AccrossVerticals.API.model.GetToken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTokenData {

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    @SerializedName("Token")
    @Expose
    private String Token;


}