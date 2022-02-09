package com.nowfloats.instagram.models

import com.google.gson.annotations.SerializedName

data class IGFBPageLinkedResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("instagram_business_account")
    val instagram_business_account: InstagramBusinessAccount
){
    data class InstagramBusinessAccount(
        @SerializedName("id")
        val id: String
    )
}