package com.onboarding.nowfloats.model.channel.respose

import java.io.Serializable

data class Extra(
    val CurrentIndex: Int? = null,
    val PageSize: Int? = null,
    val TotalCount: Int? = null
) : Serializable