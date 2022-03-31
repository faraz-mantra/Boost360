package com.appservice.model

import com.appservice.R
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Created by Admin on 27-04-2017.
 */
data class VmnCallModel(
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
  var callDuration: Long? = null,

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

  @SerializedName("fpTag")
  @Expose
  var fpTag: Any? = null,

  @SerializedName("merchantActualNumber")
  @Expose
  var merchantActualNumber: String? = null,

  @SerializedName("virtualNumber")
  @Expose
  var virtualNumber: String? = null,

  var audioPosition: Long = 0L,
  var audioLength: Long = 0L,
  var isAudioPlayState: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.VMN_CALL.getLayout()
  }

}