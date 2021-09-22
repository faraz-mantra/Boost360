package com.onboarding.nowfloats.model.channel.respose

import java.io.Serializable

data class ChannelExtra(
  val CurrentIndex: Int? = null,
  val PageSize: Int? = null,
  val TotalCount: Int? = null
) : Serializable