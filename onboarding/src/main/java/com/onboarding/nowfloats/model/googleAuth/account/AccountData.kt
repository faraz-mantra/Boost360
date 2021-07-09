package com.onboarding.nowfloats.model.googleAuth.account

import com.framework.base.BaseResponse

data class AccountData(
    val accountName: String? = null,
    val name: String? = null,
    val profilePhotoUrl: String? = null,
    val state: State? = null,
    val type: String? = null
) : BaseResponse()