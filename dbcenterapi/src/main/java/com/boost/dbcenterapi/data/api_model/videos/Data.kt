package com.boost.dbcenterapi.data.api_model.videos

data class Data(
    val _kid: String,
    val createdon: String,
    val featurevideo: List<Featurevideo>,
    val guidevideos: List<Guidevideo>,
    val isarchived: Boolean,
    val marketplacecustomervideos: List<Marketplacecustomervideo>,
    val rootaliasurl: Rootaliasurl,
    val schemaid: String,
    val updatedon: String,
    val userid: String,
    val websiteid: String
)