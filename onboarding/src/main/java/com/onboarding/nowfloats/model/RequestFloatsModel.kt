package com.onboarding.nowfloats.model

import android.os.Parcel
import android.os.Parcelable
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.model.registration.BusinessInfoModel

class RequestFloatsModel(
        var categoryDataModel: CategoryDataModel? = null,
        var channels: ArrayList<ChannelModel>? = null,
        var contactInfo: BusinessInfoModel? = null,
        var channelAccessTokens: ArrayList<ChannelAccessToken>? = ArrayList(),
        var channelActionDatas: ArrayList<ChannelActionData>? = ArrayList(),
        var floatingPointId: String? = null
) : Parcelable {

    constructor(source: Parcel) : this(
            source.readParcelable<CategoryDataModel>(CategoryDataModel::class.java.classLoader),
            source.createTypedArrayList(ChannelModel.CREATOR),
            source.readParcelable<BusinessInfoModel>(BusinessInfoModel::class.java.classLoader),
            source.createTypedArrayList(ChannelAccessToken.CREATOR),
            source.createTypedArrayList(ChannelActionData.CREATOR),
            source.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(categoryDataModel, 0)
        writeTypedList(channels)
        writeParcelable(contactInfo, 0)
        writeTypedList(channelAccessTokens)
        writeTypedList(channelActionDatas)
        writeString(floatingPointId)
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