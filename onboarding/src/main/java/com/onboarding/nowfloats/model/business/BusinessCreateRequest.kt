package com.onboarding.nowfloats.model.business

import com.framework.base.BaseRequest

data class BusinessCreateRequest(
        val autoFillSampleWebsiteData: Boolean? = null,
        val clientId: String? = null,
        val tag: String? = null,
        val contactName: String? = null,
        val name: String? = null,
        val desc: String? = null,
        val address: String? = null,
        val city: String? = null,
        val pincode: String? = null,
        val country: String? = null,
        val primaryNumber: String? = null,
        val email: String? = null,
        val primaryNumberCountryCode: String? = null,
        val uri: String? = null,
        val fbPageName: String? = null,
        val primaryCategory: String? = null,
        val lat: Int? = null,
        val lng: Int? = null
) : BaseRequest()