package com.onboarding.nowfloats.model.business

import com.framework.base.BaseRequest
import java.io.Serializable

data class BusinessCreateRequest(
  var autoFillSampleWebsiteData: Boolean? = null,
  var clientId: String? = null,
  var webTemplateId: String? = null,
  var tag: String? = null,
  var contactName: String? = null,
  var name: String? = null,
  var desc: String? = null,
  var address: String? = null,
  var city: String? = null,
  var pincode: String? = null,
  var country: String? = null,
  var primaryNumber: String? = null,
  var email: String? = null,
  var primaryNumberCountryCode: String? = null,
  var uri: String? = null,
  var fbPageName: String? = null,
  var primaryCategory: String? = null,
  var lat: Int? = null,
  var lng: Int? = null,
  var appExperienceCode: String? = null,
  var whatsAppNumber: String? = null,
  var whatsAppNotificationOptIn: Boolean? = null,
  var boostXWebsiteUrl: String? = null
) : BaseRequest(), Serializable