package com.nowfloats.education.model

import com.nowfloats.education.toppers.model.Data

data class OurTopperResponse(
  val Data: List<Data>,
  val Extra: Extra
)

data class Testimonialimage(
  val description: String = "",
  val url: String = ""
)