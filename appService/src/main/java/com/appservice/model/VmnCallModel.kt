package com.appservice.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Created by Admin on 27-04-2017.
 */
data class VmnCallModel (
    @SerializedName("_id")
    @Expose
    var id: String? = null,

    @SerializedName("applicationId")
    @Expose
    var applicationId: String? = null,

    @SerializedName("callDateTime")
    @Expose
    var callDateTime: String? = null,

    @SerializedName("callDuration")
    @Expose
    var callDuration: Int? = null,

    @SerializedName("callRecordingUri")
    @Expose
    var callRecordingUri: String? = null,

    @SerializedName("callStatus")
    @Expose
    var callStatus: String? = null,

    @SerializedName("callerNumber")
    @Expose
    var callerNumber: String? = null,

    @SerializedName("externalTrackingId")
    @Expose
    var externalTrackingId: String? = null,

    @SerializedName("fpId")
    @Expose
    var fpId: String? = null,
    @SerializedName("viewType")
    @Expose
    var viewType: Int = 0,

    @SerializedName("fpTag")
    @Expose
    var fpTag: Any? = null,

    @SerializedName("merchantActualNumber")
    @Expose
    var merchantActualNumber: String? = null,

    @SerializedName("virtualNumber")
    @Expose
    var virtualNumber: String? = null,
    var audioPosition: Int = 0,
    var audioLength: Int = 0,
    var isAudioPlayState: Boolean = false,
): BaseResponse()