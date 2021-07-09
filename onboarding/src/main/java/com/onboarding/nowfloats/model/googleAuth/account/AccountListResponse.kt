package com.onboarding.nowfloats.model.googleAuth.account

import com.framework.base.BaseResponse

data class AccountListResponse(
    val accounts: List<AccountData>? = null
) : BaseResponse()