package com.onboarding.nowfloats.model.category

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

@Deprecated("old")
data class CategoryModel(
  val image: String? = null,
  val imageTpe: String? = null,
  private val sectionType: Boolean = false,
  val title: String? = null
) : BaseResponse(), AppBaseRecyclerViewItem, Parcelable {

  enum class CategoryType {
    DOCTORS_CLINICS, EDUCATION_COACHING, HOTELS_MOTELS, MANUFACTURING_EQUIPMENT, SPAS_SALONS, RESTAURANT_CAFES, RETAIL_BUSINESS, SERVICES_BUSINESS
  }

  var isSelected = false

  override fun getViewType(): Int {
    return if (sectionType) RecyclerViewItemType.SECTION_HEADER_ITEM.getLayout() else RecyclerViewItemType.CATEGORY_ITEM.getLayout()
  }

  fun getImage(context: Context?): Drawable? {
    if (context == null) return null
    return when (CategoryType.values().firstOrNull { it.name == imageTpe }) {
      CategoryType.DOCTORS_CLINICS -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_doctors_pro,
        context.theme
      )
      CategoryType.EDUCATION_COACHING -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_edu,
        context.theme
      )
      CategoryType.HOTELS_MOTELS -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_hotel,
        context.theme
      )
      CategoryType.MANUFACTURING_EQUIPMENT -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_mfg,
        context.theme
      )
      CategoryType.SPAS_SALONS -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_spa,
        context.theme
      )
      CategoryType.RESTAURANT_CAFES -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_cafe,
        context.theme
      )
      CategoryType.RETAIL_BUSINESS -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_other_retails,
        context.theme
      )
      CategoryType.SERVICES_BUSINESS -> ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_business_services_pro,
        context.theme
      )
      else -> null
    }
  }

  constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    1 == source.readInt(),
    source.readString()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeString(image)
    writeString(imageTpe)
    writeInt((if (sectionType) 1 else 0))
    writeString(title)
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<CategoryModel> =
      object : Parcelable.Creator<CategoryModel> {
        override fun createFromParcel(source: Parcel): CategoryModel = CategoryModel(source)
        override fun newArray(size: Int): Array<CategoryModel?> = arrayOfNulls(size)
      }
  }
}