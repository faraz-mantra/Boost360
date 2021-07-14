package com.framework.models.firestore;

import com.google.gson.annotations.SerializedName

data class Events(
  @SerializedName("state") val state: Int
)