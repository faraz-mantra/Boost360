package com.appservice.ui.testimonial.newflow.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.ui.model.Paging
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialListingResponse(

  @field:SerializedName("StatusCode")
  var statusCode: Int? = null,

  @field:SerializedName("Result")
  var result: Result? = null
) : BaseResponse(), Serializable

data class Result(

  @field:SerializedName("Paging")
  var paging: Paging? = null,

  @field:SerializedName("Data")
  var data: List<DataItem?>? = null
) : Serializable

data class DataItem(

  @field:SerializedName("ReviewerTitle")
  var reviewerTitle: String? = null,

  @field:SerializedName("TestimonialBody")
  var testimonialBody: String? = null,

  @field:SerializedName("TestimonialTitle")
  var testimonialTitle: String? = null,

  @field:SerializedName("ProfileImage")
  var profileImage: ProfileImageTestimonial? = null,

  @field:SerializedName("ReviewerCity")
  var reviewerCity: String? = null,

  @field:SerializedName("CreatedOn")
  var createdOn: String? = null,

  @field:SerializedName("TestimonialId")
  var testimonialId: String? = null,

  @field:SerializedName("TestimonialDate")
  var testimonialDate: String? = null,

  @field:SerializedName("ReviewerName")
  var reviewerName: String? = null,
  @field:SerializedName("FloatingPointTag")
  var floatingPointTag: String? = null,

  @field:SerializedName("FloatingPointId")
  var floatingPointId: String? = null,

  ) : BaseResponse(), AppBaseRecyclerViewItem, Serializable {

  var recyclerViewItem: Int = RecyclerViewItemType.TESTIMONIAL_LISTING_VIEW.getLayout();

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun getLoaderItem(): DataItem {
    this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
    return this
  }


  data class Paging(

    @field:SerializedName("Skip")
    var skip: Int? = null,

    @field:SerializedName("Limit")
    var limit: Int? = null,

    @field:SerializedName("Count")
    var count: Int? = null
  ) : Serializable
}