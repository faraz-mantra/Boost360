package com.onboarding.nowfloats.model.googleAuth.location

import java.io.Serializable

data class LocationState(
  val canDelete: Boolean? = null,
  val canModifyServiceList: Boolean? = null,
  val canUpdate: Boolean? = null,
  val hasPendingVerification: Boolean? = null,
  val isDisconnected: Boolean? = null
) : Serializable