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
import java.io.Serializable

const val CATEGORY_DASHBOARD_DATA = "CATEGORY_DASHBOARD_DATA"

class CategoryDataModel(
    val experience_code: String? = null,
    val webTemplateId: String? = null,
    val category_key: String? = null,
    val category_Name: String? = null,
    val category_descriptor: String? = null,
    val icon: String? = null,
    val sections: ArrayList<SectionsFeature>? = null,
) : BaseResponse(), AppBaseRecyclerViewItem, Serializable {
  var sectionType: Boolean = false
  var isSelected = false

  fun experienceCode(): String {
    return experience_code ?: ""
  }

  override fun getViewType(): Int {
    return if (sectionType) RecyclerViewItemType.SECTION_HEADER_ITEM.getLayout() else RecyclerViewItemType.CATEGORY_ITEM.getLayout()
  }


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

  }


