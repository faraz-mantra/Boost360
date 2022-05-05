package com.appservice.model.testimonial.response

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialData(
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("ProfileImage")
  var profileImage: ProfileImageN? = null,
  @SerializedName("ReviewerCity")
  var reviewerCity: String? = null,
  @SerializedName("ReviewerName")
  var reviewerName: String? = null,
  @SerializedName("ReviewerTitle")
  var reviewerTitle: String? = null,
  @SerializedName("TestimonialBody")
  var testimonialBody: String? = null,
  @SerializedName("TestimonialDate")
  var testimonialDate: String? = null,
  @SerializedName("TestimonialId")
  var testimonialId: String? = null,
  @SerializedName("TestimonialTitle")
  var testimonialTitle: String? = null
) : AppBaseRecyclerViewItem, Serializable {

  var recyclerViewItem: Int = RecyclerViewItemType.TESTIMONIAL_ITEM.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun getLoaderItem(): TestimonialData {
    this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
    return this
  }

  fun getImageUrl(): String {
    return profileImage?.tileImage ?: profileImage?.actualImage ?: ""

  }

  fun getTitleName(): String {
    return reviewerName ?: ""

  }

  fun getTitleDesc(): String {
    return reviewerTitle ?: ""

  }

  fun getTestimonialName(): String {
    return testimonialTitle ?: ""

  }

  fun getTestimonialDesc(): String {
    return testimonialBody ?: ""
  }
}