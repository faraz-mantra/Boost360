package com.nowfloats.education.toppers.model

import com.nowfloats.education.model.Profileimage

data class AddTopperModel(
  val ActionData: ActionData,
  val WebsiteId: String
)

data class ActionData(
  val coursecategory: String,
  val name: String,
  val profileimage: Profileimage,
  val programavailed: String,
  val rank: String,
  val testimonialimage: Testimonialimage,
  val testimonialtext: String
)

data class Testimonialimage(
  val description: String,
  val url: String
)