package com.boost.presignin.model.category

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.utils.PreferencesUtils

const val CATEGORY_DASHBOARD_DATA = "CATEGORY_DASHBOARD_DATA"

class CategoryDataModel(
    val experience_code: String? = null,
    val webTemplateId: String? = null,
    val category_key: String? = null,
    val category_Name: String? = null,
    val category_descriptor: String? = null,
    val icon: String? = null,
//    var channels: ArrayList<ChannelModel>? = null,
    val sections: ArrayList<SectionsFeature>? = null,
) : BaseResponse(), AppBaseRecyclerViewItem, Parcelable {
  val sectionType: Boolean = false
  var isSelected = false

  fun experienceCode(): String {
    return experience_code ?: ""
  }

//  fun resetIsSelect() {
//    channels?.forEach { it.isSelected = it.isGoogleChannel() }
//  }

  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
//      parcel.createTypedArrayList(ChannelModel.CREATOR),
//      parcel.createTypedArrayList(SectionsFeature.CREATOR)
  ) {
    isSelected = parcel.readByte() != 0.toByte()
  }

  override fun getViewType(): Int {
    return if (sectionType) RecyclerViewItemType.SECTION_HEADER_ITEM.getLayout() else RecyclerViewItemType.CATEGORY_ITEM.getLayout()
  }
//
//  fun getChannelList(): ArrayList<ChannelModel>? {
//    channels?.forEach {
//      val data = it.type?.let { it1 -> ChannelType.from(it1) }
//      if (data != null && data.name == ChannelType.G_SEARCH.name) ChannelModel(isSelected = true)
//    }
//    return channels
//  }

  fun getImage(context: Context?): Drawable? {
  
    if (context == null) return null

    val resId =  when (icon?.let { CategoryType.from(it) }) {
      CategoryType.DOCTORS -> R.drawable.ic_category_doctor
      CategoryType.CLINICS_HOSPITALS -> R.drawable.ic_categoty_hospital_architectonic
      CategoryType.EDUCATION_COACHING -> R.drawable.ic_category_education
      CategoryType.HOTELS_MOTELS -> R.drawable.ic_category_hotel
      CategoryType.MANUFACTURING_EQUIPMENT -> R.drawable.ic_manufacturing
      CategoryType.SPAS_WELLNESS -> R.drawable.ic_category_spa_bathrobe
      CategoryType.SALON -> R.drawable.ic_salon
      CategoryType.RESTAURANT_CAFES -> R.drawable.ic_cafe
      CategoryType.RETAIL_BUSINESS -> R.drawable.ic_other_retails
      CategoryType.SERVICES_BUSINESS -> R.drawable.ic_business_services_pro
      else -> null
    }?:return null

    return ContextCompat.getDrawable(context, resId)
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(experience_code)
    parcel.writeString(webTemplateId)
    parcel.writeString(category_key)
    parcel.writeString(category_Name)
    parcel.writeString(icon)
//    parcel.writeTypedList(channels)
//    parcel.writeTypedList(sections)
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

//  fun getCategoryChannelData(): CategoryDataModel? {
//    val resp = PreferencesUtils.instance.getData(CATEGORY_DASHBOARD_DATA, "") ?: ""
//    return convertStringToObj(resp)
//  }
//
//  fun saveData() {
//    PreferencesUtils.instance.saveData(CATEGORY_DASHBOARD_DATA, convertObjToString(this) ?: "")
//  }
}
