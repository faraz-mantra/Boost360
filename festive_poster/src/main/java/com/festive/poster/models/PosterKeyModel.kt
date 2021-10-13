package com.festive.poster.models

data class PosterKeyModel(
    val default: String,
    val length: Int,
    val name: String,
    val type: String,
    var custom:String?,
)