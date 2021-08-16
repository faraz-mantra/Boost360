package com.boost.presignin.model.fpList

import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FPListResponse(
  @field:SerializedName("StatusCode")
  var statusCode: Int? = null,

  @field:SerializedName("Result")
  var result: ArrayList<ResultItem>? = null,
) : BaseResponse(), Serializable

data class ResultItem(

  @field:SerializedName("Description")
  var description: String? = null,

  @field:SerializedName("LogoUrl")
  var logoUrl: String? = null,

  @field:SerializedName("FloatingPointTag")
  var floatingPointTag: String? = null,

  @field:SerializedName("FloatingPointId")
  var floatingPointId: String? = null,

  @field:SerializedName("Name")
  var name: String? = null,

  @field:SerializedName("RootAliasUri")
  var rootAliasUri: String? = null,
  var isItemSelected: Boolean? = false,

  ) : AppBaseRecyclerViewItem {
  override fun getViewType(): Int {
    return RecyclerViewItemType.BUSINESS_LIST_ITEM.getLayout()

  }
}

data class Error(

  @field:SerializedName("ErrorList")
  var errorList: Any? = null,

  @field:SerializedName("ErrorCode")
  var errorCode: Any? = null,
)
