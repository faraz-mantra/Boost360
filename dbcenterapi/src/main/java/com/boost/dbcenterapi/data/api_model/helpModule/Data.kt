package com.boost.dbcenterapi.data.api_model.helpModule

data class Data(
    val _kid: String,
    val createdon: String,
    val featurevideo: List<Featurevideo>,
    val isarchived: Boolean,
    val rootaliasurl: Rootaliasurl,
    val schemaid: String,
    val updatedon: String,
    val userid: String,
    val websiteid: String
)