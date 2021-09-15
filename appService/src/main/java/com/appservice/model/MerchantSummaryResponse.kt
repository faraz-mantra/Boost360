package com.appservice.model

import com.framework.base.BaseResponse

data class MerchantSummaryResponse(
    val Entity: List<Map<String,Int>>,
    val ErrorList: List<Any>,
    val OperationStatus: Boolean,
    val ReferenceId: Any
):BaseResponse()

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
