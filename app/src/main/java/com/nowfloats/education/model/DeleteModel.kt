package com.nowfloats.education.model


data class DeleteModel(
  val Multi: Boolean,
  val Query: String,
  val UpdateValue: String
)

data class UpdatedValue(
  val `$set`: Set
)

data class Set(
  val IsArchived: Boolean
)