package com.boost.dbcenterapi.data.api_model.videos

data class Featurevideo(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val categories: List<Any>,
    val createdon: String,
    val helpsectionidentifier: String,
    val importance: Double,
    val isarchived: Boolean,
    val isenabled: Boolean,
    val platformid: String,
    val platformminversion: String,
    val relatedfeatures: List<String>,
    val thumbnailimage: Thumbnailimage,
    val updatedon: String,
    val videodescription: String,
    val videodurationseconds: Double,
    val videolanguage: String,
    val videoorientation: String,
    val videotitle: String,
    val videotype: String,
    val videourl: Videourl,
    val websiteid: String
)