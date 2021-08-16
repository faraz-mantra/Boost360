package com.appservice.model.account.testimonial.webActionList

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialWebActionResponse(

  @field:SerializedName("WebActions")
  val webActions: ArrayList<WebActionsItem?>? = null,

  @field:SerializedName("Token")
  val token: String? = null
) : BaseResponse(), Serializable

data class PropertiesItem(

  @field:SerializedName("PropertyName")
  val propertyName: String? = null,

  @field:SerializedName("IsRequired")
  val isRequired: Boolean? = null,

  @field:SerializedName("DisplayName")
  val displayName: String? = null,

  @field:SerializedName("DataType")
  val dataType: String? = null,

  @field:SerializedName("PropertyType")
  val propertyType: Int? = null,

  @field:SerializedName("ValidationRegex")
  val validationRegex: Any? = null
)

data class WebActionsItem(

  @field:SerializedName("WebsiteId")
  val websiteId: Any? = null,

  @field:SerializedName("ActionId")
  val actionId: String? = null,

  @field:SerializedName("UserName")
  val userName: Any? = null,

  @field:SerializedName("Description")
  val description: String? = null,

  @field:SerializedName("UserId")
  val userId: String? = null,

  @field:SerializedName("DisplayName")
  val displayName: String? = null,

  @field:SerializedName("UpdatedOn")
  val updatedOn: String? = null,

  @field:SerializedName("Properties")
  val properties: ArrayList<PropertiesItem?>? = null,

  @field:SerializedName("Name")
  val name: String? = null
) : Serializable
