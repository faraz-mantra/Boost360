package com.nowfloats.signup.UI.Model

data class Get_Feature_DetailsItem(

    val activatedDate: String,
    val createdDate: String,
    val expiryDate: String,
    val featureKey: String,
    val featureState: Int,
    val featureType: Int,
    val properties: List<Get_Properties_Details>
)