package com.nowfloats.instagram.models

data class IGFBPageLinkedResponse(
    val id: String,
    val instagram_business_account: InstagramBusinessAccount
){
    data class InstagramBusinessAccount(
        val id: String
    )
}