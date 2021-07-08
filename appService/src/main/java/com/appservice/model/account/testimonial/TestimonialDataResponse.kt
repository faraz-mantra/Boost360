package com.appservice.model.account.testimonial

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialDataResponse(

  @field:SerializedName("Extra")
  val extra: ExtraItem? = null,

  @field:SerializedName("Data")
  val data: ArrayList<TestimonialData>? = null,
) : BaseResponse(), Serializable

data class ExtraItem(

  @field:SerializedName("TotalCount")
  val totalCount: Int? = null,

  @field:SerializedName("PageSize")
  val pageSize: Int? = null,

  @field:SerializedName("CurrentIndex")
  val currentIndex: Int? = null,
) : Serializable

data class Profileimage(

  @field:SerializedName("description")
  val description: String? = null,

  @field:SerializedName("url")
  val url: String? = null,
) : Serializable

data class TestimonialData(

  @field:SerializedName("WebsiteId")
  val websiteId: String? = null,

  @field:SerializedName("IsArchived")
  val isArchived: Boolean? = null,

  @field:SerializedName("ActionId")
  val actionId: String? = null,

  @field:SerializedName("UserId")
  val userId: String? = null,

  @field:SerializedName("description")
  val description: String? = null,

  @field:SerializedName("UpdatedOn")
  val updatedOn: String? = null,

  @field:SerializedName("_id")
  val id: String? = null,

  @field:SerializedName("CreatedOn")
  val createdOn: String? = null,

  @field:SerializedName("profileimage")
  val profileimage: Profileimage? = null,

  @field:SerializedName("username")
  val username: String? = null,
) : Serializable
