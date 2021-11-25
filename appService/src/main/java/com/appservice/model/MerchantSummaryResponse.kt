package com.appservice.model

import com.framework.base.BaseResponse
import com.framework.utils.*

const val MERCHANT_SUMMARY_RESPONSE = "MERCHANT_SUMMARY_RESPONSE"

data class MerchantSummaryResponse(
  val Entity: List<Map<String, Int>>,
  val ErrorList: List<Any>,
  val OperationStatus: Boolean,
  val ReferenceId: Any
) : BaseResponse()


fun MerchantSummaryResponse.saveMerchantSummary() {
  PreferencesUtils.instance.saveData(MERCHANT_SUMMARY_RESPONSE, convertObjToString(this))
}

fun getMerchantSummaryWebsite(): MerchantSummaryResponse? {
  return convertStringToObj(PreferencesUtils.instance.getData(MERCHANT_SUMMARY_RESPONSE, "") ?: "")
}


/*
data class MerchantSummaryResponseEntity(
    val NoOfBrochures: Int,
    val NoOfCustomPages: Int,
    val NoOfFaculty: Int,
    val NoOfImages: Int,
    val NoOfMessages: Int,
    val NoOfPlacesAround: Int,
    val NoOfProducts: Int,
    val NoOfProjects: Int,
    val NoOfSeasonalOffers: Int,
    val NoOfServices: Int,
    val NoOfStaff: Int,
    val NoOfSubscribers: Int,
    val NoOfTeamMembers: Int,
    val NoOfTestimonials: Int,
    val NoOfToppers: Int,
    val NoOfTripAdvisor: Int,
    val NoOfUpdates: Int,
    val NoOfupcomingBatches: Int
)*/
