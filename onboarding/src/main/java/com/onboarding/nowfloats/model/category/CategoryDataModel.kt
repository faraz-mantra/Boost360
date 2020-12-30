package com.onboarding.nowfloats.model.category

import SectionsFeature
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.framework.base.BaseResponse
import com.framework.utils.PreferencesUtils
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.framework.utils.getData
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.ChannelType
import com.onboarding.nowfloats.model.channel.isGoogleChannel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

const val CATEGORY_DASHBOARD_DATA = "CATEGORY_DASHBOARD_DATA"

class CategoryDataModel(
    val experience_code: String? = null,
    val webTemplateId: String? = null,
    val category_key: String? = null,
    val category_Name: String? = null,
    val category_descriptor: String? = null,
    val icon: String? = null,
    var channels: ArrayList<ChannelModel>? = null,
    val sections: ArrayList<SectionsFeature>? = null,
) : BaseResponse(), AppBaseRecyclerViewItem, Parcelable {
  val sectionType: Boolean = false
  var isSelected = false

  fun experienceCode(): String {
    return experience_code ?: ""
  }

  fun resetIsSelect() {
    channels?.forEach { it.isSelected = it.isGoogleChannel() }
  }

  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.createTypedArrayList(ChannelModel.CREATOR),
      parcel.createTypedArrayList(SectionsFeature.CREATOR)
  ) {
    isSelected = parcel.readByte() != 0.toByte()
  }

  override fun getViewType(): Int {
    return if (sectionType) RecyclerViewItemType.SECTION_HEADER_ITEM.getLayout() else RecyclerViewItemType.CATEGORY_ITEM.getLayout()
  }

  fun getChannelList(): ArrayList<ChannelModel>? {
    channels?.forEach {
      val data = it.type?.let { it1 -> ChannelType.from(it1) }
      if (data != null && data.name == ChannelType.G_SEARCH.name) ChannelModel(isSelected = true)
    }
    return channels
  }

  fun getImage(context: Context?): Drawable? {
    if (context == null) return null
    return when (icon?.let { CategoryTypeNew.from(it) }) {
      CategoryTypeNew.DOCTORS -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_doctors_pro,
          context.theme
      )
      CategoryTypeNew.CLINICS_HOSPITALS -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_hospital,
          context.theme
      )
      CategoryTypeNew.EDUCATION_COACHING -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_edu,
          context.theme
      )
      CategoryTypeNew.HOTELS_MOTELS -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_hotel,
          context.theme
      )
      CategoryTypeNew.MANUFACTURING_EQUIPMENT -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_mfg,
          context.theme
      )
      CategoryTypeNew.SPAS_WELLNESS -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_spa,
          context.theme
      )
      CategoryTypeNew.SALON -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_salon,
          context.theme
      )
      CategoryTypeNew.RESTAURANT_CAFES -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_cafe,
          context.theme
      )
      CategoryTypeNew.RETAIL_BUSINESS -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_other_retails,
          context.theme
      )
      CategoryTypeNew.SERVICES_BUSINESS -> ResourcesCompat.getDrawable(
          context.resources,
          R.drawable.ic_business_services_pro,
          context.theme
      )
      else -> null
    }
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(experience_code)
    parcel.writeString(webTemplateId)
    parcel.writeString(category_key)
    parcel.writeString(category_Name)
    parcel.writeString(icon)
    parcel.writeTypedList(channels)
    parcel.writeTypedList(sections)
    parcel.writeByte(if (isSelected) 1 else 0)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<CategoryDataModel> {
    override fun createFromParcel(parcel: Parcel): CategoryDataModel {
      return CategoryDataModel(parcel)
    }

    override fun newArray(size: Int): Array<CategoryDataModel?> {
      return arrayOfNulls(size)
    }
  }

  fun getCategoryChannelData(): CategoryDataModel? {
    val resp = PreferencesUtils.instance.getData(CATEGORY_DASHBOARD_DATA, "") ?: ""
    return convertStringToObj(resp)
  }

  fun saveData() {
    PreferencesUtils.instance.saveDataN(CATEGORY_DASHBOARD_DATA, convertObjToString(this) ?: "")
  }
}
