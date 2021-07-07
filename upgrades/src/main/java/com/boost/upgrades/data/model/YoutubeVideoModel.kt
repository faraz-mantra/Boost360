package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "YoutubeVideo")
data class YoutubeVideoModel(

  @PrimaryKey
  @ColumnInfo(name = "YoutubeVideo_key")
  var YoutubeVideo_key: String,

  @ColumnInfo(name = "desc")
  var desc: String,

  @ColumnInfo(name = "duration")
  var duration: String? = null,

  @ColumnInfo(name = "title")
  var title: String? = null,

  @ColumnInfo(name = "youtube_link")
  var youtube_link: String? = null

)