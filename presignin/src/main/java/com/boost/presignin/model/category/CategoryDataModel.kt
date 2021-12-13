package com.boost.presignin.model.category

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.appservice.utils.capitalizeUtil
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import java.io.Serializable

class CategoryDataModel(
  val experience_code: String? = null,
  val webTemplateId: String? = null,
  val category_key: String? = null,
  val category_Name: String? = null,
  val category_descriptor: String? = null,
  val icon: String? = null,
  val sections: ArrayList<SectionsFeature>? = null,
) : BaseResponse(), AppBaseRecyclerViewItem, Serializable {
  var recyclerViewItem: Int = RecyclerViewItemType.CATEGORY_ITEM.getLayout()
  var sectionType: Boolean = false
  var isSelected = false

  var textChangeRTLAndSVC = false

  fun getCategoryWithoutNewLine(): String? {
    return category_Name?.replace("\\n".toRegex(), " ")
  }

  fun getCategoryName(): String? {
    return when (experience_code) {
      "SVC" -> "Do you provide services?"
      else -> "Do you sell products?" //"RTL"
    }
  }

  fun experienceCode(): String {
    return experience_code ?: ""
  }

  fun getSectionsTitles(): String? {
    return if (sections.isNullOrEmpty()) category_descriptor?.capitalizeUtil() else {
      val item = sections.take(2)
      "${item.firstOrNull()?.title ?: ""}, ${item.lastOrNull()?.title ?: ""}".capitalizeUtil()
    }
  }

  companion object {
    var saveeCategory: CategoryDataModel? = null
    fun saveCategoryState(category: CategoryDataModel?) {
      saveeCategory = category
    }

    fun getSavedStateCategory(): CategoryDataModel? {
      return saveeCategory
    }

    fun clearSelection() {
      saveeCategory = null
    }
  }

  override fun getViewType(): Int {
//    return if (sectionType) RecyclerViewItemType.SECTION_HEADER_ITEM.getLayout() else
//    RecyclerViewItemType.CATEGORY_ITEM_OV2.getLayout()
    return recyclerViewItem
  }

  fun getSelectedItem() {
    return
  }


  fun getImage(context: Context?): Drawable? {

    if (context == null) return null

    val resId = when (icon?.let { CategoryType.from(it) }) {
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
    } ?: return null

    return ContextCompat.getDrawable(context, resId)
  }

  fun getCategoryImage(context: Context?, selected: Boolean? = false): Drawable? {

    if (context == null) return null

    val resId = when (icon?.let { CategoryType.from(it) }) {
      CategoryType.DOCTORS -> if (selected == false) R.drawable.doctors else R.drawable.doctors_dark
      CategoryType.CLINICS_HOSPITALS -> if (selected == false) R.drawable.clinics_hospital else R.drawable.clinics_hospital_dark
      CategoryType.EDUCATION_COACHING -> if (selected == false) R.drawable.education else R.drawable.education_dark
      CategoryType.HOTELS_MOTELS -> if (selected == false) R.drawable.category_hotel else R.drawable.category_hotel_dark
      CategoryType.MANUFACTURING_EQUIPMENT -> if (selected == false) R.drawable.manufacturing else R.drawable.manufacturing_dark
      CategoryType.SPAS_WELLNESS -> if (selected == false) R.drawable.spa_bathrobe else R.drawable.spa_bathrobe_dark
      CategoryType.SALON -> if (selected == false) R.drawable.beauty_salons else R.drawable.beauty_salons_dark
      CategoryType.RESTAURANT_CAFES -> if (selected == false) R.drawable.resturants_cafe else R.drawable.resturants_cafe_dark
      CategoryType.RETAIL_BUSINESS -> if (selected == false) R.drawable.retail_business else R.drawable.retail_business_dark
      CategoryType.SERVICES_BUSINESS -> if (selected == false) R.drawable.service_providers else R.drawable.service_providers_dark
      else -> null
    } ?: return null

    return ContextCompat.getDrawable(context, resId)
  }
}


