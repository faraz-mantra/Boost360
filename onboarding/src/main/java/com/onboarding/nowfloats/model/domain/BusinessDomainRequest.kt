package com.onboarding.nowfloats.model.domain

import com.framework.base.BaseRequest

data class BusinessDomainRequest(
    val clientId: String? = null,
    val fpTag: String? = null,
    val fpName: String? = null,
) : BaseRequest()