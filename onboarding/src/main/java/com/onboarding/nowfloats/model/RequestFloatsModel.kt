package com.onboarding.nowfloats.model

import android.os.Parcel
import android.os.Parcelable
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.model.channel.request.isLinked
import com.onboarding.nowfloats.model.registration.BusinessInfoModel

class RequestFloatsModel(
    var categoryDataModel: CategoryDataModel? = null,
    var channels: ArrayList<ChannelModel>? = null,
    var contactInfo: BusinessInfoModel? = null,
    var channelAccessTokens: ArrayList<ChannelAccessToken>? = ArrayList(),
    var channelActionDatas: ArrayList<ChannelActionData>? = ArrayList(),
    var floatingPointId: String? = null,
    var fpTag: String? = null,
    var isUpdate: Boolean? = false,
    var whatsappEntransactional: Boolean? = null,
    var isFpCreate: Boolean? = false,
) : Parcelable {
  var websiteUrl: String? = ""
  var profileUrl: String? = null
  var businessUrl: String? = null

  fun getWebSiteId(): String? {
    return if (isUpdate == true) fpTag else contactInfo?.domainName
  }

  fun  getConnectedAccessToken():ArrayList<ChannelAccessToken>{
    return ArrayList(channelAccessTokens?.filter { it.isLinked() })
  }

  constructor(source: Parcel) : this(
      source.readParcelable<CategoryDataModel>(CategoryDataModel::class.java.classLoader),
      source.createTypedArrayList(ChannelModel.CREATOR),
      source.readParcelable<BusinessInfoModel>(BusinessInfoModel::class.java.classLoader),
      source.createTypedArrayList(ChannelAccessToken.CREATOR),
      source.createTypedArrayList(ChannelActionData.CREATOR),
      source.readString(),
      source.readString(),
      source.readValue(Boolean::class.java.classLoader) as? Boolean) {
    profileUrl = source.readString()
    businessUrl = source.readString()
  }

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeParcelable(categoryDataModel, 0)
    writeTypedList(channels)
    writeParcelable(contactInfo, 0)
    writeTypedList(channelAccessTokens)
    writeTypedList(channelActionDatas)
    writeString(floatingPointId)
    writeString(fpTag)
    writeValue(isUpdate)
    writeString(profileUrl)
    writeString(businessUrl)
  }

  override fun describeContents(): Int {
    return 0
  }


  companion object CREATOR : Parcelable.Creator<RequestFloatsModel> {
    override fun createFromParcel(parcel: Parcel): RequestFloatsModel {
      return RequestFloatsModel(parcel)
    }

    override fun newArray(size: Int): Array<RequestFloatsModel?> {
      return arrayOfNulls(size)
    }
  }
}



