package com.marketplace.model.features


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VideoGallery(
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("desc")
  var desc: String? = null,
  @SerializedName("duration")
  var duration: String? = null,
  @SerializedName("isarchived")
  var isarchived: Boolean? = null,
  @SerializedName("_kid")
  var kid: String? = null,
  @SerializedName("_parentClassId")
  var parentClassId: String? = null,
  @SerializedName("_parentClassName")
  var parentClassName: String? = null,
  @SerializedName("_propertyName")
  var propertyName: String? = null,
  @SerializedName("thumbnail")
  var thumbnail: Thumbnail? = null,
  @SerializedName("title")
  var title: String? = null,
  @SerializedName("updatedon")
  var updatedon: String? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null,
  @SerializedName("youtube_link")
  var youtubeLink: String? = null
): Serializable