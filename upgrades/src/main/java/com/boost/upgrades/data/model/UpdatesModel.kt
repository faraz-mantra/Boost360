package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "updates")
data class UpdatesModel(

    @Json(name = "id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @Json(name = "title")
    @ColumnInfo(name = "title")
    val title: String?,

    @Json(name = "name")
    @ColumnInfo(name = "name")
    val name: String?,

    @Json(name = "price")
    @ColumnInfo(name = "price")
    var price: Float = 0f,

    @Json(name = "image")
    @ColumnInfo(name = "image")
    val image: String?



) : Serializable