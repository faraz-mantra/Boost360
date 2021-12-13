package com.boost.presignin.model.category

import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.utils.*
import java.io.Serializable

const val CATEGORY_LIVE_DATA = "CATEGORY_LIVE_DATA"

data class ApiCategoryResponse(
  val Data: ArrayList<ApiCategoryResponseData>? = null,
) : BaseResponse(), Serializable

data class ApiCategoryResponseData(
  val _kid: String? = null,
  val categories: ArrayList<ApiCategoryResponseCategory>? = null,
//  val createdon: String? = null,
//  val isarchived: Boolean? = null,
  val rootaliasurl: ApiCategoryResponseRootaliasurl? = null,
//  val schemaid: String? = null,
//  val updatedon: String? = null,
  val userid: String? = null,
  val websiteid: String? = null
) : Serializable

data class ApiCategoryResponseCategory(
  val _kid: String? = null,
//  val _parentClassId: String? = null,
//  val _parentClassName: String? = null,
//  val _propertyName: String? = null,
  val appexperiencecodedetails: ArrayList<Appexperiencecodedetail>? = null,
//  val createdon: String? = null,
//  val isarchived: Boolean? = null,
//  val appexperiencecodes: ArrayList<String>? = null,
  var name: String? = null,
//  val updatedon: String? = null,
  val websiteid: String? = null
) : Serializable, AppBaseRecyclerViewItem {

  var fpExperienceCode: Appexperiencecodedetail? = null
  var searchKeyword: String? = ""
  var subCategory: String? = ""

  fun getNameLower(): String {
    return (name ?: "").lowercase()
  }

  override fun getViewType(): Int {
    return RecyclerViewItemType.CATEGORY_SUGGESTION_ITEM.getLayout()
  }
}

data class ApiCategoryResponseRootaliasurl(
  val _kid: String? = null,
//  val _parentClassId: String? = null,
//  val _parentClassName: String? = null,
//  val _propertyName: String? = null,
//  val createdon: String? = null,
//  val isarchived: Boolean? = null,
//  val updatedon: String? = null,
  val url: String? = null,
  val websiteid: String? = null
) : Serializable

data class ApiCategoryResponsePreview(
  val _kid: String? = null,
//  val _parentClassId: String? = null,
//  val _parentClassName: String? = null,
//  val _propertyName: String? = null,
//  val createdon: String? = null,
//  val isarchived: Boolean? = null,
//  val updatedon: String? = null,
  val url: String? = null,
  val websiteid: String? = null
) : Serializable

data class Appexperiencecodedetail(
  val _kid: String? = null,
//  val _parentClassId: String? = null,
//  val _parentClassName: String? = null,
//  val _propertyName: String? = null,
//  val createdon: String? = null,
  val desktoppreview: ApiCategoryResponsePreview? = null,
//  val isarchived: Boolean? = null,
  val mobilepreview: ApiCategoryResponsePreview? = null,
  val name: String? = null,
//  val updatedon: String? = null,
//  val websiteid: String? = null
) : Serializable

fun getCategoryLiveData(): List<ApiCategoryResponseCategory>? {
  return convertStringToList(PreferencesUtils.instance.getData(CATEGORY_LIVE_DATA, "") ?: "")
}

fun List<ApiCategoryResponseCategory>.saveCategoryLiveData() {
  PreferencesUtils.instance.saveData(CATEGORY_LIVE_DATA, convertListObjToString(this))
}
