package com.nowfloats.education.batches.model

data class UpdateUpcomingBatchModel(
  val Multi: Boolean,
  val Query: String,
  val UpdateValue: String
)

data class Query(
  val _id: String
)

data class UpdateValue(
  val `$set`: Set
)

data class Set(
  val Coursecategorytag: String,
  val batchtiming: String,
  val commencementdate: String,
  val duration: String
)