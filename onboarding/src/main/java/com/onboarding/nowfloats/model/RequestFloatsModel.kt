package com.onboarding.nowfloats.model

import android.os.Parcel
import android.os.Parcelable
import com.onboarding.nowfloats.model.category.CategoryModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.registration.RegistrationViewModel

class RequestFloatsModel(
        var category: CategoryModel? = null,
        var channels: ArrayList<ChannelModel>? = null,
        var contactInfo: RegistrationViewModel? = null
) : Parcelable {

  constructor(source: Parcel) : this(
          source.readParcelable<CategoryModel>(CategoryModel::class.java.classLoader),
          source.createTypedArrayList(ChannelModel.CREATOR),
          source.readParcelable<RegistrationViewModel>(RegistrationViewModel::class.java.classLoader)
  )

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeParcelable(category, 0)
    writeTypedList(channels)
    writeParcelable(contactInfo, 0)
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