package com.framework.firebaseUtils.firestore;

import com.google.gson.annotations.SerializedName

data class Events(
  @SerializedName("state") val state: Int
)