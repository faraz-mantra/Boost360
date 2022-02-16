package com.appservice.model

data class CreateUpdateBody(
    val IsHtmlString: Boolean,
    val actualImageUri: String,
    val clientId: String,
    val externalSourceName: String,
    val extraDetails: ExtraDetails,
    val extraSmallImageUri: String,
    val fpIds: List<String>,
    val isPictureMessage: Boolean,
    val keywords: List<String>,
    val merchantId: String,
    val message: String,
    val messageType: String,
    val parentId: String,
    val sendToSubscribers: Boolean,
    val socialParameters: String,
    val tags: List<String>,
    val tileImageUri: String,
    val type: String
)
data class ExtraDetails(
    val templateId: String
)
